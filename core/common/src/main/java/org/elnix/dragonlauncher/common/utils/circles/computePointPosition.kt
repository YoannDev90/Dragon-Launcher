package org.elnix.dragonlauncher.common.utils.circles

import androidx.compose.ui.geometry.Offset
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.UiCircle
import kotlin.math.cos
import kotlin.math.sin

fun computePointPosition(
    point: SwipePointSerializable,
    circles: List<UiCircle>,
    center: Offset
): Offset {
    // Find the circle this point belongs to
    val circle = circles.find { it.id == point.circleNumber } ?: return center

    // Convert angleDeg to radians and compute the Offset
    val angleRad = Math.toRadians(point.angleDeg)
    return Offset(
        x = center.x + circle.radius * sin(angleRad).toFloat(),
        y = center.y - circle.radius * cos(angleRad).toFloat()
    )
}
