package org.elnix.dragonlauncher.ui

import android.annotation.SuppressLint
import android.os.Build
import android.util.DisplayMetrics
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.base.ktx.toPixels
import org.elnix.dragonlauncher.common.FloatingAppObject
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.common.serializables.dummySwipePoint
import org.elnix.dragonlauncher.common.utils.Constants.Logging.TAG
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.WidgetHostProvider
import org.elnix.dragonlauncher.common.utils.circles.rememberNestNavigation
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.models.FloatingAppsViewModel
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore
import org.elnix.dragonlauncher.ui.actions.AppLaunchException
import org.elnix.dragonlauncher.ui.actions.launchAppDirectly
import org.elnix.dragonlauncher.ui.actions.launchSwipeAction
import org.elnix.dragonlauncher.ui.components.FloatingAppsHostView
import org.elnix.dragonlauncher.ui.components.burger.BurgerAction
import org.elnix.dragonlauncher.ui.components.burger.BurgerListAction
import org.elnix.dragonlauncher.ui.components.resolveShape
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.components.settings.asStateNull
import org.elnix.dragonlauncher.ui.dialogs.FilePickerDialog
import org.elnix.dragonlauncher.ui.helpers.HoldToActivateArc
import org.elnix.dragonlauncher.ui.helpers.WallpaperDim
import org.elnix.dragonlauncher.ui.remembers.rememberHoldToOpenSettings
import org.elnix.dragonlauncher.ui.statusbar.StatusBar
import org.elnix.dragonlauncher.ui.wellbeing.AppTimerService
import org.elnix.dragonlauncher.ui.wellbeing.DigitalPauseActivity
import kotlin.math.max


