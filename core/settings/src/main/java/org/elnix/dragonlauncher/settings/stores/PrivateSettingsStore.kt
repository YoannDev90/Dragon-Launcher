package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object PrivateSettingsStore : MapSettingsStore() {

    override val name: String = "Private"
    override val dataStoreName: DataStoreName = DataStoreName.PRIVATE_SETTINGS

    val hasSeenWelcome = SettingObject(
        key = "hasSeenWelcome",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val hasInitialized = SettingObject(
        key = "hasInitialized",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val showSetDefaultLauncherBanner = SettingObject(
        key = "showSetDefaultLauncherBanner",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showMethodAsking = SettingObject(
        key = "showMethodAsking",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val lastSeenVersionCode = SettingObject(
        key = "lastSeenVersionCode",
        dataStoreName = dataStoreName,
        default = 0,
        type = SettingType.Int
    )

    override val ALL: List<SettingObject<*>> = listOf(
        hasSeenWelcome,
        hasInitialized,
        showSetDefaultLauncherBanner,
        showMethodAsking,
        lastSeenVersionCode
    )
}
