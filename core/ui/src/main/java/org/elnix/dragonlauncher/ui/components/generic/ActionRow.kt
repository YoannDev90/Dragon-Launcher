package org.elnix.dragonlauncher.ui.components.generic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.utils.semiTransparentIfDisabled
import org.elnix.dragonlauncher.ui.modifiers.shapedClickable

@Composable
fun <T> ActionRow(
    actions: List<T>,
    selectedView: T?,
    enabled: Boolean = true,
    backgroundColorUnselected: Color? = null,
    actionName: @Composable ((T) -> String)? = null,
    actionIcon: @Composable ((T) -> ImageVector)? = null,
    onClick: (T) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        actions.forEach { mode ->
            val isSelected = mode == selectedView


            val backgroundColor = (
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else backgroundColorUnselected ?: MaterialTheme.colorScheme.surface
                    ).semiTransparentIfDisabled(enabled)

            val textColor = (
                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                    ).copy(if (enabled) 1f else 0.5f)

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .shapedClickable(
                        enabled = enabled,
                        isSelected = isSelected,
                        onClick = { onClick(mode) }
                    )
                    .background(backgroundColor)
                    .padding(5.dp)
            ) {
                actionIcon?.let {
                    Icon(
                        imageVector = it(mode),
                        contentDescription = null,
                        tint = textColor
                    )
                    Spacer(Modifier.width(5.dp))
                }

                actionName?.let {
                    Text(
                        text = it(mode),
                        modifier = Modifier
                            .padding(12.dp),
                        color = textColor,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
