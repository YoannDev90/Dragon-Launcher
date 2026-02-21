@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.vibrate
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.components.AppPreviewTitle
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.helpers.nests.actionsInCircle
import org.elnix.dragonlauncher.ui.helpers.nests.rememberSwipeDefaultParams
import org.elnix.dragonlauncher.ui.remembers.LocalNests
import org.elnix.dragonlauncher.ui.remembers.LocalPoints
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin


@Composable
fun MainScreenOverlay(
    start: Offset?,
    current: Offset?,
    nestId: Int,
    isDragging: Boolean,
    surface: IntSize,
    onLaunch: (SwipePointSerializable) -> Unit
) {
    val ctx = LocalContext.current
    val nests = LocalNests.current
    val points = LocalPoints.current
    val extraColors = LocalExtraColors.current

    val rgbLine by UiSettingsStore.rgbLine.asState()
    val debugInfos by DebugSettingsStore.debugInfos.asState()

    val showLaunchingAppLabel by UiSettingsStore.showLaunchingAppLabel.asState()
    val showLaunchingAppIcon by UiSettingsStore.showLaunchingAppIcon.asState()

    val showAppLaunchPreview by UiSettingsStore.showAppLaunchingPreview.asState()
    val showAppCirclePreview by UiSettingsStore.showCirclePreview.asState()
    val showAppLinePreview by UiSettingsStore.showLinePreview.asState()
    val showAppAnglePreview by UiSettingsStore.showAnglePreview.asState()
    val showAppPreviewIconCenterStartPosition by UiSettingsStore.showAppPreviewIconCenterStartPosition.asState()
    val linePreviewSnapToAction by UiSettingsStore.linePreviewSnapToAction.asState()
    val showAllActionsOnCurrentCircle by UiSettingsStore.showAllActionsOnCurrentCircle.asState()
    val appLabelIconOverlayTopPadding by UiSettingsStore.appLabelIconOverlayTopPadding.asState()
    val appLabelOverlaySize by UiSettingsStore.appLabelOverlaySize.asState()
    val appIconOverlaySize by UiSettingsStore.appIconOverlaySize.asState()
    val disableHapticFeedback by BehaviorSettingsStore.disableHapticFeedbackGlobally.asState()
    val pointsActionSnapsToOuterCircle by BehaviorSettingsStore.pointsActionSnapsToOuterCircle.asState()


    var lastAngle by remember { mutableStateOf<Double?>(null) }
    var cumulativeAngle by remember { mutableDoubleStateOf(0.0) }   // continuous rotation without jumps


    val dragRadii = nests.find { it.id == nestId }?.dragDistances ?: CircleNest().dragDistances
    val haptics = nests.find { it.id == nestId }?.haptic ?: CircleNest().haptic
    val minAngles =
        nests.find { it.id == nestId }?.minAngleActivation ?: CircleNest().minAngleActivation

    val dx: Float
    val dy: Float
    val dist: Float
    val angleRad: Double
    val angleDeg: Double
    val angle0to360: Double

    val lineColor: Color
    val circleRadius = 48f

    if (start != null && current != null) {
        dx = current.x - start.x
        dy = current.y - start.y
        dist = hypot(dx, dy)

        // angle relative to UP = 0°
        angleRad = atan2(dx.toDouble(), -dy.toDouble())
        angleDeg = Math.toDegrees(angleRad)
        angle0to360 = if (angleDeg < 0) angleDeg + 360 else angleDeg

        // --- smooth 360° tracking ---
        lastAngle?.let { prev ->
            val diff = angle0to360 - prev

            val adjustedDiff = when {
                diff > 180 -> diff - 360   // jumped CW past 360→0
                diff < -180 -> diff + 360   // jumped CCW past 0→360
                else -> diff                // normal small movement
            }

            cumulativeAngle += adjustedDiff
        }
        @Suppress("AssignedValueIsNeverRead")
        lastAngle = angle0to360


        lineColor = if (rgbLine) Color.hsv(angle0to360.toFloat(), 1f, 1f)
        else extraColors.angleLine

    } else {
        dx = 0f; dy = 0f
        dist = 0f
        angleDeg = 0.0
        angle0to360 = 0.0
        cumulativeAngle = 0.0
        lineColor = Color.Transparent
    }

    val sweepAngle = (cumulativeAngle % 360).toFloat()

    var exposedClosest by remember { mutableStateOf<SwipePointSerializable?>(null) }
    var exposedAsbAngle by remember { mutableStateOf<Double?>(null) }


    // ───────────── For displaying the banner ─────────────
    var hoveredPoint by remember { mutableStateOf<SwipePointSerializable?>(null) }
    var bannerVisible by remember { mutableStateOf(false) }


    // The chosen swipe action
    var currentAction: SwipePointSerializable? by remember { mutableStateOf(null) }


    // Computes the target circle based on the mode selected
    val targetCircle = if (pointsActionSnapsToOuterCircle) {
        var best: Map.Entry<Int, Int>? = null

        for (entry in dragRadii) {
            if (dist <= entry.value) {
                if (best == null || entry.value < best.value) {
                    best = entry
                }
            }
        }

        best?.key ?: dragRadii.maxByOrNull { it.value }!!.key
    } else {
        var best: Map.Entry<Int, Int>? = null

        for (entry in dragRadii) {
            if (dist >= entry.value) {
                if (best == null || entry.value > best.value) {
                    best = entry
                }
            }
        }

        best?.key ?: dragRadii.minByOrNull { it.value }!!.key
    }



    if (start != null && current != null && isDragging) {

        val closestPoint =
            points.filter { it.nestId == nestId && it.circleNumber == targetCircle }
                .minByOrNull {
                    val d = abs(it.angleDeg - angle0to360)
                    minOf(d, 360 - d)
                }

        exposedClosest = closestPoint

        val selectedPoint = closestPoint?.let { p ->
            val d = abs(p.angleDeg - angle0to360)
            val shortest = minOf(d, 360 - d)
            exposedAsbAngle = shortest

            // if not provided, uses infinite angle, aka no limit
            val minAngleTargetCircle = minAngles[targetCircle] ?: 0

            // If minAngle == 0 -> no limit, always accept closest
            if (minAngleTargetCircle == 0 ||
                shortest <= minAngleTargetCircle
            ) {
                p
            } else {
                null
            }
        }

        currentAction = selectedPoint
//        currentAction = if (dist > dragRadii[0]) selectedPoint else null

        hoveredPoint = currentAction
        bannerVisible = currentAction != null
    } else if (!isDragging) {
        bannerVisible = false
        exposedClosest = null
    }

    val alpha by animateFloatAsState(
        targetValue = if (bannerVisible) 1f else 0f,
        animationSpec = tween(150)
    )
    val offsetY by animateDpAsState(
        targetValue = if (bannerVisible) 0.dp else (-20).dp,
        animationSpec = tween(150)
    )

    LaunchedEffect(hoveredPoint?.id) {
        hoveredPoint?.let { point ->
            (point.haptic ?: haptics[targetCircle]
            ?: defaultHapticFeedback(targetCircle)).let { milliseconds ->
                if (milliseconds > 0 && !disableHapticFeedback) {
                    vibrate(ctx, milliseconds.toLong())
                }
            }
        }
    }

    LaunchedEffect(isDragging) {
        if (!isDragging) {
            if (currentAction != null) {
                onLaunch(currentAction!!)
            }
            hoveredPoint = null
            currentAction = null
            bannerVisible = false
        }
    }


    Box(Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            if (debugInfos) {
                Text(
                    text = "start = ${start?.let { "%.1f, %.1f".format(it.x, it.y) } ?: "—"}",
                    color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium
                )
                Text(
                    text = "current = ${current?.let { "%.1f, %.1f".format(it.x, it.y) } ?: "—"}",
                    color = Color.White, fontSize = 12.sp)
                Text(
                    text = "dx = %.1f   dy = %.1f".format(dx, dy),
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "dist = %.1f".format(dist),
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "angle raw = %.1f°".format(angleDeg),
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "angle 0–360 = %.1f°".format(angle0to360),
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "closest point angle = ${exposedClosest?.angleDeg ?: "—"}",
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "asb angle to closest point= $exposedAsbAngle",
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "drag = $isDragging, size = ${surface.width}×${surface.height}",
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "target circle = $targetCircle",
                    color = Color.White, fontSize = 12.sp
                )
                Text(
                    text = "current action = $currentAction",
                    color = Color.White, fontSize = 12.sp
                )
            }
        }

//        val colorAction = if (hoveredPoint != null) actionColor(hoveredPoint!!.action, extraColors) else Color.Unspecified


        val drawParams = rememberSwipeDefaultParams()
        // Main drawing canva (the lines, circles and selected actions
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { // I use that to let the action in circle remove the background, otherwise it doesn't work
                    compositingStrategy = CompositingStrategy.Offscreen
                }
        ) {

            // Draw only if the user is dragging (has a start pos and a end (current) Offsets
            if (start != null && current != null) {


                // The line that goes from start dragging pos to the user's finger
                if (showAppLinePreview) {
                    drawCircle(
                        color = lineColor,
                        radius = circleRadius,
                        center = start,
                        style = Stroke(width = 3f)
                    )

                    if (!(linePreviewSnapToAction && hoveredPoint != null)) {
                        actionLine(
                            drawScope = this,
                            start = start,
                            end = current,
                            radius = circleRadius,
                            color = lineColor
                        )
                    }
                }

                if (showAppCirclePreview || showAppLinePreview || showAppLaunchPreview) {
                    hoveredPoint?.let { point ->

                        // same circle radii as SettingsScreen
                        val radius = dragRadii[targetCircle]!!.toFloat()
                        // Main circle (the selected) drawn before any apps to be behind
                        if (showAppCirclePreview) {
                            drawCircle(
                                color = extraColors.circle,
                                radius = radius,
                                center = start,
                                style = Stroke(4f)
                            )
                        }


                        // compute point position relative to origin
                        val end = Offset(
                            x = start.x + radius * sin(Math.toRadians(point.angleDeg)).toFloat(),
                            y = start.y - radius * cos(Math.toRadians(point.angleDeg)).toFloat()
                        )

                        // If the user selected that the line has to snap to action, it is drawn here and not above
                        if (linePreviewSnapToAction) {
                            actionLine(
                                drawScope = this,
                                start = start,
                                end = end,
                                radius = circleRadius,
                                color = lineColor
                            )
                        }


                        // if you choose to draw every actions, they are drawn here, excepted for
                        // the selected one, that is always drawn last to prevent overlapping issues,
                        // even though it shouldn't happened due to my separatePoints functions
                        if (showAllActionsOnCurrentCircle) {
                            points.filter { it.nestId == nestId && it.circleNumber == targetCircle && it != point }
                                .forEach { p ->
                                    val localCenter = Offset(
                                        x = start.x + radius * sin(Math.toRadians(p.angleDeg)).toFloat(),
                                        y = start.y - radius * cos(Math.toRadians(p.angleDeg)).toFloat()
                                    )
                                    actionsInCircle(
                                        selected = false,
                                        point = p,
                                        drawParams = drawParams,
                                        center = localCenter,
                                        depth = 1
                                    )
                                }
                        }

                        // Draw here the actual selected action (if requested)
                        if (showAppLaunchPreview) {
                            actionsInCircle(
                                selected = true,
                                point = point,
                                drawParams = drawParams,
                                center = end,
                                depth = 1
                            )
                        }
                    }
                }


                // Show the current selected app in the center of the circle (start pos)
                if (showAppPreviewIconCenterStartPosition && hoveredPoint != null) {
                    val currentPoint = hoveredPoint!!

                    actionsInCircle(
                        selected = true,
                        point = currentPoint,
                        drawParams = drawParams,
                        center = start,
                        depth = 1
                    )
                }


                // The angle rotating around the start point (have to fix that and allow more customization) TODO
                // Draws last to display over the sub nests
                // The "do you hate it" thing in settings
                if (showAppAnglePreview) {
                    val arcRadius = 72f
                    val rect = Rect(
                        start.x - arcRadius,
                        start.y - arcRadius,
                        start.x + arcRadius,
                        start.y + arcRadius
                    )

                    drawArc(
                        color = lineColor,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = Size(rect.width, rect.height),
                        style = Stroke(width = 3f)
                    )
                }
            }
        }
    }


    // Label on top of the screen to indicate the launching app
    if (hoveredPoint != null && (showLaunchingAppLabel || showLaunchingAppIcon)) {
        val currentPoint = hoveredPoint!!
        AppPreviewTitle(
            offsetY = offsetY,
            alpha = alpha,
            point = currentPoint,
            topPadding = appLabelIconOverlayTopPadding.dp,
            labelSize = appLabelOverlaySize,
            iconSize = appIconOverlaySize,
            showLabel = showLaunchingAppLabel,
            showIcon = showLaunchingAppIcon
        )
    }
}


private fun actionLine(
    drawScope: DrawScope,
    start: Offset,
    end: Offset,
    radius: Float,
    color: Color,
) {
    // Draw the main line from start to end
    drawScope.drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )

    // Erases the color, instead of putting it, that lets the wallpaper pass trough
    drawScope.drawCircle(
        color = Color.Transparent,
        radius = radius - 2,
        center = start,
        blendMode = BlendMode.Clear
    )

    // Small circle at the end of the trail
    drawScope.drawCircle(
        color = color,
        radius = 8f,
        center = end,
        style = Fill
    )
}


fun defaultHapticFeedback(id: Int): Int = when (id) {
    -1 -> 5 // Cancel Zone, small feedback
    0 -> 20  // First circle 20ms
    else -> 20 + 20 * id // others: add 20ms each
}
