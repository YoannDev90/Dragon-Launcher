package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector


/**
 * Icon c; generic helper to accept both imageVector and painter
 * not very used across project
 *
 * @param icon either [ImageVector] or [Painter]
 * @param contentDescription
 * @param tint how's the icon tinted, use [Color.Unspecified] to avoid tinting
 * @param modifier optional [Modifier]
 */
@Composable
fun IconC(
    icon: Any?,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier
) {
    when (icon) {
        is ImageVector -> {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = modifier
            )
        }
        is Painter -> {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = modifier
            )
        }
        else -> {} // No icon
    }
}
