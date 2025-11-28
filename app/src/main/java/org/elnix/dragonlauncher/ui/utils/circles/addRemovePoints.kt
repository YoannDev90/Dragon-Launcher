package org.elnix.dragonlauncher.ui.utils.circles

import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.data.UiCircle
import org.elnix.dragonlauncher.data.UiSwipePoint
import java.util.UUID

fun addPoint(circle: UiCircle, action: SwipeActionSerializable?) {
    circle.points.add(
        UiSwipePoint(
            id = UUID.randomUUID().toString(),
            angleDeg = randomFreeAngle(circle.points),
            action = action,
            circleNumber = circle.id
        )
    )
    autoSeparate(circle.points,circle.id)
}

fun removePoint(circle: UiCircle, point: UiSwipePoint) {
    circle.points.remove(point)
}
