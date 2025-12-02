package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.privateSettingsStore

object PrivateSettingsStore {

    private data class PrivateSettingsBackup(
        val hasSeenWelcome: Boolean = false,
        val hasInitialized: Boolean = false,
    )
    private val defaults = PrivateSettingsBackup()

    private val HAS_SEEN_WELCOME = booleanPreferencesKey(defaults.hasSeenWelcome.toString())
    private val HAS_INITIALIZED = booleanPreferencesKey(defaults.hasInitialized.toString())

    fun getHasSeenWelcome(ctx: Context): Flow<Boolean> =
        ctx.privateSettingsStore.data.map { prefs ->
            prefs[HAS_SEEN_WELCOME] ?: defaults.hasSeenWelcome
        }

    suspend fun setHasSeenWelcome(ctx: Context, value: Boolean) {
        ctx.privateSettingsStore.edit { it[HAS_SEEN_WELCOME] = value }
    }

    fun getHasInitialized(ctx: Context): Flow<Boolean> =
        ctx.privateSettingsStore.data.map { prefs ->
            prefs[HAS_INITIALIZED] ?: defaults.hasInitialized
        }

    suspend fun setHasInitialized(ctx: Context, value: Boolean) {
        ctx.privateSettingsStore.edit { it[HAS_INITIALIZED] = value }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.privateSettingsStore.edit { prefs ->
            prefs.remove(HAS_SEEN_WELCOME)
            prefs.remove(HAS_INITIALIZED)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.privateSettingsStore.data.first()

        return buildMap {
            fun putIfNonDefault(key: String, value: Any?, default: Any?) {
                if (value != null && value != default) {
                    put(key, value.toString())
                }
            }

            putIfNonDefault(defaults.hasSeenWelcome.toString(), prefs[HAS_SEEN_WELCOME], defaults.hasSeenWelcome)
            putIfNonDefault(defaults.hasInitialized.toString(), prefs[HAS_INITIALIZED], defaults.hasInitialized)
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, String>) {
        ctx.privateSettingsStore.edit { prefs ->

            data[defaults.hasSeenWelcome.toString()]?.let { raw ->
                runCatching { raw.toBooleanStrictOrNull() ?: false }
                    .getOrNull()
                    ?.let { prefs[HAS_SEEN_WELCOME] = it }
            }

            data[defaults.hasInitialized.toString()]?.let { raw ->
                runCatching { raw.toBooleanStrictOrNull() ?: false }
                    .getOrNull()
                    ?.let { prefs[HAS_INITIALIZED] = it }
            }
        }
    }
}
