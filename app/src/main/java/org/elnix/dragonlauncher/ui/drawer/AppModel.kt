package org.elnix.dragonlauncher.ui.drawer

import org.elnix.dragonlauncher.data.SwipeActionSerializable

data class AppModel(
    val name: String,
    val packageName: String,
    val isSystem: Boolean,
) {
    val action = SwipeActionSerializable.LaunchApp(packageName)
}
