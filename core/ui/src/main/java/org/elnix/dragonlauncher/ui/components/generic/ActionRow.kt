package org.elnix.dragonlauncher.ui.components.generic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun <T> ActionRow(
    actions: List<T>,
    selectedView: T,
    enabled: Boolean = true,
    selectedBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    backgroundColor: Color,
    actionName: (T) -> String,
    actionIcon: ((T) -> ImageVector)? = null,
    onClick: (T) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .clip(CircleShape),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        actions.forEach { mode ->
            val isSelected = mode == selectedView
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .clickable(enabled) { onClick(mode) }
                    .background(
                        (
                                if (isSelected) selectedBackgroundColor
                                else backgroundColor
                                ).copy(if (enabled) 1f else 0.5f)
                    )
                    .padding(5.dp)
            ) {
                actionIcon?.let {
                    Icon(
                        imageVector = it(mode),
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onSecondary
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(5.dp))
                }

                Text(
                    text = actionName(mode),
                    modifier = Modifier
                        .padding(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun <T> ActionRow(
    actions: List<T>,
    selectedView: T,
    enabled: Boolean = true,
    selectedBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    backgroundColor: Color,
    actionIcon: (T) -> ImageVector,
    onClick: (T) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .clip(CircleShape),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        actions.forEach { mode ->
            val isSelected = mode == selectedView
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .clickable(enabled) { onClick(mode) }
                    .background(
                        (
                                if (isSelected) selectedBackgroundColor
                                else backgroundColor
                                ).copy(if (enabled) 1f else 0.5f)
                    )
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = actionIcon(mode),
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(5.dp))
            }
        }
    }
}
