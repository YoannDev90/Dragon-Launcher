package org.elnix.dragonlauncher.ui.helpers

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.CircleNest
import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.utils.actions.actionIconBitmap
import org.elnix.dragonlauncher.utils.actions.loadDrawableResAsBitmap


fun actionsInCircle(
    selected: Boolean,
    drawScope: DrawScope,
    action: SwipeActionSerializable,
    nests:  List<CircleNest>,
    px: Float,
    py: Float,
    ctx: Context,
    circleColor: Color,
    colorAction: Color,
    backgroundColor: Color? = null,
    drawBorder: Boolean,
    icons: Map<String, ImageBitmap>
) {

    if (action !is SwipeActionSerializable.OpenCircleNest) {
        // if no background color provided, erases the background
        val eraseBg = backgroundColor == null

        // Erases the color, instead of putting it, that lets the wallpaper pass trough
        if (eraseBg) {
            drawScope.drawCircle(
                color = Color.Transparent,
                radius = 44f,
                center = Offset(px, py),
                blendMode = BlendMode.Clear
            )
        } else
            drawScope.drawCircle(
                color = backgroundColor,
                radius = 44f,
                center = Offset(px, py)
            )

        if (drawBorder || selected) {
            drawScope.drawCircle(
                color = circleColor,
                radius = 44f,
                center = Offset(px, py),
                style = Stroke(if (selected) 8f else 4f)
            )
        }


        drawScope.drawImage(
            image = actionIconBitmap(
                icons = icons,
                action = action,
                ctx = ctx,
                tintColor = colorAction,
                width = 56,
                height = 56
            ),
            dstOffset = IntOffset(px.toInt() - 28, py.toInt() - 28),
            dstSize = IntSize(56, 56)
        )
    } else {
        nests.find { it.id == action.nestId }?.let { nest ->


            val circlesWidthIncrement = 1f / (nest.dragDistances.size - 1)


            // Action is OpenCirclesNext (draws small the circleNests)
            nests.find { it.id == action.nestId }!!.dragDistances.filter { it.key != -1 }.forEach { (index, _) ->
                val radius = 100f * circlesWidthIncrement * (index +1)
                drawScope.drawCircle(
                    color = colorAction,
                    radius = radius,
                    center = Offset(px, py),
                    style = Stroke(if (selected) 8f else 4f)
                )
            }
        } ?: drawScope.drawImage(
            image = loadDrawableResAsBitmap(ctx, R.drawable.ic_app_default, 48, 48),
            dstOffset = IntOffset(px.toInt() - 28, py.toInt() - 28),
            dstSize = IntSize(56, 56)
        )
    }
}
