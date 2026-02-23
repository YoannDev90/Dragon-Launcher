package org.elnix.dragonlauncher.ui.components.generic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape

@Composable
fun <T> ActionColumn(
    actions: List<T>,
    selectedView: T,
    enabled: Boolean = true,
    selectedBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    backgroundColor: Color,
    actionName: (T) -> String = { it.toString() },
    actionIcon: ((T) -> ImageVector)? = null,
    onClick: (T) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(DragonShape),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        actions.forEach { mode ->
            val isSelected = mode == selectedView
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .clickable(enabled) { onClick(mode) }
                    .background(
                        (
                                if (isSelected) selectedBackgroundColor
                                else backgroundColor
                                ).copy(if (enabled) 1f else 0.5f)
                    )
            ) {
                actionIcon?.let {
                    Icon(
                        imageVector = it(mode),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(5.dp))
                }

                Text(
                    text = actionName(mode),
                    modifier = Modifier
                        .padding(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary.copy(if (enabled) 1f else 0.5f)
                    else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
