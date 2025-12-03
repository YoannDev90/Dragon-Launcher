package org.elnix.dragonlauncher.utils.circles

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import org.elnix.dragonlauncher.data.UiCircle
import org.elnix.dragonlauncher.data.UiSwipePoint
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.round

private const val SNAP_STEP_DEG = 15.0   // change to 30.0, 10.0, 5.0 etc.

fun updatePointPosition(
    point: UiSwipePoint,
    circles: SnapshotStateList<UiCircle>,
    center: Offset,
    pos: Offset,
    snap: Boolean
) {
    // 1. Compute raw angle from center -> pos
    val dx = pos.x - center.x
    val dy = center.y - pos.y
    var angle = Math.toDegrees(atan2(dx.toDouble(), dy.toDouble()))
    if (angle < 0) angle += 360.0

    // 2. Apply snapping if enabled
    val finalAngle = if (snap) {
        round(angle / SNAP_STEP_DEG) * SNAP_STEP_DEG
    } else {
        angle
    }

    point.angleDeg = finalAngle

    // 3. Find nearest circle based on radius
    val distFromCenter = hypot(dx, dy)
    val closest = circles.minByOrNull { c -> abs(c.radius - distFromCenter) }
        ?: return

    // 4. Reassign to new circle if needed
    if (point.circleNumber != closest.id) {
        val oldCircle = circles.find { it.id == point.circleNumber }
        oldCircle?.points?.removeIf { it.id == point.id }

        closest.points.add(point)
        point.circleNumber = closest.id
    }
}
