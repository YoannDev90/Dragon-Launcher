package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Hold to activate arc
 * The loading circle RGB that shows when holding for 0.5 to 3 sec on main screen,
 * used to display settings loading progress,
 * It is hard coded in the app, to avoid people blocking them from accessing settings.
 * You won't ever have any ways to disable it I guess cause otherwise it could be impossible
 * to access app settings by another way if not set
 *
 * @param center where to draw it
 * @param progress from 0f to 1f, how much it has loading
 * @param radius
 * @param stroke
 * @param rgbLoading whether to display it as rbg HSV gradient during progress or just using the default color
 * @param defaultColor default color that draws if [rgbLoading] is disabled
 */
@Composable
fun HoldToActivateArc(
    center: Offset?,
    progress: Float,     // 0f..1f
    radius: Int,
    stroke: Int,
    rgbLoading: Boolean,
    defaultColor: Color,
    showHoldTolerance: (() -> Float)? = null
) {
    if (center == null || progress <= 0f) return

     val color =
         if (rgbLoading) Color.hsv(progress * 360f, 1f, 1f)
         else defaultColor

    Canvas(modifier = Modifier.fillMaxSize()) {
        val radius = radius.dp.toPx()
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = stroke.dp.toPx(), cap = StrokeCap.Round)
        )

        showHoldTolerance?.let {
            holdTolerance(center, it())
        }
    }
}


private fun DrawScope.holdTolerance(
    center: Offset,
    tolerance: Float
) {
    drawCircle(
        color = Color.Cyan,
        center = center,
        radius = tolerance,
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )
}
