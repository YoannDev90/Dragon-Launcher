package org.elnix.dragonlauncher.common.utils.circles

import androidx.compose.ui.geometry.Offset
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.UiCircle
import kotlin.math.cos
import kotlin.math.sin


// TODO Add hoover position calculation, and put that in another viewmodel, to reuse the points positions
// TODO With that, I'll be able to perform on hoover operations, such as moving points to a nest


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
