package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.debugDatastore

object DebugSettingsStore {

    // -------------------------------------------------------------------------
    // Backup data class
    // -------------------------------------------------------------------------
    private data class DebugSettingsBackup(
        val debugEnabled: Boolean = false,
        val debugInfos: Boolean = false,
        val forceAppLanguageSelector: Boolean = false
    )

    private val defaults = DebugSettingsBackup()

    // -------------------------------------------------------------------------
    // Keys
    // -------------------------------------------------------------------------
    private val DEBUG_ENABLED = booleanPreferencesKey(defaults.debugEnabled.toString())
    private val DEBUG_INFOS = booleanPreferencesKey(defaults.debugInfos.toString())
    private val FORCE_APP_LANGUAGE_SELECTOR =
        booleanPreferencesKey(defaults.forceAppLanguageSelector.toString())

    // -------------------------------------------------------------------------
    // Accessors + Mutators
    // -------------------------------------------------------------------------
    fun getDebugEnabled(ctx: Context): Flow<Boolean> =
        ctx.debugDatastore.data.map { prefs ->
            prefs[DEBUG_ENABLED] ?: defaults.debugEnabled
        }

    suspend fun setDebugEnabled(ctx: Context, enabled: Boolean) {
        ctx.debugDatastore.edit { it[DEBUG_ENABLED] = enabled }
    }

    fun getDebugInfos(ctx: Context): Flow<Boolean> =
        ctx.debugDatastore.data.map { prefs ->
            prefs[DEBUG_INFOS] ?: defaults.debugInfos
        }

    suspend fun setDebugInfos(ctx: Context, enabled: Boolean) {
        ctx.debugDatastore.edit { it[DEBUG_INFOS] = enabled }
    }

    fun getForceAppLanguageSelector(ctx: Context): Flow<Boolean> =
        ctx.debugDatastore.data.map { prefs ->
            prefs[FORCE_APP_LANGUAGE_SELECTOR] ?: defaults.forceAppLanguageSelector
        }

    suspend fun setForceAppLanguageSelector(ctx: Context, enabled: Boolean) {
        ctx.debugDatastore.edit { it[FORCE_APP_LANGUAGE_SELECTOR] = enabled }
    }

    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------
    suspend fun resetAll(ctx: Context) {
        ctx.debugDatastore.edit { prefs ->
            prefs.remove(DEBUG_ENABLED)
            prefs.remove(DEBUG_INFOS)
            prefs.remove(FORCE_APP_LANGUAGE_SELECTOR)
        }
    }

    // -------------------------------------------------------------------------
    // Backup export
    // -------------------------------------------------------------------------
    suspend fun getAll(ctx: Context): Map<String, Boolean> {
        val prefs = ctx.debugDatastore.data.first()

        return buildMap {

            fun putIfNonDefault(key: String, value: Boolean?, defaultVal: Boolean) {
                if (value != null && value != defaultVal) {
                    put(key, value)
                }
            }

            putIfNonDefault(
                defaults.debugEnabled.toString(),
                prefs[DEBUG_ENABLED],
                defaults.debugEnabled
            )

            putIfNonDefault(
                defaults.debugInfos.toString(),
                prefs[DEBUG_INFOS],
                defaults.debugInfos
            )

            putIfNonDefault(
                defaults.forceAppLanguageSelector.toString(),
                prefs[FORCE_APP_LANGUAGE_SELECTOR],
                defaults.forceAppLanguageSelector
            )
        }
    }

    // -------------------------------------------------------------------------
    // Backup import
    // -------------------------------------------------------------------------
    suspend fun setAll(ctx: Context, backup: Map<String, Boolean>) {
        ctx.debugDatastore.edit { prefs ->
            backup[defaults.debugEnabled.toString()]?.let {
                prefs[DEBUG_ENABLED] = it
            }
            backup[defaults.debugInfos.toString()]?.let {
                prefs[DEBUG_INFOS] = it
            }
            backup[defaults.forceAppLanguageSelector.toString()]?.let {
                prefs[FORCE_APP_LANGUAGE_SELECTOR] = it
            }
        }
    }
}
