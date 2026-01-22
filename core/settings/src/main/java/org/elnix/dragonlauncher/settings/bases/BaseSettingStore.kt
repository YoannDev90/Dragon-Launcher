package org.elnix.dragonlauncher.settings.bases

import android.content.Context
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.json.JSONObject


abstract class BaseSettingsStore<T> {

    abstract val name: String
    abstract val dataStoreName: DataStoreName

    @Suppress("PropertyName")
    abstract val ALL: List<SettingObject<*>>

    suspend fun resetAll(ctx: Context) {
        ALL.forEach { it.reset(ctx) }
    }

    abstract suspend fun getAll(ctx: Context): T
    abstract suspend fun setAll(ctx: Context, value: T)

    abstract suspend fun exportForBackup(ctx: Context): JSONObject?
    abstract suspend fun importFromBackup(ctx: Context, json: JSONObject)
}
