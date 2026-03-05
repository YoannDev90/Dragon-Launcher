package org.elnix.dragonlauncher.ui.helpers.customobjects

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.elnix.dragonlauncher.common.serializables.CustomObjectSerializable
import org.elnix.dragonlauncher.common.utils.resolveShape
import org.elnix.dragonlauncher.ui.helpers.nests.glowOverlay

fun DrawScope.customObject(
    customObject: CustomObjectSerializable,
    default: CustomObjectSerializable,
    angleColor: Color,
    center: Offset,
) {
    val size = customObject.size ?: default.size!!

    val shape = customObject.shape?.resolveShape() ?: CircleShape
    val baseSize = Size(size, size)

    // Apply glow first (background effect)
    customObject.glow?.let { glow ->
        val glowRadius = glow.radius ?: default.glow!!.radius!!
        if (glowRadius > 0f) {
            glowOverlay(
                center = center,
                color = glow.color ?: angleColor,
                radius = glowRadius
            )
        }
    }

    // Main shape
    val shapeColor = customObject.color ?: angleColor
    val shapeStoke = customObject.stroke ?: default.stroke!!

    // Not zero size
    if (baseSize.width > 0 ) {
        drawShapeWithColor(
            shape = shape,
            center = center,
            size = baseSize,
            color = shapeColor,
            strokeWidth = shapeStoke,
            erase = customObject.eraseBackground ?: default.eraseBackground!!
        )
    }
}
