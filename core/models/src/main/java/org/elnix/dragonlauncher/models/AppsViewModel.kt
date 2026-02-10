package org.elnix.dragonlauncher.models

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.logging.logI
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.AppOverride
import org.elnix.dragonlauncher.common.serializables.CustomIconSerializable
import org.elnix.dragonlauncher.common.serializables.IconMapping
import org.elnix.dragonlauncher.common.serializables.IconPackInfo
import org.elnix.dragonlauncher.common.serializables.IconType
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.serializables.Workspace
import org.elnix.dragonlauncher.common.serializables.WorkspaceState
import org.elnix.dragonlauncher.common.serializables.WorkspaceType
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.common.serializables.defaultWorkspaces
import org.elnix.dragonlauncher.common.serializables.dummySwipePoint
import org.elnix.dragonlauncher.common.serializables.resolveApp
import org.elnix.dragonlauncher.common.utils.APPS_TAG
import org.elnix.dragonlauncher.common.utils.ICONS_TAG
import org.elnix.dragonlauncher.common.utils.ImageUtils.createUntintedBitmap
import org.elnix.dragonlauncher.common.utils.ImageUtils.loadDrawableAsBitmap
import org.elnix.dragonlauncher.common.utils.ImageUtils.resolveCustomIconBitmap
import org.elnix.dragonlauncher.common.utils.PackageManagerCompat
import org.elnix.dragonlauncher.common.utils.TAG
import org.elnix.dragonlauncher.settings.stores.AppsSettingsStore
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.settings.stores.WorkspaceSettingsStore
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser


