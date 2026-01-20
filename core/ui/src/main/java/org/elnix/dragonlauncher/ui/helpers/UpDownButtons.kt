@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun UpDownButton(
    upIcon: ImageVector,
    downIcon: ImageVector,
    color: Color,
    contentDescriptionUp: String,
    contentDescriptionDown: String,
    upEnabled: Boolean = true,
    downEnabled: Boolean = true,
    padding: Dp = 20.dp,
    onClickUp: (() -> Unit)?,
    onClickDown: (() -> Unit)?
) {
    val upTint = color.copy(alpha = if (upEnabled) 1f else 0.5f)
    val downTint = color.copy(alpha = if (downEnabled) 1f else 0.5f)

    val upBackground = color.copy(alpha = if (upEnabled) 0.2f else 0f)
    val downBackground = color.copy(alpha = if (downEnabled) 0.2f else 0f)

    var showHelpUp by remember { mutableStateOf(false) }
    var showHelpDown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, color.copy(alpha = 0.5f), CircleShape)
            .width(56.dp)
    ) {

        Box {
            Icon(
                imageVector = upIcon,
                contentDescription = null,
                tint = upTint,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(upBackground)
                    .combinedClickable(
                        onLongClick = { showHelpUp = true },
                        onClick = { onClickUp?.invoke() }
                    )
                    .padding(padding)
            )
            DropdownMenu(
                expanded = showHelpUp,
                onDismissRequest = { showHelpUp = false },
                containerColor = Color.Transparent,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp
            ) {
                Text(
                    text = contentDescriptionUp,
                    color = upTint,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(upBackground.copy(0.4f))
                        .padding(5.dp)
                )
            }
        }

        Box {
            Icon(
                imageVector = downIcon,
                contentDescription = null,
                tint = downTint,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(downBackground)
                    .combinedClickable(
                        onLongClick = { showHelpDown = true },
                        onClick = { onClickDown?.invoke() }
                    )
                    .padding(padding)
            )

            DropdownMenu(
                expanded = showHelpDown,
                onDismissRequest = { showHelpDown = false },
                containerColor = Color.Transparent,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp
            ) {
                Text(
                    text = contentDescriptionDown,
                    color = downTint,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(downBackground.copy(0.4f))
                        .padding(5.dp)
                )
            }
        }
    }
}
