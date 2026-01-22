package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object LanguageSettingsStore : MapSettingsStore() {
    override val name: String = "Language"
    override val dataStoreName: DataStoreName
        get() = DataStoreName.LANGUAGE
    override val ALL: List<SettingObject<*>>
        get() = listOf(keyLang)

    val keyLang = SettingObject<String?>(
        key = "pref_app_language",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.String
    )
}
