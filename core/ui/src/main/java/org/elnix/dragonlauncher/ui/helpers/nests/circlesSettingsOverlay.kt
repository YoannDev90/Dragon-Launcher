package org.elnix.dragonlauncher.ui.helpers.nests

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.ui.theme.ExtraColors
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.circlesSettingsOverlay(
    circles: SnapshotStateList<UiCircle>,
    circleColor: Color,
    showCircle: Boolean,
    center: Offset,
    points: List<SwipePointSerializable>,
    defaultPoint: SwipePointSerializable,
    selectedPoint: SwipePointSerializable?,
    backgroundColor: Color,
    nests: List<CircleNest>,
    ctx: Context,
    extraColors: ExtraColors,
    pointIcons: Map<String, ImageBitmap>,
    nestId: Int,
    deepNest: Int,
    shape: IconShape,
    density: Density,
    selectedAll: Boolean = false,
    preventBgErasing: Boolean = false
) {
    // 1. Draw all circles
    circles.forEach { circle ->
        if (showCircle) {
            drawCircle(
                color = circleColor,
                radius = circle.radius,
                center = center,
                style = Stroke(if (selectedAll) 8f else 4f)
            )
        }


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


                actionsInCircle(
                    selected = selectedAll || (p.id == selectedPoint?.id),
                    point = p,
                    nests = nests,
                    points = points,
                    center = newCenter,
                    ctx = ctx,
                    circleColor = circleColor,
                    showCircle = showCircle,
                    surfaceColorDraw = backgroundColor,
                    extraColors = extraColors,
                    pointIcons = pointIcons,
                    defaultPoint = defaultPoint,
                    deepNest = deepNest,
                    iconShape = shape,
                    density = density,
                    preventBgErasing = preventBgErasing
                )
            }
    }
}
