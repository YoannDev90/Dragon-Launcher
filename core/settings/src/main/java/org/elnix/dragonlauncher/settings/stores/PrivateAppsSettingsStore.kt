package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.JsonArraySettingsStore

object PrivateAppsSettingsStore : JsonArraySettingsStore() {
    override val name: String = "Apps"
    override val dataStoreName= DataStoreName.PRIVATE_APPS

    override val ALL: List<BaseSettingObject<*,*>>
        get() = listOf(jsonSetting)


    override val jsonSetting =  Settings.string(
        key = "private_assigned_packages_json",
        dataStoreName = dataStoreName,
        default = ""
    )
}
