package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.ImageBitmap
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.StatusBarSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.models.BackupViewModel
import org.elnix.dragonlauncher.models.FloatingAppsViewModel

val LocalIcons = compositionLocalOf<Map<String, ImageBitmap>> { error("No icons provided") }
val LocalIconShape = compositionLocalOf<IconShape> { error("No iconShape Provided") }
val LocalNests = compositionLocalOf<List<CircleNest>> { error("No nests provided") }
val LocalPoints = compositionLocalOf<List<SwipePointSerializable>> { error("No points provided") }
val LocalDefaultPoint = compositionLocalOf<SwipePointSerializable> { error("No default point provided") }
val LocalStatusBarElements = compositionLocalOf<List<StatusBarSerializable>> {
    error("No status bar elements provided")
}
val LocalShowStatusBar = compositionLocalOf<Boolean> {
    error("No show status bar provided")
}


val LocalAppsViewModel = compositionLocalOf<AppsViewModel> {
    error("No AppsViewModel bar provided")
}


val LocalAppLifecycleViewModel = compositionLocalOf<AppLifecycleViewModel> {
    error("No AppLifecycleViewModel bar provided")
}


val LocalBackupViewModel = compositionLocalOf<BackupViewModel> {
    error("No BackupViewModel bar provided")
}

val LocalFloatingAppsViewModel = compositionLocalOf<FloatingAppsViewModel> {
    error("No FloatingAppsViewModel bar provided")
}
