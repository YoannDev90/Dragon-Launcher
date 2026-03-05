@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.helpers.customobjects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CustomGlow
import org.elnix.dragonlauncher.common.serializables.CustomObjectBlockProperties
import org.elnix.dragonlauncher.common.serializables.CustomObjectSerializable
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.dialogs.ShapePickerDialog
import org.elnix.dragonlauncher.ui.helpers.ShapeRow
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.SwitchRow

@Composable
fun EditCustomObjectBlock(
    editObject: CustomObjectSerializable,
    default: CustomObjectSerializable,
    properties: CustomObjectBlockProperties = CustomObjectBlockProperties(),
    onEdit: (CustomObjectSerializable) -> Unit
) {

    var tempSize by remember { mutableStateOf(editObject.size) }
    var tempStroke by remember { mutableStateOf(editObject.stroke) }
    var tempColor by remember { mutableStateOf(editObject.color) }

    var tempGlowColor by remember { mutableStateOf(editObject.glow?.color) }
    var tempGlowRadius by remember { mutableStateOf(editObject.glow?.radius) }

    var showSelectedShapePickerDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (properties.allowSizeCustomization) {
            SliderWithLabel(
                label = stringResource(R.string.size),
                value = tempSize ?: default.size!!,
                valueRange = 0f..200f,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                decimals = 1,
                onReset = {
                    tempSize = null
                    onEdit(editObject.copy(size = null))
                },
                onDragStateChange = { onEdit(editObject.copy(size = tempSize)) }
            ) { tempSize = it }
        }

        if (properties.allowStrokeCustomization) {
            SliderWithLabel(
                label = stringResource(R.string.stroke),
                value = tempStroke ?: default.stroke!!,
                valueRange = 0f..20f,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                decimals = 1,
                onReset = {
                    tempStroke = null
                    onEdit(editObject.copy(stroke = null))
                },
                onDragStateChange = { onEdit(editObject.copy(stroke = tempStroke)) }
            ) { tempStroke = it }
        }

        if (properties.allowColorCustomization) {
            ColorPickerRow(
                label = stringResource(R.string.color),
                showLabel = true,
                enabled = true,
                currentColor = tempColor ?: Color.Unspecified,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                onColorPicked = {
                    tempColor = it
                    onEdit(editObject.copy(color = it))
                }
            )
        }

        if (properties.allowGlowCustomization) {
            SwitchRow(
                state = editObject.glow != null,
                text = stringResource(R.string.enable_glow)
            ) { enabled ->
                if (enabled) {
                    onEdit(editObject.copy(glow = CustomGlow()))
                } else {
                    onEdit(editObject.copy(glow = null))
                }
            }

            AnimatedVisibility(editObject.glow != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ColorPickerRow(
                        label = stringResource(R.string.glow_color),
                        showLabel = true,
                        enabled = true,
                        currentColor = tempGlowColor ?: default.glow?.color ?: Color.Unspecified,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        onColorPicked = {
                            tempGlowColor = it
                            onEdit(
                                editObject.copy(
                                    glow = (editObject.glow ?: CustomGlow())
                                        .copy(color = it)
                                )
                            )
                        }
                    )


                    SliderWithLabel(
                        label = stringResource(R.string.glow_radius),
                        value = tempGlowRadius ?: default.glow?.radius!!,
                        valueRange = 0f..50f,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        decimals = 1,
                        onReset = {
                            tempGlowRadius = null
                            onEdit(
                                editObject.copy(
                                    glow = editObject.glow?.copy(radius = null)
                                )
                            )
                        },
                        onDragStateChange = {
                            onEdit(
                                editObject.copy(
                                    glow = (editObject.glow ?: CustomGlow())
                                        .copy(radius = tempGlowRadius)
                                )
                            )
                        }
                    ) { tempGlowRadius = it }
                }
            }
        }

        if (properties.allowShapeCustomization) {
            ShapeRow(
                editObject.shape ?: default.shape!!,
                title = stringResource(R.string.edit_shape),
                onReset = { onEdit(editObject.copy(shape = null)) }
            ) { showSelectedShapePickerDialog = true }
        }

        if (properties.allowEraseBackgroundCustomization) {
            SwitchRow(
                state = editObject.eraseBackground
                    ?: default.eraseBackground!!,
                text = stringResource(R.string.erase_background)
            ) {
                onEdit(editObject.copy(eraseBackground = it))
            }
        }
    }

    if (showSelectedShapePickerDialog) {
        ShapePickerDialog(
            selected = editObject.shape ?: default.shape!!,
            onDismiss = { showSelectedShapePickerDialog = false }
        ) {
            onEdit(editObject.copy(shape = it))
            showSelectedShapePickerDialog = false
        }
    }
}
