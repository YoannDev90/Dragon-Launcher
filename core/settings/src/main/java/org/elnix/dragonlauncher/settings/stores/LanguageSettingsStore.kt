package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object LanguageSettingsStore : MapSettingsStore() {
    override val name: String = "Language"
    override val dataStoreName: DataStoreName
        get() = DataStoreName.LANGUAGE
    override val ALL: List<BaseSettingObject<*,*>>
        get() = listOf(keyLang)

    val keyLang = Settings.string(
        key = "pref_app_language",
        dataStoreName = dataStoreName,
        default = ""
    )
}
