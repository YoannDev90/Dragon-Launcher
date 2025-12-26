package org.elnix.dragonlauncher.ui

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.SwipePointSerializable
import org.elnix.dragonlauncher.data.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.data.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.data.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.data.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.components.dialogs.FilePickerDialog
import org.elnix.dragonlauncher.ui.helpers.HoldToActivateArc
import org.elnix.dragonlauncher.ui.components.dialogs.UserValidation
import org.elnix.dragonlauncher.ui.helpers.rememberHoldToOpenSettings
import org.elnix.dragonlauncher.utils.actions.launchSwipeAction
import org.elnix.dragonlauncher.utils.models.AppDrawerViewModel

@Suppress("AssignedValueIsNeverRead")
@Composable
fun MainScreen(
    appsViewModel: AppDrawerViewModel,
    wallpaper: Bitmap?,
    useWallpaper: Boolean,
    onAppDrawer: () -> Unit,
    onGoWelcome: () -> Unit,
    onLongPress3Sec: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var showFilePicker: SwipePointSerializable? by remember { mutableStateOf(null) }
    var showMethodDialog by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }


//    val showMethodAsking by PrivateSettingsStore.getShowMethodAsking(ctx)
//        .collectAsState(initial = false)
    // Removed hacky popup on notifications, use debug on my phone to open quick settings
    val showMethodAsking = false

    val doubleClickAction by BehaviorSettingsStore.getDoubleClickAction(ctx)
        .collectAsState(initial = null)

    val backAction by BehaviorSettingsStore.getBackAction(ctx)
        .collectAsState(initial = null)

    val leftPadding by BehaviorSettingsStore.getLeftPadding(ctx)
        .collectAsState(initial = 0)

    val rightPadding by BehaviorSettingsStore.getRightPadding(ctx)
        .collectAsState(initial = 0)

    val upPadding by BehaviorSettingsStore.getUpPadding(ctx)
        .collectAsState(initial = 0)

    val downPadding by BehaviorSettingsStore.getDownPadding(ctx)
        .collectAsState(initial = 0)


    val icons by appsViewModel.icons.collectAsState()

    var start by remember { mutableStateOf<Offset?>(null) }
    var current by remember { mutableStateOf<Offset?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    val hold = rememberHoldToOpenSettings(
        onSettings = onLongPress3Sec
    )

    val defaultColor = Color.Red
    val rgbLoading by UiSettingsStore.getRGBLoading(ctx)
        .collectAsState(initial = true)

    val hasSeenWelcome by PrivateSettingsStore.getHasSeenWelcome(ctx)
        .collectAsState(initial = true)

    val useAccessibilityInsteadOfContextToExpandActionPanel by PrivateSettingsStore
        .getUseAccessibilityInsteadOfContextToExpandActionPanel(ctx)
        .collectAsState(initial = true)


    LaunchedEffect(hasSeenWelcome) {
        if (!hasSeenWelcome) onGoWelcome()
    }

    LaunchedEffect(Unit) { lastClickTime = 0 }

    val points by SwipeSettingsStore.getPointsFlow(ctx).collectAsState(emptyList())


    fun launchAction(point: SwipePointSerializable?) {
        isDragging = false
        start = null
        current = null

        launchSwipeAction(
            ctx = ctx,
            action = point?.action,
            useAccessibilityInsteadOfContextToExpandActionPanel = useAccessibilityInsteadOfContextToExpandActionPanel,
            onAskWhatMethodToUseToOpenQuickActions = { showMethodDialog = true },
            onReloadApps = { scope.launch { appsViewModel.reloadApps(ctx) } },
            onReselectFile = { showFilePicker = point },
            onAppSettings = onLongPress3Sec,
            onAppDrawer = onAppDrawer
        )
    }

    BackHandler {
        if (backAction != null){
            launchAction(
                SwipePointSerializable(
                    circleNumber = 0,
                    angleDeg = 0.toDouble(),
                    action = backAction
                )
            )
        }
    }


    // I should store a plain wallpaper color, and print it for performances and optimisations issues but nobody's using a plain color as wallpaper anyways
    if (useWallpaper) {
        wallpaper?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { size = it },
                contentScale = ContentScale.Crop
            )
        }
    } else {
        // Moved the background drawing here for more clarity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .onSizeChanged { size = it }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()


                    // Detects if the drag is inside the padded zone or not
                    val allowed = isInsideActiveZone(
                        pos = down.position,
                        size = size,
                        left = leftPadding,
                        right = rightPadding,
                        top = upPadding,
                        bottom = downPadding
                    )

                    if (!allowed) {
                        down.consume()
                        return@awaitEachGesture
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
                                SwipePointSerializable(
                                    circleNumber = 0,
                                    angleDeg = 0.toDouble(),
                                    action = action
                                )
                            )
                            isDragging = false
                            return@awaitEachGesture
                        }
                    }
                    lastClickTime = currentTime

                    while (true) {
                        val event = awaitPointerEvent()
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
            .onSizeChanged { size = it }
            .then(hold.pointerModifier)
    ) {

        MainScreenOverlay(
            icons = icons,
            start = start,
            current = current,
            isDragging = isDragging,
            surface = size,
            points = points,
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
                    if (p == currentPoint) updatedPoint else p
                }


                scope.launch {
                    SwipeSettingsStore.save(ctx, finalList)
                }

                showFilePicker = null
            }
        )
    }


    if (showMethodDialog and showMethodAsking) {
        UserValidation(
            title = stringResource(R.string.what_method_to_open_quick_actions),
            message = stringResource(R.string.did_the_notif_or_quick_actions),
            validateText = stringResource(R.string.quick_actions),
            cancelText = stringResource(R.string.notifications),
            canDismissByOuterClick = false,
            doNotRemindMeAgain = {
                scope.launch {
                    PrivateSettingsStore.setShowMethodAsking(ctx, false)
                }
            },
            onCancel = {
                // The simple ctx method didn't work, so forced to use the accessibility method, that doesn't work well on my phone
                scope.launch {
                    PrivateSettingsStore.setUseAccessibilityInsteadOfContextToExpandActionPanel(ctx, false)
                }
                showMethodDialog = false
            },
            onAgree = {
                // The simple ctx method worked, keep it
                scope.launch {
                    PrivateSettingsStore.setUseAccessibilityInsteadOfContextToExpandActionPanel(ctx, true)
                }
                showMethodDialog = false
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
