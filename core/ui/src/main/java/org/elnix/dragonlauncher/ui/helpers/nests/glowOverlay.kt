package org.elnix.dragonlauncher.ui.helpers.nests

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp


fun DrawScope.glowOverlay(
    center: Offset,
    color: Color,
    radius: Float
) {
    val radius = radius.coerceAtLeast(1f)
    drawCircle(
        brush = Brush.radialGradient(
            0.0f to color,
            1.0f to Color.Transparent,
            center = center,
            radius = radius.dp.toPx()
            ),
        radius = radius.dp.toPx(),
        center = center
    )
}



fun DrawScope.drawNeonGlowLine(
    start: Offset,
    end: Offset,
    color: Color,
    lineStrokeWidth: Float,
    glowRadius: Float,
    glowColor: Color,
    erase: Boolean
) {
    val strokePx = lineStrokeWidth.dp.toPx()

    // Crashes if 0
    val glowPx = glowRadius.dp.toPx()

    // Glow overlay (behind)
    drawIntoCanvas { canvas ->
        val frameworkPaint = android.graphics.Paint().apply {
            this.color = glowColor.copy(alpha = 0.7f).toArgb()
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = glowPx
            maskFilter = android.graphics.BlurMaskFilter(
                glowPx,
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
            isAntiAlias = true
        }

        canvas.nativeCanvas.drawLine(
            start.x,
            start.y,
            end.x,
            end.y,
            frameworkPaint
        )
    }

    if (erase) {
        drawLine(
            color = Color.Transparent,
            start = start,
            end = end,
            strokeWidth = strokePx,
            cap = StrokeCap.Round,
            blendMode = BlendMode.Clear
        )
    }

    // Sharp center line
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokePx,
        cap = StrokeCap.Round
    )
}
