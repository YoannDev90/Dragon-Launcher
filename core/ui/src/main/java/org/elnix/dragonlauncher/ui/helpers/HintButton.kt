package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp


// Still in progress TODO
@Composable
fun HintButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String,
    tint: Color= MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    clickable: Boolean = true,
    content: @Composable () -> Unit
) {
    var showHelp by remember { mutableStateOf(false) }

    Box(
//        modifier = modifier
//            .clip(CircleShape)
//            .then (
//                if (clickable) {
//                    Modifier.combinedClickable(
//                        onLongClick = { showHelp = true },
//                        onClick = {
//                            if (clickable) {
//                                onClick?.invoke()
//                            } else {
//                                showHelp = true
//                            }
//                        }
//                    )
//                } else Modifier
//            )
    ) {
        content()

        DropdownMenu(
            expanded = showHelp,
            onDismissRequest = { showHelp = false },
            containerColor = Color.Transparent,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Text(
                text = contentDescription,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background.copy(0.4f))
                    .padding(5.dp)
            )
        }
    }
}