class AppsViewModel(
    application: Application,
    coroutineScope: CoroutineScope
) {
    private val scope = coroutineScope

    private val _apps = MutableStateFlow<List<AppModel>>(emptyList())
    val allApps: StateFlow<List<AppModel>> = _apps.asStateFlow()

    private val _iconPacksList = MutableStateFlow<List<IconPackInfo>>(emptyList())
    val iconPacksList = _iconPacksList.asStateFlow()


    private val _packIcons = MutableStateFlow<List<String>>(emptyList())
    val packIcons: StateFlow<List<String>> = _packIcons.asStateFlow()

    private val _packTint = MutableStateFlow<Int?>(null)
    val packTint = _packTint.asStateFlow()


    private val _icons = MutableStateFlow<Map<String, ImageBitmap>>(emptyMap())
    val icons = _icons.asStateFlow()

    private val _pointIcons = MutableStateFlow<Map<String, ImageBitmap>>(emptyMap())
    val pointIcons = _pointIcons.asStateFlow()


    private val _defaultPoint = MutableStateFlow(defaultSwipePointsValues)
    val defaultPoint = _defaultPoint.asStateFlow()

    // Only used for preview, the real user apps getter are using the appsForWorkspace function
    val userApps: StateFlow<List<AppModel>> = _apps.map { list ->
        list.filter { it.isLaunchable == true }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())


    private val _selectedIconPack = MutableStateFlow<IconPackInfo?>(null)
    val selectedIconPack: StateFlow<IconPackInfo?> = _selectedIconPack.asStateFlow()

    private val iconPackCache = mutableMapOf<String, IconPackCache>()


    @SuppressLint("StaticFieldLeak")
    private val ctx = application.applicationContext

    private val pm: PackageManager = application.packageManager
    private val pmCompat = PackageManagerCompat(pm, ctx)
    private val resourceIdCache = mutableMapOf<String, Int>()

    /**
     * Used to correctly dispatch the heavy background load, as long as I understand
     */
    private val iconSemaphore = Semaphore(4)


    private val gson = Gson()


    /* ───────────── Workspace things ───────────── */
    private val _workspacesState = MutableStateFlow(
        WorkspaceState()
    )
    val state: StateFlow<WorkspaceState> = _workspacesState.asStateFlow()

    /** Get enabled workspaces only */
    val enabledState: StateFlow<WorkspaceState> = _workspacesState
        .map { state ->
            state.copy(
                workspaces = state.workspaces.filter { it.enabled }
            )
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = WorkspaceState()
        )


    private val _selectedWorkspaceId = MutableStateFlow("user")
    val selectedWorkspaceId: StateFlow<String> = _selectedWorkspaceId.asStateFlow()

    /* ───────────── Recently Used Apps ───────────── */
    private val _recentlyUsedPackages = MutableStateFlow<List<String>>(emptyList())
//    val recentlyUsedPackages: StateFlow<List<String>> = _recentlyUsedPackages.asStateFlow()


    init {
        scope.launch {
            loadAll()
        }
    }

    /**
     * Loads everything the model needs, runs at start and when the user restore from a backup
     */
    suspend fun loadAll() {
        loadWorkspaces()
        loadDefaultPoint()
        loadRecentlyUsedApps()
        val savedPackTint = UiSettingsStore.iconPackTint.get(ctx)
        savedPackTint?.let { tint ->
            _packTint.value = tint.toArgb()
        }

        val savedPackName = UiSettingsStore.selectedIconPack.get(ctx)
        savedPackName?.let { pkg ->
            loadIconPacks()
            _selectedIconPack.value = _iconPacksList.value.find { it.packageName == pkg }
        }

        loadApps()
    }

    /**
     * Returns a filtered and sorted list of apps for the specified workspace as a reactive Flow.
     *
     * @param workspace The target workspace configuration defining app filtering rules
     * @param overrides Custom app overrides to apply (icon/label changes, etc.)
     * @param getOnlyAdded If true, returns ONLY apps explicitly added to this workspace [default: false]
     * @param getOnlyRemoved If true, returns ONLY apps hidden/removed from this workspace [default: false]
     * @return Flow of filtered, sorted, and resolved [AppModel] list
     *
     * @throws IllegalArgumentException if both [getOnlyAdded] and [getOnlyRemoved] are true
     *
     * @see WorkspaceType for base filtering behavior
     * @see AppOverride for override application details
     * @see resolveApp for final app resolution logic
     */
    fun appsForWorkspace(
        workspace: Workspace,
        overrides: Map<String, AppOverride>,
        getOnlyAdded: Boolean = false,
        getOnlyRemoved: Boolean = false
    ): StateFlow<List<AppModel>> {

        require(!(getOnlyAdded && getOnlyRemoved))

        // May be null cause I added the removed app ids lately, so some user may still have the old app model without it
        val removed = workspace.removedAppIds ?: emptyList()

        return _apps.map { list ->
            when {
                getOnlyAdded -> list.filter { it.packageName in workspace.appIds }
                getOnlyRemoved -> list.filter { it.packageName in removed }
                else -> {
                    val base = when (workspace.type) {
                        WorkspaceType.ALL, WorkspaceType.CUSTOM -> list
                        WorkspaceType.USER -> list.filter { !it.isWorkProfile && it.isLaunchable == true }
                        WorkspaceType.SYSTEM -> list.filter { it.isSystem }
                        WorkspaceType.WORK -> list.filter { it.isWorkProfile && it.isLaunchable == true }
                    }

                    val added = list.filter { it.packageName in workspace.appIds }

                    // Use the base list, and add the new ones (present in added list) and filter them,
                    // to remove the removed packages from the workspace
                    (base + added)
                        .distinctBy { it.packageName }
                        .filter { it.packageName !in removed }
                        .sortedBy { it.name.lowercase() }
                        .map { resolveApp(it, overrides) }
                }
            }
        }.stateIn(
            scope,
            SharingStarted.Eagerly,
            emptyList()
        )
    }


    private suspend fun loadApps() {
        val cachedJson = AppsSettingsStore.cachedApps.get(ctx)

        if (!cachedJson.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<AppModel>>() {}.type
                _apps.value = gson.fromJson(cachedJson, type) ?: emptyList()
            } catch (e: Exception) {
                logE(TAG, "Failed to parse cached apps, clearing: ${e.message}")
                AppsSettingsStore.cachedApps.reset(ctx) // Clear bad cache
                _apps.value = emptyList()
            }
        }

        scope.launch { reloadApps() }
    }


    /**
     * Reloads apps fresh from PackageManager.
     * Saves updated list into DataStore.
     * This is used by the BroadcastReceiver.
     */
    suspend fun reloadApps() {
        try {

            val apps = withContext(Dispatchers.IO) {
                pmCompat.getAllApps()
            }

            _apps.value = apps.toList()
            _icons.value = loadIcons(apps)

            invalidateAllPointIcons()

            val points = SwipeSettingsStore.getPoints(ctx)

            preloadPointIcons(
                points = points,
                sizePx = 128,
                reloadAll = true,
            )


            withContext(Dispatchers.IO) {
                AppsSettingsStore.cachedApps.set(ctx, gson.toJson(apps))
            }

            logE(
                APPS_TAG,
                "Reloaded packages, ${apps.filter { it.isLaunchable == true }.size} total apps, (${apps.size} user apps)"
            )

        } catch (e: Exception) {
            logE(APPS_TAG, e.toString())
        }
    }


    /**
     * Renders a [CustomIconSerializable] from a given orig [ImageBitmap]
     * @param orig the base [ImageBitmap] that will be edited
     * @param customIcon the custom icon to render with
     * @param sizePx size of the output [ImageBitmap]
     *
     * @return [ImageBitmap] the rendered icon after customIcon process
     */
    private fun renderCustomIcon(
        orig: ImageBitmap,
        customIcon: CustomIconSerializable,
        sizePx: Int
    ): ImageBitmap {

        val base: ImageBitmap =
            customIcon.takeIf { it.type == IconType.ICON_PACK }
                ?.source
                ?.takeIf { ',' in it }
                ?.let { source ->
                    val (drawable, packName) = source.split(',', limit = 2)

                    logD(ICONS_TAG, "$drawable $packName")

                    // If source is a specified icon from icon pack, use it, else, load the action icon
                    loadIconFromPack(packName, drawable)
                        ?.let {
                            logD(ICONS_TAG, "$it")

                            loadDrawableAsBitmap(it, sizePx, sizePx, packTint.value)
                        }
                }
                ?: orig

        return resolveCustomIconBitmap(
            base = base,
            icon = customIcon,
            sizePx = sizePx
        )
    }

    private fun invalidateAllPointIcons() {
        _pointIcons.value = emptyMap()
    }







    /*  ────── THE MOST IMPORTANT FUNCTIONS BELOW, LOAD ALL ICONS ──────  */

    /**
     * Load point icon
     *
     * @param point a [SwipePointSerializable] Object, that can contain a custom icon to render
     * @param sizePx size of the output [ImageBitmap]
     * @return [ImageBitmap]
     */
    fun loadPointIcon(
        point: SwipePointSerializable,
        sizePx: Int
    ): ImageBitmap {

        // Create the default bitmap, uses the app icons for default value if action is an app
        val orig = createUntintedBitmap(
            action = point.action,
            ctx = ctx,
            icons = _icons.value,
            width = sizePx,
            height = sizePx
        )

        // Returns either the icon rendered using the custom icon renderer, or the base icon if no render provided
        val rendered = point.customIcon?.let { customIcon ->

            logD(ICONS_TAG, point.toString())
            renderCustomIcon(
                orig = orig,
                customIcon = customIcon,
                sizePx = sizePx
            )
        } ?: orig


        return rendered
    }


    // DO a single function to load icons instead of 2 separated and shitty
    // No, in fact they are working and well now, no need to change

    private fun loadSingleIcon(
        packageName: String,
        userId: Int?,
        useOverrides: Boolean
    ): Pair<String, ImageBitmap> {

        var isIconPack = false
        val packIconName = getCachedIconMapping(packageName)
        val drawable =
            packIconName?.let {
                isIconPack = true
                loadIconFromPack(
                    selectedIconPack.value?.packageName,
                    it
                )
            } ?: pmCompat.getAppIcon(packageName, userId ?: 0)


        val orig = loadDrawableAsBitmap(
            drawable, 128, 128, _packTint.value.takeIf { isIconPack }
        )

        if (useOverrides) {
            _workspacesState.value.appOverrides[packageName]?.customIcon?.let { customIcon ->

                return packageName to renderCustomIcon(
                    orig = orig,
                    customIcon = customIcon,
                    sizePx = 128
                )
            }
        }

        return packageName to orig
    }


     /* ──────────────────────────  */

    fun preloadPointIcons(
        points: List<SwipePointSerializable>,
        sizePx: Int,
        reloadAll: Boolean = false
    ) {
        scope.launch(Dispatchers.Default) {
            val newIcons = buildMap {
                points.forEach { p ->
                    val id = p.id
                    if (_pointIcons.value.containsKey(id) && !reloadAll) return@forEach

                    put(
                        id,
                        loadPointIcon(
                            point = p,
                            sizePx = sizePx
                        )
                    )
                }
            }

            if (newIcons.isNotEmpty()) {
                _pointIcons.update { it + newIcons }
            }
        }
    }


    fun reloadPointIcon(
        point: SwipePointSerializable,
        sizePx: Int
    ) {
        val id = point.id

        scope.launch(Dispatchers.IO) {
            val bmp = loadPointIcon(
                point = point,
                sizePx = sizePx
            )

            _pointIcons.update { it + (id to bmp) }
        }
    }


    private suspend fun loadIcons(
        apps: List<AppModel>
    ): Map<String, ImageBitmap> =
        withContext(Dispatchers.IO) {
            apps.mapNotNull { app ->
                runCatching {
                    iconSemaphore.withPermit {
                        loadSingleIcon(app.packageName, app.userId, true)
                    }
                }.getOrNull()
            }.toMap()
        }


    fun updateSingleIcon(
        app: AppModel,
        useOverride: Boolean
    ) {
        _icons.update { it + loadSingleIcon(app.packageName, app.userId, useOverride) }
    }




    @SuppressLint("DiscouragedApi")
    fun loadIconFromPack(packPkg: String?, iconName: String): Drawable? {
        if (packPkg == null || iconName.isEmpty()) return null

        return try {
            logI(ICONS_TAG, "packPkg: $packPkg; iconName: $iconName")
            val packResources = ctx.packageManager.getResourcesForApplication(packPkg)
            val resId = packResources.getIdentifier(iconName, "drawable", packPkg)
            if (resId != 0) {
                ResourcesCompat.getDrawable(packResources, resId, null)
            } else null
        } catch (_: Exception) {
            logE(ICONS_TAG, "Failed to load icon $iconName from $packPkg")
            null
        }
    }

    fun loadAllIconsFromPack(pack: IconPackInfo) {

        scope.launch(Dispatchers.IO) {
            val cache = iconPackCache.getOrPut(pack.packageName) {
                loadIconPackMappings(pack.packageName)
            }

            if (cache.pkgToDrawables.isEmpty()) {
                _packIcons.value = emptyList()
                return@launch
            }

            _packIcons.value = cache.pkgToDrawables.values.flatten().distinct()
        }
    }


    private fun getCachedIconMapping(pkgName: String): String? {
        return selectedIconPack.value?.let { pack ->
            val cache = iconPackCache.getOrPut(pack.packageName) {
                loadIconPackMappings(pack.packageName)
            }

            // 1) Exact package match
            cache.pkgToDrawables[pkgName]?.let { list ->
                if (list.size == 1) return list[0]

                // Try to prefer drawable matching launch activity or package hint
                try {
                    val launchIntent = pm.getLaunchIntentForPackage(pkgName)
                    val compClass = launchIntent?.component?.className
                    if (!compClass.isNullOrEmpty()) {
                        cache.componentToDrawable.entries.forEach { (comp, drawable) ->
                            // comp is normalized as "package/class" or just "package"
                            if (comp.startsWith("$pkgName/") && comp.endsWith(compClass)) {
                                return drawable
                            }
                        }
                    }
                } catch (_: Exception) {
                }

                    val lastSeg = pkgName.substringAfterLast('.')
                list.find { it.equals(lastSeg, ignoreCase = true) }?.let { return it }
                list.find { it.contains(lastSeg, ignoreCase = true) }?.let { return it }

                // Try matching against app label (helps with packages like Play Store which may be referenced differently in packs)
                try {
                    val label = pm.getApplicationLabel(pm.getApplicationInfo(pkgName, 0)).toString().replace("\\s+".toRegex(), "").lowercase()
                    list.find { it.equals(label, ignoreCase = true) }?.let { return it }
                    list.find { it.contains(label, ignoreCase = true) }?.let { return it }
                } catch (_: Exception) { }

                list.find { !it.contains("_x") && !it.endsWith("x", true) }?.let { return it }

                return list.first()
            }

            // 2) Try component-level fallback (some icon packs specify full component only)
            cache.componentToDrawable.entries.find { (comp, _) ->
                val norm = comp
                val p = if (norm.contains('/')) norm.substringBefore('/') else norm
                p == pkgName
            }?.value
        }
    }

