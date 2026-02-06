package org.elnix.dragonlauncher.ui.helpers.nests

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.serializables.applyColorAction
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.common.serializables.parent
import org.elnix.dragonlauncher.common.utils.ImageUtils
import org.elnix.dragonlauncher.common.utils.ImageUtils.loadDrawableResAsBitmap
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.ui.actions.actionColor
import org.elnix.dragonlauncher.ui.components.resolveShape
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
    depth: Int,
    iconShape: IconShape,
    density: Density,
    preventBgErasing: Boolean = false
) {
    val action = point.action

    val px = center.x
    val py = center.y

    val size = point.size ?: defaultPoint.size ?: defaultSwipePointsValues.size!!
    val innerPadding =
        point.innerPadding ?: defaultPoint.innerPadding ?: defaultSwipePointsValues.innerPadding!!

    val iconSize = size / depth
    val borderRadii = ((size / 2 + innerPadding).coerceAtLeast(0) / depth).toFloat()

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

    val borderIconShape = if (selected) {
        point.borderShapeSelected ?: defaultPoint.borderShapeSelected
    } else {
        point.borderShape ?: defaultPoint.borderShape
    } ?: IconShape.Circle




    val borderShape = resolveShape(borderIconShape)

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

    val shape = resolveShape(point.customIcon?.shape ?: iconShape)

    // if in the settings, set the alpha
//    if (preventBgErasing) backgroundColor = backgroundColor.copy(1f)

    if (action !is SwipeActionSerializable.OpenCircleNest) {


        val iconSizeF = borderRadii * 2f


        val outline = borderShape.createOutline(
            size = Size(iconSizeF, iconSizeF),
            layoutDirection = layoutDirection,
            density = this
        )

        val path = when (outline) {
            is Outline.Rectangle -> Path().apply { addRect(outline.rect) }
            is Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
            is Outline.Generic -> outline.path
        }

        // if no background color provided, erases the background
        val eraseBg = backgroundColor == Color.Transparent && !preventBgErasing


//        drawCircle(
//            color = Color.Red,
//            center = center,
//            radius = 8f
//        )
        // Move drawing to icon position
        translate(
            left = center.x - borderRadii,
            top = center.y - borderRadii
        ) {


            if (eraseBg) {
                // Erases the color, instead of putting it, that lets the wallpaper pass trough
                drawPath(
                    path = path,
                    color = borderColor,
                    style = Stroke(width = borderStroke),
                    blendMode = BlendMode.Clear
                )
            } else {
                drawPath(
                    path = path,
                    color = backgroundColor,
                    style = Stroke(width = borderStroke)
                )
            }
            if (borderColor.alpha != 0f && borderStroke > 0f) {

                drawPath(
                    path = path,
                    color = borderColor,
                    style = Stroke(width = borderStroke)

                )
            }
        }


        val icon = point.id.let { pointIcons[it] }
        val colorAction = actionColor(point.action, extraColors)



        if (icon != null) {

            val clipped = ImageUtils.clipImageToShape(
                image = icon,
                shape = shape,
                sizePx = size,
                density = density
            )

            drawImage(
                image = clipped,
                dstOffset = dstOffset,
                dstSize = intSize,
                colorFilter =
                    if (point.applyColorAction()) ColorFilter.tint(colorAction)
                    else null
            )
        }

    } else {
        nests.find { it.id == action.nestId }?.let { nest ->


            val circlesWidthIncrement = 1f / (nest.dragDistances.size - 1)

            val newCircles: SnapshotStateList<UiCircle> = mutableStateListOf()


            nest.dragDistances.filter { it.key != -1 }.forEach { (index, _) ->
                val radius = (100f / depth) * circlesWidthIncrement * (index + 1)
                newCircles.add(
                    UiCircle(index, radius)
                )
            }

            if (depth < nest.parent(nests).depth) {
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
                    depth = depth + 1,
                    shape = iconShape,
                    density = density,
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
