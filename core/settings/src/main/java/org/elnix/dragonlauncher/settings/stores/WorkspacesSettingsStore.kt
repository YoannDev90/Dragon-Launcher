package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import org.elnix.dragonlauncher.settings.BaseSettingsStore
import org.elnix.dragonlauncher.settings.workspaceDataStore
import org.json.JSONObject

object WorkspaceSettingsStore : BaseSettingsStore<JSONObject>() {

    override val name: String = "Workspaces"

    private object Keys {
        val WORKSPACE_KEY = stringPreferencesKey("workspace_state")
    }


    override suspend fun resetAll(ctx: Context) {
        ctx.workspaceDataStore.edit { prefs ->
            prefs.remove(Keys.WORKSPACE_KEY)
        }
    }

    override suspend fun getAll(ctx: Context): JSONObject {
        val prefs = ctx.workspaceDataStore.data.first()
        val json = prefs[Keys.WORKSPACE_KEY] ?: return JSONObject()
        return JSONObject(json)
    }


    override suspend fun setAll(ctx: Context, value: JSONObject) {
        ctx.workspaceDataStore.edit { prefs ->
            prefs[Keys.WORKSPACE_KEY] = value.toString()
        }
    }
}
