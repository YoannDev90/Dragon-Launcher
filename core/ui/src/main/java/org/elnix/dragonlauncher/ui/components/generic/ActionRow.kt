package org.elnix.dragonlauncher.ui.components.generic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun <T> ActionRow(
    actions: List<T>,
    selectedView: T,
    selectedBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    backgroundColor: Color,
    actionName: (T) -> String,
    onClick: (T) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(CircleShape),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        actions.forEach { mode ->
            val isSelected = mode == selectedView
            Text(
                text = actionName(mode),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClick(mode) }
                    .background(
                        if (isSelected) selectedBackgroundColor
                        else backgroundColor
                    )
                    .padding(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.onSecondary
                else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
