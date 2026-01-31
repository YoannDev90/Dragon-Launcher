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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.base.ktx.toPixels
import org.elnix.dragonlauncher.common.FloatingAppObject
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.common.serializables.dummySwipePoint
import org.elnix.dragonlauncher.common.utils.TAG
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
import org.elnix.dragonlauncher.ui.components.resolveShape
import org.elnix.dragonlauncher.ui.dialogs.FilePickerDialog
import org.elnix.dragonlauncher.ui.helpers.HoldToActivateArc
import org.elnix.dragonlauncher.ui.helpers.WallpaperDim
import org.elnix.dragonlauncher.ui.helpers.rememberHoldToOpenSettings
import org.elnix.dragonlauncher.ui.statusbar.StatusBar
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
    onAppDrawer: () -> Unit,
    onGoWelcome: () -> Unit,
    onLongPress3Sec: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var showFilePicker: SwipePointSerializable? by remember { mutableStateOf(null) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

    val floatingAppObjects by floatingAppsViewModel.floatingApps.collectAsState()
    val defaultPoint by appsViewModel.defaultPoint.collectAsState(defaultSwipePointsValues)


    val doubleClickAction by BehaviorSettingsStore.doubleClickAction.flow(ctx)
        .collectAsState(initial = null)

    val backAction by BehaviorSettingsStore.backAction.flow(ctx)
        .collectAsState(initial = null)

    val homeAction by BehaviorSettingsStore.homeAction.flow(ctx)
        .collectAsState(initial = null)

    val leftPadding by BehaviorSettingsStore.leftPadding.flow(ctx)
        .collectAsState(initial = 0)

    val rightPadding by BehaviorSettingsStore.rightPadding.flow(ctx)
        .collectAsState(initial = 0)

    val topPadding by BehaviorSettingsStore.topPadding.flow(ctx)
        .collectAsState(initial = 0)

    val bottomPadding by BehaviorSettingsStore.bottomPadding.flow(ctx)
        .collectAsState(initial = 0)

    val iconsShape by DrawerSettingsStore.iconsShape.flow(ctx)
        .collectAsState(DrawerSettingsStore.iconsShape.default)



    /*  ─────────────  Wellbeing Settings  ─────────────  */
    val socialMediaPauseEnabled by WellbeingSettingsStore.socialMediaPauseEnabled.flow(ctx)
        .collectAsState(initial = false)
    val guiltModeEnabled by WellbeingSettingsStore.guiltModeEnabled.flow(ctx)
        .collectAsState(initial = false)
    val pauseDuration by WellbeingSettingsStore.pauseDurationSeconds.flow(ctx)
        .collectAsState(initial = 10)
    val pausedApps by WellbeingSettingsStore.getPausedAppsFlow(ctx)
        .collectAsState(initial = emptySet())

    // Store pending package to launch after pause
    var pendingPackageToLaunch by remember { mutableStateOf<String?>(null) }

    val digitalPauseLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == DigitalPauseActivity.RESULT_PROCEED && pendingPackageToLaunch != null) {
            try {
                launchAppDirectly(ctx, pendingPackageToLaunch!!)
            } catch (e: Exception) {
                ctx.logE(TAG, "Failed to launch after pause: ${e.message}")
            }
        }
        pendingPackageToLaunch = null
    }


    val icons by appsViewModel.icons.collectAsState()
    val pointIcons by appsViewModel.pointIcons.collectAsState()

    var start by remember { mutableStateOf<Offset?>(null) }
    var current by remember { mutableStateOf<Offset?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    val hold = rememberHoldToOpenSettings(
        onSettings = onLongPress3Sec
    )

    val defaultColor = Color.Red
    val rgbLoading by UiSettingsStore.rgbLoading.flow(ctx)
        .collectAsState(initial = true)

    val hasSeenWelcome by PrivateSettingsStore.hasSeenWelcome.flow(ctx)
        .collectAsState(initial = true)

    val useAccessibilityInsteadOfContextToExpandActionPanel by DebugSettingsStore
        .useAccessibilityInsteadOfContextToExpandActionPanel.flow(ctx)
        .collectAsState(initial = true)


    /* ───────────── status bar things ───────────── */

    val showStatusBar by StatusBarSettingsStore.showStatusBar.flow(ctx)
        .collectAsState(initial = false)

    val systemInsets = WindowInsets.systemBars.asPaddingValues()

    val isRealFullscreen = systemInsets.calculateTopPadding() == 0.dp

    /* ────────────────────────────────────────────── */




    /* Dim wallpaper system */
    val mainBlurRadius by UiSettingsStore.wallpaperDimMainScreen.flow(ctx)
        .collectAsState(UiSettingsStore.wallpaperDimMainScreen.default)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        WallpaperDim(mainBlurRadius)
    }



    LaunchedEffect(hasSeenWelcome) {
        if (!hasSeenWelcome) onGoWelcome()
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

    val appIconOverlaySize by UiSettingsStore.appIconOverlaySize.flow(ctx)
        .collectAsState(initial = 22)

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
        }

        try {
            launchSwipeAction(
                ctx = ctx,
                action = action,
                useAccessibilityInsteadOfContextToExpandActionPanel = useAccessibilityInsteadOfContextToExpandActionPanel,
                pausedApps = pausedApps,
                socialMediaPauseEnabled = socialMediaPauseEnabled,
                guiltModeEnabled = guiltModeEnabled,
                pauseDuration = pauseDuration,
                digitalPauseLauncher = digitalPauseLauncher,
                onReloadApps = { scope.launch { appsViewModel.reloadApps() } },
                onReselectFile = { showFilePicker = point },
                onAppSettings = onLongPress3Sec,
                onAppDrawer = onAppDrawer,
                onOpenNestCircle = { nestNavigation.goToNest(it) },
                onParentNest = { nestNavigation.goBack() }
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
                onDateAction = { launchAction( dummySwipePoint(it)) }
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
        val right = left + with(density) { (widget.spanX * cellSizePx).toDp() }.value.times(density.density).toInt()
        val bottom = top + with(density) { (widget.spanY * cellSizePx).toDp() }.value.times(density.density).toInt()

        pos.x >= left && pos.x <= right &&
                pos.y >= top && pos.y <= bottom
    }
}
