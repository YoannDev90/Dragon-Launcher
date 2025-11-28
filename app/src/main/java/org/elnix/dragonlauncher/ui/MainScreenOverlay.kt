package org.elnix.dragonlauncher.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import androidx.compose.ui.text.font.FontWeight
import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.data.SwipePointSerializable
import org.elnix.dragonlauncher.data.datastore.SettingsStore
import kotlin.math.hypot

@Composable
fun MainScreenOverlay(
    start: Offset?,
    current: Offset?,
    isDragging: Boolean,
    surface: IntSize,
    points: List<SwipePointSerializable>,
    onLaunch: (SwipeActionSerializable?) -> Unit
) {
    val ctx = LocalContext.current

    val rgbLine by SettingsStore.getRGBLine(ctx)
        .collectAsState(initial = true)

    val debugInfos by SettingsStore.getDebugInfos(ctx)
        .collectAsState(initial = true)


    val angleLineColor by SettingsStore.getAngleLineColor(ctx)
        .collectAsState(initial = null)


    var lastAngle by remember { mutableStateOf<Double?>(null) }
    var cumulativeAngle by remember { mutableDoubleStateOf(0.0) }   // continuous rotation without jumps


    val dx: Float
    val dy: Float
    val dist: Float
    val angleRad: Double
    val angleDeg: Double
    val angle0to360: Double

    val lineColor: Color

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
                diff > 180  -> diff - 360   // jumped CW past 360→0
                diff < -180 -> diff + 360   // jumped CCW past 0→360
                else -> diff                // normal small movement
            }

            cumulativeAngle += adjustedDiff
        }
        lastAngle = angle0to360

        lineColor = if (angleLineColor != null) angleLineColor!!
                    else if (rgbLine) Color.hsv(angle0to360.toFloat(),1f,1f)
                    else Color.Red
    } else {
        dx = 0f; dy = 0f
        dist = 0f
        angleDeg = 0.0
        angle0to360 = 0.0
        cumulativeAngle = 0.0
        lineColor = Color.Transparent
    }

    val sweepAngle = (cumulativeAngle % 360).toFloat()


    // Launch app logic

    // -- For displaying the banner --
    var hoveredAction by remember { mutableStateOf<SwipeActionSerializable?>(null) }
    var bannerVisible by remember { mutableStateOf(false) }

    // Distance thresholds for 3 circles
    val circleR1 = 90f
    val circleR2 = 180f
    val circleR3 = 260f

    // Safe zone = dragging returns close to origin → cancel
    val cancelZone = 40f

    // The chosen swipe action
    var currentAction: SwipeActionSerializable? by remember { mutableStateOf(null) }

    if (start != null && current != null && isDragging) {

        val targetCircle =
            when {
                dist < circleR1 -> 1
                dist < circleR2 -> 2
                else -> 3
            }

        val closestPoint =
            points.filter { it.circleNumber == targetCircle }
                .minByOrNull {
                    val d = kotlin.math.abs(it.angleDeg - angle0to360)
                    minOf(d, 360 - d)
                }

        currentAction = if (dist > cancelZone) closestPoint?.action else null

        hoveredAction = currentAction
        bannerVisible = currentAction != null
    } else if (!isDragging) {
        bannerVisible = false
    }

    val alpha by animateFloatAsState(
        targetValue = if (bannerVisible) 1f else 0f,
        animationSpec = tween(150)
    )
    val offsetY by animateDpAsState(
        targetValue = if (bannerVisible) 0.dp else (-20).dp,
        animationSpec = tween(150)
    )

    LaunchedEffect(isDragging) {
        if (!isDragging) {
            if (currentAction != null) {
                onLaunch(currentAction)
            }
            hoveredAction = null
            currentAction = null
            bannerVisible = false
        }
    }


    Box(Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            if (debugInfos) {
                Text(
                    text = "start = ${start?.let { "%.1f, %.1f".format(it.x, it.y) } ?: "—"}",
                    color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = "current = ${current?.let { "%.1f, %.1f".format(it.x, it.y) } ?: "—"}",
                    color = Color.White, fontSize = 14.sp)
                Text(
                    text = "dx = %.1f   dy = %.1f".format(dx, dy),
                    color = Color.White, fontSize = 14.sp
                )
                Text(
                    text = "dist = %.1f".format(dist),
                    color = Color.White, fontSize = 14.sp
                )
                Text(
                    text = "angle raw = %.1f°".format(angleDeg),
                    color = Color.White, fontSize = 14.sp
                )
                Text(
                    text = "angle 0–360 = %.1f°".format(angle0to360),
                    color = Color.White, fontSize = 14.sp
                )
                Text(
                    text = "drag = $isDragging, size = ${surface.width}×${surface.height}",
                    color = Color.White, fontSize = 12.sp
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            if (start != null && current != null) {
                val circleRadius = 48f
                drawCircle(
                    color = lineColor,
                    radius = circleRadius,
                    center = start,
                    style = Stroke(width = 3f)
                )

                drawLine(
                    color = lineColor,
                    start = start,
                    end = current,
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = Color.Black,
                    radius = circleRadius - 2,
                    center = start
                )

                drawCircle(
                    color = lineColor,
                    radius = 8f,
                    center = current,
                    style = Fill
                )

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
    if (hoveredAction != null) {
        Box(
            Modifier
                .fillMaxWidth()
                .offset(y = offsetY)
                .padding(top = 20.dp)
                .alpha(alpha),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = hoveredAction.toString(),   // name of action
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

}
