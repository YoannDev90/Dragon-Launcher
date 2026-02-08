package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.dummySwipePoint
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.components.AppPreviewTitle
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSlider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsItem
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader


@Composable
fun AppearanceTab(
    appsViewModel: AppsViewModel,
    navController: NavController,
    onBack: () -> Unit
) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val showLaunchingAppLabel by UiSettingsStore.showLaunchingAppLabel.flow(ctx)
        .collectAsState(initial = true)

    val showLaunchingAppIcon by UiSettingsStore.showLaunchingAppIcon.flow(ctx)
        .collectAsState(initial = true)

    val showAppAnglePreview by UiSettingsStore.showAnglePreview.flow(ctx)
        .collectAsState(initial = true)

    val appLabelIconOverlayTopPadding by UiSettingsStore.appLabelIconOverlayTopPadding.flow(ctx)
        .collectAsState(initial = 30)
    val appLabelOverlaySize by UiSettingsStore.appLabelOverlaySize.flow(ctx)
        .collectAsState(initial = 18)
    val appIconOverlaySize by UiSettingsStore.appIconOverlaySize.flow(ctx)
        .collectAsState(initial = 22)
    val iconsShape by DrawerSettingsStore.iconsShape.flow(ctx)
        .collectAsState(DrawerSettingsStore.iconsShape.default)

    var isDraggingAppPreviewOverlays by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (isDraggingAppPreviewOverlays) 1f else 0f,
        animationSpec = tween(150)
    )
    val offsetY by animateDpAsState(
        targetValue = if (isDraggingAppPreviewOverlays) 0.dp else (-20).dp,
        animationSpec = tween(150)
    )

    val icons by appsViewModel.pointIcons.collectAsState()


    SettingsLazyHeader(
        title = stringResource(R.string.appearance),
        onBack = onBack,
        helpText = stringResource(R.string.appearance_tab_text),
        onReset = {
            scope.launch {
                UiSettingsStore.resetAll(ctx)
            }
        }
    ) {

        item {
            SettingsItem(
                title = stringResource(R.string.color_selector),
                icon = Icons.Default.ColorLens
            ) { navController.navigate(SETTINGS.COLORS) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.wallpaper),
                icon = Icons.Default.Wallpaper
            ) { navController.navigate(SETTINGS.WALLPAPER) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.icon_pack),
                icon = Icons.Default.Palette
            ) { navController.navigate(SETTINGS.ICON_PACK) }
        }


        item {
            SettingsItem(
                title = stringResource(R.string.status_bar),
                icon = Icons.Default.SignalCellular4Bar
            ) { navController.navigate(SETTINGS.STATUS_BAR) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.theme_selector),
                icon = Icons.Default.Style
            ) { navController.navigate(SETTINGS.THEME) }
        }

        item {
            SettingsItem(
                title = stringResource(R.string.widgets_floating_apps),
                icon = Icons.Default.Widgets
            ) { navController.navigate(SETTINGS.FLOATING_APPS) }
        }

        item { TextDivider(stringResource(R.string.app_display)) }


        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.fullScreen,
                title = stringResource(R.string.fullscreen_app),
                description = stringResource(R.string.fullscreen_description)
            )
        }

        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.rgbLoading,
                title = stringResource(R.string.rgb_loading_settings),
                description = stringResource(R.string.rgb_loading_description)
            )
        }

        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.rgbLine,
                title = stringResource(R.string.rgb_line_selector),
                description = stringResource(R.string.rgb_line_selector_description)
            )
        }

        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.showLaunchingAppLabel,
                title = stringResource(R.string.show_launching_app_label),
                description = stringResource(R.string.show_launching_app_label_description)
            )
        }

        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.showLaunchingAppIcon,
                title = stringResource(R.string.show_launching_app_icon),
                description = stringResource(R.string.show_launching_app_icon_description)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .clip(DragonShape)
                    .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.7f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.adjustBrightness(0.2f),
                        DragonShape
                    )
                    .padding(8.dp)
            ) {
                SettingsSlider(
                    setting = UiSettingsStore.appLabelIconOverlayTopPadding,
                    title = stringResource(R.string.app_label_icon_overlay_top_padding),
                    valueRange = 0..1000,
                    color = MaterialTheme.colorScheme.primary,
                    onDragStateChange = { isDraggingAppPreviewOverlays = it }
                )

                SettingsSlider(
                    setting = UiSettingsStore.appLabelOverlaySize,
                    title = stringResource(R.string.app_label_overlay_size),
                    valueRange = 0..100,
                    color = MaterialTheme.colorScheme.primary,
                    onDragStateChange = { isDraggingAppPreviewOverlays = it }
                )

                SettingsSlider(
                    setting = UiSettingsStore.appIconOverlaySize,
                    title = stringResource(R.string.app_icon_overlay_size),
                    valueRange = 0..400,
                    color = MaterialTheme.colorScheme.primary,
                    onDragStateChange = { isDraggingAppPreviewOverlays = it }
                )
            }
        }

        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.showAppLaunchingPreview,
                title = stringResource(R.string.show_app_launch_preview),
                description = stringResource(R.string.show_app_launch_preview_description)
            )
        }

        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.showCirclePreview,
                title = stringResource(R.string.show_app_circle_preview),
                description = stringResource(R.string.show_app_circle_preview_description)
            )
        }

        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.showLinePreview,
                title = stringResource(R.string.show_app_line_preview),
                description = stringResource(R.string.show_app_line_preview_description)
            )
        }

        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.showAnglePreview,
                title = stringResource(
                    R.string.show_app_angle_preview,
                    if (!showAppAnglePreview) stringResource(R.string.do_you_hate_it) else ""
                ),
                description = stringResource(R.string.show_app_angle_preview_description)
            )
        }

        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.showAppPreviewIconCenterStartPosition,
                title = stringResource(R.string.show_app_icon_start_drag_position),
                description = stringResource(R.string.show_app_icon_start_drag_position_description)
            )
        }

        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.linePreviewSnapToAction,
                title = stringResource(R.string.line_preview_snap_to_action),
                description = stringResource(R.string.line_preview_snap_to_action_description)
            )
        }


        item {
            SettingsSwitchRow(
                setting = UiSettingsStore.showAllActionsOnCurrentCircle,
                title = stringResource(R.string.show_all_actions_on_current_circle),
                description = stringResource(R.string.show_all_actions_on_current_circle_description)
            )
        }
    }

    if (isDraggingAppPreviewOverlays) {
        AppPreviewTitle(
            offsetY = offsetY,
            alpha = alpha,
            pointIcons = icons,
            point = dummySwipePoint(SwipeActionSerializable.OpenRecentApps).copy(
                customName = "Preview",
                id = icons.keys.random() // Kinda funny so I'll keep it :)
            ),
            iconsShape = iconsShape,
            topPadding = appLabelIconOverlayTopPadding.dp,
            labelSize = appLabelOverlaySize,
            iconSize = appIconOverlaySize,
            showLabel = showLaunchingAppLabel,
            showIcon = showLaunchingAppIcon
        )
    }
}
