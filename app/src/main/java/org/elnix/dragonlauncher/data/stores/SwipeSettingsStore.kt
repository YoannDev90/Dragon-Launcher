package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.SwipeJson
import org.elnix.dragonlauncher.data.SwipePointSerializable
import org.elnix.dragonlauncher.data.swipeDataStore

object SwipeSettingsStore {

//    data class SwipeBackup(
//        val pointsJson: String? = null
//    )

    private val POINTS = stringPreferencesKey("points_json")

    suspend fun getPoints(ctx: Context): List<SwipePointSerializable> {
        return ctx.swipeDataStore.data
            .map { prefs ->
                prefs[POINTS]?.let { SwipeJson.decode(it) } ?: emptyList()
            }
            .first()
    }

    fun getPointsFlow(ctx: Context) =
        ctx.swipeDataStore.data.map { prefs ->
            prefs[POINTS]?.let { SwipeJson.decode(it) } ?: emptyList()
        }

    suspend fun save(ctx: Context, points: List<SwipePointSerializable>) {
        ctx.swipeDataStore.edit { prefs ->
            prefs[POINTS] = SwipeJson.encode(points)
        }
    }


    suspend fun resetAll(ctx: Context) {
        ctx.swipeDataStore.edit { prefs ->
            prefs.remove(POINTS)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.swipeDataStore.data.first()
        val raw = prefs[POINTS] ?: return emptyMap()

        val decoded = SwipeJson.decode(raw)
        val pretty = SwipeJson.encodePretty(decoded)

        return mapOf("points" to pretty)
    }


    suspend fun setAll(ctx: Context, backup: Map<String, String>) {
        val pretty = backup["points"] ?: return

        val decoded = SwipeJson.decode(pretty)
        val compact = SwipeJson.encode(decoded)

        ctx.swipeDataStore.edit { prefs ->
            prefs[POINTS] = compact
        }
    }
}
