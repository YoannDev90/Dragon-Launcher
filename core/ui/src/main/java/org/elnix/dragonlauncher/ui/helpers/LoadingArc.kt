package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun HoldToActivateArc(
    center: Offset?,
    progress: Float,     // 0f..1f
    radius: Float = 200f,
    rgbLoading: Boolean,
    defaultColor: Color
) {
    if (center == null || progress <= 0f) return

     val color =
         if (rgbLoading) Color.hsv(progress * 360f, 1f, 1f)
         else defaultColor

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )
    }
}
