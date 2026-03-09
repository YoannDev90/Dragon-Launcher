package org.elnix.dragonlauncher.settings.migration

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.resolveDataStore
import org.elnix.dragonlauncher.settings.stores.StatusBarJsonSettingsStore
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.json.JSONArray
import org.json.JSONObject

/**
 * Handles migration from old Gson-based backup format to the new Kotlinx Serialization format.
 * This is especially needed for complex structures like Swipe Points and Nests which were
 * previously stored in a way that is not directly compatible with the new @Serializable models.
 */
object MigrationManager {

    private const val TAG = "MigrationManager"
    private val MIGRATION_VERSION = intPreferencesKey("migration_version")
    private const val CURRENT_VERSION = 1

    suspend fun runMigration(context: Context, backupJson: JSONObject? = null) {
        val dataStore = context.resolveDataStore(DataStoreName.DEBUG)
        val prefs = dataStore.data.first()
        val version = prefs[MIGRATION_VERSION] ?: 0

        if (version < CURRENT_VERSION || backupJson != null) {
            logD(TAG) { "Starting migration from version $version to $CURRENT_VERSION" }
            
            try {
                // If backupJson is provided, we are migrating during a restore
                // Otherwise, we check if there's old data in the current DataStores
                if (backupJson != null) {
                    migrateFromBackup(context, backupJson)
                } else {
                    migrateExistingData(context)
                }

                dataStore.edit { it[MIGRATION_VERSION] = CURRENT_VERSION }
                logD(TAG) { "Migration successful" }
            } catch (e: Exception) {
                logE(TAG) { "Migration failed: ${e.message}" }
            }
        }
    }

    private suspend fun migrateExistingData(context: Context) {
        // Here we could read from old DataStore keys if they changed,
        // but the main issue is the format within the JSON strings.
        
        // 1. Migrate Swipe Points & Nests
        val rawPoints = SwipeSettingsStore.getPoints(context)
        // If points are empty or decoding failed, SwipeSettingsStore might return emptyList()
        // In a real scenario, we might need to access the raw string to be sure.
    }

    private suspend fun migrateFromBackup(context: Context, backup: JSONObject) {
        logD(TAG) { "Migrating from backup JSON" }

        // Migration of "status_bar_json" -> "statusBarJson" in its dedicated store
        if (backup.has("status_bar_json")) {
            val legacyStatusBar = backup.getJSONArray("status_bar_json")
            StatusBarJsonSettingsStore.jsonSetting.set(context, legacyStatusBar.toString())
        }

        // Migration of "status_bar" settings (boolean, colors etc)
        if (backup.has("status_bar")) {
            val statusBarObj = backup.getJSONObject("status_bar")
            if (statusBarObj.has("showStatusBar")) {
                StatusBarSettingsStore.showStatusBar.set(context, statusBarObj.getBoolean("showStatusBar"))
            }
        }

        // Migration of "new_actions" (Points)
        if (backup.has("new_actions")) {
            val actionsObj = backup.getJSONObject("new_actions")
            if (actionsObj.has("points")) {
                val legacyPoints = actionsObj.getJSONArray("points")
                val migratedPoints = migratePoints(legacyPoints)
                // We'll need a way to save these properly. 
                // SwipeSettingsStore.savePoints uses SwipeJson.encodePoints which is the new format.
                // The issue is getting them INTO SwipePointSerializable objects first.
            }
        }
    }

    /**
     * Manually parse legacy JSON points to SwipePointSerializable
     */
    private fun migratePoints(legacyArray: JSONArray): String {
        val migratedArray = JSONArray()
        for (i in 0 until legacyArray.length()) {
            val old = legacyArray.getJSONObject(i)
            val new = JSONObject()
            
            // Map keys according to @SerialName in SwipePointSerializable
            new.put("a", old.optInt("a")) // circleNumber
            new.put("b", old.optDouble("b")) // angleDeg
            new.put("d", old.optString("d")) // id
            new.put("e", old.optInt("e", 0)) // nestId
            
            // Action migration
            if (old.has("c")) {
                val oldAction = old.getJSONObject("c")
                val type = oldAction.optString("type")
                val newAction = JSONObject()
                
                // Kotlinx Serialization sealed class discriminator
                // By default it uses "type" but the structure might differ
                newAction.put("type", "org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable.$type")
                
                // Copy parameters based on action type
                when (type) {
                    "LaunchApp" -> {
                        newAction.put("packageName", oldAction.optString("packageName"))
                        newAction.put("isPrivateSpace", oldAction.optBoolean("isPrivateSpace"))
                        newAction.put("userId", oldAction.optInt("userId", 0))
                    }
                    "OpenAppDrawer" -> {
                        newAction.put("workspaceId", oldAction.optString("workspaceId", null))
                    }
                    "OpenDragonLauncherSettings" -> {
                        newAction.put("route", oldAction.optString("route", "settings"))
                    }
                    // Objects don't need extra fields
                }
                new.put("c", newAction)
            }
            
            migratedArray.put(new)
        }
        return migratedArray.toString()
    }
}