@SuppressLint("LocalContextResourcesRead")
@Suppress("AssignedValueIsNeverRead")
@Composable
fun MainScreen(
    appsViewModel: AppsViewModel,
    floatingAppsViewModel: FloatingAppsViewModel,
    appLifecycleViewModel: AppLifecycleViewModel,
    widgetHostProvider: WidgetHostProvider,
    onAppDrawer: (workspaceId: String?) -> Unit,
    onGoWelcome: () -> Unit,
    onSettings: (route: String) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var showFilePicker: SwipePointSerializable? by remember { mutableStateOf(null) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

    val floatingAppObjects by floatingAppsViewModel.floatingApps.collectAsState()
    val defaultPoint by appsViewModel.defaultPoint.collectAsState(defaultSwipePointsValues)


    /* ───────────── Custom Actions ─────────────*/
    val doubleClickAction by BehaviorSettingsStore.doubleClickAction.asStateNull()
    val backAction by BehaviorSettingsStore.backAction.asStateNull()
    val homeAction by BehaviorSettingsStore.homeAction.asStateNull()

    val leftPadding by BehaviorSettingsStore.leftPadding.asState()
    val rightPadding by BehaviorSettingsStore.rightPadding.asState()
    val topPadding by BehaviorSettingsStore.topPadding.asState()
    val bottomPadding by BehaviorSettingsStore.bottomPadding.asState()

    val iconsShape by DrawerSettingsStore.iconsShape.asState()

    val holdDelayBeforeStartingLongClickSettings by BehaviorSettingsStore
        .holdDelayBeforeStartingLongClickSettings.asState()
    val longCLickSettingsDuration by BehaviorSettingsStore.longCLickSettingsDuration.asState()


    /*  ─────────────  Wellbeing Settings  ─────────────  */
    val socialMediaPauseEnabled by WellbeingSettingsStore.socialMediaPauseEnabled.asState()
    val guiltModeEnabled by WellbeingSettingsStore.guiltModeEnabled.asState()
    val pauseDuration by WellbeingSettingsStore.pauseDurationSeconds.asState()
    val pausedApps by WellbeingSettingsStore.getPausedAppsFlow(ctx)
        .collectAsState(initial = emptySet())
    val reminderEnabled by WellbeingSettingsStore.reminderEnabled.asState()
    val reminderInterval by WellbeingSettingsStore.reminderIntervalMinutes.asState()
    val reminderMode by WellbeingSettingsStore.reminderMode.asState()
    val returnToLauncherEnabled by WellbeingSettingsStore.returnToLauncherEnabled.asState()

    // Store pending package to launch after pause
    var pendingPackageToLaunch by remember { mutableStateOf<String?>(null) }
    var pendingUserIdToLaunch by remember { mutableStateOf<Int?>(null) }
    var pendingAppName by remember { mutableStateOf<String?>(null) }

    val digitalPauseLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == DigitalPauseActivity.RESULT_PROCEED && pendingPackageToLaunch != null) {
            try {
                // Start reminder-only timer if enabled (no time limit)
                if (reminderEnabled) {
                    AppTimerService.start(
                        ctx = ctx,
                        packageName = pendingPackageToLaunch!!,
                        appName = pendingAppName ?: pendingPackageToLaunch!!,
                        reminderEnabled = true,
                        reminderIntervalMinutes = reminderInterval,
                        reminderMode = reminderMode
                    )
                }

                launchAppDirectly(
                    appsViewModel,
                    ctx,
                    pendingPackageToLaunch!!,
                    pendingUserIdToLaunch!!
                )
            } catch (e: Exception) {
                ctx.logE(TAG, "Failed to launch after pause: ${e.message}")
            }
        } else if (result.resultCode == DigitalPauseActivity.RESULT_PROCEED_WITH_TIMER && pendingPackageToLaunch != null) {
            try {
                val data = result.data
                val timeLimitMin =
                    data?.getIntExtra(DigitalPauseActivity.RESULT_EXTRA_TIME_LIMIT, 10) ?: 10
                val hasReminder =
                    data?.getBooleanExtra(DigitalPauseActivity.EXTRA_REMINDER_ENABLED, false)
                        ?: false
                val remInterval =
                    data?.getIntExtra(DigitalPauseActivity.EXTRA_REMINDER_INTERVAL, 5) ?: 5
                val remMode =
                    data?.getStringExtra(DigitalPauseActivity.EXTRA_REMINDER_MODE) ?: "overlay"

                AppTimerService.start(
                    ctx = ctx,
                    packageName = pendingPackageToLaunch!!,
                    appName = pendingAppName ?: pendingPackageToLaunch!!,
                    reminderEnabled = hasReminder,
                    reminderIntervalMinutes = remInterval,
                    reminderMode = remMode,
                    timeLimitEnabled = true,
                    timeLimitMinutes = timeLimitMin
                )

                launchAppDirectly(
                    appsViewModel,
                    ctx,
                    pendingPackageToLaunch!!,
                    pendingUserIdToLaunch!!
                )
            } catch (e: Exception) {
                ctx.logE(TAG, "Failed to launch after pause with timer: ${e.message}")
            }
        }
        pendingPackageToLaunch = null
        pendingAppName = null
    }


    val icons by appsViewModel.icons.collectAsState()
    val pointIcons by appsViewModel.pointIcons.collectAsState()

    var start by remember { mutableStateOf<Offset?>(null) }
    var current by remember { mutableStateOf<Offset?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize.Zero) }


    var tempStartPos by remember { mutableStateOf(start) }
    var showDropDownMenuSettings by remember { mutableStateOf(false) }

    val hold = rememberHoldToOpenSettings(
        onSettings = {
            showDropDownMenuSettings = true
            tempStartPos = start
        },
        holdDelay = holdDelayBeforeStartingLongClickSettings.toLong(),
        loadDuration = longCLickSettingsDuration.toLong(),
    )

    val defaultColor = Color.Red
    val rgbLoading by UiSettingsStore.rgbLoading.asState()

    val hasSeenWelcome by PrivateSettingsStore.hasSeenWelcome.asStateNull()

    val useAccessibilityInsteadOfContextToExpandActionPanel by DebugSettingsStore
        .useAccessibilityInsteadOfContextToExpandActionPanel.asState()


    /* ───────────── status bar things ───────────── */

    val showStatusBar by StatusBarSettingsStore.showStatusBar.asState()

    val systemInsets = WindowInsets.systemBars.asPaddingValues()

    val isRealFullscreen = systemInsets.calculateTopPadding() == 0.dp

    /* ────────────────────────────────────────────── */


    /* Dim wallpaper system */
    val mainBlurRadius by UiSettingsStore.wallpaperDimMainScreen.asState()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        WallpaperDim(mainBlurRadius)
    }



    LaunchedEffect(hasSeenWelcome) {
        if (hasSeenWelcome == false) onGoWelcome()
    }

    LaunchedEffect(Unit) { lastClickTime = 0 }

    val points by SwipeSettingsStore.getPointsFlow(ctx).collectAsState(emptyList())
    val nests by SwipeSettingsStore.getNestsFlow(ctx).collectAsState(emptyList())

    val nestNavigation = rememberNestNavigation(nests)
    val nestId = nestNavigation.nestId

    val filteredFloatingAppObjects by remember(floatingAppObjects, nestId) {
        derivedStateOf {
            floatingAppObjects.filter { it.nestId == nestId }
        }
    }


    val dm = ctx.resources.displayMetrics
    val density = LocalDensity.current
    val cellSizePx = floatingAppsViewModel.cellSizePx

    val appIconOverlaySize by UiSettingsStore.appIconOverlaySize.asState()

    val densityPixelsIconOverlaySize = appIconOverlaySize.dp.toPixels().toInt()
    /**
     * Reload all point icons on every change of the points, nestId, appIconOverlaySize, or default point
     * Set the size of the icons to the max size between the 2 overlays sizes preview to display them cleanly
     */
    LaunchedEffect(points, nestId, appIconOverlaySize, defaultPoint.hashCode()) {

        val sizePx = max(densityPixelsIconOverlaySize, defaultPoint.size ?: 128)

        appsViewModel.preloadPointIcons(
            points = points.filter { it.nestId == nestId },
            sizePx = sizePx
        )

        /* Load asynchronously all the other points, to avoid lag */
        scope.launch(Dispatchers.IO) {
            appsViewModel.preloadPointIcons(
                points = points,
                sizePx = sizePx
            )
        }
    }


    fun launchAction(point: SwipePointSerializable?) {
        isDragging = false
        nestNavigation.goToNest(0)
        start = null
        current = null
        lastClickTime = 0

        // Store package for potential pause callback
        val action = point?.action
        if (action is SwipeActionSerializable.LaunchApp) {
            pendingPackageToLaunch = action.packageName
            pendingUserIdToLaunch = action.userId ?: 0
            pendingAppName = point.customName ?: try {
                ctx.packageManager.getApplicationLabel(
                    ctx.packageManager.getApplicationInfo(action.packageName, 0)
                ).toString()
            } catch (_: Exception) {
                action.packageName
            }
        }

        try {
            launchSwipeAction(
                ctx = ctx,
                appsViewModel = appsViewModel,
                action = action,
                useAccessibilityInsteadOfContextToExpandActionPanel = useAccessibilityInsteadOfContextToExpandActionPanel,
                pausedApps = pausedApps,
                socialMediaPauseEnabled = socialMediaPauseEnabled,
                guiltModeEnabled = guiltModeEnabled,
                pauseDuration = pauseDuration,
                reminderEnabled = reminderEnabled,
                reminderIntervalMinutes = reminderInterval,
                reminderMode = reminderMode,
                returnToLauncherEnabled = returnToLauncherEnabled,
                appName = pendingAppName ?: "",
                digitalPauseLauncher = digitalPauseLauncher,
                onReloadApps = { scope.launch { appsViewModel.reloadApps() } },
                onReselectFile = { showFilePicker = point },
                onAppSettings = onSettings,
                onAppDrawer = onAppDrawer,
                onParentNest = { nestNavigation.goBack() },
                onOpenNestCircle = { nestNavigation.goToNest(it) }
            )
        } catch (e: AppLaunchException) {
            ctx.logE(TAG, e.message!!) // Lol if it crashes when logging for an exception
        } catch (e: Exception) {
            ctx.logE(TAG, e.message ?: "")
        }
    }



    LaunchedEffect(Unit) {
        appLifecycleViewModel.homeEvents.collect {
            // HOME while already on MAIN
            // Decide locally what it means

            if (homeAction != null) {
                launchAction(dummySwipePoint(homeAction))
            }
        }
    }

    /**
     * 1. Tests if the current nest is the main, if not, go back one nest
     * 2. Activate the back actions
     */
    BackHandler {
        if (nestId != 0) {
            nestNavigation.goBack()
        } else if (backAction != null) {
            launchAction(
                dummySwipePoint(backAction)
            )
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit, nestId) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)

                        val down = event.changes.firstOrNull { it.changedToDown() } ?: continue
                        val pos = down.position

                        val allowed = isInsideActiveZone(
                            pos = pos,
                            size = size,
                            left = leftPadding,
                            right = rightPadding,
                            top = topPadding,
                            bottom = bottomPadding
                        )

                        if (!allowed) {
                            continue
                        }

                        if (isInsideForegroundWidget(
                                pos = pos,
                                floatingAppObjects = filteredFloatingAppObjects,
                                dm = dm,
                                density = density,
                                cellSizePx = cellSizePx
                            )
                        ) {
                            // Let widget handle scroll - do NOT consume or process
                            continue
                        }

                        start = down.position
                        current = down.position
                        isDragging = true

                        val pointerId = down.id

                        val currentTime = System.currentTimeMillis()
                        val diff = currentTime - lastClickTime
                        if (diff < 500) {
                            doubleClickAction?.let { action ->
                                launchAction(
                                    dummySwipePoint(action)
                                )
                                isDragging = false
                                continue
                            }
                        }
                        lastClickTime = currentTime

                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val change = event.changes.firstOrNull { it.id == pointerId }

                            if (change != null) {
                                if (change.pressed) {
                                    change.consume()
                                    current = change.position
                                } else {
                                    isDragging = false
                                    start = null
                                    current = null
                                    break
                                }
                            } else {
                                isDragging = false
                                start = null
                                current = null
                                break
                            }
                        }
                    }
                }
            }
            .onSizeChanged { size = it }
            .then(hold.pointerModifier)
    ) {

        filteredFloatingAppObjects.forEach { floatingAppObject ->
            key(floatingAppObject.id, nestId) {
                FloatingAppsHostView(
                    floatingAppObject = floatingAppObject,
                    icons = icons,
                    shape = resolveShape(iconsShape),
                    cellSizePx = cellSizePx,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = (floatingAppObject.x * dm.widthPixels).toInt(),
                                y = (floatingAppObject.y * dm.heightPixels).toInt()
                            )
                        }
                        .size(
                            width = with(density) { (floatingAppObject.spanX * cellSizePx).toDp() },
                            height = with(density) { (floatingAppObject.spanY * cellSizePx).toDp() }
                        ),
                    onLaunchAction = {
                        launchAction(
                            dummySwipePoint(
                                action = floatingAppObject.action
                            )
                        )
                    },
                    blockTouches = floatingAppObject.ghosted == true,
                    widgetHostProvider = widgetHostProvider
                )
            }
        }

        if (showStatusBar && isRealFullscreen) {
            StatusBar(
                onClockAction = { launchAction(dummySwipePoint(it)) },
                onDateAction = { launchAction(dummySwipePoint(it)) }
            )
        }

        MainScreenOverlay(
            start = start,
            current = current,
            nestId = nestId,
            isDragging = isDragging,
            surface = size,
            points = points,
            defaultPoint = defaultPoint,
            pointIcons = pointIcons,
            nests = nests,
            onLaunch = { launchAction(it) }
        )

        HoldToActivateArc(
            center = hold.centerProvider(),
            progress = hold.progressProvider(),
            defaultColor = defaultColor,
            rgbLoading = rgbLoading
        )

        if (tempStartPos != null) {
            DropdownMenu(
                expanded = showDropDownMenuSettings,
                onDismissRequest = {
                    showDropDownMenuSettings = false
                    tempStartPos = null
                },
                containerColor = Color.Transparent,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                offset = with(density) {
                    DpOffset(
                        x = tempStartPos!!.x.toDp(),
                        y = tempStartPos!!.y.toDp()
                    )
                }
            ) {
                BurgerListAction(
                    actions = listOf(
                        BurgerAction(
                            onClick = {
                                showDropDownMenuSettings = false
                                onSettings(SETTINGS.ROOT)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(R.string.settings),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        BurgerAction(
                            onClick = {
                                showDropDownMenuSettings = false
                                onSettings(SETTINGS.FLOATING_APPS)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(R.string.widgets),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        BurgerAction(
                            onClick = {
                                showDropDownMenuSettings = false
                                onSettings(SETTINGS.WALLPAPER)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wallpaper,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(R.string.wallpaper),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                )
            }
        }
    }

    if (showFilePicker != null) {
        val currentPoint = showFilePicker!!

        FilePickerDialog(
            onDismiss = { showFilePicker = null },
            onFileSelected = { newAction ->

                // Build the updated point
                val updatedPoint = currentPoint.copy(action = newAction)

                // Replace only this point
                val finalList = points.map { p ->
                    if (p.id == currentPoint.id) updatedPoint else p
                }


                scope.launch {
                    SwipeSettingsStore.savePoints(ctx, finalList)
                }

                showFilePicker = null
            }
        )
    }
}


/**
 * Determines whether a pointer position lies within the allowed interaction zone.
 *
 * The active zone is defined as the rectangular area of the screen obtained by
 * excluding padding margins from each edge. Any position inside this rectangle
 * is considered valid for gesture handling.
 *
 * @param pos Pointer position in screen coordinates.
 * @param size Full size of the available surface.
 * @param left Excluded distance from the left edge.
 * @param right Excluded distance from the right edge.
 * @param top Excluded distance from the top edge.
 * @param bottom Excluded distance from the bottom edge.
 *
 * @return `true` if the position is inside the active zone, `false` otherwise.
 */
private fun isInsideActiveZone(
    pos: Offset,
    size: IntSize,
    left: Int,
    right: Int,
    top: Int,
    bottom: Int
): Boolean {
    return pos.x >= left &&
            pos.x <= size.width - right &&
            pos.y >= top &&
            pos.y <= size.height - bottom
}


/**
 * Checks if pointer position is inside any foreground widget bounds.
 */
private fun isInsideForegroundWidget(
    pos: Offset,
    floatingAppObjects: List<FloatingAppObject>,
    dm: DisplayMetrics,
    density: Density,
    cellSizePx: Float
): Boolean {
    return floatingAppObjects.any { widget ->
        // Skip if not foreground
        if (widget.foreground == false) return@any false

        val left = (widget.x * dm.widthPixels).toInt()
        val top = (widget.y * dm.heightPixels).toInt()
        val right =
            left + with(density) { (widget.spanX * cellSizePx).toDp() }.value.times(density.density)
                .toInt()
        val bottom =
            top + with(density) { (widget.spanY * cellSizePx).toDp() }.value.times(density.density)
                .toInt()

        pos.x >= left && pos.x <= right &&
                pos.y >= top && pos.y <= bottom
    }
}
