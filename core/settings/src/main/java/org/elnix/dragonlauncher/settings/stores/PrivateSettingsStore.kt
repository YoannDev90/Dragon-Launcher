package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object PrivateSettingsStore : MapSettingsStore() {

    override val name: String = "Private"
    override val dataStoreName: DataStoreName = DataStoreName.PRIVATE_SETTINGS

    val hasSeenWelcome = Settings.boolean(
        key = "hasSeenWelcome",
        dataStoreName = dataStoreName,
        default = false
    )

    val hasInitialized = Settings.boolean(
        key = "hasInitialized",
        dataStoreName = dataStoreName,
        default = false
    )

    val showSetDefaultLauncherBanner = Settings.boolean(
        key = "showSetDefaultLauncherBanner",
        dataStoreName = dataStoreName,
        default = true
    )

    val showMethodAsking = Settings.boolean(
        key = "showMethodAsking",
        dataStoreName = dataStoreName,
        default = false
    )

    val lastSeenVersionCode = Settings.int(
        key = "lastSeenVersionCode",
        dataStoreName = dataStoreName,
        default = 0,
        allowedRange = 0..Int.MAX_VALUE
    )

    override val ALL: List<BaseSettingObject<*,*>> = listOf(
        hasSeenWelcome,
        hasInitialized,
        showSetDefaultLauncherBanner,
        showMethodAsking,
        lastSeenVersionCode
    )
}
