package org.elnix.dragonlauncher.ui.settings.customization


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.theme.AmoledDefault
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.common.utils.colors.blendWith
import org.elnix.dragonlauncher.enumsui.ColorCustomisationMode
import org.elnix.dragonlauncher.enumsui.DefaultThemes
import org.elnix.dragonlauncher.enumsui.colorCustomizationModeName
import org.elnix.dragonlauncher.enumsui.defaultThemeName
import org.elnix.dragonlauncher.settings.applyDefaultThemeColors
import org.elnix.dragonlauncher.settings.stores.ColorModesSettingsStore
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.components.burger.BurgerAction
import org.elnix.dragonlauncher.ui.components.burger.BurgerListAction
import org.elnix.dragonlauncher.ui.dialogs.UserValidation
import org.elnix.dragonlauncher.ui.helpers.TextDivider
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors

@Suppress("AssignedValueIsNeverRead")
@Composable
fun ColorSelectorTab(
    onBack: (() -> Unit)
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // === Collect all theme color states ===
    val primary by ColorSettingsStore.primaryColor.flow(ctx).collectAsState(initial = null)
    val onPrimary by ColorSettingsStore.onPrimaryColor.flow(ctx).collectAsState(initial = null)

    val secondary by ColorSettingsStore.secondaryColor.flow(ctx).collectAsState(initial = null)
    val onSecondary by ColorSettingsStore.onSecondaryColor.flow(ctx).collectAsState(initial = null)

    val tertiary by ColorSettingsStore.tertiaryColor.flow(ctx).collectAsState(initial = null)
    val onTertiary by ColorSettingsStore.onTertiaryColor.flow(ctx).collectAsState(initial = null)

    val background by ColorSettingsStore.backgroundColor.flow(ctx).collectAsState(initial = null)
    val onBackground by ColorSettingsStore.onBackgroundColor.flow(ctx).collectAsState(initial = null)

    val surface by ColorSettingsStore.surfaceColor.flow(ctx).collectAsState(initial = null)
    val onSurface by ColorSettingsStore.onSurfaceColor.flow(ctx).collectAsState(initial = null)

    val error by ColorSettingsStore.errorColor.flow(ctx).collectAsState(initial = null)
    val onError by ColorSettingsStore.onErrorColor.flow(ctx).collectAsState(initial = null)

    val outline by ColorSettingsStore.outlineColor.flow(ctx).collectAsState(initial = null)

    val angleLineColor by ColorSettingsStore.angleLineColor.flow(ctx).collectAsState(initial = null)
    val circleColor by ColorSettingsStore.circleColor.flow(ctx).collectAsState(initial = null)

    val launchAppColor by ColorSettingsStore.launchAppColor.flow(ctx).collectAsState(initial = null)
    val openUrlColor by ColorSettingsStore.openUrlColor.flow(ctx).collectAsState(initial = null)
    val notificationShadeColor by ColorSettingsStore.notificationShadeColor.flow(ctx).collectAsState(initial = null)
    val controlPanelColor by ColorSettingsStore.controlPanelColor.flow(ctx).collectAsState(initial = null)
    val openAppDrawerColor by ColorSettingsStore.openAppDrawerColor.flow(ctx).collectAsState(initial = null)
    val launcherSettingsColor by ColorSettingsStore.launcherSettingsColor.flow(ctx).collectAsState(initial = null)
    val lockColor by ColorSettingsStore.lockColor.flow(ctx).collectAsState(initial = null)
    val openFileColor by ColorSettingsStore.openFileColor.flow(ctx).collectAsState(initial = null)
    val reloadColor by ColorSettingsStore.reloadColor.flow(ctx).collectAsState(initial = null)
    val openRecentAppsColor by ColorSettingsStore.openRecentAppsColor.flow(ctx).collectAsState(initial = null)
    val openCircleNest by ColorSettingsStore.openCircleNestColor.flow(ctx).collectAsState(initial = null)
    val goParentCircle by ColorSettingsStore.goParentNestColor.flow(ctx).collectAsState(initial = null)

    val colorCustomisationMode by ColorModesSettingsStore.colorCustomisationMode.flow(ctx).collectAsState(initial = ColorCustomisationMode.DEFAULT)
    val selectedDefaultTheme by ColorModesSettingsStore.defaultTheme.flow(ctx).collectAsState(initial = DefaultThemes.DARK)


    var showResetValidation by remember { mutableStateOf(false) }

    var showBurgerMenu by remember { mutableStateOf(false) }

    var showRandomColorsValidation by remember { mutableStateOf(false) }
    var showAllColorsValidation by remember { mutableStateOf(false) }

    SettingsLazyHeader(
        title = stringResource(R.string.color_selector),
        onBack = onBack,
        helpText = stringResource(R.string.color_selector_text),
        onReset = {
            scope.launch {
                ColorSettingsStore.resetAll(ctx)
            }
        }
    ) {

        item {
            TextDivider(stringResource(R.string.color_custom_mode))

            Spacer(Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColorCustomisationMode.entries.forEach {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                scope.launch {
                                    ColorModesSettingsStore.colorCustomisationMode.set(ctx, it)
                                }
                            }
                            .padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        when (it) {
                            ColorCustomisationMode.DEFAULT -> {
                                Icon(
                                    imageVector = Icons.Default.InvertColors,
                                    contentDescription = stringResource(R.string.color_mode_default),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            ColorCustomisationMode.NORMAL -> {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = stringResource(R.string.color_mode_normal),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            ColorCustomisationMode.ALL -> {
                                Icon(
                                    imageVector = Icons.Default.AllInclusive,
                                    contentDescription = stringResource(R.string.color_mode_all),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = colorCustomizationModeName(it),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )

                        RadioButton(
                            selected = colorCustomisationMode == it,
                            onClick = {
                                scope.launch {
                                    ColorModesSettingsStore.colorCustomisationMode.set(ctx, it)
                                }
                            },
                            colors = AppObjectsColors.radioButtonColors()
                        )
                    }
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showResetValidation = true },
                    modifier = Modifier.weight(1f),
                    colors = AppObjectsColors.buttonColors()
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = stringResource(R.string.reset),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .clip(CircleShape)
                            .padding(5.dp)
                    )

                    Text(
                        text = stringResource(R.string.reset_to_default_colors),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                if (colorCustomisationMode == ColorCustomisationMode.ALL) {

                    Box {
                        IconButton(
                            onClick = { showBurgerMenu = true },
                            colors = AppObjectsColors.iconButtonColors(
                                backgroundColor = MaterialTheme.colorScheme.primary.copy(0.5f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.open_burger_menu),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .padding(5.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showBurgerMenu,
                            onDismissRequest = { showBurgerMenu = false },
                            containerColor = Color.Transparent,
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp
                        ) {
                            BurgerListAction(
                                actions = listOf(
                                    BurgerAction(
                                        onClick = { showRandomColorsValidation = true }
                                    ) {
                                        Icon(Icons.Default.Shuffle, null)
                                        Text(stringResource(R.string.make_every_colors_random))
                                    },
                                    BurgerAction(
                                        onClick = { showAllColorsValidation = true }
                                    ) {
                                        Icon(Icons.Default.SelectAll, null)
                                        Text(stringResource(R.string.make_all_colors_identical))
                                    }
                                )
                            )
                        }
                    }
                }

            }
        }

        item { HorizontalDivider(color = MaterialTheme.colorScheme.outline) }

        when (colorCustomisationMode) {
            ColorCustomisationMode.ALL -> {

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.primary_color),
                        defaultColor = AmoledDefault.Primary,
                        currentColor = primary ?: MaterialTheme.colorScheme.primary
                    ) { scope.launch { ColorSettingsStore.primaryColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.on_primary_color),
                        defaultColor = AmoledDefault.OnPrimary,
                        currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary
                    ) { scope.launch { ColorSettingsStore.setOnPrimary(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.secondary_color),
                        defaultColor = AmoledDefault.Secondary,
                        currentColor = secondary ?: MaterialTheme.colorScheme.secondary
                    ) { scope.launch { ColorSettingsStore.setSecondary(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.on_secondary_color),
                        defaultColor = AmoledDefault.OnSecondary,
                        currentColor = onSecondary
                            ?: MaterialTheme.colorScheme.onSecondary
                    ) { scope.launch { ColorSettingsStore.setOnSecondary(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.tertiary_color),
                        defaultColor = AmoledDefault.Tertiary,
                        currentColor = tertiary ?: MaterialTheme.colorScheme.tertiary
                    ) { scope.launch { ColorSettingsStore.setTertiary(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.on_tertiary_color),
                        defaultColor = AmoledDefault.OnTertiary,
                        currentColor = onTertiary
                            ?: MaterialTheme.colorScheme.onTertiary
                    ) { scope.launch { ColorSettingsStore.setOnTertiary(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.background_color),
                        defaultColor = AmoledDefault.Background,
                        currentColor = background
                            ?: MaterialTheme.colorScheme.background
                    ) { scope.launch { ColorSettingsStore.setBackground(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.on_background_color),
                        defaultColor = AmoledDefault.OnBackground,
                        currentColor = onBackground
                            ?: MaterialTheme.colorScheme.onBackground
                    ) { scope.launch { ColorSettingsStore.setOnBackground(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.surface_color),
                        defaultColor = AmoledDefault.Surface,
                        currentColor = surface ?: MaterialTheme.colorScheme.surface
                    ) { scope.launch { ColorSettingsStore.setSurface(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.on_surface_color),
                        defaultColor = AmoledDefault.OnSurface,
                        currentColor = onSurface ?: MaterialTheme.colorScheme.onSurface
                    ) { scope.launch { ColorSettingsStore.setOnSurface(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.error_color),
                        defaultColor = AmoledDefault.Error,
                        currentColor = error ?: MaterialTheme.colorScheme.error
                    ) { scope.launch { ColorSettingsStore.setError(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.on_error_color),
                        defaultColor = AmoledDefault.OnError,
                        currentColor = onError ?: MaterialTheme.colorScheme.onError
                    ) { scope.launch { ColorSettingsStore.setOnError(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.outline_color),
                        defaultColor = AmoledDefault.Outline,
                        currentColor = outline ?: MaterialTheme.colorScheme.outline
                    ) { scope.launch { ColorSettingsStore.setOutline(ctx, it) } }
                }

                // === Extra custom action colors ===
                item {
                    ColorPickerRow(
                        label = stringResource(R.string.angle_line_color),
                        defaultColor = AmoledDefault.AngleLineColor,
                        currentColor = angleLineColor ?: LocalExtraColors.current.angleLine
                    ) { scope.launch { ColorSettingsStore.setAngleLineColor(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.circle_color),
                        defaultColor = AmoledDefault.CircleColor,
                        currentColor = circleColor ?: LocalExtraColors.current.circle
                    ) { scope.launch { ColorSettingsStore.setCircleColor(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.launch_app_color),
                        defaultColor = AmoledDefault.LaunchAppColor,
                        currentColor = launchAppColor ?: LocalExtraColors.current.launchApp
                    ) { scope.launch { ColorSettingsStore.setLaunchAppColor(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.open_url_color),
                        defaultColor = AmoledDefault.OpenUrlColor,
                        currentColor = openUrlColor ?: LocalExtraColors.current.openUrl
                    ) { scope.launch { ColorSettingsStore.setOpenUrlColor(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.notification_shade_color),
                        defaultColor = AmoledDefault.NotificationShadeColor,
                        currentColor = notificationShadeColor
                            ?: LocalExtraColors.current.notificationShade
                    ) { scope.launch { ColorSettingsStore.setNotificationShadeColor(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.control_panel_color),
                        defaultColor = AmoledDefault.ControlPanelColor,
                        currentColor = controlPanelColor ?: LocalExtraColors.current.controlPanel
                    ) { scope.launch { ColorSettingsStore.setControlPanelColor(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.open_app_drawer_color),
                        defaultColor = AmoledDefault.OpenAppDrawerColor,
                        currentColor = openAppDrawerColor ?: LocalExtraColors.current.openAppDrawer
                    ) { scope.launch { ColorSettingsStore.setOpenAppDrawerColor(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.launcher_settings_color),
                        defaultColor = AmoledDefault.LauncherSettingsColor,
                        currentColor = launcherSettingsColor
                            ?: LocalExtraColors.current.launcherSettings
                    ) { scope.launch { ColorSettingsStore.setLauncherSettingsColor(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.lock_color),
                        defaultColor = AmoledDefault.LockColor,
                        currentColor = lockColor ?: LocalExtraColors.current.lock
                    ) { scope.launch { ColorSettingsStore.setLockColor(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.open_file_color),
                        defaultColor = AmoledDefault.OpenFileColor,
                        currentColor = openFileColor ?: LocalExtraColors.current.openFile
                    ) { scope.launch { ColorSettingsStore.setOpenFileColor(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.reload_color),
                        defaultColor = AmoledDefault.ReloadColor,
                        currentColor = reloadColor ?: LocalExtraColors.current.reload
                    ) { scope.launch { ColorSettingsStore.setReloadColor(ctx, it) } }
                }
                item {
                    ColorPickerRow(
                        label = stringResource(R.string.open_recent_apps_color),
                        defaultColor = AmoledDefault.OpenRecentAppsColor,
                        currentColor = openRecentAppsColor
                            ?: LocalExtraColors.current.openRecentApps
                    ) { scope.launch { ColorSettingsStore.setOpenRecentApps(ctx, it) } }
                }
                item {
                    ColorPickerRow(
                        label = stringResource(R.string.open_circle_nest_color),
                        defaultColor = AmoledDefault.OpenCircleNestColor,
                        currentColor = openCircleNest ?: LocalExtraColors.current.openCircleNest
                    ) { scope.launch { ColorSettingsStore.setOpenCircleNest(ctx, it) } }
                }
                item {
                    ColorPickerRow(
                        label = stringResource(R.string.go_parent_nest_color),
                        defaultColor = AmoledDefault.GoParentNestColor,
                        currentColor = goParentCircle ?: LocalExtraColors.current.goParentNest
                    ) { scope.launch { ColorSettingsStore.setGoParentNest(ctx, it) } }
                }
            }

            ColorCustomisationMode.NORMAL -> {
                item {
                    val bgColorFromTheme = MaterialTheme.colorScheme.background
                    ColorPickerRow(
                        label = stringResource(R.string.primary_color),
                        defaultColor = AmoledDefault.Primary,
                        currentColor = primary ?: MaterialTheme.colorScheme.primary
                    ) { newColor ->

                        val backgroundColor = background ?: bgColorFromTheme

                        val secondaryColor = newColor.adjustBrightness(1.2f)
                        val tertiaryColor = secondaryColor.adjustBrightness(1.2f)
                        val surfaceColor = newColor.blendWith(backgroundColor, 0.7f)

                        scope.launch {
                            ColorSettingsStore.setPrimary(ctx, newColor)
                            ColorSettingsStore.setSecondary(ctx, secondaryColor)
                            ColorSettingsStore.setTertiary(ctx, tertiaryColor)
                            ColorSettingsStore.setSurface(ctx, surfaceColor)
                        }
                    }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.background_color),
                        defaultColor = AmoledDefault.Background,
                        currentColor = background
                            ?: MaterialTheme.colorScheme.background
                    ) {
                        scope.launch {
                            ColorSettingsStore.setBackground(ctx, it)
                        }
                    }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.text_color),
                        defaultColor = AmoledDefault.OnPrimary,
                        currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary
                    ) {
                        scope.launch {
                            ColorSettingsStore.setOnPrimary(ctx, it)
                            ColorSettingsStore.setOnSecondary(ctx, it)
                            ColorSettingsStore.setOnTertiary(ctx, it)
                            ColorSettingsStore.setOnSurface(ctx, it)
                            ColorSettingsStore.setOnBackground(ctx, it)
                            ColorSettingsStore.setOutline(ctx, it)
                            ColorSettingsStore.setOnError(ctx, it)
                        }
                    }
                }
            }

            ColorCustomisationMode.DEFAULT -> {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DefaultThemes.entries.forEach {
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        scope.launch {
                                            ColorModesSettingsStore.setDefaultTheme(ctx, it)
                                            applyDefaultThemeColors(ctx, it)
                                        }
                                    }
                                    .padding(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                when (it) {
                                    DefaultThemes.AMOLED -> Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline.copy(0.5f),
                                                CircleShape
                                            )
                                    )

                                    DefaultThemes.DARK -> Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.DarkGray)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline.copy(0.5f),
                                                CircleShape
                                            )
                                    )

                                    DefaultThemes.LIGHT -> Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline.copy(0.5f),
                                                CircleShape
                                            )
                                    )

                                    DefaultThemes.SYSTEM -> Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline.copy(0.5f),
                                                CircleShape
                                            )
                                    )
                                }

                                Spacer(Modifier.height(5.dp))

                                Text(
                                    text = defaultThemeName(it),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center
                                )

                                RadioButton(
                                    selected = selectedDefaultTheme == it,
                                    onClick = {
                                        scope.launch {
                                            ColorModesSettingsStore.setDefaultTheme(ctx, it)
                                            applyDefaultThemeColors(ctx, it)
                                        }
                                    },
                                    colors = AppObjectsColors.radioButtonColors()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showResetValidation) {
        UserValidation(
            title = stringResource(R.string.reset_to_default_colors),
            message = stringResource(R.string.reset_to_default_colors_explanation),
            onCancel = { showResetValidation = false }
        ) {
            scope.launch {
                ColorSettingsStore.resetColors(
                    ctx,
                    colorCustomisationMode,
                    selectedDefaultTheme
                )
                showResetValidation = false
            }
        }
    }
    if (showRandomColorsValidation) {
        UserValidation(
            title = stringResource(R.string.make_every_colors_random),
            message = stringResource(R.string.make_every_colors_random_explanation),
            onCancel = { showRandomColorsValidation = false }
        ) {
            scope.launch {
                ColorSettingsStore.setAllRandomColors(ctx)
                showRandomColorsValidation = false
            }
        }
    }


    if (showAllColorsValidation) {
        var applyColor by remember { mutableStateOf(Color.Black) }
        AlertDialog(
            onDismissRequest = { showAllColorsValidation = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            ColorSettingsStore.setAllSameColors(ctx, applyColor)
                            showAllColorsValidation = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .padding(5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.apply),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            title = {
                ColorPickerRow(
                    label = stringResource(R.string.color_mode_all),
                    defaultColor = Color.Black,
                    currentColor = applyColor,
                    backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f)
                ) { applyColor = it }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
