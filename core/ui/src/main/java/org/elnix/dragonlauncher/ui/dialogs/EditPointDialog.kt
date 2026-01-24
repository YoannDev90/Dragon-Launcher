@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.common.theme.AmoledDefault
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.enumsui.SelectedUnselectedViewMode
import org.elnix.dragonlauncher.enumsui.selectedUnselectedViewName
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.ui.actions.actionColor
import org.elnix.dragonlauncher.ui.actions.actionLabel
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.components.PointPreviewCanvas
import org.elnix.dragonlauncher.ui.components.ValidateCancelButtons
import org.elnix.dragonlauncher.ui.components.generic.ActionRow
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.TextDivider
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors


@Composable
fun EditPointDialog(
    appsViewModel: AppsViewModel,
    point: SwipePointSerializable,
    isDefaultEditing: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (SwipePointSerializable) -> Unit
) {

    val ctx = LocalContext.current
    val extraColors = LocalExtraColors.current

    val points by SwipeSettingsStore.getPointsFlow(ctx).collectAsState(emptyList())
    val nests by SwipeSettingsStore.getNestsFlow(ctx).collectAsState(emptyList())

    var editPoint by remember { mutableStateOf(point) }
    var showEditIconDialog by remember { mutableStateOf(false) }
    var showEditActionDialog by remember { mutableStateOf(false) }

    val circleColor by ColorSettingsStore.circleColor.flow(ctx)
        .collectAsState(initial = AmoledDefault.CircleColor)

    val pointIcons by appsViewModel.pointIcons.collectAsState()
    val defaultPoint by appsViewModel.defaultPoint.collectAsState(defaultSwipePointsValues)


    val backgroundSurfaceColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f)

    val currentActionColor = actionColor(editPoint.action, extraColors)

    val label = actionLabel(editPoint.action, editPoint.customName)
    val actionColor =
        actionColor(editPoint.action, extraColors, editPoint.customActionColor?.let { Color(it) })


    var selectedView by remember { mutableStateOf(SelectedUnselectedViewMode.UNSELECTED) }


    val defaultBorderStroke =
        defaultPoint.borderStroke
            ?.takeIf { !isDefaultEditing }
            ?: defaultSwipePointsValues.borderStroke!!

    val defaultBorderColor =
        defaultPoint.borderColor
            ?.takeIf { !isDefaultEditing }
            ?.let(::Color)
            ?: circleColor

    val defaultBackgroundColor =
        defaultPoint.backgroundColor
            ?.takeIf { !isDefaultEditing }
            ?.let(::Color)
            ?: Color.Unspecified

    val defaultBorderStrokeSelected =
        defaultPoint.borderStroke
            ?.takeIf { !isDefaultEditing }
            ?: defaultSwipePointsValues.borderStrokeSelected!!

    val defaultBorderColorSelected =
        defaultPoint.borderColorSelected
            ?.takeIf { !isDefaultEditing }
            ?.let(::Color)
            ?: circleColor

    val defaultBackgroundColorSelected =
        defaultPoint.backgroundColorSelected
            ?.takeIf { !isDefaultEditing }
            ?.let(::Color)
            ?: Color.Unspecified

    val defaultSize =
        defaultPoint.size
            ?.takeIf { !isDefaultEditing }
            ?: defaultSwipePointsValues.size!!


    val defaultInnerPadding =
        defaultPoint.innerPadding
            ?.takeIf { !isDefaultEditing }
            ?: defaultSwipePointsValues.innerPadding!!



    LaunchedEffect(
        editPoint.action,
        editPoint.customIcon,
        editPoint.customActionColor
    ) {
        appsViewModel.reloadPointIcon(editPoint)
    }


    CustomAlertDialog(
        modifier = Modifier
            .padding(16.dp),
        onDismissRequest = onDismiss,
        imePadding = false,
        scroll = false,
        alignment = Alignment.Center,
        confirmButton = {
            ValidateCancelButtons(
                onCancel = onDismiss
            ) {
                onConfirm(editPoint)
            }
        },
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.weight(1f))

                    Text(
                        text = stringResource(R.string.edit_point),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            editPoint = SwipePointSerializable(
                                circleNumber = editPoint.circleNumber,
                                angleDeg = editPoint.angleDeg,
                                nestId = editPoint.nestId,
                                action = editPoint.action,
                                id = editPoint.id
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = stringResource(R.string.reset)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundSurfaceColor)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = stringResource(R.string.unselected_action),
                            style = MaterialTheme.typography.labelSmall
                        )

                        Text(
                            text = stringResource(R.string.selected_action),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    PointPreviewCanvas(
                        editPoint = editPoint,
                        nests = nests,
                        points = points,
                        defaultPoint = defaultPoint,
                        ctx = ctx,
                        circleColor = circleColor,
                        backgroundSurfaceColor = backgroundSurfaceColor,
                        extraColors = extraColors,
                        pointIcons = pointIcons,
                        modifier = Modifier.fillMaxWidth(1f)
                    )
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                if (!isDefaultEditing) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(backgroundSurfaceColor)
                                    .clickable {
                                        showEditActionDialog = true
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = actionColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit_action),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            val editIconEnabled =
                                editPoint.action !is SwipeActionSerializable.OpenCircleNest
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(backgroundSurfaceColor)
                                    .clickable(editIconEnabled) {
                                        showEditIconDialog = true
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.edit_icon),
                                    color = MaterialTheme.colorScheme.onSurface.copy(if (editIconEnabled) 1f else 0.5f)
                                )
                                Spacer(Modifier.weight(1f))

                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit_action),
                                    tint = MaterialTheme.colorScheme.primary.copy(if (editIconEnabled) 1f else 0.5f),
                                )
                            }
                        }
                    }


                    item {
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
                            modifier = Modifier.fillMaxWidth(),
                            colors = AppObjectsColors.outlinedTextFieldColors(
                                removeBorder = true,
                                backgroundColor = backgroundSurfaceColor
                            )
                        )
                    }


                    item {
                        ColorPickerRow(
                            label = stringResource(R.string.custom_action_color),
                            defaultColor = currentActionColor,
                            currentColor = editPoint.customActionColor?.let { Color(it) }
                                ?: currentActionColor,
                            backgroundColor = backgroundSurfaceColor
                        ) { selectedColor ->
                            editPoint = editPoint.copy(customActionColor = selectedColor.toArgb())
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundSurfaceColor)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        SliderWithLabel(
                            label = stringResource(R.string.inner_padding),
                            value = editPoint.innerPadding ?: defaultInnerPadding,
                            valueRange = 0..100,
                            color = MaterialTheme.colorScheme.primary,
                            onReset = { editPoint = editPoint.copy(innerPadding = null) }
                        ) { editPoint = editPoint.copy(innerPadding = it) }

                        SliderWithLabel(
                            label = stringResource(R.string.size),
                            value = editPoint.size ?: defaultSize,
                            valueRange = 0..200,
                            color = MaterialTheme.colorScheme.primary,
                            onReset = { editPoint = editPoint.copy(size = null) }
                        ) { editPoint = editPoint.copy(size = it) }
                    }
                }


                /* Selected / Unselected Options Toggler */

                item {
                    TextDivider(
                        text = stringResource(R.string.individual_options),
                        backgroundColor = MaterialTheme.colorScheme.surface
                    )
                }
                item {
                    ActionRow(
                        actions = SelectedUnselectedViewMode.entries,
                        selectedView = selectedView,
                        actionName = { selectedUnselectedViewName(ctx, it) },
                        backgroundColor = backgroundSurfaceColor
                    ) { selectedView = it }
                }

                if (selectedView == SelectedUnselectedViewMode.UNSELECTED) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(backgroundSurfaceColor)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            SliderWithLabel(
                                label = stringResource(R.string.border_stroke),
                                value = editPoint.borderStroke
                                    ?: defaultBorderStroke,
                                valueRange = 0f..50f,
                                color = MaterialTheme.colorScheme.primary,
                                onReset = {
                                    editPoint = editPoint.copy(borderStroke = null)
                                }
                            ) {
                                editPoint = editPoint.copy(borderStroke = it)
                            }

                            ColorPickerRow(
                                label = stringResource(R.string.border_color),
                                defaultColor = defaultBorderColor,
                                currentColor = editPoint.borderColor?.let { Color(it) }
                                    ?: defaultBorderColor
                            ) { selectedColor ->
                                editPoint = editPoint.copy(borderColor = selectedColor.toArgb())
                            }

                            ColorPickerRow(
                                label = stringResource(R.string.background_color),
                                defaultColor = defaultBackgroundColor,
                                currentColor = editPoint.backgroundColor?.let { Color(it) }
                                    ?: defaultBackgroundColor
                            ) { selectedColor ->
                                editPoint = editPoint.copy(
                                    backgroundColor = selectedColor.takeIf { it != Color.Unspecified }
                                        ?.toArgb()
                                )
                            }
                        }
                    }
                } else {

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(backgroundSurfaceColor)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            SliderWithLabel(
                                label = stringResource(R.string.border_stroke_selected),
                                value = editPoint.borderStrokeSelected
                                    ?: defaultBorderStrokeSelected,
                                valueRange = 0f..50f,
                                color = MaterialTheme.colorScheme.primary,
                                onReset = {
                                    editPoint =
                                        editPoint.copy(borderStrokeSelected = null)
                                }
                            ) {
                                editPoint = editPoint.copy(borderStrokeSelected = it)
                            }


                            ColorPickerRow(
                                label = stringResource(R.string.border_color_selected),
                                defaultColor = defaultBorderColorSelected,
                                currentColor = editPoint.borderColorSelected?.let { Color(it) }
                                    ?: defaultBorderColorSelected
                            ) { selectedColor ->
                                editPoint = editPoint.copy(borderColorSelected = selectedColor.toArgb())
                            }


                            ColorPickerRow(
                                label = stringResource(R.string.background_selected),
                                defaultColor = defaultBackgroundColorSelected,
                                currentColor = editPoint.backgroundColorSelected?.let { Color(it) }
                                    ?: defaultBackgroundColorSelected
                            ) { selectedColor ->
                                editPoint = editPoint.copy(
                                    backgroundColorSelected = selectedColor.takeIf { it != Color.Unspecified }
                                        ?.toArgb()
                                )
                            }
                        }
                    }
                }


                item {

                    SliderWithLabel(
                        value = editPoint.haptic ?: 0,
                        label = stringResource(R.string.haptic_feedback),
                        valueRange = 0..1000,
                        color = MaterialTheme.colorScheme.primary,
                        onReset = {
                            editPoint = editPoint.copy(haptic = null)
                        }
                    ) {
                        editPoint = editPoint.copy(haptic = it)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )

    if (showEditIconDialog) {
        IconEditorDialog(
            point = editPoint,
            appsViewModel = appsViewModel,
            onDismiss = { showEditIconDialog = false }
        ) { newIcon ->

            val previewPoint = point.copy(customIcon = newIcon)

            appsViewModel.reloadPointIcon(previewPoint)

            showEditIconDialog = false
            editPoint = editPoint.copy(customIcon = newIcon)
        }
    }
    if (showEditActionDialog) {
        AddPointDialog(
            appsViewModel = appsViewModel,
            onDismiss = { showEditActionDialog = false },
        ) { selectedAction ->
            editPoint = editPoint.copy(action = selectedAction)
            showEditActionDialog = false
        }
    }
}
