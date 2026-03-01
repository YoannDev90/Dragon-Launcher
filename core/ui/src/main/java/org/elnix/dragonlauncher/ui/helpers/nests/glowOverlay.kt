package org.elnix.dragonlauncher.ui.helpers.nests

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp


fun DrawScope.glowOverlay(
    center: Offset,
    color: Color,
    radius: Int
) {
    val radius = radius.coerceAtLeast(1)
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
