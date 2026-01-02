@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui

import android.graphics.Bitmap
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
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.data.SwipePointSerializable
import org.elnix.dragonlauncher.data.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.data.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.data.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.data.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.helpers.HoldToActivateArc
import org.elnix.dragonlauncher.ui.helpers.rememberHoldToOpenSettings
import org.elnix.dragonlauncher.utils.actions.launchSwipeAction
import org.elnix.dragonlauncher.utils.circles.rememberNestNavigation
import org.elnix.dragonlauncher.utils.models.AppsViewModel


/**
 * Originally I wanted to use that as the widget but die to some android limitations,
 * it is not possible to track pointer location / precise gestures, only clicks and vertical scroll
 */
@Composable
fun DragonWidget(
    appsViewModel: AppsViewModel,
    wallpaper: Bitmap?,
    useWallpaper: Boolean,
    onAppDrawer: () -> Unit,
    onGoWelcome: () -> Unit,
    onLongPress3Sec: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var lastClickTime by remember { mutableLongStateOf(0L) }

    val doubleClickAction by BehaviorSettingsStore.getDoubleClickAction(ctx)
        .collectAsState(initial = null)



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
    val nests by SwipeSettingsStore.getNestsFlow(ctx).collectAsState(emptyList())


    val nestNavigation = rememberNestNavigation(nests)
    val nestId = nestNavigation.nestId


    fun launchAction(point: SwipePointSerializable?) {
        isDragging = false
        nestNavigation.goToNest(0)
        start = null
        current = null
        lastClickTime = 0

        launchSwipeAction(
            ctx = ctx,
            action = point?.action,
            useAccessibilityInsteadOfContextToExpandActionPanel = useAccessibilityInsteadOfContextToExpandActionPanel,
            onReloadApps = { scope.launch { appsViewModel.reloadApps(ctx) } },
            onAppSettings = onLongPress3Sec,
            onAppDrawer = onAppDrawer,
            onOpenNestCircle = { nestNavigation.goToNest(it) },
            onParentNest = { nestNavigation.goBack() }
        )
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
            nestId = nestId,
            isDragging = isDragging,
            surface = size,
            points = points,
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
}
