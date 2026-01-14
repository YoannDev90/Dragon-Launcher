package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String? = null,
    color: Color,
    enabled: Boolean = true,
    clickable: Boolean = true,
    padding: Dp = 20.dp,
    onClick: (() -> Unit)?
) {
    val displayColor = color.copy(if (enabled) 1f else 0.5f)
    val backgroundColor = color.copy(if (enabled) 0.2f else 0f)
    val borderColor = color.copy(if (enabled) 1f else 0.5f)

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = displayColor,
        modifier = Modifier
            .clip(CircleShape)
            .then(
                if (clickable) Modifier.clickable { onClick?.invoke()}
                else Modifier
            )
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
            .padding(padding)
    )
}
