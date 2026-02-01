package org.elnix.dragonlauncher.ui.settings.customization.colors


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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.common.utils.colors.blendWith
import org.elnix.dragonlauncher.common.utils.orDefault
import org.elnix.dragonlauncher.enumsui.ColorCustomisationMode
import org.elnix.dragonlauncher.enumsui.DefaultThemes
import org.elnix.dragonlauncher.enumsui.DefaultThemes.AMOLED
import org.elnix.dragonlauncher.enumsui.DefaultThemes.CUSTOM
import org.elnix.dragonlauncher.enumsui.DefaultThemes.DARK
import org.elnix.dragonlauncher.enumsui.DefaultThemes.LIGHT
import org.elnix.dragonlauncher.enumsui.DefaultThemes.SYSTEM
import org.elnix.dragonlauncher.enumsui.colorCustomizationModeName
import org.elnix.dragonlauncher.enumsui.defaultThemeName
import org.elnix.dragonlauncher.settings.stores.ColorModesSettingsStore
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.burger.BurgerAction
import org.elnix.dragonlauncher.ui.components.burger.BurgerListAction
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.components.settings.asStateNull
import org.elnix.dragonlauncher.ui.dialogs.UserValidation
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
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
    val primary by ColorSettingsStore.primaryColor.asStateNull()
    val onPrimary by ColorSettingsStore.onPrimaryColor.asStateNull()

    val secondary by ColorSettingsStore.secondaryColor.asStateNull()
    val onSecondary by ColorSettingsStore.onSecondaryColor.asStateNull()

    val tertiary by ColorSettingsStore.tertiaryColor.asStateNull()
    val onTertiary by ColorSettingsStore.onTertiaryColor.asStateNull()

    val background by ColorSettingsStore.backgroundColor.asStateNull()
    val onBackground by ColorSettingsStore.onBackgroundColor.asStateNull()

    val surface by ColorSettingsStore.surfaceColor.asStateNull()
    val onSurface by ColorSettingsStore.onSurfaceColor.asStateNull()

    val error by ColorSettingsStore.errorColor.asStateNull()
    val onError by ColorSettingsStore.onErrorColor.asStateNull()

    val outline by ColorSettingsStore.outlineColor.asStateNull()

    val angleLineColor by ColorSettingsStore.angleLineColor.asStateNull()
    val circleColor by ColorSettingsStore.circleColor.asStateNull()

    val launchAppColor by ColorSettingsStore.launchAppColor.asStateNull()
    val openUrlColor by ColorSettingsStore.openUrlColor.asStateNull()
    val notificationShadeColor by ColorSettingsStore.notificationShadeColor.asStateNull()
    val controlPanelColor by ColorSettingsStore.controlPanelColor.asStateNull()
    val openAppDrawerColor by ColorSettingsStore.openAppDrawerColor.asStateNull()
    val launcherSettingsColor by ColorSettingsStore.launcherSettingsColor.asStateNull()
    val lockColor by ColorSettingsStore.lockColor.asStateNull()
    val openFileColor by ColorSettingsStore.openFileColor.asStateNull()
    val reloadColor by ColorSettingsStore.reloadColor.asStateNull()
    val openRecentAppsColor by ColorSettingsStore.openRecentAppsColor.asStateNull()
    val openCircleNest by ColorSettingsStore.openCircleNestColor.asStateNull()
    val goParentCircle by ColorSettingsStore.goParentNestColor.asStateNull()

    val colorCustomisationMode by ColorModesSettingsStore.colorCustomisationMode.flow(ctx).collectAsState(initial = ColorCustomisationMode.DEFAULT)
    val selectedDefaultTheme by ColorModesSettingsStore.defaultTheme.flow(ctx).collectAsState(initial = DARK)


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
                        shape = DragonShape
                    )
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColorCustomisationMode.entries.forEach {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(DragonShape)
                            .clickable {
                                scope.launch {
                                    ColorModesSettingsStore.colorCustomisationMode.set(ctx, it)
                                }
                            }
                            .padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val icon = when (it) {
                            ColorCustomisationMode.DEFAULT -> Icons.Default.InvertColors
                            ColorCustomisationMode.NORMAL -> Icons.Default.Palette
                            ColorCustomisationMode.ALL -> Icons.Default.AllInclusive
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(R.string.color_mode_all),
                            tint = MaterialTheme.colorScheme.onSurface
                        )

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

        if (colorCustomisationMode != ColorCustomisationMode.DEFAULT) {
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
                                            onClick = {
                                                showRandomColorsValidation = true
                                                showBurgerMenu = false
                                            }
                                        ) {
                                            Icon(Icons.Default.Shuffle, null)
                                            Text(stringResource(R.string.make_every_colors_random))
                                        },
                                        BurgerAction(
                                            onClick = {
                                                showAllColorsValidation = true
                                                showBurgerMenu = false
                                            }
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
        }

        item { HorizontalDivider(color = MaterialTheme.colorScheme.outline) }

        when (colorCustomisationMode) {
            ColorCustomisationMode.ALL -> {

                item {
                    ColorPickerRow(
                        currentColor = primary ?: MaterialTheme.colorScheme.primary,
                        label = stringResource(R.string.primary_color),
                        ) { scope.launch { ColorSettingsStore.primaryColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary,
                        label = stringResource(R.string.on_primary_color),
                        ) { scope.launch { ColorSettingsStore.onPrimaryColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = secondary ?: MaterialTheme.colorScheme.secondary,
                        label = stringResource(R.string.secondary_color),
                        ) { scope.launch { ColorSettingsStore.secondaryColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = onSecondary
                            ?: MaterialTheme.colorScheme.onSecondary,
                        label = stringResource(R.string.on_secondary_color),
                        ) { scope.launch { ColorSettingsStore.onSecondaryColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = tertiary ?: MaterialTheme.colorScheme.tertiary,
                        label = stringResource(R.string.tertiary_color),
                        ) { scope.launch { ColorSettingsStore.tertiaryColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        label = stringResource(R.string.on_tertiary_color),
                        currentColor = onTertiary
                            ?: MaterialTheme.colorScheme.onTertiary
                    ) { scope.launch { ColorSettingsStore.onTertiaryColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = background
                            ?: MaterialTheme.colorScheme.background,
                        label = stringResource(R.string.background_color),
                        ) { scope.launch { ColorSettingsStore.backgroundColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = onBackground
                            ?: MaterialTheme.colorScheme.onBackground,
                        label = stringResource(R.string.on_background_color),
                        ) { scope.launch { ColorSettingsStore.onBackgroundColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = surface ?: MaterialTheme.colorScheme.surface,
                        label = stringResource(R.string.surface_color),
                        ) { scope.launch { ColorSettingsStore.surfaceColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = onSurface ?: MaterialTheme.colorScheme.onSurface,
                        label = stringResource(R.string.on_surface_color),
                        ) { scope.launch { ColorSettingsStore.onSurfaceColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = error ?: MaterialTheme.colorScheme.error,
                        label = stringResource(R.string.error_color),
                        ) { scope.launch { ColorSettingsStore.errorColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = onError ?: MaterialTheme.colorScheme.onError,
                        label = stringResource(R.string.on_error_color),
                        ) { scope.launch { ColorSettingsStore.onErrorColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = outline ?: MaterialTheme.colorScheme.outline,
                        label = stringResource(R.string.outline_color),
                        ) { scope.launch { ColorSettingsStore.outlineColor.set(ctx, it) } }
                }

                // === Extra custom action colors ===
                item {
                    ColorPickerRow(
                        currentColor = angleLineColor ?: LocalExtraColors.current.angleLine,
                        label = stringResource(R.string.angle_line_color),
                        ) { scope.launch { ColorSettingsStore.angleLineColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = circleColor ?: LocalExtraColors.current.circle,
                        label = stringResource(R.string.circle_color),
                        ) { scope.launch { ColorSettingsStore.circleColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = launchAppColor ?: LocalExtraColors.current.launchApp,
                        label = stringResource(R.string.launch_app_color),
                        ) { scope.launch { ColorSettingsStore.launchAppColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = openUrlColor ?: LocalExtraColors.current.openUrl,
                        label = stringResource(R.string.open_url_color),
                        ) { scope.launch { ColorSettingsStore.openUrlColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = notificationShadeColor
                            ?: LocalExtraColors.current.notificationShade,
                        label = stringResource(R.string.notification_shade_color),
                        ) { scope.launch { ColorSettingsStore.notificationShadeColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = controlPanelColor ?: LocalExtraColors.current.controlPanel,
                        label = stringResource(R.string.control_panel_color),
                        ) { scope.launch { ColorSettingsStore.controlPanelColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = openAppDrawerColor ?: LocalExtraColors.current.openAppDrawer,
                        label = stringResource(R.string.open_app_drawer_color),
                        ) { scope.launch { ColorSettingsStore.openAppDrawerColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = launcherSettingsColor
                            ?: LocalExtraColors.current.launcherSettings,
                        label = stringResource(R.string.launcher_settings_color),
                        ) { scope.launch { ColorSettingsStore.launcherSettingsColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = lockColor ?: LocalExtraColors.current.lock,
                        label = stringResource(R.string.lock_color),
                        ) { scope.launch { ColorSettingsStore.lockColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = openFileColor ?: LocalExtraColors.current.openFile,
                        label = stringResource(R.string.open_file_color),
                        ) { scope.launch { ColorSettingsStore.openFileColor.set(ctx, it) } }
                }

                item {
                    ColorPickerRow(
                        currentColor = reloadColor ?: LocalExtraColors.current.reload,
                        label = stringResource(R.string.reload_color),
                        ) { scope.launch { ColorSettingsStore.reloadColor.set(ctx, it) } }
                }
                item {
                    ColorPickerRow(
                        currentColor = openRecentAppsColor
                            ?: LocalExtraColors.current.openRecentApps,
                        label = stringResource(R.string.open_recent_apps_color),
                        ) { scope.launch { ColorSettingsStore.openRecentAppsColor.set(ctx, it) } }
                }
                item {
                    ColorPickerRow(
                        currentColor = openCircleNest ?: LocalExtraColors.current.openCircleNest,
                        label = stringResource(R.string.open_circle_nest_color),
                        ) { scope.launch { ColorSettingsStore.openCircleNestColor.set(ctx, it) } }
                }
                item {
                    ColorPickerRow(
                        currentColor = goParentCircle ?: LocalExtraColors.current.goParentNest,
                        label = stringResource(R.string.go_parent_nest_color),
                        ) { scope.launch { ColorSettingsStore.goParentNestColor.set(ctx, it) } }
                }
            }

            ColorCustomisationMode.NORMAL -> {
                item {
                    val bgColorFromTheme = MaterialTheme.colorScheme.background
                    ColorPickerRow(
                        currentColor = primary ?: MaterialTheme.colorScheme.primary,
                        label = stringResource(R.string.primary_color),
                        ) { newColor ->

                        val backgroundColor = background ?: bgColorFromTheme
                        val color = newColor.orDefault(backgroundColor)


                        val secondaryColor = color.adjustBrightness(1.2f)
                        val tertiaryColor = secondaryColor.adjustBrightness(1.2f)
                        val surfaceColor = color.blendWith(backgroundColor, 0.7f)

                        scope.launch {
                            ColorSettingsStore.primaryColor.set(ctx, newColor)
                            ColorSettingsStore.secondaryColor.set(ctx, secondaryColor)
                            ColorSettingsStore.tertiaryColor.set(ctx, tertiaryColor)
                            ColorSettingsStore.surfaceColor.set(ctx, surfaceColor)
                        }
                    }
                }

                item {
                    ColorPickerRow(
                        currentColor = background
                            ?: MaterialTheme.colorScheme.background,
                        label = stringResource(R.string.background_color),
                        ) {
                        scope.launch {
                            ColorSettingsStore.backgroundColor.set(ctx, it)
                        }
                    }
                }

                item {
                    ColorPickerRow(
                        currentColor = onPrimary ?: MaterialTheme.colorScheme.onPrimary,
                        label = stringResource(R.string.text_color),
                        ) {
                        scope.launch {
                            ColorSettingsStore.onPrimaryColor.set(ctx, it)
                            ColorSettingsStore.onSecondaryColor.set(ctx, it)
                            ColorSettingsStore.onTertiaryColor.set(ctx, it)
                            ColorSettingsStore.onSurfaceColor.set(ctx, it)
                            ColorSettingsStore.onBackgroundColor.set(ctx, it)
                            ColorSettingsStore.outlineColor.set(ctx, it)
                            ColorSettingsStore.onErrorColor.set(ctx, it)
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
                                shape = DragonShape
                            )
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DefaultThemes.entries.filter { it != AMOLED }.forEach {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(DragonShape)
                                    .clickable {
                                        scope.launch {
                                            ColorModesSettingsStore.defaultTheme.set(ctx, it)
                                        }
                                    }
                                    .padding(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                val background = when (it) {
                                    AMOLED -> null

                                    DARK   -> Color.DarkGray
                                    LIGHT  -> Color.White
                                    SYSTEM -> MaterialTheme.colorScheme.primary

                                    CUSTOM -> Brush.sweepGradient(
                                        colors = listOf(
                                            Color.Red,
                                            Color.Yellow,
                                            Color.Green,
                                            Color.Cyan,
                                            Color.Blue,
                                            Color.Magenta
                                        )
                                    )
                                }

                                if (background != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .then(
                                                when (background) {
                                                    is Color -> Modifier.background(background)
                                                    is Brush -> Modifier.background(background)
                                                    else     -> Modifier
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
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
                                            ColorModesSettingsStore.defaultTheme.set(ctx, it)
                                        }
                                    },
                                    colors = AppObjectsColors.radioButtonColors()
                                )
                            }
                        }
                    }
                }

                if (selectedDefaultTheme == DARK || selectedDefaultTheme == AMOLED) {
                    item {
                        SwitchRow(
                            state = selectedDefaultTheme == AMOLED,
                            text = stringResource(R.string.amoled_theme),
                            subText = stringResource(R.string.use_pure_black_background)
                        ) {
                            val theme = if (it) {
                                AMOLED
                            } else DARK

                            scope.launch {
                                ColorModesSettingsStore.defaultTheme.set(ctx, theme)
                            }
                        }
                    }
                }

                // Only show the dynamic colors switch when in SYSTEM view
                if (selectedDefaultTheme == SYSTEM) {
                    item {
                        SettingsSwitchRow(
                            setting = ColorModesSettingsStore.dynamicColor,
                            title = stringResource(R.string.dynamic_colors),
                            description = stringResource(R.string.dynamic_colors_desc)
                        )
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
                ColorSettingsStore.resetAll(
                    ctx,
//                    colorCustomisationMode,
//                    selectedDefaultTheme
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
                    currentColor = applyColor,
                    label = stringResource(R.string.color_mode_all),
                    backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f)
                ) { applyColor = it ?: Color.Black }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = DragonShape
        )
    }
}
