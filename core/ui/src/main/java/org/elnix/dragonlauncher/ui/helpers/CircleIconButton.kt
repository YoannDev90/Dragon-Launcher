package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircleIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String,
    tint: Color= MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    padding: Dp = 20.dp,
    onClick: (() -> Unit)?
) {
    val displayColor = tint.copy(if (enabled) 1f else 0.5f)
    val backgroundColor = tint.copy(if (enabled) 0.2f else 0f)
    val borderColor = tint.copy(if (enabled) 1f else 0.5f)


    var showHelp by remember { mutableStateOf(false) }

    Box {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = displayColor,
            modifier = modifier
                .clip(CircleShape)
                .combinedClickable(
                    onLongClick = { showHelp = true },
                    onClick = { onClick?.invoke() }
                )
                .background(backgroundColor)
                .border(width = 1.dp, color = borderColor, shape = CircleShape)
                .padding(padding)
        )

        DropdownMenu(
            expanded = showHelp,
            onDismissRequest = { showHelp = false },
            containerColor = Color.Transparent,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Text(
                text = contentDescription,
                color = displayColor,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(backgroundColor.copy(0.4f))
                    .padding(5.dp)
            )
        }
    }
}


@Composable
fun CircleIconButton(
    text: String,
    modifier: Modifier = Modifier,
    contentDescription: String,
    tint: Color= MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    padding: Dp = 20.dp,
    onClick: (() -> Unit)?
) {
    val displayColor = tint.copy(if (enabled) 1f else 0.5f)
    val backgroundColor = tint.copy(if (enabled) 0.2f else 0f)
    val borderColor = tint.copy(if (enabled) 1f else 0.5f)


    var showHelp by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
                .combinedClickable(
                    onLongClick = { showHelp = true },
                    onClick = { onClick?.invoke() }
                )
                .background(backgroundColor)
                .border(width = 1.dp, color = borderColor, shape = CircleShape)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = displayColor
            )
        }

        DropdownMenu(
            expanded = showHelp,
            onDismissRequest = { showHelp = false },
            containerColor = Color.Transparent,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Text(
                text = contentDescription,
                color = displayColor,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(backgroundColor.copy(0.4f))
                    .padding(5.dp)
            )
        }
    }
}
