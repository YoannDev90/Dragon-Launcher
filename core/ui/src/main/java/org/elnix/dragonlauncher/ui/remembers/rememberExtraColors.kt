package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.elnix.dragonlauncher.common.theme.AmoledDefault
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.ui.theme.ExtraColors

@Composable
fun rememberExtraColors(): ExtraColors {

    val ctx = LocalContext.current


    /*  ─────────────  CUSTOM COLORS ─────────────  */

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
    val reloadAppsColor by ColorSettingsStore.reloadColor.flow(ctx).collectAsState(initial = null)
    val openRecentAppsColor by ColorSettingsStore.openRecentAppsColor.flow(ctx).collectAsState(initial = null)
    val openCircleNestColor by ColorSettingsStore.openCircleNestColor.flow(ctx).collectAsState(initial = null)
    val goParentNestColor by ColorSettingsStore.goParentNestColor.flow(ctx).collectAsState(initial = null)

    return remember {
        ExtraColors(
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
}
