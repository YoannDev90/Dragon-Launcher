package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.elnix.dragonlauncher.common.theme.AmoledDefault
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.ui.components.settings.asStateNull
import org.elnix.dragonlauncher.ui.theme.ExtraColors

@Composable
fun rememberExtraColors(): ExtraColors {

    /*  ─────────────  CUSTOM COLORS ─────────────  */

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
    val reloadAppsColor by ColorSettingsStore.reloadColor.asStateNull()
    val openRecentAppsColor by ColorSettingsStore.openRecentAppsColor.asStateNull()
    val openCircleNestColor by ColorSettingsStore.openCircleNestColor.asStateNull()
    val goParentNestColor by ColorSettingsStore.goParentNestColor.asStateNull()

    return ExtraColors(
        angleLineColor ?: AmoledDefault.AngleLineColor,
        circleColor ?: AmoledDefault.CircleColor,

        launchAppColor ?: AmoledDefault.LaunchAppColor,
        openUrlColor ?: AmoledDefault.OpenUrlColor,
        notificationShadeColor ?: AmoledDefault.NotificationShadeColor,
        controlPanelColor ?: AmoledDefault.ControlPanelColor,
        openAppDrawerColor ?: AmoledDefault.OpenAppDrawerColor,
        launcherSettingsColor ?: AmoledDefault.LauncherSettingsColor,
        lockColor ?: AmoledDefault.LockColor,
        openFileColor ?: AmoledDefault.OpenFileColor,
        reloadAppsColor ?: AmoledDefault.ReloadColor,
        openRecentAppsColor ?: AmoledDefault.OpenRecentAppsColor,
        openCircleNestColor ?: AmoledDefault.OpenCircleNestColor,
        goParentNestColor ?: AmoledDefault.GoParentNestColor
    )
}
