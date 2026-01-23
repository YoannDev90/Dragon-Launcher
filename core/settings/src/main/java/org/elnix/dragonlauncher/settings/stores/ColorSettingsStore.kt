package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.theme.AmoledDefault
import org.elnix.dragonlauncher.common.theme.ThemeColors
import org.elnix.dragonlauncher.common.utils.colors.randomColor
import org.elnix.dragonlauncher.enumsui.ColorCustomisationMode
import org.elnix.dragonlauncher.enumsui.DefaultThemes
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore
import org.elnix.dragonlauncher.settings.getDefaultColorScheme


object ColorSettingsStore : MapSettingsStore() {
    override val name: String = "Colors"
    override val dataStoreName = DataStoreName.COLOR


    /* ───────────── Colors ───────────── */

    val primaryColor = Settings.color(
        key = "primary_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.Primary
    )

    val onPrimaryColor = Settings.color(
        key = "on_primary_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OnPrimary
    )

    val secondaryColor = Settings.color(
        key = "secondary_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.Secondary
    )

    val onSecondaryColor = Settings.color(
        key = "on_secondary_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OnSecondary
    )

    val tertiaryColor = Settings.color(
        key = "tertiary_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.Tertiary
    )

    val onTertiaryColor = Settings.color(
        key = "on_tertiary_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OnTertiary
    )

    val backgroundColor = Settings.color(
        key = "background_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.Background
    )

    val onBackgroundColor = Settings.color(
        key = "on_background_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OnBackground
    )

    val surfaceColor = Settings.color(
        key = "surface_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.Surface
    )

    val onSurfaceColor = Settings.color(
        key = "on_surface_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OnSecondary
    )

    val errorColor = Settings.color(
        key = "error_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.Error
    )

    val onErrorColor = Settings.color(
        key = "on_error_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OnError
    )

    val outlineColor = Settings.color(
        key = "outline_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.Outline
    )

    val angleLineColor = Settings.color(
        key = "angle_line_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.AngleLineColor
    )

    val circleColor = Settings.color(
        key = "circle_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.CircleColor
    )

    /* ───────────── Action colors ───────────── */

    val launchAppColor = Settings.color(
        key = "launch_app_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.LaunchAppColor
    )

    val openUrlColor = Settings.color(
        key = "open_url_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OpenUrlColor
    )

    val notificationShadeColor = Settings.color(
        key = "notification_shade_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.NotificationShadeColor
    )

    val controlPanelColor = Settings.color(
        key = "control_panel_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.ControlPanelColor
    )

    val openAppDrawerColor = Settings.color(
        key = "open_app_drawer_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OpenAppDrawerColor
    )

    val launcherSettingsColor = Settings.color(
        key = "launcher_settings_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.LauncherSettingsColor
    )

    val lockColor = Settings.color(
        key = "lock_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.LockColor
    )

    val openFileColor = Settings.color(
        key = "open_file_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OpenFileColor
    )

    val reloadColor = Settings.color(
        key = "reload_color",
        dataStoreName = dataStoreName,
        default = AmoledDefault.ReloadColor
    )

    val openRecentAppsColor = Settings.color(
        key = "open_recent_apps",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OpenRecentAppsColor
    )

    val openCircleNestColor = Settings.color(
        key = "open_circle_nest",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OpenCircleNestColor
    )

    val goParentNestColor = Settings.color(
        key = "go_parent_nest",
        dataStoreName = dataStoreName,
        default = AmoledDefault.GoParentNestColor
    )

    /* ───────────── Registry ───────────── */

    override val ALL: List<BaseSettingObject<Color, Int>>
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


suspend fun applyThemeColors(ctx: Context, colors: ThemeColors) {

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
