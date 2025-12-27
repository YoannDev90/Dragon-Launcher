package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import org.elnix.dragonlauncher.data.BaseSettingsStore
import org.elnix.dragonlauncher.data.workspaceDataStore
import org.json.JSONObject

object WorkspaceSettingsStore : BaseSettingsStore() {

    override val name: String = "Workspaces"

    private object Keys {
        const val WORKSPACE_STATE = "workspace_state"
    }

    private val WORKSPACE_KEY = stringPreferencesKey(Keys.WORKSPACE_STATE)

    // -------------------------------------------------------------------------
    // Backup export
    // -------------------------------------------------------------------------
    suspend fun getAll(ctx: Context): JSONObject {
        val prefs = ctx.workspaceDataStore.data.first()
        val json = prefs[WORKSPACE_KEY] ?: return JSONObject()
        return JSONObject(json)
    }


    // -------------------------------------------------------------------------
    // Backup import (strict)
    // -------------------------------------------------------------------------
    suspend fun setAll(ctx: Context, obj: JSONObject) {
        ctx.workspaceDataStore.edit { prefs ->
            prefs[WORKSPACE_KEY] = obj.toString()
        }
    }


    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------
    override suspend fun resetAll(ctx: Context) {
        ctx.workspaceDataStore.edit { prefs ->
            prefs.remove(WORKSPACE_KEY)
        }
    }
}
