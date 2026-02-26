package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.JsonSettingsStore

object StatusBarJsonSettingsStore : JsonSettingsStore() {
    override val name: String = "Status Bar Json"
    override val dataStoreName= DataStoreName.STATUS_BAR_JSON

    override val ALL: List<BaseSettingObject<*,*>>
        get() = listOf(
            statusBarJson
        )

    val statusBarJson = Settings.string(
        key = "statusBarJson",
        dataStoreName = dataStoreName,
        default = "{}"
    )

    override val jsonSetting: BaseSettingObject<String, String>
        get() = statusBarJson
}
