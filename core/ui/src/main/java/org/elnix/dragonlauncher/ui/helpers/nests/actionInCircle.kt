package org.elnix.dragonlauncher.ui.helpers.nests

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.common.utils.ImageUtils.loadDrawableResAsBitmap
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.ui.theme.ExtraColors


fun DrawScope.actionsInCircle(
    selected: Boolean,
    point: SwipePointSerializable,
    nests: List<CircleNest>,
    points: List<SwipePointSerializable>,
    center: Offset,
    ctx: Context,
    circleColor: Color,
    showCircle: Boolean,
    surfaceColorDraw: Color,
    extraColors: ExtraColors,
    pointIcons: Map<String, ImageBitmap>,
    defaultPoint: SwipePointSerializable,
    deepNest: Int,
    preventBgErasing: Boolean = false
) {
    val action = point.action

    val px = center.x
    val py = center.y

    val size = point.size ?: defaultPoint.size ?: defaultSwipePointsValues.size!!
    val innerPadding =
        point.innerPadding ?: defaultPoint.innerPadding ?: defaultSwipePointsValues.innerPadding!!

    val iconSize = size / deepNest
    val borderRadii = ((size / 2 + innerPadding).coerceAtLeast(0) / deepNest).toFloat()

    val dstOffset = IntOffset(px.toInt() - iconSize / 2, py.toInt() - iconSize / 2)
    val intSize = IntSize(iconSize, iconSize)

    val borderColor = if (selected) {
        point.borderColorSelected?.let { Color(it) }
            ?: defaultPoint.borderColorSelected?.let { Color(it) }
    } else {
        point.borderColor?.let { Color(it) } ?: defaultPoint.borderColor?.let { Color(it) }
    } ?: circleColor

    val borderStroke = if (selected) {
        point.borderStrokeSelected ?: defaultPoint.borderStrokeSelected ?: 8f
    } else {
        point.borderStroke ?: defaultPoint.borderStroke ?: 4f
    }


    val backgroundColor = if (selected) {
        point.backgroundColorSelected?.let { Color(it) }
            ?: defaultPoint.backgroundColorSelected?.let { Color(it) }
    } else {
        point.backgroundColor?.let { Color(it) } ?: defaultPoint.backgroundColor?.let { Color(it) }
    } ?: if (preventBgErasing) {
        surfaceColorDraw
    } else {
        Color.Transparent
    }

    // if in the settings, set the alpha
//    if (preventBgErasing) backgroundColor = backgroundColor.copy(1f)

    if (action !is SwipeActionSerializable.OpenCircleNest) {
        // if no background color provided, erases the background
        val eraseBg = backgroundColor == Color.Transparent && !preventBgErasing

        // Erases the color, instead of putting it, that lets the wallpaper pass trough
        if (eraseBg) {
            drawCircle(
                color = Color.Transparent,
                radius = borderRadii,
                center = center,
                blendMode = BlendMode.Clear
            )
        } else {
            drawCircle(
                color = backgroundColor,
                radius = borderRadii,
                center = center
            )
        }

        if (borderColor.alpha != 0f && borderStroke > 0f) {
            drawCircle(
                color = borderColor,
                radius = borderRadii,
                center = center,
                style = Stroke(borderStroke)
            )
        }


        val icon = point.id.let { pointIcons[it] }

        if (icon != null) {
            drawImage(
                image = icon,
                dstOffset = dstOffset,
                dstSize = intSize
            )
        }

    } else {
        nests.find { it.id == action.nestId }?.let { nest ->


            val circlesWidthIncrement = 1f / (nest.dragDistances.size - 1)

            val newCircles: SnapshotStateList<UiCircle> = mutableStateListOf()


            nest.dragDistances.filter { it.key != -1 }
                .forEach { (index, _) ->
                    val radius = (100f / deepNest) * circlesWidthIncrement * (index + 1)
                    newCircles.add(
                        UiCircle(index, radius)
                    )
                }

            if (deepNest < 3) {
                circlesSettingsOverlay(
                    circles = newCircles,
                    circleColor = circleColor,
                    showCircle = showCircle,
                    center = center,
                    points = points,
                    defaultPoint = defaultPoint,
                    selectedPoint = point,
                    backgroundColor = backgroundColor,
                    nests = nests,
                    ctx = ctx,
                    extraColors = extraColors,
                    pointIcons = pointIcons,
                    nestId = nest.id,
                    deepNest = deepNest + 1,
                    selectedAll = selected,
                    preventBgErasing = preventBgErasing
                )
            } else {
                // Draw a placeholder for a sub nest, cause otherwise it'll eat all the phone's resources by trying to draw infinite sub nests

                // Action is OpenCirclesNext (draws small the circleNests)
                newCircles.forEach { (_, radius) ->
                    drawCircle(
                        color = extraColors.circle,
                        radius = radius,
                        center = center,
                        style = Stroke(if (selected) 8f else 4f)
                    )
                }
            }

        } ?: drawImage(
            image = loadDrawableResAsBitmap(ctx, R.drawable.ic_app_default, 48, 48),
            dstOffset = dstOffset,
            dstSize = intSize
        )
    }
}
