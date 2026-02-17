package org.elnix.dragonlauncher.ui.helpers.nests

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.common.utils.circles.computePointPosition
import org.elnix.dragonlauncher.ui.theme.ExtraColors

fun DrawScope.circlesSettingsOverlay(
    circles: List<UiCircle>,
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
    depth: Int,
    maxDepth: Int,
    shape: IconShape,
    density: Density,
    selectedAll: Boolean = false,
    preventBgErasing: Boolean = false
) {

    // 0. Erases the inner circle
    /* ───────────── Erases the circle in the point ───────────── */

    // if no background color provided, erases the background
    val eraseBg = backgroundColor == Color.Transparent && !preventBgErasing
    val maxCircleSize = circles.maxBy { it.radius }

    // Erases the color, instead of putting it, that lets the wallpaper pass trough
    drawCircle(
        color = Color.Transparent,
        radius = maxCircleSize.radius,
        center = center,
        blendMode = BlendMode.Clear
    )

    // If requested to not erase the bg, draw it (this avoid the more tinted bg when using a half transparent bg color
    if (!eraseBg) {
        drawCircle(
            color = backgroundColor,
            radius = maxCircleSize.radius,
            center = center
        )
    }

    // 1. Draw all circles
    circles.forEach { circle ->
        if (showCircle) {
            drawCircle(
                color = extraColors.circle,
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

                val newCenter = computePointPosition(
                    point = p,
                    circles = circles,
                    center = center
                )

                actionsInCircle(
                    selected = selectedAll || (p.id == selectedPoint?.id),
                    point = p,
                    nests = nests,
                    points = points,
                    center = newCenter,
                    ctx = ctx,
                    showCircle = showCircle,
                    surfaceColorDraw = backgroundColor,
                    extraColors = extraColors,
                    pointIcons = pointIcons,
                    defaultPoint = defaultPoint,
                    depth = depth,
                    maxDepth = maxDepth,
                    iconShape = shape,
                    density = density,
                    preventBgErasing = preventBgErasing
                )
            }
    }
}
