package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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

    val primaryColor = SettingObject<Int?>(
        key = "primary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val onPrimaryColor = SettingObject<Int?>(
        key = "on_primary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val secondaryColor = SettingObject<Int?>(
        key = "secondary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val onSecondaryColor = SettingObject<Int?>(
        key = "on_secondary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val tertiaryColor = SettingObject<Int?>(
        key = "tertiary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val onTertiaryColor = SettingObject<Int?>(
        key = "on_tertiary_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val backgroundColor = SettingObject<Int?>(
        key = "background_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val onBackgroundColor = SettingObject<Int?>(
        key = "on_background_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val surfaceColor = SettingObject<Int?>(
        key = "surface_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val onSurfaceColor = SettingObject<Int?>(
        key = "on_surface_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val errorColor = SettingObject<Int?>(
        key = "error_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val onErrorColor = SettingObject<Int?>(
        key = "on_error_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val outlineColor = SettingObject<Int?>(
        key = "outline_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val angleLineColor = SettingObject<Int?>(
        key = "delete_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val circleColor = SettingObject<Int?>(
        key = "circle_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    /* ───────────── Action colors ───────────── */

    val launchAppColor = SettingObject<Int?>(
        key = "launch_app_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val openUrlColor = SettingObject<Int?>(
        key = "open_url_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val notificationShadeColor = SettingObject<Int?>(
        key = "notification_shade_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val controlPanelColor = SettingObject<Int?>(
        key = "control_panel_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val openAppDrawerColor = SettingObject<Int?>(
        key = "open_app_drawer_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val launcherSettingsColor = SettingObject<Int?>(
        key = "launcher_settings_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val lockColor = SettingObject<Int?>(
        key = "lock_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val openFileColor = SettingObject<Int?>(
        key = "open_file_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val reloadColor = SettingObject<Int?>(
        key = "reload_color",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val openRecentAppsColor = SettingObject<Int?>(
        key = "open_recent_apps",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val openCircleNestColor = SettingObject<Int?>(
        key = "open_circle_nest",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    val goParentNestColor = SettingObject<Int?>(
        key = "go_parent_nest",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.Int
    )

    /* ───────────── Registry ───────────── */

    override val ALL: List<SettingObject<Int?>>
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

        ALL.forEach { it.set(ctx, color().toArgb()) }
    }
}


private suspend fun applyThemeColors(ctx: Context, colors: ThemeColors) {

    /* ───────────── MaterialTheme Colors ───────────── */
    ColorSettingsStore.primaryColor.set(ctx, colors.Primary.toArgb())
    ColorSettingsStore.onPrimaryColor.set(ctx, colors.OnPrimary.toArgb())
    ColorSettingsStore.secondaryColor.set(ctx, colors.Secondary.toArgb())
    ColorSettingsStore.onSecondaryColor.set(ctx, colors.OnSecondary.toArgb())
    ColorSettingsStore.tertiaryColor.set(ctx, colors.Tertiary.toArgb())
    ColorSettingsStore.onTertiaryColor.set(ctx, colors.OnTertiary.toArgb())
    ColorSettingsStore.backgroundColor.set(ctx, colors.Background.toArgb())
    ColorSettingsStore.onBackgroundColor.set(ctx, colors.OnBackground.toArgb())
    ColorSettingsStore.surfaceColor.set(ctx, colors.Surface.toArgb())
    ColorSettingsStore.onSurfaceColor.set(ctx, colors.OnSurface.toArgb())
    ColorSettingsStore.errorColor.set(ctx, colors.Error.toArgb())
    ColorSettingsStore.onErrorColor.set(ctx, colors.OnError.toArgb())
    ColorSettingsStore.outlineColor.set(ctx, colors.Outline.toArgb())

    /* ───────────── Custom Colors ───────────── */
    ColorSettingsStore.angleLineColor.set(ctx, colors.AngleLineColor.toArgb())
    ColorSettingsStore.circleColor.set(ctx, colors.CircleColor.toArgb())

    /* ───────────── Actions Colors ───────────── */
    ColorSettingsStore.launchAppColor.set(ctx, colors.LaunchAppColor.toArgb())
    ColorSettingsStore.openUrlColor.set(ctx, colors.OpenUrlColor.toArgb())
    ColorSettingsStore.notificationShadeColor.set(ctx, colors.NotificationShadeColor.toArgb())
    ColorSettingsStore.controlPanelColor.set(ctx, colors.ControlPanelColor.toArgb())
    ColorSettingsStore.openAppDrawerColor.set(ctx, colors.OpenAppDrawerColor.toArgb())
    ColorSettingsStore.launcherSettingsColor.set(ctx, colors.LauncherSettingsColor.toArgb())
    ColorSettingsStore.lockColor.set(ctx, colors.LockColor.toArgb())
    ColorSettingsStore.openFileColor.set(ctx, colors.OpenFileColor.toArgb())
    ColorSettingsStore.reloadColor.set(ctx, colors.ReloadColor.toArgb())
    ColorSettingsStore.openRecentAppsColor.set(ctx, colors.OpenRecentAppsColor.toArgb())
    ColorSettingsStore.openCircleNestColor.set(ctx, colors.OpenCircleNestColor.toArgb())
    ColorSettingsStore.goParentNestColor.set(ctx, colors.GoParentNestColor.toArgb())
}
