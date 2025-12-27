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
import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.utils.actions.actionIconBitmap


fun actionsInCircle(
    drawScope: DrawScope,
    action: SwipeActionSerializable,
    px: Float,
    py: Float,
    ctx: Context,
    circleColor: Color,
    colorAction: Color,
    backgroundColor: Color? = null,
    drawBorder: Boolean,
    icons: Map<String, ImageBitmap>
) {

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

    if (drawBorder) {
        drawScope.drawCircle(
            color = circleColor,
            radius = 44f,
            center = Offset(px, py),
            style = Stroke(4f)
        )
    }


    drawScope.drawImage(
        image = actionIconBitmap(
            icons = icons,
            action = action,
            context = ctx,
            tintColor = colorAction
        ),
        dstOffset = IntOffset(px.toInt() - 28, py.toInt() - 28),
        dstSize = IntSize(56, 56)
    )
}
