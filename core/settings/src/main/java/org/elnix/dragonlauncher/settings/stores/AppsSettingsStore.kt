package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.JsonSettingsStore

object AppsSettingsStore : JsonSettingsStore() {
    override val name: String = "Apps"
    override val dataStoreName= DataStoreName.APPS

    override val ALL: List<BaseSettingObject<*,*>>
        get() = listOf(
            cachedApps
        )

    val cachedApps = Settings.string(
        key = "cached_apps_json",
        dataStoreName = dataStoreName,
        default = ""
    )

    override val jsonSetting = cachedApps
}
