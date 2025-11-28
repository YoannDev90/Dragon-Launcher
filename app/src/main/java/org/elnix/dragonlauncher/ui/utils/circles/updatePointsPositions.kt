package org.elnix.dragonlauncher.ui.utils.circles

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import org.elnix.dragonlauncher.data.UiCircle
import org.elnix.dragonlauncher.data.UiSwipePoint
import kotlin.collections.find
import kotlin.math.atan2

fun updatePointPosition(
    point: UiSwipePoint,
    circles: SnapshotStateList<UiCircle>,
    center: Offset,
    pos: Offset
) {
    val closest = findClosestCircle(circles, center, pos) ?: return
    val dx = pos.x - center.x
    val dy = center.y - pos.y
    var angle = Math.toDegrees(atan2(dx.toDouble(), dy.toDouble()))
    if (angle < 0) angle += 360.0

    point.angleDeg = angle

    if (point.circleNumber != closest.id) {
        val oldCircle = circles.find { it.id == point.circleNumber }
        oldCircle?.points?.removeIf { it.id == point.id }
        closest.points.add(point)
        point.circleNumber = closest.id
    }
}
