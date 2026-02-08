package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object BehaviorSettingsStore : MapSettingsStore() {

    override val name: String = "Behavior"

    override val dataStoreName = DataStoreName.BEHAVIOR

    override val ALL: List<BaseSettingObject <*, *> >
        get() = listOf(
            backAction,
            doubleClickAction,
            homeAction,
            keepScreenOn,
            leftPadding,
            rightPadding,
            topPadding,
            bottomPadding
        )

    val backAction = Settings.swipeAction(
        key = "backAction",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = SwipeActionSerializable.None
    )

    val doubleClickAction = Settings.swipeAction(
        key = "doubleClickAction",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = SwipeActionSerializable.None
    )

    val homeAction = Settings.swipeAction(
        key = "homeAction",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = SwipeActionSerializable.None
    )

    val keepScreenOn = Settings.boolean(
        key = "keepScreenOn",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = false
    )

    val leftPadding = Settings.int(
        key = "leftPadding",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = 60,
        allowedRange = 0..300
    )

    val rightPadding = Settings.int(
        key = "rightPadding",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = 60,
        allowedRange = 0..300
    )

    val topPadding = Settings.int(
        key = "upPadding",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = 80,
        allowedRange = 0..300
    )

    val bottomPadding = Settings.int(
        key = "downPadding",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = 100,
        allowedRange = 0..300
    )
}
