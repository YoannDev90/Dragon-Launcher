package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.JsonObjectSettingsStore

object WorkspaceSettingsStore : JsonObjectSettingsStore() {

    override val name: String = "Workspaces"
    override val dataStoreName= DataStoreName.WORKSPACES


    override val ALL: List<BaseSettingObject<*,*>>
        get() = listOf(
            RAW_JSON
        )

    private val RAW_JSON = Settings.string(
        key = "workspace_state",
        dataStoreName = dataStoreName,
        default = ""
    )

    override val jsonSetting = RAW_JSON
}
