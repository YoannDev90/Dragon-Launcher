package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import org.elnix.dragonlauncher.common.FloatingAppObject
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipeJson
import org.elnix.dragonlauncher.common.utils.FLOATING_APPS_TAG
import org.elnix.dragonlauncher.settings.BaseSettingsStore
import org.elnix.dragonlauncher.settings.floatingAppsDatastore
import org.json.JSONArray
import org.json.JSONObject
import java.util.Collections.emptyList

object FloatingAppsSettingsStore : BaseSettingsStore<JSONObject>() {

    override val name: String = "Floating Apps"

    private object Keys {
        val FLOATING_APPS_KEY = stringPreferencesKey("floating_apps")
    }

    suspend fun loadFloatingApps(ctx: Context): List<FloatingAppObject> {
        return try {
            val allJson = getAll(ctx)
            logD(FLOATING_APPS_TAG, "Raw: $allJson")
            val floatingAppArray = allJson.optJSONArray("floating_apps") ?: return emptyList()

            val floatingApps = mutableListOf<FloatingAppObject>()
            for (i in 0 until floatingAppArray.length()) {
                val obj = floatingAppArray.getJSONObject(i)

                floatingApps.add(
                    FloatingAppObject(
                        id = obj.getInt("id"),
                        nestId = obj.getInt("nestId"),
                        action = SwipeJson.decodeAction(obj.getString("action"))
                            ?: SwipeActionSerializable.LaunchApp(ctx.packageName),
                        spanX = obj.optDouble("spanX", 1.0).toFloat(),
                        spanY = obj.optDouble("spanY", 1.0).toFloat(),
                        x = obj.optDouble("x", 0.0).toFloat(),
                        y = obj.optDouble("y", 0.0).toFloat(),
                        angle = obj.optDouble("angle", 0.0),
                        ghosted = obj.optBoolean("ghosted", false),
                        foreground = obj.optBoolean("foreground", true)
                    )
                )
            }
            logD(FLOATING_APPS_TAG, "Loaded ${floatingApps.size} floatingApps")
            floatingApps
        } catch (e: Exception) {
            logE(FLOATING_APPS_TAG, "Load failed", e)
            emptyList()
        }
    }

    suspend fun saveFloatingApp(ctx: Context, floatingApp: FloatingAppObject) {
        logD(FLOATING_APPS_TAG, "Saving floatingApps ${floatingApp.id}")

        val floatingApps = loadFloatingApps(ctx).toMutableList().apply {
            removeAll { it.id == floatingApp.id }
            add(floatingApp)
        }

        val floatingAppsArray = JSONArray().apply {
            floatingApps.forEach { floatingApp ->
                put(JSONObject().apply {
                    put("id", floatingApp.id)
                    put("nestId", floatingApp.nestId)
                    put("action", SwipeJson.encodeAction(floatingApp.action))
                    put("spanX", floatingApp.spanX)
                    put("spanY", floatingApp.spanY)
                    put("x", floatingApp.x)
                    put("y", floatingApp.y)
                    put("angle", floatingApp.angle)
                    put("ghosted", floatingApp.ghosted)
                    put("foreground", floatingApp.foreground)
                })
            }
        }

        val json = JSONObject().apply {
            put("floating_apps", floatingAppsArray)
        }

        logD(FLOATING_APPS_TAG, "Saved: $json")
        setAll(ctx, json)
    }

    suspend fun deleteFloatingApp(ctx: Context, id: Int) {
        val floatingApps = loadFloatingApps(ctx).filterNot { it.id == id }

        val floatingAppsArray = JSONArray().apply {
            floatingApps.forEach { floatingApp ->
                put(JSONObject().apply {
                    put("id", floatingApp.id)
                    put("nestId", floatingApp.nestId)
                    put("action", SwipeJson.encodeAction(floatingApp.action))
                    put("spanX", floatingApp.spanX)
                    put("spanY", floatingApp.spanY)
                    put("x", floatingApp.x)
                    put("y", floatingApp.y)
                    put("angle", floatingApp.angle)
                    put("ghosted", floatingApp.ghosted)
                    put("foreground", floatingApp.foreground)
                })
            }
        }

        val json = JSONObject().apply {
            put("floating_apps", floatingAppsArray)
        }

        setAll(ctx, json)
    }


    override suspend fun resetAll(ctx: Context) {
        ctx.floatingAppsDatastore.edit { prefs ->
            prefs.remove(Keys.FLOATING_APPS_KEY)
        }
    }

    override suspend fun getAll(ctx: Context): JSONObject {
        val prefs = ctx.floatingAppsDatastore.data.first()
        val raw = prefs[Keys.FLOATING_APPS_KEY] ?: return JSONObject()
        return JSONObject(raw)
    }

    override suspend fun setAll(ctx: Context, value: JSONObject) {
        ctx.floatingAppsDatastore.edit { prefs ->
            prefs[Keys.FLOATING_APPS_KEY] = value.toString()
        }
    }
}
