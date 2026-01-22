package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.JsonSettingsStore

object AppsSettingsStore : JsonSettingsStore() {
    override val name: String = "Apps"
    override val dataStoreName= DataStoreName.APPS

    override val ALL: List<SettingObject<*>>
        get() = listOf(
            RAW_JSON
        )

    private val RAW_JSON = SettingObject(
        key = "cached_apps_json",
        dataStoreName = dataStoreName,
        default = "",
        type = SettingType.String
    )

    override val jsonSetting = RAW_JSON
}
