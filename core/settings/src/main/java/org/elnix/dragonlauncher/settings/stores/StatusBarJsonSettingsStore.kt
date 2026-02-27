package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.JsonArraySettingsStore

object StatusBarJsonSettingsStore : JsonArraySettingsStore() {
    override val name: String = "Status Bar Json"
    override val dataStoreName= DataStoreName.STATUS_BAR_JSON

    override val ALL: List<BaseSettingObject<*,*>>
        get() = listOf(jsonSetting)

    override val jsonSetting = Settings.string(
        key = "statusBarJson",
        dataStoreName = dataStoreName,
        default = ""
    )
}
