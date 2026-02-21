package org.elnix.dragonlauncher.ui.helpers.nests

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import org.elnix.dragonlauncher.common.points.SwipeDrawParams
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.common.utils.circles.computePointPosition

fun DrawScope.circlesSettingsOverlay(
    drawParams: SwipeDrawParams,


    center: Offset,
    depth: Int,

    circles: List<UiCircle>,
    selectedPoint: SwipePointSerializable?,
    nestId: Int,
    selectedAll: Boolean = false,
    preventBgErasing: Boolean = false
) {

    val points = drawParams.points
    val surfaceColorDraw = drawParams.surfaceColorDraw
    val extraColors = drawParams.extraColors
    val showCircle = drawParams.showCircle

    // 0. Erases the inner circle
    /* ───────────── Erases the circle in the point ───────────── */

    // if no background color provided, erases the background
    val eraseBg = surfaceColorDraw == Color.Transparent && !preventBgErasing
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
            color = surfaceColorDraw,
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
            .filter {
                it.circleNumber == circle.id &&
                it.nestId == nestId
            }
            .sortedBy { it.id == selectedPoint?.id }
            .forEach { p ->

                val newCenter = computePointPosition(
                    point = p,
                    circles = circles,
                    center = center
                )

                actionsInCircle(
                    drawParams = drawParams,

                    center = newCenter,
                    depth = depth,
                    point = p,
                    selected = selectedAll || (p.id == selectedPoint?.id),
                    preventBgErasing = preventBgErasing
                )
            }
    }
}
