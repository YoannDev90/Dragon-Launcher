@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.components.generic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.dialogs.CustomAlertDialog


@Composable
fun <T> ActionSelectorRow(
    options: List<T>,
    selected: T,
    enabled: Boolean = true,
    switchEnabled: Boolean = true,
    label: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    optionLabel: (T) -> String = { it.toString() },
    onToggle: ((Boolean) -> Unit)? = null,
    toggled: Boolean? = null,
    onSelected: (T) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val baseModifier = if (label != null) Modifier.fillMaxWidth() else Modifier.wrapContentWidth()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (label != null) Arrangement.SpaceBetween else Arrangement.Center,
        modifier = baseModifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = backgroundColor.copy(if (enabled) 1f else 0.5f),
            )
            .clickable(enabled) { showDialog = true }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        if (label != null) {
            Text(
                text = label,
                color = textColor.copy(if (enabled) 1f else 0.5f),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }

        if (toggled == true) {
            Text(
                text = optionLabel(selected),
                color = textColor.adjustBrightness(if (enabled) 1f else 0.5f),
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Right side toggle + divider wrapped in a clickable container
        if (onToggle != null && toggled != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(enabled = switchEnabled) { onToggle(!toggled) }
            ) {
                VerticalDivider(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = toggled,
                    enabled = switchEnabled,
                    onCheckedChange = { onToggle(it) },
                    colors = AppObjectsColors.switchColors()
                )
            }
        }
    }

    // Options dialog
    ActionSelector(
        visible = showDialog,
        label = label,
        textColor = textColor,
        surfaceColor = surfaceColor,
        options = options,
        optionLabel = optionLabel,
        selected = selected,
        onSelected = onSelected,
        onDismiss = { showDialog = false }
    )
}


@Composable
fun <T> ActionSelector(
    visible: Boolean,
    label: String?,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    options: List<T>,
    optionLabel: (T) -> String = { it.toString() },
    selected: T?,
    onSelected: (T) -> Unit,
    onDismiss: () -> Unit,
) {

    if (visible) {
        CustomAlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {},
            dismissButton = {},
            title = {
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column {
                    options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSelected(option)
                                    onDismiss()
                                }
                                .padding(15.dp)
                        ) {
                            Text(
                                text = optionLabel(option),
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )

                            RadioButton(
                                selected = (selected == option),
                                onClick = {
                                    onSelected(option)
                                    onDismiss()
                                },
                                colors = AppObjectsColors.radioButtonColors(),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            },
            containerColor = surfaceColor,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
