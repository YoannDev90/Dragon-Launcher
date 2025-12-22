package org.elnix.dragonlauncher.utils.actions

import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.ui.theme.ExtraColors

fun actionColor(
    action: SwipeActionSerializable?,
    extra: ExtraColors
): Color =
    when (action) {
        is SwipeActionSerializable.LaunchApp -> extra.launchApp
        is SwipeActionSerializable.OpenUrl -> extra.openUrl
        SwipeActionSerializable.NotificationShade -> extra.notificationShade
        SwipeActionSerializable.ControlPanel -> extra.controlPanel
        SwipeActionSerializable.OpenAppDrawer -> extra.openAppDrawer
        SwipeActionSerializable.OpenDragonLauncherSettings -> extra.launcherSettings
        SwipeActionSerializable.Lock -> extra.lock
        is SwipeActionSerializable.OpenFile -> extra.openFile
        is SwipeActionSerializable.ReloadApps -> extra.reload
        SwipeActionSerializable.OpenRecentApps -> extra.openRecentApps
        null -> Color.Unspecified
    }
