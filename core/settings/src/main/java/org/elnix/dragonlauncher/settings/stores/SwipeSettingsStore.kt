package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipeJson
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.settings.BaseSettingsStore
import org.elnix.dragonlauncher.settings.swipeDataStore
import org.json.JSONArray
import org.json.JSONObject

object SwipeSettingsStore : BaseSettingsStore<JSONObject>() {

    override val name: String = "Swipe"

    private val POINTS = stringPreferencesKey("points_json")
    private val CIRCLE_NESTS = stringPreferencesKey("nests_json")

    private val DEFAULT_CIRCLE = stringPreferencesKey("default_circle")

    /* ───────────── Points ───────────── */

    suspend fun getPoints(ctx: Context): List<SwipePointSerializable> =
        ctx.swipeDataStore.data
            .map { prefs -> prefs[POINTS]?.let(SwipeJson::decodePoints) ?: emptyList() }
            .first()

    fun getPointsFlow(ctx: Context) =
        ctx.swipeDataStore.data.map { prefs ->
            prefs[POINTS]?.let(SwipeJson::decodePoints) ?: emptyList()
        }

    suspend fun savePoints(ctx: Context, points: List<SwipePointSerializable>) {
        ctx.swipeDataStore.edit { prefs ->
            prefs[POINTS] = SwipeJson.encodePoints(points)
        }
    }

    /* ───────────── Nests ───────────── */

    suspend fun getNests(ctx: Context): List<CircleNest> =
        ctx.swipeDataStore.data
            .map { prefs -> prefs[CIRCLE_NESTS]?.let(SwipeJson::decodeNests) ?: listOf(CircleNest()) }
            .first()

    fun getNestsFlow(ctx: Context) =
        ctx.swipeDataStore.data.map { prefs ->
            prefs[CIRCLE_NESTS]?.let(SwipeJson::decodeNests) ?: listOf(CircleNest())
        }

    suspend fun saveNests(ctx: Context, nests: List<CircleNest>) {
        ctx.swipeDataStore.edit { prefs ->
            prefs[CIRCLE_NESTS] = SwipeJson.encodeNests(nests)
        }
    }


    /* ───────────── Default circle ───────────── */

    fun getDefaultPointFlow(ctx: Context): Flow<SwipePointSerializable> =
        ctx.swipeDataStore.data.map { prefs ->
            prefs[DEFAULT_CIRCLE]?.let { SwipeJson.decodePoints(it).first() } ?: defaultSwipePointsValues
        }

    suspend fun getDefaultPoint(ctx: Context): SwipePointSerializable =
        ctx.swipeDataStore.data.map { prefs ->
            prefs[DEFAULT_CIRCLE]?.let { SwipeJson.decodePoints(it).first() } ?: defaultSwipePointsValues
        }.first()

    suspend fun setDefaultPoint(ctx: Context, point: SwipePointSerializable) {
        ctx.swipeDataStore.edit { prefs ->
            prefs[DEFAULT_CIRCLE] = SwipeJson.encodePoints(listOf(point))
        }
    }



    /* ───────────── Reset ───────────── */

    override suspend fun resetAll(ctx: Context) {
        ctx.swipeDataStore.edit { prefs ->
            prefs.remove(POINTS)
            prefs.remove(CIRCLE_NESTS)
            prefs.remove(DEFAULT_CIRCLE)
        }
    }

    override suspend fun getAll(ctx: Context): JSONObject {
        val points = getPoints(ctx)
        val nests = getNests(ctx)
        val defaultPoint = getDefaultPointFlow(ctx).first()

        if (points.isEmpty() && nests.isEmpty()) return JSONObject()

        return JSONObject().apply {
            if (points.isNotEmpty()) {
                put("points", JSONArray(SwipeJson.encodePointsPretty(points)))
            }
            if (nests.isNotEmpty()) {
                put("nests", JSONArray(SwipeJson.encodeNestsPretty(nests)))
            }
            if (points.isNotEmpty()) {
                put("default_point", JSONArray(SwipeJson.encodePointsPretty(listOf(defaultPoint))))
            }
        }
    }


    override suspend fun setAll(ctx: Context, value: JSONObject) {
        if (value.has("points") || value.has("nests")) {
            value.optJSONArray("points")?.let {
                savePoints(ctx, SwipeJson.decodePoints(it.toString()))
            }
            value.optJSONArray("nests")?.let {
                saveNests(ctx, SwipeJson.decodeNests(it.toString()))
            }
            value.optJSONArray("default_point")?.let {
                setDefaultPoint(ctx, SwipeJson.decodePoints(it.toString()).first())
            }
            return
        }
    }
}
