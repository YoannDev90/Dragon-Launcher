package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors

@Composable
fun SwitchRow(
    state: Boolean?,
    text: String,
    subText: String? = null,
    enabled: Boolean = true,
    defaultValue: Boolean = false,
    onToggle: ((Boolean) -> Unit)? = null,
    onCheck: (Boolean) -> Unit
) {
    val checked = state ?: defaultValue

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DragonShape)
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = if (enabled) 1f else 0.5f)
            )
            .clickable(enabled) { onCheck(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
//            modifier = Modifier.fillMaxWidth()
            modifier = Modifier.weight(1f)
        ){
            Text(
                text = text,
//                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.5f)
            )

            if (subText != null) {
                Text(
                    text = subText,
//                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 0.7f else 0.3f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        if (onToggle != null) {
            VerticalDivider(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
            )
        } else {
            Spacer(modifier = Modifier.width(12.dp))
        }

        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = { if (onToggle != null) onToggle(it) else onCheck(it) },
            colors = AppObjectsColors.switchColors()
        )
    }
}
