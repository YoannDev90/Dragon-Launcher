@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.SwipePointSerializable
import org.elnix.dragonlauncher.data.defaultSwipePointsValues
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.components.ValidateCancelButtons
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsItem
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors
import org.elnix.dragonlauncher.utils.actions.ActionIcon
import org.elnix.dragonlauncher.utils.colors.AppObjectsColors
import org.elnix.dragonlauncher.utils.models.AppsViewModel
import org.elnix.dragonlauncher.utils.models.WorkspaceViewModel

@Composable
fun EditPointDialog(
    appsViewModel: AppsViewModel,
    workspaceViewModel: WorkspaceViewModel,
    point: SwipePointSerializable,
    onDismiss: () -> Unit,
    onConfirm: (SwipePointSerializable) -> Unit
) {

    var editPoint by remember { mutableStateOf(point) }
    var showEditIconDialog by remember { mutableStateOf(false) }
    var showEditActionDialog by remember { mutableStateOf(false) }

    val icons by appsViewModel.icons.collectAsState()

    CustomAlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            ValidateCancelButtons(
                onCancel = onDismiss
            ) {
                onConfirm(editPoint)
            }
        },
        title = { Text(stringResource(R.string.edit_point)) },
        text = {
            Column(
                modifier = Modifier
            ) {

                SettingsItem(
                    title = stringResource(R.string.edit_action),
                    leadIcon = Icons.Default.Edit
                ) {
                    showEditActionDialog = true
                }

                /**
                 * Icon Row; preview it and edit button at right that opens the icon editor
                 */
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    ActionIcon(
                        action = editPoint.action,
                        icons = icons,
                        modifier = Modifier
                            .size(50.dp),
                        size = 50
                    )

                    IconButton(
                        onClick = { showEditIconDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_icon)
                        )
                    }

                }

                SliderWithLabel(
                    label = stringResource(R.string.border_stroke),
                    value = editPoint.borderStroke ?: defaultSwipePointsValues.borderStroke!!,
                    valueRange = 0f..50f,
                    color = MaterialTheme.colorScheme.primary,
                    onReset = {
                        editPoint = editPoint.copy(borderStroke = defaultSwipePointsValues.borderStroke!!)
                    }
                ) {
                    editPoint = editPoint.copy(borderStroke = it)
                }


                SliderWithLabel(
                    label = stringResource(R.string.border_stroke_selected),
                    value = editPoint.borderStrokeSelected ?: defaultSwipePointsValues.borderStrokeSelected!!,
                    valueRange = 0f..50f,
                    color = MaterialTheme.colorScheme.primary,
                    onReset = {
                        editPoint = editPoint.copy(borderStrokeSelected = defaultSwipePointsValues.borderStrokeSelected!!)
                    }
                ) {
                    editPoint = editPoint.copy(borderStrokeSelected = it)
                }


                ColorPickerRow(
                    label = stringResource(R.string.border_color),
                    defaultColor = LocalExtraColors.current.circle,
                    currentColor = editPoint.borderColor?.let { Color(it) }
                        ?: LocalExtraColors.current.circle,
                ) { selectedColor ->
                    editPoint = editPoint.copy(borderColor = selectedColor.toArgb())
                }

                ColorPickerRow(
                    label = stringResource(R.string.border_color_selected),
                    defaultColor = LocalExtraColors.current.circle,
                    currentColor = editPoint.borderColorSelected?.let { Color(it) }
                        ?: LocalExtraColors.current.circle,
                ) { selectedColor ->
                    editPoint = editPoint.copy(borderColorSelected = selectedColor.toArgb())
                }

                ColorPickerRow(
                    label = stringResource(R.string.border_color),
                    defaultColor = LocalExtraColors.current.circle,
                    currentColor = editPoint.borderColor?.let { Color(it) }
                        ?: LocalExtraColors.current.circle,
                ) { selectedColor ->
                    editPoint = editPoint.copy(borderColor = selectedColor.toArgb())
                }

                ColorPickerRow(
                    label = stringResource(R.string.border_color_selected),
                    defaultColor = LocalExtraColors.current.circle,
                    currentColor = editPoint.borderColorSelected?.let { Color(it) }
                        ?: LocalExtraColors.current.circle,
                ) { selectedColor ->
                    editPoint = editPoint.copy(borderColorSelected = selectedColor.toArgb())
                }

                ColorPickerRow(
                    label = stringResource(R.string.background_color),
                    defaultColor = MaterialTheme.colorScheme.background,
                    currentColor = editPoint.backgroundColor?.let { Color(it) }
                        ?: MaterialTheme.colorScheme.background,
                ) { selectedColor ->
                    editPoint = editPoint.copy(backgroundColor = selectedColor.toArgb())
                }

                ColorPickerRow(
                    label = stringResource(R.string.background_color_selected),
                    defaultColor = MaterialTheme.colorScheme.background,
                    currentColor = editPoint.backgroundColorSelected?.let { Color(it) }
                        ?: MaterialTheme.colorScheme.background,
                ) { selectedColor ->
                    editPoint = editPoint.copy(backgroundColorSelected = selectedColor.toArgb())
                }

                SliderWithLabel(
                    label = stringResource(R.string.opacity),
                    value = editPoint.opacity ?: defaultSwipePointsValues.opacity!!,
                    valueRange = 0f..1f,
                    color = MaterialTheme.colorScheme.primary,
                    onReset = {
                        editPoint = editPoint.copy(opacity = defaultSwipePointsValues.opacity!!)
                    }
                ) {
                    editPoint = editPoint.copy(opacity = it)
                }

                TextField(
                    value = editPoint.customName ?: "",
                    onValueChange = {
                        editPoint = editPoint.copy(customName = it)
                    },
                    label = { Text(stringResource(R.string.custom_name)) },
                    trailingIcon = {
                        if (editPoint.customName != null && editPoint.customName!!.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    editPoint = editPoint.copy(customName = null)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = stringResource(R.string.reset)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = AppObjectsColors.outlinedTextFieldColors(removeBorder = true)
                )


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) { }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )

    if (showEditIconDialog) {
        IconPickerDialog { TODO() }
    }
    if (showEditActionDialog) {
        AddPointDialog(
            appsViewModel = appsViewModel,
            workspaceViewModel = workspaceViewModel,
            onDismiss = { showEditActionDialog = false },
        ) { selectedAction ->
            editPoint = editPoint.copy(action = selectedAction)
            showEditActionDialog = false
        }
    }
}
