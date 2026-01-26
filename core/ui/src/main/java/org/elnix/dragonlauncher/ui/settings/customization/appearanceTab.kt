package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.components.AppPreviewTitle
import org.elnix.dragonlauncher.ui.components.settings.SettingsSlider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.TextDivider
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

    val showAppLaunchPreview by UiSettingsStore.showAppLaunchingPreview.flow(ctx)
        .collectAsState(initial = true)

    val showAppCirclePreview by UiSettingsStore.showCirclePreview.flow(ctx)
        .collectAsState(initial = true)

    val showLinePreview by UiSettingsStore.showLinePreview.flow(ctx)
        .collectAsState(initial = true)

    val showAppAnglePreview by UiSettingsStore.showAnglePreview.flow(ctx)
        .collectAsState(initial = true)

    val showAppPreviewIconCenterStartPosition by UiSettingsStore.showAppPreviewIconCenterStartPosition.flow(ctx)
        .collectAsState(initial = false)

    val linePreviewSnapToAction by UiSettingsStore.linePreviewSnapToAction.flow(ctx)
        .collectAsState(initial = false)

    val minAngleFromAPointToActivateIt by UiSettingsStore.minAngleFromAPointToActivateIt.flow(ctx)
        .collectAsState(initial = 0)

    val showAllActionsOnCurrentCircle by UiSettingsStore.showAllActionsOnCurrentCircle.flow(ctx)
        .collectAsState(initial = false)

    val appLabelIconOverlayTopPadding by UiSettingsStore.appLabelIconOverlayTopPadding.flow(ctx)
        .collectAsState(initial = 30)
    val appLabelOverlaySize by UiSettingsStore.appLabelOverlaySize.flow(ctx)
        .collectAsState(initial = 18)
    val appIconOverlaySize by UiSettingsStore.appIconOverlaySize.flow(ctx)
        .collectAsState(initial = 22)

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
                setting = UiSettingsStore.showLaunchingAppLabel,
                title = stringResource(R.string.show_launching_app_icon),
                description = stringResource(R.string.show_launching_app_icon_description)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.7f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.adjustBrightness(0.2f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            ) {
                SettingsSlider(
                    setting = UiSettingsStore.appLabelIconOverlayTopPadding,
                    title = stringResource(R.string.app_label_icon_overlay_top_padding),
                    valueRange = 0..360,
                    color = MaterialTheme.colorScheme.primary,
                )

                SettingsSlider(
                    setting = UiSettingsStore.appLabelOverlaySize,
                    title = stringResource(R.string.app_label_icon_overlay_top_padding),
                    valueRange = 0..70,
                    color = MaterialTheme.colorScheme.primary,
                )

                SettingsSlider(
                    setting = UiSettingsStore.appIconOverlaySize,
                    title = stringResource(R.string.app_label_icon_overlay_top_padding),
                    valueRange = 0..360,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        item {
            SwitchRow(
                showAppLaunchPreview,
                stringResource(R.string.show_app_launch_preview),
            ) { scope.launch { UiSettingsStore.showAppLaunchingPreview.set(ctx, it) } }
        }

        item {
            SwitchRow(
                showAppCirclePreview,
                stringResource(R.string.show_app_circle_preview),
            ) { scope.launch { UiSettingsStore.showCirclePreview.set(ctx, it) } }
        }

        item {
            SwitchRow(
                showLinePreview,
                stringResource(R.string.show_app_line_preview),
            ) { scope.launch { UiSettingsStore.showLinePreview.set(ctx, it) } }
        }

        item {
            SwitchRow(
                showAppAnglePreview,
                stringResource(
                    R.string.show_app_angle_preview,
                    if (!showAppAnglePreview) stringResource(R.string.do_you_hate_it) else ""
                ),
            ) { scope.launch { UiSettingsStore.showAnglePreview.set(ctx, it) } }
        }

        item {
            SwitchRow(
                showAppPreviewIconCenterStartPosition,
                stringResource(R.string.show_app_icon_start_drag_position),
            ) { scope.launch { UiSettingsStore.showAppPreviewIconCenterStartPosition.set(ctx, it) } }
        }

        item {
            SwitchRow(
                linePreviewSnapToAction,
                stringResource(R.string.line_preview_snap_to_action),
            ) { scope.launch { UiSettingsStore.linePreviewSnapToAction.set(ctx, it) } }
        }


        item {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.7f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.adjustBrightness(0.2f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            ) {
                SliderWithLabel(
                    label = stringResource(R.string.min_dist_to_activate_action),
                    value = minAngleFromAPointToActivateIt,
                    showValue = true,
                    valueRange = 0..360,
                    color = MaterialTheme.colorScheme.primary,
                    onReset = {
                        scope.launch {
                            UiSettingsStore.minAngleFromAPointToActivateIt.reset(ctx)
                        }
                    }
                ) {
                    scope.launch { UiSettingsStore.minAngleFromAPointToActivateIt.set(ctx, it) }
                }
            }
        }

        item {
            SwitchRow(
                state = showAllActionsOnCurrentCircle,
                text = stringResource(R.string.show_all_actions_on_current_circle),
            ) { scope.launch { UiSettingsStore.showAllActionsOnCurrentCircle.set(ctx, it) } }
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
            topPadding = appLabelIconOverlayTopPadding.dp,
            labelSize = appLabelOverlaySize,
            iconSize = appIconOverlaySize,
            showLabel = showLaunchingAppLabel,
            showIcon = showLaunchingAppIcon
        )
    }
}
