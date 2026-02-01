package org.elnix.dragonlauncher.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.theme.AmoledDefault

data class ExtraColors(
    val angleLine: Color,
    val circle: Color,
    val launchApp: Color,
    val openUrl: Color,
    val notificationShade: Color,
    val controlPanel: Color,
    val openAppDrawer: Color,
    val launcherSettings: Color,
    val lock: Color,
    val openFile: Color,
    val reload: Color,
    val openRecentApps: Color,
    val openCircleNest: Color,
    val goParentNest: Color
)


val DefaultExtraColors = ExtraColors(
    angleLine = AmoledDefault.AngleLineColor,
    circle = AmoledDefault.CircleColor,
    launchApp = AmoledDefault.LaunchAppColor,
    openUrl = AmoledDefault.OpenUrlColor,
    notificationShade = AmoledDefault.NotificationShadeColor,
    controlPanel = AmoledDefault.ControlPanelColor,
    openAppDrawer = AmoledDefault.OpenAppDrawerColor,
    launcherSettings = AmoledDefault.LauncherSettingsColor,
    lock = AmoledDefault.LockColor,
    openFile = AmoledDefault.OpenFileColor,
    reload = AmoledDefault.ReloadColor,
    openRecentApps = AmoledDefault.OpenRecentAppsColor,
    openCircleNest = AmoledDefault.OpenCircleNestColor,
    goParentNest = AmoledDefault.GoParentNestColor
)

val LocalExtraColors = staticCompositionLocalOf { DefaultExtraColors }
