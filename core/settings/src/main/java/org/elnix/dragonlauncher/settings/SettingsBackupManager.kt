package org.elnix.dragonlauncher.settings

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.logging.logI
import org.elnix.dragonlauncher.common.logging.logW
import org.elnix.dragonlauncher.common.serializables.SwipeJson
import org.elnix.dragonlauncher.common.utils.BACKUP_TAG
import org.elnix.dragonlauncher.common.utils.getFilePathFromUri
import org.elnix.dragonlauncher.common.utils.hasUriReadWritePermission
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.settings.stores.BackupSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.json.JSONObject
import java.io.FileOutputStream

object SettingsBackupManager {

    /**
     * Automatic backup to pre-selected file
     */
    suspend fun triggerBackup(ctx: Context) {
        if (BackupSettingsStore.autoBackupEnabled.get(ctx) != true) {
            logW(BACKUP_TAG, "Auto-backup disabled")
            return
        }

        try {
            val uriString = BackupSettingsStore.autoBackupUri.get(ctx)
            if (uriString.isNullOrBlank()) {
                logW(BACKUP_TAG, "No backup URI set")
                return
            }

            val uri = uriString.toUri()
            val path = getFilePathFromUri(ctx, uri)

            if (!ctx.hasUriReadWritePermission(uri)) {
                this.logW(BACKUP_TAG, "URI permission expired!")
                ctx.showToast("Auto-backup URI expired. Please reselect file.")
                return
            }

            val selectedStores = BackupSettingsStore.backupStores.get(ctx)
                ?.mapNotNull {
                    storeValue -> DataStoreName.entries.find { it.value == storeValue }
                }
                ?.toSet()
                ?: backupableStores.keys


            exportSettings(ctx, uri, selectedStores)

            BackupSettingsStore.lastBackupTime.set(ctx, System.currentTimeMillis())
            logI(BACKUP_TAG, "Auto-backup completed to $path")

        } catch (e: Exception) {
            this.logE(BACKUP_TAG, "Auto-backup failed", e)
            if (e.message?.contains("permission") == true) {
                ctx.showToast("URI permission lost. Reselect backup file.")
            }
        }
    }


    suspend fun writeJson(ctx: Context, uri: Uri, json: JSONObject) {
        withContext(Dispatchers.IO) {
            ctx.contentResolver.openFileDescriptor(uri, "wt")?.use { pfd ->
                FileOutputStream(pfd.fileDescriptor).use { fos ->
                    fos.channel.truncate(0) // Ensure file is cleared before writing
                    fos.write(json.toString(2).toByteArray()) // Pretty print with 2 spaces
                    fos.flush()
                }
            } ?: run {
                this.logE(
                    BACKUP_TAG,
                    "Failed to open FileDescriptor - URI permission expired!"
                )
                throw IllegalStateException("Cannot write to URI - permission expired")
            }
        }
    }


    /**
     * Exports only the requested stores.
     * @param requestedStores List of DataStoreName objects
     */
    suspend fun exportSettings(
        ctx: Context,
        uri: Uri,
        requestedStores: Set<DataStoreName>
    ) {
        val json = JSONObject()

        allStores.forEach { entry ->
            val dataStoreName = entry.key
            val settingsStore = entry.value

            if (dataStoreName.backupKey in requestedStores.map { it.backupKey }) {
                settingsStore.exportForBackup(ctx)?.let {
                    json.put(dataStoreName.backupKey, it)
                }
            }
        }

        writeJson(ctx, uri, json)
    }

    /**
     * Imports app settings from a JSON object directly, without reading a file.
     *
     * This method supports both the current store-based backup system and the legacy
     * "actions" JSON array format. For each requested store, if the JSON contains
     * a corresponding object, it will be passed to the store's `importFromBackup`.
     *
     * @param ctx Context used for accessing DataStores
     * @param json Parsed JSONObject containing backup data
     * @param requestedStores List of DataStoreName objects specifying which stores to restore
     */
    suspend fun importSettingsFromJson(
        ctx: Context,
        json: JSONObject,
        requestedStores: Set<DataStoreName>
    ) {
        logD(BACKUP_TAG, json.toString())

        allStores.forEach { entry ->
            val dataStoreName = entry.key
            val settingsStore = entry.value

            val key = dataStoreName.backupKey
            if (key in requestedStores.map { it.backupKey }) {
                json.optJSONObject(key)?.let {
                    settingsStore.importFromBackup(ctx, it)
                }
            }
        }

        logE(BACKUP_TAG,json.optJSONArray("actions")?.toString() ?: "No actions")

        // LEGACY format: fallback for "actions" array
        json.optJSONArray("actions")?.let { actionsArray ->
            logD(BACKUP_TAG, "Fallback to legacy system")
            val legacyPoints = SwipeJson.decodeLegacy(actionsArray.toString())
            logE(BACKUP_TAG,legacyPoints.toString())
            SwipeSettingsStore.savePoints(ctx, legacyPoints)
        }
    }
}
