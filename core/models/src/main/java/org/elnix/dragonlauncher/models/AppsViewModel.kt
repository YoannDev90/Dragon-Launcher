package org.elnix.dragonlauncher.models

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Xml
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.logging.logI
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.AppOverride
import org.elnix.dragonlauncher.common.serializables.CacheKey
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
import org.elnix.dragonlauncher.common.utils.Constants.Logging.APPS_TAG
import org.elnix.dragonlauncher.common.utils.Constants.Logging.APP_LAUNCH_TAG
import org.elnix.dragonlauncher.common.utils.Constants.Logging.ICONS_TAG
import org.elnix.dragonlauncher.common.utils.Constants.Logging.TAG
import org.elnix.dragonlauncher.common.utils.ImageUtils.createUntintedBitmap
import org.elnix.dragonlauncher.common.utils.ImageUtils.loadDrawableAsBitmap
import org.elnix.dragonlauncher.common.utils.ImageUtils.resolveCustomIconBitmap
import org.elnix.dragonlauncher.common.utils.PackageManagerCompat
import org.elnix.dragonlauncher.common.utils.PrivateSpaceUtils
import org.elnix.dragonlauncher.enumsui.PrivateSpaceLoadingState
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


    /**
     * The list of icons available in the selected pack
     */
    private val _packIcons = MutableStateFlow<List<String>>(emptyList())
    val packIcons: StateFlow<List<String>> = _packIcons.asStateFlow()

    private val _packTint = MutableStateFlow<Int?>(null)
    val packTint = _packTint.asStateFlow()

    /**
     * All the icons that are drawn around the app, changed from 2 separate lists to only one.
     *
     * For the app icons, in the drawer for example, the icons are stored using the cache key [AppModel.iconCacheKey]
     * For the points icons, in the circles and other places around the app, ths icons are linked using the point id
     *
     * When an icon that opens an app has no overrides, it tries to pick the icon that corresponds to its app
     */
    private val _icons = MutableStateFlow<Map<String, ImageBitmap>>(emptyMap())
    val icons = _icons.asStateFlow()


    private val _defaultPoint = MutableStateFlow(defaultSwipePointsValues)
    val defaultPoint = _defaultPoint.asStateFlow()

    // Only used for preview, the real user apps getter are using the appsForWorkspace function
    val userApps: StateFlow<List<AppModel>> = _apps.map { list ->
        list.filter { it.isLaunchable == true && !it.isWorkProfile && !it.isSystem }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())


    private val _selectedIconPack = MutableStateFlow<IconPackInfo?>(null)
    val selectedIconPack: StateFlow<IconPackInfo?> = _selectedIconPack.asStateFlow()

    private val iconPackCache = mutableMapOf<String, IconPackCache>()


    @SuppressLint("StaticFieldLeak")
    private val ctx = application.applicationContext

    private val pm: PackageManager = application.packageManager
    private val pmCompat = PackageManagerCompat(pm, ctx)

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


    /**
     * Loads everything the AppViewModel needs
     * Runs at start and when the user restore from a backup
     */
    suspend fun loadAll() {
        loadWorkspaces()
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
     *
     * TODO review this to let user customize as they want, just the private space should have special rules
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
                        WorkspaceType.USER -> list.filter { !it.isWorkProfile && !it.isPrivateProfile && it.isLaunchable == true }
                        WorkspaceType.SYSTEM -> list.filter { it.isSystem }
                        WorkspaceType.WORK -> list.filter { it.isWorkProfile && it.isLaunchable == true }
                        WorkspaceType.PRIVATE -> {
                            val privateApps =
                                list.filter { it.isPrivateProfile && it.isLaunchable == true }
                            logI(
                                APPS_TAG,
                                "Private workspace filter: ${privateApps.size} apps from ${list.size} total (${list.count { it.isPrivateProfile }} private in total)"
                            )
                            if (privateApps.isNotEmpty()) {
                                logI(
                                    APPS_TAG,
                                    "Private apps: ${privateApps.joinToString(", ") { it.name }}"
                                )
                            }
                            privateApps
                        }
                    }

                    val added = list.filter { it.packageName in workspace.appIds }

                    // For special workspaces (USER, WORK, PRIVATE), make sure manually-added apps
                    // don't violate the workspace's profile constraints
                    val filtered = when (workspace.type) {
                        WorkspaceType.USER -> {
                            // Exclude manually-added apps that are Work or Private profile apps
                            val userAdded =
                                added.filter { !it.isWorkProfile && !it.isPrivateProfile }
                            if (added.size != userAdded.size) {
                                logI(
                                    APPS_TAG,
                                    "USER workspace: filtering out ${added.size - userAdded.size} non-user apps from manually-added"
                                )
                                added.filter { it.isWorkProfile || it.isPrivateProfile }.forEach {
                                    logI(
                                        APPS_TAG,
                                        "  Excluded: ${it.name} (work=${it.isWorkProfile}, private=${it.isPrivateProfile})"
                                    )
                                }
                            }
                            userAdded
                        }

                        WorkspaceType.WORK -> {
                            // Exclude manually-added apps that are not Work profile apps
                            added.filter { it.isWorkProfile }
                        }

                        WorkspaceType.PRIVATE -> {
                            // Exclude manually-added apps that are not Private profile apps
                            val privateAdded = added.filter { it.isPrivateProfile }
                            if (added.size != privateAdded.size) {
                                logI(
                                    APPS_TAG,
                                    "PRIVATE workspace: added apps = ${added.size}, actual private = ${privateAdded.size}"
                                )
                                added.forEach {
                                    logI(
                                        APPS_TAG,
                                        "  - ${it.name}: isPrivate=${it.isPrivateProfile}, userId=${it.userId}"
                                    )
                                }
                            }
                            privateAdded
                        }

                        else -> added  // For ALL and CUSTOM, allow any manually-added apps
                    }

                    // Use the base list, and add the filtered manually-added apps, then remove explicitly removed ones
                    (base + filtered)
                        .distinctBy { "${it.packageName}_${it.userId}" }
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

        // try to het from the json cache before querying the package manager
        // TODO this thing doesn't seem to work properly
        if (!(cachedJson.isNullOrEmpty() || cachedJson == "{}")) {
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
    // Snapshot / differential fields for Private Space detection
    private var privateSnapshotBefore: Set<String>? = null
    private var pendingPrivateAssignments: Map<String, Int?>? = null


    /**
     * _private space state, describe the current state of the private space
     * reflected in UI in the AppDrawerScreen and PrivateSpaceUnlockScreen
     */
    private val _privateSpaceState = MutableStateFlow(
        PrivateSpaceLoadingState(
            isLocked = true,
            isLoading = false,
            isAuthenticating = false
        )
    )
    val privateSpaceState = _privateSpaceState.asStateFlow()


//    fun setPrivateSpaceAvailable() {
//        logW(APP_LAUNCH_TAG, "setPrivateSpaceAvailable() was called!")
//        _privateSpaceState.value = PrivateSpaceLoadingState.Available
//    }

    fun setPrivateSpaceLocked() {
        _privateSpaceState.update {
            it.copy(
                isLocked = true,
                isLoading = false,
                isAuthenticating = false
            )
        }
    }

    // Debounce / coalesce reloads
    private var scheduledReloadJob: Job? = null
    private val reloadMutex = Mutex()


//    private fun scheduleReload(delayMs: Long = 0L) {
//        scheduledReloadJob?.cancel()
//        scheduledReloadJob = scope.launch {
//            if (delayMs > 0) delay(delayMs)
//            reloadApps()
//        }
//    }

    suspend fun reloadApps() {
        try {
            logD(APPS_TAG, "========== Starting reloadApps() ==========")

            val apps = withContext(Dispatchers.IO) {
                pmCompat.getAllApps()
            }

            // Apply differential private-package marking if present
            var finalApps = apps
            if (!pendingPrivateAssignments.isNullOrEmpty()) {
                val assignments = pendingPrivateAssignments ?: emptyMap()
                logI(
                    APPS_TAG,
                    "Applying differential Private Space detection: ${assignments.size} app identities"
                )

                // Persist assignments
                try {
                    val existingJson = AppsSettingsStore.privateAssignedPackages.get(ctx)
                    val existingMap: MutableMap<String, Int?> =
                        if (existingJson.isNullOrEmpty() || existingJson == "{}") mutableMapOf()
                        else gson.fromJson(
                            existingJson,
                            object : TypeToken<MutableMap<String, Int?>>() {}.type
                        )

                    assignments.forEach { (identity, userId) ->
                        existingMap[identity] = userId
                    }

                    AppsSettingsStore.privateAssignedPackages.set(ctx, gson.toJson(existingMap))
                    logI(APPS_TAG, "Persisted ${assignments.size} private app assignments")
                } catch (e: Exception) {
                    logE(APPS_TAG, "Error persisting private package assignments: ${e.message}", e)
                }

                finalApps = apps.map { app ->
                    val identity = app.iconCacheKey
                    val cacheKeyString = identity.cacheKey

                    val assignedUserId = assignments[cacheKeyString]
                    if (assignedUserId != null || assignments.containsKey(cacheKeyString)) {
                        logI(
                            APPS_TAG,
                            "Marking ${app.packageName} as Private Space (diff), assigning userId=${assignedUserId ?: app.userId}"
                        )
                        app.copy(
                            isPrivateProfile = true,
                            isWorkProfile = false,
                            userId = assignedUserId ?: app.userId
                        )
                    } else app
                }
                // Clear pending after consumption
                pendingPrivateAssignments = null
            }

            // Apply persisted private assignments (survives reloads)
            try {
                val persistedJson = AppsSettingsStore.privateAssignedPackages.get(ctx)
                if (!persistedJson.isNullOrEmpty() && persistedJson != "{}") {
                    val persistedMap: Map<String, Int?> = gson.fromJson(
                        persistedJson,
                        object : TypeToken<Map<String, Int?>>() {}.type
                    )
                    if (persistedMap.isNotEmpty()) {
                        logI(
                            APPS_TAG,
                            "Applying persisted private assignments: ${persistedMap.size} entries"
                        )
                        finalApps = finalApps.map { app ->
                            val identity = app.iconCacheKey
                            val cacheKeyString = identity.cacheKey

                            val identityAssigned = persistedMap[cacheKeyString]

                            if (identityAssigned != null || persistedMap.containsKey(cacheKeyString)) {
                                app.copy(
                                    isPrivateProfile = true,
                                    isWorkProfile = false,
                                    userId = identityAssigned ?: app.userId
                                )
                            } else {
                                // Backward-compat for legacy persisted format (packageName -> userId)
                                val legacyAssigned = persistedMap[app.packageName]
                                if (legacyAssigned != null && app.userId == legacyAssigned) {
                                    app.copy(
                                        isPrivateProfile = true,
                                        isWorkProfile = false,
                                        userId = legacyAssigned
                                    )
                                } else {
                                    app
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logE(APPS_TAG, "Error applying persisted private assignments: ${e.message}", e)
            }

            logD(APPS_TAG, "Total apps loaded: ${finalApps.size}")
            logD(APPS_TAG, "Private apps: ${finalApps.count { it.isPrivateProfile }}")
            logD(APPS_TAG, "Work apps: ${finalApps.count { it.isWorkProfile }}")
            logD(
                APPS_TAG,
                "User apps: ${finalApps.count { !it.isWorkProfile && !it.isPrivateProfile }}"
            )

            if (finalApps.count { it.isPrivateProfile } > 0) {
                logD(APPS_TAG, "Private apps list:")
                finalApps.filter { it.isPrivateProfile }.forEach {
                    logD(APPS_TAG, "  - ${it.name} (${it.packageName}, userId=${it.userId})")
                }
            }

            // Sort and create new list to ensure StateFlow emission
            _apps.value = finalApps.sortedBy { it.name.lowercase() }.toList()
            loadAppIcons(finalApps, 128)

            val points = SwipeSettingsStore.getPoints(ctx)

            preloadPointIcons(
                points = points,
                sizePx = 128,
                reloadAll = true,
            )


            withContext(Dispatchers.IO) {
                AppsSettingsStore.cachedApps.set(ctx, gson.toJson(finalApps))
            }

            // Auto-enable Private Space workspace if Private Space exists (Android 15+)
            if (PrivateSpaceUtils.isPrivateSpaceSupported()) {
                val privateSpaceExists = PrivateSpaceUtils.getPrivateSpaceUserHandle(ctx) != null
                val privateWorkspace =
                    _workspacesState.value.workspaces.find { it.type == WorkspaceType.PRIVATE }

                logI(APPS_TAG, "Private Space exists: $privateSpaceExists")
                logI(
                    APPS_TAG,
                    "Private workspace found: ${privateWorkspace != null}, enabled: ${privateWorkspace?.enabled}"
                )

                if (privateSpaceExists && privateWorkspace != null && !privateWorkspace.enabled) {
                    logI(
                        APPS_TAG,
                        "Enabling Private Space workspace (Private Space profile detected)"
                    )
                    setWorkspaceEnabled("private", true)
                } else if (!privateSpaceExists && privateWorkspace != null && privateWorkspace.enabled) {
                    logI(
                        APPS_TAG,
                        "Disabling Private Space workspace (Private Space profile not found)"
                    )
                    setWorkspaceEnabled("private", false)

                    // Clear persisted private assignments since Private Space no longer exists
                    try {
                        AppsSettingsStore.privateAssignedPackages.set(ctx, "{}")
                        logI(
                            APPS_TAG,
                            "Cleared persisted Private Space assignments because Private Space not found"
                        )
                    } catch (e: Exception) {
                        logE(
                            APPS_TAG,
                            "Error clearing persisted private assignments: ${e.message}",
                            e
                        )
                    }
                }
            }

            logI(
                APPS_TAG,
                "Reloaded packages, ${apps.filter { it.isLaunchable == true }.size} launchable apps, ${apps.size} total apps"
            )
            logI(APPS_TAG, "========== Finished reloadApps() ==========")

        } catch (e: Exception) {
            logE(APPS_TAG, "Error in reloadApps: ${e.message}", e)
        }
    }

    /**
     * Differential Private Space detection helpers
     */
    private suspend fun captureMainProfileSnapshotBeforeUnlock() {
        try {
            logD(APPS_TAG, "Capturing visible app snapshot before Private Space unlock...")
            privateSnapshotBefore = withContext(Dispatchers.IO) {
                pmCompat.getAllApps()
                    .filter { it.isLaunchable == true }
                    .map { it.iconCacheKey.cacheKey }
                    .toSet()
            }
            logD(APPS_TAG, "Snapshot captured: ${privateSnapshotBefore?.size ?: 0} packages")
        } catch (e: Exception) {
            logE(APPS_TAG, "Error capturing main profile snapshot: ${e.message}", e)
            privateSnapshotBefore = null
        }
    }

    private suspend fun detectPrivateAppsDiffAndReload() {
        try {
            logD(APPS_TAG, "Detecting Private Space apps via differential snapshot...")
            val before = privateSnapshotBefore ?: emptySet()
            val afterApps = withContext(Dispatchers.IO) {
                pmCompat.getAllApps().filter { it.isLaunchable == true }
            }

            val after = afterApps.map { it.iconCacheKey.cacheKey }.toSet()
            val diffKeys = after.subtract(before)
            val diffApps = afterApps.filter { it.iconCacheKey.cacheKey in diffKeys }

            logI(
                APPS_TAG,
                "Differential detection: found ${diffApps.size} candidate private apps: ${
                    diffApps.joinToString(", ") { "${it.packageName}@${it.userId}" }
                }"
            )

            pendingPrivateAssignments =
                diffApps.associate { it.iconCacheKey.cacheKey to it.userId }

            // Remove any of these packages from USER workspaces (they belong to Private)
            try {
                val userWorkspaces =
                    _workspacesState.value.workspaces.filter { it.type == WorkspaceType.USER }
                diffApps.distinct().forEach { app ->
                    val cacheKey = app.iconCacheKey
                    val cacheKeyString = cacheKey.cacheKey

                    userWorkspaces.forEach { ws ->
                        if (cacheKeyString in ws.appIds) {
                            logI(
                                APPS_TAG,
                                "Removing $cacheKey from USER workspace (${ws.id}) because it's Private"
                            )
                            removeAppFromWorkspace(ws.id, cacheKey)
                        }
                    }
                }
            } catch (e: Exception) {
                logE(APPS_TAG, "Error removing packages from USER workspaces: ${e.message}")
            }

            // Clear the before snapshot
            privateSnapshotBefore = null

            // Start Private Space loading state and schedule a debounced reload
            _privateSpaceState.update {
                it.copy(
                    isLoading = true,
                )
            }

            // Schedule reload (debounced) and wait for it to complete
            scheduledReloadJob?.cancel()
            scheduledReloadJob = scope.launch {
                delay(300) // short debounce to coalesce multiple triggers
                reloadMutex.withLock {
                    reloadApps()
                }
            }
            scheduledReloadJob?.join()

        } catch (e: Exception) {
            logE(APPS_TAG, "Error during differential private detection: ${e.message}", e)
            pendingPrivateAssignments = null
            privateSnapshotBefore = null

            setPrivateSpaceLocked()

            // best-effort fallback: full reload
            try {
                reloadApps()
            } catch (_: Exception) { /* ignore */
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    suspend fun unlockPrivateSpace(): Boolean {

        val reallyLocked = withContext(Dispatchers.IO) {
            PrivateSpaceUtils.isPrivateSpaceLocked(ctx) ?: true
        }

        if (!reallyLocked) {
            _privateSpaceState.update {
                it.copy(isLocked = false, isAuthenticating = false)
            }
            return true
        }

        _privateSpaceState.update { it.copy(isAuthenticating = true) }


        // This only request the auth, it does not handle whether the private space was unlocked
        PrivateSpaceUtils.requestUnlockPrivateSpace(ctx)

        // Test with timeout the real unlock state
        val unlocked = withTimeoutOrNull(10_000L) {
            while (true) {
                val locked = withContext(Dispatchers.IO) {
                    PrivateSpaceUtils.isPrivateSpaceLocked(ctx) ?: true
                }
                if (!locked) break
                delay(200)
            }
            true // return true when unlocked
        } ?: false // if timeout


        _privateSpaceState.update {
            it.copy(
                isAuthenticating = false,
                isLocked = !unlocked
            )
        }

        return unlocked
    }


    suspend fun unlockAndReload() {

        if (!PrivateSpaceUtils.isPrivateSpaceSupported()) return

        // Suspends until unlock, timeout or user cancel
        val unlocked = unlockPrivateSpace()

        if (!unlocked) return

        // Reloads asynchronously the apps, for letting the unlock UI quit ASAP
        scope.launch {
            reloadPrivateSpace()
        }
    }


    suspend fun reloadPrivateSpace() {

        // Set loading state before load
        _privateSpaceState.update {
            it.copy(
                isLoading = true,
            )
        }
        captureMainProfileSnapshotBeforeUnlock()
        detectPrivateAppsDiffAndReload()
        logI(APP_LAUNCH_TAG, "Available after full private space reload")

        // Finished loading
        _privateSpaceState.update {
            it.copy(
                isLoading = false,
            )
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
            if (customIcon.type == IconType.ICON_PACK) {

                val source = customIcon.source

                if (!source.isNullOrBlank() && ',' in source) {

                    val (drawableName, packPkg) = source.split(',', limit = 2)

                    loadIconFromPack(
                        packPkg = packPkg,
                        iconName = drawableName,
                        targetPkg = "" // Manual selection
                    )?.let { drawable ->
                        loadDrawableAsBitmap(
                            drawable = drawable,
                            width = sizePx,
                            height = sizePx,
                            tint = packTint.value
                        )
                    } ?: orig

                } else orig

            } else orig

        return resolveCustomIconBitmap(
            base = base,
            icon = customIcon,
            sizePx = sizePx
        )
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
        app: AppModel,
        useOverrides: Boolean,
        sizePx: Int
    ): ImageBitmap {
        val packageName = app.packageName
        val userId = app.userId
        val isPrivateProfile = app.isPrivateProfile
        val cacheKeyString = app.iconCacheKey.cacheKey

        var isIconPack = false
        val packIconName = getCachedIconMapping(packageName)
        val selectedPack = selectedIconPack.value

        val drawable =
            if (selectedPack != null) {
                isIconPack = true

                packIconName?.let { packName ->
                    loadIconFromPack(
                        packPkg = selectedPack.packageName,
                        iconName = packName,
                        targetPkg = packageName
                    )
                }
            } else {
                null
            } ?: pmCompat.getAppIcon(packageName, userId ?: 0, isPrivateProfile)


        val orig = loadDrawableAsBitmap(
            drawable = drawable,
            width = sizePx,
            height = sizePx,
            tint = _packTint.value.takeIf { isIconPack }
        )

        if (useOverrides) {
            _workspacesState.value.appOverrides[cacheKeyString]?.customIcon?.let { customIcon ->
                return renderCustomIcon(
                    orig = orig,
                    customIcon = customIcon,
                    sizePx = sizePx
                )
            }
        }

        return orig
    }


    /* ──────────────────────────────────────────────────  */


    /* ───────────── Reload Functions ───────────── */

    /**
     * Reload a single point icon to the icons list, override if already existing
     *
     * @param point which point's icon to load
     * @param sizePx the size of the [ImageBitmap] loaded
     */
    fun reloadPointIcon(
        point: SwipePointSerializable,
        sizePx: Int = 128
    ) {
        val id = point.id

        scope.launch(Dispatchers.IO) {
            val bmp = loadPointIcon(
                point = point,
                sizePx = sizePx
            )

            _icons.update { it + (id to bmp) }
        }
    }

    /**
     * Update single icon (for app)
     * Basically the same thing as [reloadPointIcon] but for an AppModel instead of the [SwipePointSerializable] you input an [AppModel]
     *
     *
     * @param app
     * @param useOverride
     */
    fun reloadAppIcon(
        app: AppModel,
        useOverride: Boolean,
        sizePx: Int = 128
    ) {
        val icon = loadSingleIcon(
            app = app,
            useOverrides = useOverride,
            sizePx = sizePx
        )
        _icons.update { current ->
            val updated = current.toMutableMap()
            updated[app.iconCacheKey.cacheKey] = icon

            if (!updated.containsKey(app.packageName) || (!app.isWorkProfile && !app.isPrivateProfile)) {
                updated[app.packageName] = icon
            }

            updated
        }
    }


    /* ───────────── Multiple Load Functions ───────────── */


    /**
     * Preload a given list of point icons asynchronously and per icon updates the icons list
     *
     * @param points which points to load
     * @param sizePx size of the [ImageBitmap]  loaded
     * @param reloadAll whether to override the existing already loaded or skip them
     */
    fun preloadPointIcons(
        points: List<SwipePointSerializable>,
        sizePx: Int,
        reloadAll: Boolean = false
    ) {
        scope.launch(Dispatchers.Default) {
            points.forEach { p ->
                val id = p.id
                if (_icons.value.containsKey(id) && !reloadAll) return@forEach

                reloadPointIcon(p, sizePx)
            }
        }
    }

    /**
     * Load app icons from a list of [AppModel]
     *
     * @param apps list of app icons to load
     * @param sizePx size of the loaded [ImageBitmap]
     */
    private suspend fun loadAppIcons(
        apps: List<AppModel>,
        sizePx: Int
    ) = withContext(Dispatchers.IO) {
        val updated = _icons.value.toMutableMap()

        apps.forEach { app ->
            val bitmap = runCatching {
                iconSemaphore.withPermit {
                    loadSingleIcon(
                        app = app,
                        useOverrides = true,
                        sizePx = sizePx
                    )
                }
            }.getOrNull() ?: return@forEach

            updated[app.iconCacheKey.cacheKey] = bitmap
        }
        _icons.update { updated }
    }


    /* ──────────────────────────────────────────────────  */


    /**
     * Loads a drawable from the specified icon pack using a resolved drawable name.
     *
     * The function attempts to resolve the provided [iconName] as a `drawable`
     * resource within the icon pack identified by [packPkg]. If a matching
     * resource is found, it is returned as a [Drawable].
     *
     * This method assumes that the correct drawable name has already been
     * determined (e.g., via appfilter mapping or manual naming strategy).
     * No additional fallback logic is performed here.
     *
     * @param packPkg Package name of the icon pack. If `null`, the function
     *                returns `null` immediately.
     * @param iconName Name of the drawable resource inside the icon pack.
     * @param targetPkg Package name of the target application (used for logging/debugging).
     *
     * @return The resolved [Drawable] if found, or `null` if the drawable
     *         resource does not exist in the icon pack.
     */
    @SuppressLint("DiscouragedApi")
    fun loadIconFromPack(
        packPkg: String?,
        iconName: String,
        targetPkg: String
    ): Drawable? {

        logD(ICONS_TAG, "Resolving icon → app=$targetPkg pack=$packPkg resolvedName=$iconName")

        if (packPkg == null) return null

        val packResources = ctx.packageManager.getResourcesForApplication(packPkg)

        // 1. Try standard drawable name
        val drawableId = packResources.getIdentifier(iconName, "drawable", packPkg)
        logD(ICONS_TAG, "Trying drawable: name=$iconName id=$drawableId")
        if (drawableId != 0) {
            return ResourcesCompat.getDrawable(packResources, drawableId, null)
        }

        return null
    }


    /**
     * Load all icons mappings from pack, used to display the picker list when user picks
     * a certain icon from the pack
     *
     * Doesn't load the actual icons, but their names which is cheaper and faster
     * the rendering is handled by the UI level IconPickerListDialog  (not accessible in this scope)
     *
     * @param pack the icon pack from where to load
     */
    fun loadAllIconsMappingsFromPack(pack: IconPackInfo) {

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

    /**
     * Retrieves a cached icon mapping for the given application package.
     *
     * This method checks the currently selected icon pack for a drawable
     * mapping corresponding to [pkgName]. It first attempts an exact
     * component-level match using the app's launch intent. If no exact match
     * is found, it falls back to a package-level match.
     *
     * The result is cached in [IconPackCache] to avoid repeatedly parsing
     * icon pack resources.
     *
     * @param pkgName The package name of the target application.
     *
     * @return The drawable name from the icon pack if a mapping exists,
     *         or `null` if no mapping is found.
     */
    private fun getCachedIconMapping(pkgName: String): String? {
        val pack = selectedIconPack.value ?: return null
        val cache = getCache(pack.packageName)

        logD(ICONS_TAG, "getCachedIconMapping → app=$pkgName pack=${pack.packageName}")

        val launchIntent = runCatching {
            pm.getLaunchIntentForPackage(pkgName)
        }.getOrNull()

        val component = launchIntent?.component?.let {
            normalizeComponent("${it.packageName}/${it.className}")
        }

        // Exact component match (best case)
        component?.let {
            cache.componentToDrawable[it]?.let { drawable ->
                return drawable
            }
        }

        // Package-level match
        cache.pkgToDrawables[pkgName]?.firstOrNull()?.let {
            return it
        }

        logD(ICONS_TAG, "No mapping found for $pkgName")
        return null
    }


    fun selectIconPack(pack: IconPackInfo) {
        _selectedIconPack.value = pack
        scope.launch(Dispatchers.IO) {
            UiSettingsStore.selectedIconPack.set(ctx, pack.packageName)
            reloadApps()
        }
    }

    private fun getCache(packPkg: String): IconPackCache {
        return iconPackCache[packPkg]
            ?: loadIconPackMappings(packPkg).also {
                iconPackCache[packPkg] = it
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
                val hasAppfilter = hasStandardAppFilter(packResources)

                if (hasAppfilter) {
                    val name = pkgInfo.applicationInfo?.loadLabel(pm).toString()
                    logD(
                        ICONS_TAG,
                        "FOUND icon pack: $name (${pkgInfo.packageName}"
                    )

                    packs.add(
                        IconPackInfo(
                            packageName = pkgInfo.packageName,
                            name = name
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val uniquePacks = packs.distinctBy { it.packageName }
        logD(ICONS_TAG, "Total icon packs found: ${uniquePacks.size}")
        _iconPacksList.value = uniquePacks
    }

    fun loadIconPackMappings(packPkg: String): IconPackCache {
        return try {
            val entries = parseAppFilterXml(ctx, packPkg) ?: emptyList()

            val componentToDrawable = mutableMapOf<String, String>()
            val pkgToDrawables = mutableMapOf<String, MutableList<String>>()

            entries.forEach { mapping ->
                val normalized = normalizeComponent(mapping.component)
                val pkg = normalized.substringBefore('/')

                componentToDrawable[normalized] = mapping.drawable

                val list = pkgToDrawables.getOrPut(pkg) { mutableListOf() }
                if (!list.contains(mapping.drawable)) {
                    list.add(mapping.drawable)
                }
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


    private fun normalizeComponent(raw: String): String {
        var comp = raw

        if (comp.contains('{')) comp = comp.substringAfter('{')
        if (comp.contains('}')) comp = comp.substringBefore('}')
        comp = comp.trim()

        if (!comp.contains('/')) return comp

        val pkg = comp.substringBefore('/')
        var cls = comp.substringAfter('/')

        if (cls.startsWith(".")) {
            cls = pkg + cls
        }

        return "$pkg/$cls"
    }


    /** Load the user's workspaces into the _state var, enforced safety due to some crash at start */
    private suspend fun loadWorkspaces() {
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
                    point = dummySwipePoint(
                        SwipeActionSerializable.LaunchApp(
                            packageName,
                            false,
                            0
                        )
                    ).copy(
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
                _recentlyUsedPackages.value = json.toList()
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
            DrawerSettingsStore.recentlyUsedPackages.set(ctx, trimmed.toSet())
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


    /* ───────────── Workspace System───────────── */


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
        val target = _workspacesState.value.workspaces.find { it.id == id } ?: return
        if (target.type != WorkspaceType.CUSTOM) return

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
    fun addAppToWorkspace(workspaceId: String, cacheKey: CacheKey) {
        val cacheKey = cacheKey.cacheKey


        val target = _workspacesState.value.workspaces.find { it.id == workspaceId } ?: return
        if (target.type == WorkspaceType.PRIVATE) return

        _workspacesState.value = _workspacesState.value.copy(
            workspaces = _workspacesState.value.workspaces.map { ws ->
                if (ws.id != workspaceId) return@map ws

                val removed = ws.removedAppIds ?: emptySet()

                ws.copy(
                    appIds = ws.appIds + cacheKey,
                    removedAppIds = if (cacheKey in removed)
                        removed - cacheKey
                    else
                        ws.removedAppIds
                )
            }
        )
        persist()
    }


    fun removeAppFromWorkspace(workspaceId: String, cacheKey: CacheKey) {
        val cacheKey = cacheKey.cacheKey

        val target = _workspacesState.value.workspaces.find { it.id == workspaceId } ?: return
        if (target.type == WorkspaceType.PRIVATE) return

        _workspacesState.value = _workspacesState.value.copy(
            workspaces = _workspacesState.value.workspaces.map { ws ->
                if (ws.id != workspaceId) return@map ws

                // remove the app packageName from appsIds, and add it to removedAppIDs
                ws.copy(
                    appIds = ws.appIds - cacheKey,
                    removedAppIds = (ws.removedAppIds ?: emptyList()) + cacheKey
                )
            }
        )
        persist()
    }

    fun addAliasToApp(alias: String, cacheKey: CacheKey) {
        val cacheKey = cacheKey.cacheKey

        _workspacesState.value = _workspacesState.value.copy(
            appAliases = _workspacesState.value.appAliases +
                    (cacheKey to (_workspacesState.value.appAliases[cacheKey]
                        ?: emptySet()) + alias)
        )
        persist()
    }

//    fun resetAliasesForApp(packageName: String) {
//        _workspacesState.value = _workspacesState.value.copy(
//            appAliases = _workspacesState.value.appAliases.filter { it.key != packageName }
//        )
//        persist()
//    }

    fun removeAliasFromWorkspace(aliasToRemove: String, cacheKey: CacheKey) {
        val cacheKey = cacheKey.cacheKey


        val current = _workspacesState.value.appAliases

        val updated = current[cacheKey]
            ?.minus(aliasToRemove)
            ?.takeIf { it.isNotEmpty() }

        _workspacesState.value = _workspacesState.value.copy(
            appAliases = if (updated == null)
                current - cacheKey
            else
                current + (cacheKey to updated)
        )
        persist()
    }

    fun renameApp(cacheKey: CacheKey, name: String) {
        val cacheKey = cacheKey.cacheKey

        _workspacesState.value = _workspacesState.value.copy(
            appOverrides = _workspacesState.value.appOverrides +
                    (cacheKey to AppOverride(cacheKey, name))
        )
        persist()
    }

    fun setAppIcon(cacheKey: CacheKey, customIcon: CustomIconSerializable?) {
        val cacheKey = cacheKey.cacheKey

        val prev = _workspacesState.value.appOverrides[cacheKey]
        _workspacesState.value = _workspacesState.value.copy(
            appOverrides = _workspacesState.value.appOverrides +
                    (cacheKey to (prev?.copy(customIcon = customIcon)
                        ?: AppOverride(cacheKey, customIcon = customIcon)))
        )
        persist()
    }

    /**
     * Mainly debug funny thing, its like customizing all app icons at once
     * for each app installed, it applies to it the custom icon
     *
     * @param icon
     */
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


    fun resetAppName(cacheKey: CacheKey) {
        val cacheKey = cacheKey.cacheKey

        val prev = _workspacesState.value.appOverrides[cacheKey] ?: return

        val updated = prev.copy(customLabel = null)

        _workspacesState.value = _workspacesState.value.copy(
            appOverrides =
                if (updated.customIcon == null)
                    _workspacesState.value.appOverrides - cacheKey
                else
                    _workspacesState.value.appOverrides + (cacheKey to updated)
        )
        scope.launch {
            reloadApps()
        }
        persist()
    }

    fun resetAppIcon(cacheKey: CacheKey) {
        val cacheKey = cacheKey.cacheKey

        val prev = _workspacesState.value.appOverrides[cacheKey] ?: return

        val updated = prev.copy(customIcon = null)

        _workspacesState.value = _workspacesState.value.copy(
            appOverrides =
                if (updated.customLabel == null)
                    _workspacesState.value.appOverrides - cacheKey
                else
                    _workspacesState.value.appOverrides + (cacheKey to updated)
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

    suspend fun setIconPackTint(tint: Color?) {
        UiSettingsStore.iconPackTint.set(ctx, tint)
        _packTint.value = tint?.toArgb()
        reloadApps()
    }
}

/**
 * Checks whether the given [Resources] instance contains an `appfilter.xml`
 * inside the `assets/` directory.
 *
 * This is used as a lightweight heuristic to detect traditional icon packs
 * that ship a standard `appfilter.xml` file in their assets folder.
 *
 * Note:
 * - Some icon packs place `appfilter.xml` under `res/xml/` instead of `assets/`.
 * - A `false` result does not guarantee that no app filter exists, only that
 *   it was not found in the `assets` directory.
 *
 * @param res Resources of the icon pack application.
 * @return `true` if `assets/appfilter.xml` can be opened successfully,
 *         `false` otherwise.
 */
private fun hasStandardAppFilter(res: Resources): Boolean {
    return try {
        res.assets.open("appfilter.xml").use { true }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * Attempts to parse icon mappings from an icon pack's `appfilter.xml`.
 *
 * The function tries both common locations used by icon packs:
 *
 * 1. `assets/appfilter.xml`
 * 2. `res/xml/appfilter.xml`
 *
 * If mappings are successfully parsed from the first location, the second
 * is not attempted. If neither location yields valid mappings, `null`
 * is returned.
 *
 * This supports both traditional icon packs and variations that place
 * the filter file in different locations.
 *
 * @param ctx Context used to obtain the target application's resources.
 * @param packPkg Package name of the icon pack.
 * @return A list of [IconMapping] entries if parsing succeeds,
 *         or `null` if no valid `appfilter.xml` could be found or parsed.
 */
@SuppressLint("DiscouragedApi")
private fun parseAppFilterXml(ctx: Context, packPkg: String): List<IconMapping>? {
    val packResources = ctx.packageManager.getResourcesForApplication(packPkg)
    var mappings: List<IconMapping>? = null

    // 1. Try assets/appfilter.xml first
    try {
        packResources.assets.open("appfilter.xml").use { input ->
            val parser = Xml.newPullParser()
            parser.setInput(input.reader())
            mappings = parseXml(parser)
        }
        if (mappings?.isNotEmpty() == true) {
            ctx.logD(ICONS_TAG, "Loaded ${mappings.size} mappings from assets/appfilter.xml")
            return mappings
        }
    } catch (e: Exception) {
        ctx.logD(ICONS_TAG, "Assets appfilter.xml failed: ${e.message}")
    }

    // 2. Fallback to res/xml/appfilter.xml
    val resId = packResources.getIdentifier("appfilter", "xml", packPkg)
    if (resId == 0) return null

    try {
        val parser: XmlResourceParser = packResources.getXml(resId)
        mappings = parseXml(parser)
        ctx.logD(ICONS_TAG, "Loaded ${mappings.size} mappings from res/xml/appfilter.xml")
    } catch (e: Exception) {
        ctx.logE(ICONS_TAG, "res/xml/appfilter.xml parse failed: ${e.message}")
    }

    return mappings
}

/**
 * Parses an `appfilter.xml` document and extracts icon mapping entries.
 *
 * The parser scans for `<item>` tags and reads:
 * - `component` or `activity` attribute (component name)
 * - `drawable` attribute (icon resource name)
 *
 * Each valid pair is converted into an [IconMapping] and added to the result list.
 * Entries missing required attributes are ignored.
 *
 * @param parser An initialized [XmlPullParser] positioned at the start of
 *               an `appfilter.xml` document.
 * @return A list of parsed [IconMapping] objects. The list may be empty
 *         if no valid `<item>` entries are found.
 */
private fun parseXml(parser: XmlPullParser): List<IconMapping> {
    val mappings = mutableListOf<IconMapping>()
    var eventType = parser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && parser.name == "item") {
            val component = parser.getAttributeValue(null, "component") ?: parser.getAttributeValue(
                null,
                "activity"
            )
            val drawable = parser.getAttributeValue(null, "drawable")
            if (!component.isNullOrEmpty() && !drawable.isNullOrEmpty()) {
                mappings.add(IconMapping(component, drawable))
            }
        }
        eventType = parser.next()
    }
    return mappings
}

/**
 * Icon pack cache with normalized component mapping and package -> drawables list
 */
data class IconPackCache(
    val pkgToDrawables: Map<String, List<String>>,
    val componentToDrawable: Map<String, String>
)
