package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.theme.AmoledDefault
import org.elnix.dragonlauncher.common.theme.ThemeColors
import org.elnix.dragonlauncher.common.utils.colors.randomColor
import org.elnix.dragonlauncher.enumsui.ColorCustomisationMode
import org.elnix.dragonlauncher.enumsui.DefaultThemes
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore
import org.elnix.dragonlauncher.settings.getDefaultColorScheme


object ColorSettingsStore : MapSettingsStore() {
    override val name: String = "Colors"
    override val dataStoreName = DataStoreName.COLOR


    /* ───────────── Colors ───────────── */

    val primaryColor = SettingObject<Color?>(
        key = "primary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val onPrimaryColor = SettingObject<Color?>(
        key = "on_primary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val secondaryColor = SettingObject<Color?>(
        key = "secondary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val onSecondaryColor = SettingObject<Color?>(
        key = "on_secondary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val tertiaryColor = SettingObject<Color?>(
        key = "tertiary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val onTertiaryColor = SettingObject<Color?>(
        key = "on_tertiary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val backgroundColor = SettingObject<Color?>(
        key = "background_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val onBackgroundColor = SettingObject<Color?>(
        key = "on_background_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val surfaceColor = SettingObject<Color?>(
        key = "surface_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val onSurfaceColor = SettingObject<Color?>(
        key = "on_surface_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val errorColor = SettingObject<Color?>(
        key = "error_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val onErrorColor = SettingObject<Color?>(
        key = "on_error_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val outlineColor = SettingObject<Color?>(
        key = "outline_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val angleLineColor = SettingObject<Color?>(
        key = "delete_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val circleColor = SettingObject<Color?>(
        key = "circle_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    /* ───────────── Action colors ───────────── */

    val launchAppColor = SettingObject<Color?>(
        key = "launch_app_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val openUrlColor = SettingObject<Color?>(
        key = "open_url_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val notificationShadeColor = SettingObject<Color?>(
        key = "notification_shade_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val controlPanelColor = SettingObject<Color?>(
        key = "control_panel_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val openAppDrawerColor = SettingObject<Color?>(
        key = "open_app_drawer_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val launcherSettingsColor = SettingObject<Color?>(
        key = "launcher_settings_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val lockColor = SettingObject<Color?>(
        key = "lock_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val openFileColor = SettingObject<Color?>(
        key = "open_file_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val reloadColor = SettingObject<Color?>(
        key = "reload_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val openRecentAppsColor = SettingObject<Color?>(
        key = "open_recent_apps",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val openCircleNestColor = SettingObject<Color?>(
        key = "open_circle_nest",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    val goParentNestColor = SettingObject<Color?>(
        key = "go_parent_nest",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Color
    )

    /* ───────────── Registry ───────────── */

    override val ALL: List<SettingObject<Color?>>
        get() = listOf(
            primaryColor,
            onPrimaryColor,
            secondaryColor,
            onSecondaryColor,
            tertiaryColor,
            onTertiaryColor,
            backgroundColor,
            onBackgroundColor,
            surfaceColor,
            onSurfaceColor,
            errorColor,
            onErrorColor,
            outlineColor,
            angleLineColor,
            circleColor,
            launchAppColor,
            openUrlColor,
            notificationShadeColor,
            controlPanelColor,
            openAppDrawerColor,
            launcherSettingsColor,
            lockColor,
            openFileColor,
            reloadColor,
            openRecentAppsColor,
            openCircleNestColor,
            goParentNestColor
        )

    suspend fun resetColors(
        ctx: Context,
        selectedColorCustomisationMode: ColorCustomisationMode,
        selectedMode: DefaultThemes
    ) {

        val themeColors: ThemeColors = when (selectedColorCustomisationMode) {
            ColorCustomisationMode.DEFAULT -> getDefaultColorScheme(ctx, selectedMode)
            ColorCustomisationMode.NORMAL, ColorCustomisationMode.ALL -> AmoledDefault
        }

        applyThemeColors(ctx, themeColors)
    }


    suspend fun setAllRandomColors(ctx: Context) {
        setAllColors(ctx) { randomColor() }
    }

    suspend fun setAllSameColors(ctx: Context, color: Color) {
        setAllColors(ctx) { color }
    }

    suspend fun setAllColors(ctx: Context, color: () -> Color) {

        ALL.forEach { it.set(ctx, color()) }
    }
}


private suspend fun applyThemeColors(ctx: Context, colors: ThemeColors) {

    /* ───────────── MaterialTheme Colors ───────────── */
    ColorSettingsStore.primaryColor.set(ctx, colors.Primary)
    ColorSettingsStore.onPrimaryColor.set(ctx, colors.OnPrimary)
    ColorSettingsStore.secondaryColor.set(ctx, colors.Secondary)
    ColorSettingsStore.onSecondaryColor.set(ctx, colors.OnSecondary)
    ColorSettingsStore.tertiaryColor.set(ctx, colors.Tertiary)
    ColorSettingsStore.onTertiaryColor.set(ctx, colors.OnTertiary)
    ColorSettingsStore.backgroundColor.set(ctx, colors.Background)
    ColorSettingsStore.onBackgroundColor.set(ctx, colors.OnBackground)
    ColorSettingsStore.surfaceColor.set(ctx, colors.Surface)
    ColorSettingsStore.onSurfaceColor.set(ctx, colors.OnSurface)
    ColorSettingsStore.errorColor.set(ctx, colors.Error)
    ColorSettingsStore.onErrorColor.set(ctx, colors.OnError)
    ColorSettingsStore.outlineColor.set(ctx, colors.Outline)

    /* ───────────── Custom Colors ───────────── */
    ColorSettingsStore.angleLineColor.set(ctx, colors.AngleLineColor)
    ColorSettingsStore.circleColor.set(ctx, colors.CircleColor)

    /* ───────────── Actions Colors ───────────── */
    ColorSettingsStore.launchAppColor.set(ctx, colors.LaunchAppColor)
    ColorSettingsStore.openUrlColor.set(ctx, colors.OpenUrlColor)
    ColorSettingsStore.notificationShadeColor.set(ctx, colors.NotificationShadeColor)
    ColorSettingsStore.controlPanelColor.set(ctx, colors.ControlPanelColor)
    ColorSettingsStore.openAppDrawerColor.set(ctx, colors.OpenAppDrawerColor)
    ColorSettingsStore.launcherSettingsColor.set(ctx, colors.LauncherSettingsColor)
    ColorSettingsStore.lockColor.set(ctx, colors.LockColor)
    ColorSettingsStore.openFileColor.set(ctx, colors.OpenFileColor)
    ColorSettingsStore.reloadColor.set(ctx, colors.ReloadColor)
    ColorSettingsStore.openRecentAppsColor.set(ctx, colors.OpenRecentAppsColor)
    ColorSettingsStore.openCircleNestColor.set(ctx, colors.OpenCircleNestColor)
    ColorSettingsStore.goParentNestColor.set(ctx, colors.GoParentNestColor)
}
