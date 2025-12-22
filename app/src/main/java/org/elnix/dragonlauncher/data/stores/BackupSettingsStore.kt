package org.elnix.dragonlauncher.data.stores

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.BaseSettingsStore
import org.elnix.dragonlauncher.data.DataStoreName
import org.elnix.dragonlauncher.data.backupDatastore

object BackupSettingsStore : BaseSettingsStore() {

    override val name: String = "Backup"

    private data class SettingsBackup(
        val autoBackupEnabled: Boolean = false,
        val autoBackupUri: String? = null,
        val lastBackupTime: Long = System.currentTimeMillis()
    )

    // Cause at runtime, the app crashes due to early .entries initialization
    private val defaultBackupStores: Set<String>
        get() = DataStoreName.entries
            .filter { it.backupKey != null }
            .map { it.value }
            .toSet()


    private val defaults = SettingsBackup()

    private object Keys {
        val AUTO_BACKUP_ENABLED = booleanPreferencesKey("autoBackupEnabled")
        val AUTO_BACKUP_URI = stringPreferencesKey("autoBackupUri")
        val BACKUP_STORES = stringSetPreferencesKey("backupStores")
        val LAST_BACKUP_TIME = longPreferencesKey("lastBackupTime")
        val ALL = listOf(
            AUTO_BACKUP_ENABLED,
            AUTO_BACKUP_URI,
            BACKUP_STORES,
            LAST_BACKUP_TIME
        )
    }

    fun getAutoBackupEnabled(ctx: Context) = ctx
        .backupDatastore.data
        .map { it[Keys.AUTO_BACKUP_ENABLED] ?: defaults.autoBackupEnabled }

    suspend fun setAutoBackupEnabled(ctx: Context, enabled: Boolean) {
        ctx.backupDatastore.edit { it[Keys.AUTO_BACKUP_ENABLED] = enabled }
    }

    fun getAutoBackupUri(ctx: Context) = ctx
        .backupDatastore.data
        .map { it[Keys.AUTO_BACKUP_URI]?.ifBlank { null } }

    suspend fun setAutoBackupUri(ctx: Context, uri: Uri?) {
        ctx.backupDatastore.edit {
            it[Keys.AUTO_BACKUP_URI] = uri?.toString() ?: ""
        }
    }

    fun getBackupStores(ctx: Context) = ctx
        .backupDatastore.data
        .map { it[Keys.BACKUP_STORES] ?: defaultBackupStores }

    suspend fun setBackupStores(ctx: Context, stores: List<DataStoreName>) {
        ctx.backupDatastore.edit { prefs ->
            prefs[Keys.BACKUP_STORES] = stores.map { it.value }.toSet()
        }
    }

    fun getLastBackupTime(ctx: Context) = ctx
        .backupDatastore.data
        .map { it[Keys.LAST_BACKUP_TIME] ?: defaults.lastBackupTime }

    suspend fun setLastBackupTime(ctx: Context) {
        ctx.backupDatastore.edit { it[Keys.LAST_BACKUP_TIME] = System.currentTimeMillis() }
    }

    override suspend fun resetAll(ctx: Context) {
        ctx.backupDatastore.edit { prefs ->
            Keys.ALL.forEach { prefs.remove(it) }
        }
    }

    suspend fun getAll(ctx: Context): Map<String, Any> {
        val prefs = ctx.backupDatastore.data.first()

        return buildMap {
            fun putIfChanged(key: Preferences.Key<Boolean>, default: Boolean) {
                val v = prefs[key]
                if (v != null && v != default) put(key.name, v)
            }

            fun putIfChanged(key: Preferences.Key<String>, default: String?) {
                val v = prefs[key]
                if (v != null && v != default) put(key.name, v)
            }

            fun putIfChanged(key: Preferences.Key<Set<String>>, default: Set<String>) {
                val v = prefs[key]
                if (v != null && v != default) put(key.name, v)
            }

            putIfChanged(Keys.AUTO_BACKUP_ENABLED, defaults.autoBackupEnabled)
            putIfChanged(Keys.AUTO_BACKUP_URI, null)
            putIfChanged(Keys.BACKUP_STORES, defaultBackupStores)
        }
    }

    suspend fun setAll(ctx: Context, backup: Map<String, Any?>) {
        ctx.backupDatastore.edit { prefs ->
            fun applyString(key: Preferences.Key<String>) {
                val raw = backup[key.name] ?: return
                val stringValue = when (raw) {
                    is String -> raw
                    else -> raw.toString()
                }
                prefs[key] = stringValue
            }

            fun applyBoolean(key: Preferences.Key<Boolean>) {
                val raw = backup[key.name] ?: return
                val boolValue = when (raw) {
                    is Boolean -> raw
                    is String -> raw.toBooleanStrictOrNull() ?: return
                    else -> return
                }
                prefs[key] = boolValue
            }

            fun applyStringSet(key: Preferences.Key<Set<String>>) {
                val raw = backup[key.name] ?: return
                val setValue = when (raw) {
                    is Set<*> -> raw.filterIsInstance<String>().toSet()
                    is List<*> -> raw.filterIsInstance<String>().toSet()
                    is String -> listOf(raw).toSet()
                    else -> return
                }
                prefs[key] = setValue
            }

            applyBoolean(Keys.AUTO_BACKUP_ENABLED)
            applyString(Keys.AUTO_BACKUP_URI)
            applyStringSet(Keys.BACKUP_STORES)
        }
    }
}
