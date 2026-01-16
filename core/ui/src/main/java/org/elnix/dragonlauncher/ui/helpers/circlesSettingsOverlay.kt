package org.elnix.dragonlauncher.ui.helpers

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.ui.theme.ExtraColors
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.circlesSettingsOverlay(
    circles: SnapshotStateList<UiCircle>,
    circleColor: Color,
    center: Offset,
    points: List<SwipePointSerializable>,
    selectedPoint: SwipePointSerializable?,
    backgroundColor: Color,
    nests: List<CircleNest>,
    ctx: Context,
    extraColors: ExtraColors,
    pointIcons: Map<String, ImageBitmap>,
    nestId: Int,
    deepNest: Int,
    selectedAll: Boolean = false
) {
    // 1. Draw all circles
    circles.forEach { circle ->
        drawCircle(
            color = circleColor,
            radius = circle.radius,
            center = center,
            style = Stroke(if (selectedAll) 8f else 4f)
        )


        // 2. Draw all points that belongs to the actual circle, selected last
        points
            .filter { it.circleNumber == circle.id }
            .filter { it.nestId == nestId }
            .sortedBy { it.id == selectedPoint?.id }
            .forEach { p ->

                val newCenter = Offset(
                    x = center.x + circle.radius * sin(Math.toRadians(p.angleDeg)).toFloat(),
                    y = center.y - circle.radius * cos(Math.toRadians(p.angleDeg)).toFloat()
                )

                val displayPoint = p.copy(
                    backgroundColor = p.backgroundColor ?: backgroundColor.toArgb(),
                    backgroundColorSelected = p.backgroundColorSelected
                        ?: backgroundColor.toArgb(),
                )

                actionsInCircle(
                    selected = selectedAll || (p.id == selectedPoint?.id),
                    point = displayPoint,
                    nests = nests,
                    points = points,
                    center = newCenter,
                    ctx = ctx,
                    circleColor = circleColor,
                    extraColors = extraColors,
                    pointIcons = pointIcons,
                    deepNest = deepNest
                )
            }
    }
}