//    fun loadSavedIconPack() {
//        scope.launch {
//            val savedPackName = UiSettingsStore.selectedIconPack.get(ctx)
//            savedPackName?.let { pkg ->
//                _iconPacksList.value.find { it.packageName == pkg }?.let { pack ->
//                    _selectedIconPack.value = pack
//                }
//            }
//        }
//    }

    fun selectIconPack(pack: IconPackInfo) {
        _selectedIconPack.value = pack
        scope.launch(Dispatchers.IO) {
            UiSettingsStore.selectedIconPack.set(ctx, pack.packageName)
            iconPackCache[pack.packageName] = loadIconPackMappings(pack.packageName)
            reloadApps()
        }
    }


    fun clearIconPack() {
        _selectedIconPack.value = null
        scope.launch(Dispatchers.IO) {
            UiSettingsStore.selectedIconPack.reset(ctx)
            reloadApps()
        }
    }


    fun loadIconPacks() {
        val packs = mutableListOf<IconPackInfo>()
        val allPackages = pmCompat.getInstalledPackages()


       allPackages.forEach { pkgInfo ->
            if (pkgInfo.packageName == ctx.packageName) return@forEach

            try {
                val packResources = pmCompat.getResourcesForApplication(pkgInfo.packageName)
                val hasAppfilter = hasStandardAppFilter(packResources, pkgInfo.packageName)
                val isIps = isIconPackStudioPack(pkgInfo)

                if (hasAppfilter || isIps) {
                    val name = pkgInfo.applicationInfo?.loadLabel(pm).toString()
                    logD(ICONS_TAG, "FOUND icon pack: $name (${pkgInfo.packageName}); is standard: $isIps")

                    packs.add(
                        IconPackInfo(
                            packageName = pkgInfo.packageName,
                            name = name,
                            isManualOnly = isIps
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val uniquePacks = packs.distinctBy { it.packageName }
        logD(ICONS_TAG, "Total icon packs found: ${uniquePacks.size}")
        _iconPacksList.value = uniquePacks}

//    fun loadIconsPacks() {
//        val packs = mutableListOf<IconPackInfo>()
//        val allPackages = pmCompat.getInstalledPackages()
//
//        logD(ICONS_TAG, "Scanning ${allPackages.size} packages...")
//
//        allPackages.forEach { pkgInfo ->
//            try {
//                val packResources = pmCompat.getResourcesForApplication(pkgInfo.packageName)
//                var hasAppfilter = hasAppfilterResource(packResources, pkgInfo.packageName)
//
//                if (pkgInfo.packageName.contains("exported")) {
//                    hasAppfilter = true
//                }
//
//                if (hasAppfilter && pkgInfo.packageName != ctx.packageName) {
//                    val label = pkgInfo.applicationInfo?.loadLabel(pm).toString()
//                    logD(ICONS_TAG, "FOUND icon pack: $label (${pkgInfo.packageName})")
//                    packs.add(IconPackInfo(pkgInfo.packageName, label))
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//        val uniquePacks = packs.distinctBy { it.packageName }
//        logD(ICONS_TAG, "Total icon packs found: ${uniquePacks.size}")
//        _iconPacksList.value = uniquePacks
//    }


//    @SuppressLint("DiscouragedApi")
//    private fun hasAppfilterResource(
//        resources: Resources,
//        pkgName: String
//    ): Boolean {
//        val locations = listOf("appfilter", "theme_appfilter", "icon_appfilter")
//        return locations.any { name ->
//            val cacheKey = "$pkgName:$name"
//            val resId = resourceIdCache.getOrPut(cacheKey) {
//                resources.getIdentifier(name, "xml", pkgName)
//            }
//            resId != 0
//        }
//    }

    fun loadIconPackMappings(packPkg: String): IconPackCache {
        return try {
            val entries = parseAppFilterXml(ctx, packPkg) ?: emptyList()

            val componentToDrawable = mutableMapOf<String, String>()
            val pkgToDrawables = mutableMapOf<String, MutableList<String>>()

            entries.forEach { mapping ->
                var comp = mapping.component
                if (comp.contains('{')) comp = comp.substringAfter('{')
                if (comp.contains('}')) comp = comp.substringBefore('}')
                comp = comp.trim()

                val pkg = if (comp.contains('/')) comp.substringBefore('/') else comp

                componentToDrawable[comp] = mapping.drawable

                val list = pkgToDrawables.getOrPut(pkg) { mutableListOf() }
                if (!list.contains(mapping.drawable)) list.add(mapping.drawable)
            }

            IconPackCache(
                pkgToDrawables = pkgToDrawables,
                componentToDrawable = componentToDrawable
            )
        } catch (e: Exception) {
            logE(ICONS_TAG, "Failed to load mappings for $packPkg: ${e.message}")
            IconPackCache(emptyMap(), emptyMap())
        }
    }

    fun loadIpsIcons(packPkg: String): List<String> {
        return try {
            val clazz = Class.forName($$"$$packPkg.R$drawable")
            clazz.fields
                .filter { it.type == Int::class.javaPrimitiveType }
                .map { it.name }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }



    /** Load the user's workspaces into the _state var, enforced safety due to some crash at start */
    private suspend fun loadWorkspaces()  {
        try {
            val json = WorkspaceSettingsStore.getAll(ctx).toString()

            // Correct generic type: WorkspaceState with List<Workspace>
            val type = object : TypeToken<WorkspaceState>() {}.type
            val loadedState: WorkspaceState? = gson.fromJson(json, type)

            _workspacesState.value = loadedState?.copy(
                workspaces = loadedState.workspaces,
                appOverrides = loadedState.appOverrides,
                appAliases = loadedState.appAliases
            ) ?: WorkspaceState()
        } catch (e: Exception) {
            e.printStackTrace()
            _workspacesState.value = WorkspaceState()
        }

        // Load the appOverrides in the pointsIcons too
        _workspacesState.value.appOverrides.forEach { (packageName, override) ->
            override.customIcon?.let { customIcon ->
                reloadPointIcon(
                    point = dummySwipePoint(SwipeActionSerializable.LaunchApp(packageName, 0)).copy(
                        customIcon = customIcon,
                        id = packageName
                    ),
                    128 // Dummy value, will be loaded later with the good one
                )
            }
        }
    }


    private fun persist() = scope.launch(Dispatchers.IO) {
        WorkspaceSettingsStore.setAll(
            ctx,
            JSONObject(gson.toJson(_workspacesState.value))
        )
    }

    fun selectWorkspace(id: String) {
        _selectedWorkspaceId.value = id
    }

    /* ───────────── Recently Used Apps ───────────── */

    private suspend fun loadRecentlyUsedApps() {
        val json = DrawerSettingsStore.recentlyUsedPackages.get(ctx)
        if (!json.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<String>>() {}.type
                _recentlyUsedPackages.value = gson.fromJson(json, type) ?: emptyList()
            } catch (_: Exception) {
                _recentlyUsedPackages.value = emptyList()
            }
        }
    }

    /**
     * Record a package as recently used.
     * Moves it to the front if already present, trims the list to a reasonable max.
     */
    fun addRecentlyUsedApp(packageName: String) {
        val maxStored = 30 // store more than display, user can raise the count later
        val current = _recentlyUsedPackages.value.toMutableList()
        current.remove(packageName)
        current.add(0, packageName)
        val trimmed = current.take(maxStored)
        _recentlyUsedPackages.value = trimmed
        scope.launch {
            DrawerSettingsStore.recentlyUsedPackages.set(ctx, gson.toJson(trimmed))
        }
    }

    /**
     * Returns the recently used [AppModel]s, resolved from the current app list.
     * Uses combine to reactively update when either apps or recent packages change.
     * @param count max number of recent apps to return
     */
    fun getRecentApps(count: Int): StateFlow<List<AppModel>> {
        return _recentlyUsedPackages.combine(_apps) { packages, apps ->
            val allApps = apps.associateBy { it.packageName }
            packages
                .take(count)
                .mapNotNull { pkg -> allApps[pkg] }
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())
    }


    /** Enable/disable a workspace */
    fun setWorkspaceEnabled(id: String, enabled: Boolean) {
        _workspacesState.value = _workspacesState.value.copy(
            workspaces = _workspacesState.value.workspaces.map { workspace ->
                if (workspace.id == id) {
                    workspace.copy(enabled = enabled)
                } else {
                    workspace
                }
            }
        )
        persist()
    }

    fun createWorkspace(name: String, type: WorkspaceType) {
        _workspacesState.value = _workspacesState.value.copy(
            workspaces = _workspacesState.value.workspaces +
                    Workspace(
                        id = System.currentTimeMillis().toString(),
                        name = name,
                        type = type,
                        enabled = true,
                        removedAppIds = emptyList(),
                        appIds = emptyList()
                    )
        )
        persist()
    }

    fun editWorkspace(id: String, name: String, type: WorkspaceType) {
        _workspacesState.value = _workspacesState.value.copy(
            workspaces = _workspacesState.value.workspaces.map {
                if (it.id == id) it.copy(name = name, type = type) else it
            }
        )
        persist()
    }

    fun deleteWorkspace(id: String) {
        _workspacesState.value = _workspacesState.value.copy(
            workspaces = _workspacesState.value.workspaces.filterNot { it.id == id }
        )
        persist()
    }

    fun setWorkspaceOrder(newOrder: List<Workspace>) {
        _workspacesState.value = _workspacesState.value.copy(workspaces = newOrder)
        persist()
    }


    fun resetWorkspace(id: String) {
        _workspacesState.value = _workspacesState.value.copy(
            workspaces = _workspacesState.value.workspaces.map {
                if (it.id == id) it.copy(removedAppIds = emptyList(), appIds = emptyList()) else it
            }
        )
        persist()
    }


    // Apps operations
    fun addAppToWorkspace(workspaceId: String, packageName: String) {
        _workspacesState.value = _workspacesState.value.copy(
            workspaces = _workspacesState.value.workspaces.map { ws ->
                if (ws.id != workspaceId) return@map ws

                val removed = ws.removedAppIds ?: emptySet()

                ws.copy(
                    appIds = ws.appIds + packageName,
                    removedAppIds = if (packageName in removed)
                        removed - packageName
                    else
                        ws.removedAppIds
                )
            }
        )
        persist()
    }


    fun removeAppFromWorkspace(workspaceId: String, packageName: String) {
        _workspacesState.value = _workspacesState.value.copy(
            workspaces = _workspacesState.value.workspaces.map { ws ->
                if (ws.id != workspaceId) return@map ws

                // remove the app packageName from appsIds, and add it to removedAppIDs
                ws.copy(
                    appIds = ws.appIds - packageName,
                    removedAppIds = (ws.removedAppIds ?: emptyList()) + packageName
                )
            }
        )
        persist()
    }

    fun addAliasToApp(alias: String, packageName: String) {
        _workspacesState.value = _workspacesState.value.copy(
            appAliases = _workspacesState.value.appAliases +
                    (packageName to (_workspacesState.value.appAliases[packageName] ?: emptySet()) + alias)
        )
        persist()
    }

//    fun resetAliasesForApp(packageName: String) {
//        _workspacesState.value = _workspacesState.value.copy(
//            appAliases = _workspacesState.value.appAliases.filter { it.key != packageName }
//        )
//        persist()
//    }

    fun removeAliasFromWorkspace(aliasToRemove: String, packageName: String) {

        val current = _workspacesState.value.appAliases

        val updated = current[packageName]
            ?.minus(aliasToRemove)
            ?.takeIf { it.isNotEmpty() }

        _workspacesState.value = _workspacesState.value.copy(
            appAliases = if (updated == null)
                current - packageName
            else
                current + (packageName to updated)
        )
        persist()
    }

    fun renameApp(packageName: String, name: String) {
        _workspacesState.value = _workspacesState.value.copy(
            appOverrides = _workspacesState.value.appOverrides +
                    (packageName to AppOverride(packageName, name))
        )
        persist()
    }

    fun setAppIcon(packageName: String, customIcon: CustomIconSerializable?) {
        val prev = _workspacesState.value.appOverrides[packageName]
        _workspacesState.value = _workspacesState.value.copy(
            appOverrides = _workspacesState.value.appOverrides +
                    (packageName to (prev?.copy(customIcon = customIcon)
                        ?: AppOverride(packageName, customIcon = customIcon)))
        )
        persist()
    }

    fun applyIconToApps(
        icon: CustomIconSerializable?
    ) {
        scope.launch {
            iconSemaphore.withPermit {

                // Store icon ONCE
                val sharedIcon = icon?.copy()

                _workspacesState.value = _workspacesState.value.copy(
                    appOverrides = _apps.value.associate {
                        (it.packageName to AppOverride(it.packageName, customIcon = sharedIcon))
                    }
                )
            }
        }
        persist()
    }


    fun resetAppName(packageName: String) {
        val prev = _workspacesState.value.appOverrides[packageName] ?: return

        val updated = prev.copy(customLabel = null)

        _workspacesState.value = _workspacesState.value.copy(
            appOverrides =
                if (updated.customIcon == null)
                    _workspacesState.value.appOverrides - packageName
                else
                    _workspacesState.value.appOverrides + (packageName to updated)
        )
        scope.launch {
            reloadApps()
        }
        persist()
    }

    fun resetAppIcon(packageName: String) {
        val prev = _workspacesState.value.appOverrides[packageName] ?: return

        val updated = prev.copy(customIcon = null)

        _workspacesState.value = _workspacesState.value.copy(
            appOverrides =
                if (updated.customLabel == null)
                    _workspacesState.value.appOverrides - packageName
                else
                    _workspacesState.value.appOverrides + (packageName to updated)
        )

        scope.launch {
            reloadApps()
        }
        persist()
    }


    fun resetWorkspacesAndOverrides() {
        _workspacesState.value = WorkspaceState(
            workspaces = defaultWorkspaces,
            appOverrides = emptyMap()
        )
        persist()
    }

    suspend fun loadDefaultPoint() {
        _defaultPoint.value = SwipeSettingsStore.getDefaultPoint(ctx)
    }

    suspend fun setDefaultPoint(point: SwipePointSerializable) {
        _defaultPoint.value = point
        SwipeSettingsStore.setDefaultPoint(ctx, point)
    }

    suspend fun setIconPackTint(tint: Color?) {
        UiSettingsStore.iconPackTint.set(ctx, tint)
        _packTint.value = tint?.toArgb()
        reloadApps()
    }
}


private fun isIconPackStudioPack(pkg: PackageInfo): Boolean {
    return pkg.packageName == "ginlemon.iconpackstudio.exported"
}

@SuppressLint("DiscouragedApi")
private fun hasStandardAppFilter(res: Resources, pkg: String): Boolean {
    return res.getIdentifier("appfilter", "xml", pkg) != 0
}


@SuppressLint("DiscouragedApi")
private fun parseAppFilterXml(ctx: Context, packPkg: String): List<IconMapping>? {
    return try {
        val packResources = ctx.packageManager.getResourcesForApplication(packPkg)
        val resId = packResources.getIdentifier("appfilter", "xml", packPkg)
        if (resId == 0) return null

        val parser: XmlResourceParser = packResources.getXml(resId)
        val mappings = mutableListOf<IconMapping>()

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "item") {
                val component = parser.getAttributeValue(null, "component")
                val drawable = parser.getAttributeValue(null, "drawable")
                if (!component.isNullOrEmpty() && !drawable.isNullOrEmpty()) {
                    mappings.add(IconMapping(component, drawable))
                }
            }
            eventType = parser.next()
        }
        parser.close()
        mappings
    } catch (e: Exception) {
        ctx.logE(ICONS_TAG, "XML parse failed: ${e.message}")
        null
    }
}

/**
 * Icon pack cache with normalized component mapping and package -> drawables list
 */
data class IconPackCache(
    val pkgToDrawables: Map<String, List<String>>,
    val componentToDrawable: Map<String, String>
)

