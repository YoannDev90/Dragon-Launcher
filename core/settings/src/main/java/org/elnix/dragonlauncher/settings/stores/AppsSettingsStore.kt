package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.settings.BaseSettingsStore
import org.elnix.dragonlauncher.settings.appsDatastore
import org.json.JSONObject

object AppsSettingsStore : BaseSettingsStore<JSONObject>() {
    override val name: String = "Apps"

    private val DATASTORE_KEY = stringPreferencesKey("cached_apps_json")

    suspend fun getCachedApps(ctx: Context): String? {
        return ctx.appsDatastore.data
            .map { it[DATASTORE_KEY] }
            .firstOrNull()
    }

    suspend fun saveCachedApps(ctx: Context, json: String) {
        ctx.appsDatastore.edit { prefs ->
            prefs[DATASTORE_KEY] = json
        }
    }

    override suspend fun resetAll(ctx: Context) {
        ctx.appsDatastore.edit {
            it.remove(DATASTORE_KEY)
        }
    }

    override suspend fun getAll(ctx: Context): JSONObject {
        val prefs = ctx.appsDatastore.data.first()
        val json = prefs[DATASTORE_KEY] ?: return JSONObject()
        return JSONObject(json)
    }


    override suspend fun setAll(ctx: Context, value: JSONObject) {
        ctx.appsDatastore.edit { prefs ->
            prefs[DATASTORE_KEY] = value.toString()
        }
    }
}
