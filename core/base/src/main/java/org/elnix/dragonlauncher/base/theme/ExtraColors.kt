package org.elnix.dragonlauncher.base.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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
    angleLine = Color.Red,
    circle = Color(0xFF960000),
    launchApp = Color(0xFF55AAFF),
    openUrl = Color(0xFF66DD77),
    notificationShade = Color(0xFFFFBB44),
    controlPanel = Color(0xFFFF6688),
    openAppDrawer = Color(0xFFDD55FF),
    launcherSettings = Color.Red,
    lock = Color(0xFF555555),
    openFile = Color(0xFF00FFF7),
    reload = Color(0xFF886300),
    openRecentApps = Color(0xFF880081),
    openCircleNest = Color(0xFF1BEE14),
    goParentNest = Color(0xFF1BEE14)
)
val LocalExtraColors = staticCompositionLocalOf { DefaultExtraColors }
