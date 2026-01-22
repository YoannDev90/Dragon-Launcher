package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.JsonSettingsStore
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.TypedSettingObject

object AppsSettingsStore : JsonSettingsStore() {
    override val name: String = "Apps"
    override val dataStoreName= DataStoreName.APPS

    private val RAW_JSON = TypedSettingObject(
        key = "cached_apps_json",
        dataStoreName = dataStoreName,
        default = "",
        type = SettingType.String
    )

    override val ALL = listOf(
        RAW_JSON
    )

    override val jsonSetting = RAW_JSON

//    override suspend fun resetAll(ctx: Context) {
//        CACHED_APPS.reset(ctx)
//    }
//
//    override suspend fun getAll(ctx: Context): JSONObject {
//        return CACHED_APPS.get(ctx)
//    }
//
//    override suspend fun setAll(ctx: Context, value: JSONObject) {
//        CACHED_APPS.set(ctx, value)
//    }
}
