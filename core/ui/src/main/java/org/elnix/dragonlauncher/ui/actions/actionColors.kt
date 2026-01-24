package org.elnix.dragonlauncher.ui.actions

import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.ui.theme.ExtraColors

fun actionColor(
    action: SwipeActionSerializable?,
    extra: ExtraColors,
    customColor: Color? = null
): Color =
    customColor
        ?: when (action) {
            is SwipeActionSerializable.LaunchApp, is SwipeActionSerializable.LaunchShortcut, is SwipeActionSerializable.OpenWidget -> extra.launchApp
            is SwipeActionSerializable.OpenUrl -> extra.openUrl
            SwipeActionSerializable.NotificationShade -> extra.notificationShade
            SwipeActionSerializable.ControlPanel -> extra.controlPanel
            SwipeActionSerializable.OpenAppDrawer -> extra.openAppDrawer
            SwipeActionSerializable.OpenDragonLauncherSettings -> extra.launcherSettings
            SwipeActionSerializable.Lock -> extra.lock
            is SwipeActionSerializable.OpenFile -> extra.openFile
            is SwipeActionSerializable.ReloadApps -> extra.reload
            SwipeActionSerializable.OpenRecentApps -> extra.openRecentApps
            is SwipeActionSerializable.OpenCircleNest -> extra.openCircleNest
            SwipeActionSerializable.GoParentNest -> extra.goParentNest
            null -> Color.Unspecified
            else -> Color.Unspecified
        }
