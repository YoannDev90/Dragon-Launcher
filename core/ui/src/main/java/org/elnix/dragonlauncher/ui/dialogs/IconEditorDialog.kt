@file:Suppress("AssignedValueIsNeverRead", "DEPRECATION")

package org.elnix.dragonlauncher.ui.dialogs

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CustomIconSerializable
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.IconType
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.common.utils.ImageUtils.uriToBase64
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.common.utils.definedOrNull
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.components.PointPreviewCanvas
import org.elnix.dragonlauncher.ui.components.ValidateCancelButtons
import org.elnix.dragonlauncher.ui.helpers.ShapeRow
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors

@Composable
fun IconEditorDialog(
    point: SwipePointSerializable,
    appsViewModel: AppsViewModel,
    onReset: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onPicked: (CustomIconSerializable?) -> Unit
) {

    val ctx = LocalContext.current
    val extraColors = LocalExtraColors.current
    val scope = rememberCoroutineScope()

    val points by SwipeSettingsStore.getPointsFlow(ctx).collectAsState(emptyList())
    val nests by SwipeSettingsStore.getNestsFlow(ctx).collectAsState(emptyList())

    val circleColor = LocalExtraColors.current.circle
    val backgroundColor = MaterialTheme.colorScheme.surface

    val defaultPoint by appsViewModel.defaultPoint.collectAsState(defaultSwipePointsValues)

    var selectedIcon by remember { mutableStateOf(point.customIcon) }
    var textValue by remember { mutableStateOf("") }


    val previewPoint = point.copy(customIcon = selectedIcon)
    val previewIcon = remember(selectedIcon) {
        mapOf(point.id to appsViewModel.loadPointIcon(previewPoint, 64))
    }
    val source = selectedIcon?.source

    LaunchedEffect(Unit) {
        if (selectedIcon?.type == IconType.TEXT) {
            textValue = source ?: ""
        }
    }
    var showIconPackPicker by remember { mutableStateOf(false) }
    var showShapePickerDialog by remember { mutableStateOf(false) }


    val cropLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        val uri = result.uriContent ?: return@rememberLauncherForActivityResult

        scope.launch {
            val base64 = uriToBase64(ctx, uri)
            selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(
                type = IconType.BITMAP,
                source = base64
            )
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult

        cropLauncher.launch(
            CropImageContractOptions(
                uri,
                cropImageOptions = CropImageOptions(
                    cropShape = CropImageView.CropShape.RECTANGLE,
                    fixAspectRatio = true,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    guidelines = CropImageView.Guidelines.ON
                )
            )
        )
    }

    CustomAlertDialog(
        modifier = Modifier
            .padding(24.dp),
        onDismissRequest = onDismiss,
        imePadding = false,
        alignment = Alignment.Center,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.icon_editor),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )

                PointPreviewCanvas(
                    editPoint = previewPoint,
                    nests = nests,
                    points = points,
                    defaultPoint = defaultPoint,
                    circleColor = circleColor,
                    backgroundSurfaceColor = backgroundColor,
                    extraColors = extraColors,
                    pointIcons = previewIcon,
                    modifier = Modifier.weight(1f)
                )


                IconButton(
                    onClick = {
                        selectedIcon = null
                        onReset?.invoke()
                        textValue = ""
                    },
                    colors = AppObjectsColors.iconButtonColors(),
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = stringResource(R.string.reset)
                    )
                }
            }
        },
        confirmButton = {
            ValidateCancelButtons(
                onCancel = onDismiss,
            ) { onPicked(selectedIcon) }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.fillMaxWidth(0.5f) // Hacky lol
                    ) {
                        SelectableCard(
                            selected = selectedIcon?.type == IconType.BITMAP && source != null,
                            onClick = {
                                imagePicker.launch(arrayOf("image/*"))
                                textValue = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.pick_image),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }


                        SelectableCard(
                            selected = selectedIcon?.type == IconType.ICON_PACK && source != null,
                            onClick = {
                                showIconPackPicker = true
                                textValue = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.pick_from_icon_pack),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    SelectableCard(
                        modifier = Modifier.fillMaxWidth(),
                        selected = selectedIcon?.type == IconType.TEXT && source != null,
                        onClick = {}
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(DragonShape)
                                .background(
                                    MaterialTheme.colorScheme.surface.adjustBrightness(
                                        0.7f
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                stringResource(R.string.text_emoji),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            TextField(
                                value = textValue,
                                onValueChange = {
                                    textValue = it
                                    selectedIcon =
                                        if (it.isNotBlank()) {
                                            (selectedIcon ?: CustomIconSerializable()).copy(
                                                type = IconType.TEXT,
                                                source = it
                                            )
                                        } else {
                                            null
                                        }
                                },
                                placeholder = { Text("😀  A  ★") },
                                singleLine = true,
                                colors = AppObjectsColors.outlinedTextFieldColors(
                                    removeBorder = true,
                                    backgroundColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                }

                SelectableCard(
                    selected = selectedIcon?.type == IconType.PLAIN_COLOR && source != null,
                    onClick = {}
                ) {
                    val currentColor = run {
                            source
                                ?.takeIf { selectedIcon?.type == IconType.PLAIN_COLOR }
                                ?.let { Color(it.toInt()) }
                        } ?: Color.Black

                    ColorPickerRow(
                        label = stringResource(R.string.plain_color),
//                        defaultColor = Color.Black,
                        currentColor = currentColor
                    ) {
                        selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(
                            type = IconType.PLAIN_COLOR,
                            source = it?.toArgb().toString()
                        )
                    }
                }

                SelectableCard(
                    selected = selectedIcon?.type == null || source == null,
                    onClick = {
                        selectedIcon = selectedIcon?.copy(
                            type = null,
                            source = null
                        )
                        textValue = ""
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.no_custom_icon),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }



                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(DragonShape)
                        .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.7f))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Opacity
                    SliderWithLabel(
                        label = stringResource(R.string.opacity),
                        value = selectedIcon?.opacity ?: 1f,
                        valueRange = 0f..1f,
                        color = MaterialTheme.colorScheme.primary,
                        onReset = {
                            selectedIcon = selectedIcon?.copy(opacity = null)
                        }
                    ) {
                        selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(opacity = it)
                    }

                    // Rotation
                    SliderWithLabel(
                        label = stringResource(R.string.rotation),
                        value = selectedIcon?.rotationDeg ?: 0f,
                        valueRange = -180f..180f,
                        color = MaterialTheme.colorScheme.primary,
                        onReset = {
                            selectedIcon = selectedIcon?.copy(rotationDeg = null)
                        }
                    ) {
                        selectedIcon =
                            (selectedIcon ?: CustomIconSerializable()).copy(rotationDeg = it)
                    }

                    // Scale X
                    SliderWithLabel(
                        label = stringResource(R.string.scale_x),
                        value = selectedIcon?.scaleX ?: 1f,
                        valueRange = 0.2f..3f,
                        color = MaterialTheme.colorScheme.primary,
                        onReset = {
                            selectedIcon = selectedIcon?.copy(scaleX = null)
                        }
                    ) {
                        selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(scaleX = it)
                    }

                    // Scale Y
                    SliderWithLabel(
                        label = stringResource(R.string.scale_y),
                        value = selectedIcon?.scaleY ?: 1f,
                        valueRange = 0.2f..3f,
                        color = MaterialTheme.colorScheme.primary,
                        onReset = {
                            selectedIcon = selectedIcon?.copy(scaleY = null)
                        }
                    ) {
                        selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(scaleY = it)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(DragonShape)
                        .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.7f))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ColorPickerRow(
                        label = stringResource(R.string.tint),
//                        defaultColor = Color.Unspecified,
                        currentColor = selectedIcon?.tint?.let { Color(it) } ?: Color.Unspecified
                    ) {
                        val tintColor = it.definedOrNull()?.toArgb()
                        selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(
                           tint = tintColor
                        )
                    }

                    ShapeRow(
                        selected = selectedIcon?.shape ?: IconShape.PlatformDefault,
                        onReset = {
                            selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(
                                shape = null
                            )
                        }
                    ) { showShapePickerDialog = true }

                    // TODO Disabled cause not used and not working yet
//                    val selectedBlendMode =  selectedIcon?.blendMode?.let {
//                        BlendModes.valueOf(it)
//                    } ?: BlendModes.DEFAULT
//
//                    ActionColumn(
//                        actions = BlendModes.entries,
//                        selectedView = selectedBlendMode,
//                        backgroundColor = MaterialTheme.colorScheme.surface
//                    ) {
//                        selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(
//                            blendMode = it.toString()
//                        )
//                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )

    if (showIconPackPicker) {
        IconPackPickerDialog(
            appsViewModel = appsViewModel,
            onDismiss = { showIconPackPicker = false },
            onIconPicked = { name, packName ->
                // Now stores the name of the drawable, to avoid storing big bitmaps,
                // renders at runtime, as equally efficient since rendering bitmap also consumes lots
                // Comma separated with the name of the drawable and the pack name
                selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(
                    type = IconType.ICON_PACK,
                    source = "$name,$packName"
                )
                showIconPackPicker = false
            }
        )
    }

    if (showShapePickerDialog) {
        ShapePickerDialog(
            selected = selectedIcon?.shape ?: IconShape.PlatformDefault,
            onDismiss = { showShapePickerDialog = false }
        ) {
            selectedIcon = (selectedIcon ?: CustomIconSerializable()).copy(
                shape = it
            )

            showShapePickerDialog = false
        }
    }
}

@Composable
private fun SelectableCard(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .padding(6.dp)
            .clip(DragonShape)
            .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.7f))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            content = content,
            modifier = Modifier.weight(1f)
        )

        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
