package org.elnix.dragonlauncher.ui.helpers

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.ImageUtils.loadDrawableResAsBitmap
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.ui.actions.actionColor
import org.elnix.dragonlauncher.ui.theme.ExtraColors


fun DrawScope.actionsInCircle(
    selected: Boolean,
    point: SwipePointSerializable,
//    circles: SnapshotStateList<UiCircle>,
    nests: List<CircleNest>,
    points: List<SwipePointSerializable>,
    center: Offset,
    ctx: Context,
    circleColor: Color,
    extraColors: ExtraColors,
    pointIcons: Map<String, ImageBitmap>,
    deepNest: Int,
    preventBgErasing: Boolean = false
) {
    val action = point.action

    val px = center.x
    val py = center.y

    val iconSize = 56 / deepNest

    val dstOffset = IntOffset(px.toInt() - iconSize/2, py.toInt() - iconSize/2)
    val intSize = IntSize(iconSize, iconSize)

    val colorAction =  actionColor(point.action, extraColors)

    val borderColor = if (selected) {
        point.borderColorSelected?.let { Color(it) }
    } else {
        point.borderColor?.let { Color(it) }
    } ?: circleColor

    val borderStroke = if (selected) {
        point.borderStrokeSelected ?: 8f
    } else {
        point.borderStroke ?: 4f
    }


    val backgroundColor = if (selected) {
        point.backgroundColorSelected?.let { Color(it) }
    } else {
        point.backgroundColor?.let { Color(it) }
    } ?: Color.Transparent

    if (action !is SwipeActionSerializable.OpenCircleNest) {
        // if no background color provided, erases the background
        val eraseBg = backgroundColor == Color.Transparent && !preventBgErasing

        // Erases the color, instead of putting it, that lets the wallpaper pass trough
        if (eraseBg) {
            drawCircle(
                color = Color.Transparent,
                radius = 44f,
                center = center,
                blendMode = BlendMode.Clear
            )
        } else
            drawCircle(
                color = backgroundColor,
                radius = 44f,
                center = center
            )

            if (borderColor != Color.Transparent && borderStroke > 0f) {
                drawCircle(
                    color =  borderColor,
                    radius = 44f,
                    center = center,
                    style = Stroke(borderStroke)
                )
            }


        val icon = point.id.let { pointIcons[it] }

        if (icon != null) {
            drawImage(
                image = icon,
                dstOffset = dstOffset,
                dstSize = intSize,
                colorFilter = if (
                    action !is SwipeActionSerializable.LaunchApp &&
                    action !is SwipeActionSerializable.LaunchShortcut &&
                    action !is SwipeActionSerializable.OpenDragonLauncherSettings
                ) ColorFilter.tint(colorAction)
                else null
            )
        }

    } else if ( deepNest < 2 ) {
        nests.find { it.id == action.nestId }?.let { nest ->


            val circlesWidthIncrement = 1f / (nest.dragDistances.size - 1)

            val newCircles: SnapshotStateList<UiCircle> = mutableStateListOf()


             nest.dragDistances.filter { it.key != -1 }
                .forEach { (index, _ ) ->
                    val radius = 100f * circlesWidthIncrement * (index + 1)
                    newCircles.add(
                            UiCircle(index, radius)
                        )
                }

            circlesSettingsOverlay(
                circles = newCircles,
                circleColor = circleColor,
                center = center,
                points = points,
                selectedPoint = point,
                backgroundColor = backgroundColor,
                nests = nests,
                ctx = ctx,
                extraColors = extraColors,
                pointIcons = pointIcons,
                nestId = nest.id,
                deepNest = deepNest+1,
                selectedAll = selected
            )

//            // Action is OpenCirclesNext (draws small the circleNests)
//            nest.dragDistances.filter { it.key != -1 }
//                .forEach { (index, _) ->
//                    val radius = 100f * circlesWidthIncrement * (index + 1)
//                    drawCircle(
//                        color = colorAction,
//                        radius = radius,
//                        center = center,
//                        style = Stroke(if (selected) 8f else 4f)
//                    )
//                }
        } ?: drawImage(
            image = loadDrawableResAsBitmap(ctx, R.drawable.ic_app_default, 48, 48),
            dstOffset = dstOffset,
            dstSize = intSize
        )
    }
}
