package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object BehaviorSettingsStore : MapSettingsStore() {

    override val name: String = "Behavior"

    override val dataStoreName = DataStoreName.BEHAVIOR

    override val ALL: List<SettingObject<*>>
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

    val backAction = SettingObject<SwipeActionSerializable?>(
        key = "backAction",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = null,
        type = SettingType.SwipeActionSerializable
    )

    val doubleClickAction = SettingObject<SwipeActionSerializable?>(
        key = "doubleClickAction",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = null,
        type = SettingType.SwipeActionSerializable
    )

    val homeAction = SettingObject<SwipeActionSerializable?>(
        key = "homeAction",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = null,
        type = SettingType.SwipeActionSerializable
    )

    val keepScreenOn = SettingObject(
        key = "keepScreenOn",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val leftPadding = SettingObject(
        key = "leftPadding",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = 60,
        type = SettingType.Int
    )

    val rightPadding = SettingObject(
        key = "rightPadding",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = 60,
        type = SettingType.Int
    )

    val topPadding = SettingObject(
        key = "upPadding",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = 80,
        type = SettingType.Int
    )

    val bottomPadding = SettingObject(
        key = "downPadding",
        dataStoreName = BackupSettingsStore.dataStoreName,
        default = 100,
        type = SettingType.Int
    )
}
