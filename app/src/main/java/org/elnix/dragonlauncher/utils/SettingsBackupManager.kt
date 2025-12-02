package org.elnix.dragonlauncher.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elnix.dragonlauncher.data.stores.ColorModesSettingsStore
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore
import org.elnix.dragonlauncher.data.stores.DebugSettingsStore
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.data.stores.LanguageSettingsStore
import org.elnix.dragonlauncher.data.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.data.stores.UiSettingsStore
import org.json.JSONObject
import java.io.OutputStreamWriter

object SettingsBackupManager {

    private const val TAG = "SettingsBackupManager"

    suspend fun exportSettings(ctx: Context, uri: Uri) {
        try {


            val json = JSONObject().apply {

                fun putIfNotEmpty(key: String, map: Map<String, *>) {
                    if (map.isNotEmpty()) put(key, JSONObject(map))
                }

                putIfNotEmpty("actions", SwipeSettingsStore.getAll(ctx))
                putIfNotEmpty("drawer", DrawerSettingsStore.getAll(ctx))
                putIfNotEmpty("color_mode", ColorModesSettingsStore.getAll(ctx))
                putIfNotEmpty("color", ColorSettingsStore.getAll(ctx))
                putIfNotEmpty("debug", DebugSettingsStore.getAll(ctx))
                putIfNotEmpty("language", LanguageSettingsStore.getAll(ctx))
                putIfNotEmpty("ui", UiSettingsStore.getAll(ctx))
            }


            Log.d(TAG, "Generated JSON: $json")

            withContext(Dispatchers.IO) {
                ctx.contentResolver.openOutputStream(uri)?.use { output ->
                    OutputStreamWriter(output).use { it.write(json.toString(2)) }
                }
            }

            Log.i(TAG, "Export completed successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error during export", e)
            throw e
        }
    }

    suspend fun importSettings(ctx: Context, uri: Uri) {
        try {
            val json = withContext(Dispatchers.IO) {
                ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }

            if (json.isNullOrBlank()) {
                Log.e(TAG, "Invalid or empty file")
                throw IllegalArgumentException("Invalid or empty file")
            }

            Log.d(TAG, "Loaded JSON: $json")
            val obj = JSONObject(json)


            withContext(Dispatchers.IO) {

                obj.optJSONObject("actions")?.let {
                    SwipeSettingsStore.setAll(ctx, jsonToStringMap(it))
                }
                obj.optJSONObject("drawer")?.let {
                    DrawerSettingsStore.setAll(ctx, jsonToBooleanMap(it))
                }
                obj.optJSONObject("color_mode")?.let {
                    ColorModesSettingsStore.setAll(ctx, jsonToStringMap(it))
                }
                obj.optJSONObject("color")?.let {
                    ColorSettingsStore.setAll(ctx, jsonToIntMap(it))
                }
                obj.optJSONObject("debug")?.let {
                    DebugSettingsStore.setAll(ctx, jsonToBooleanMap(it))
                }
                obj.optJSONObject("language")?.let {
                    LanguageSettingsStore.setAll(ctx, jsonToStringMap(it))
                }
                obj.optJSONObject("ui")?.let {
                    UiSettingsStore.setAll(ctx, jsonToStringMap(it))
                }
            }

            Log.i(TAG, "Import completed successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error during import", e)
            throw e
        }
    }

    private fun jsonToBooleanMap(obj: JSONObject) = buildMap {
        obj.keys().forEach { key -> put(key, obj.optBoolean(key)) }
    }

    private fun jsonToIntMap(obj: JSONObject) = buildMap {
        obj.keys().forEach { key -> put(key, obj.optInt(key)) }
    }

    private fun jsonToStringMap(obj: JSONObject) = buildMap {
        obj.keys().forEach { key -> put(key, obj.optString(key)) }
    }
}
