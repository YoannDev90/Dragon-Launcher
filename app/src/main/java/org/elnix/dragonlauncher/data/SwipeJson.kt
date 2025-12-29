package org.elnix.dragonlauncher.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.Serializable
import java.lang.reflect.Type

// Keep the same data classes, no @Serializable needed
data class SwipePointSerializable(
    @SerializedName("a") val circleNumber: Int,
    @SerializedName("b") val angleDeg: Double,
    @SerializedName("c") val action: SwipeActionSerializable? = null,
    @SerializedName("d") val id: String? = null,
    @SerializedName("e") val nestId: Int? = 0
)

/**
 * New CircleNest system, where every bloc of circles is contained inside one of those*
 * This way, we can navigate across those nests, to achieve more actions, using the jump actions
 */
data class CircleNest(
    /**
     *  By default the id 0 is the first nest that is available,
     *  I'll try to make the old system importable, to avoid breaking changes like empty actions circle
     */
    @SerializedName("id") val id: Int = 0,
    /**
     * Holds the cancel zone (index -1), and the circle numbers for each drag distances
     * for all the circles in the nest (index positive integer)
     * the key is the circle number, made for allowing not ascending order drag distances
     * For the last one, the drag distance has no limit, it's not even counted
     */
    @SerializedName("dragDistances") val dragDistances: Map<Int, Int> = mapOf(
        -1 to 150,
        0 to 400,
        1 to 700,
        2 to 1000
    ),

    /**
     * The id of the nest that holds this one, used for drawing correctly the outer circles
     * And also to navigate across nests
     */
    @SerializedName("parentId") val parentId: Int = 0
)

// Use sealed class for actions
@Serializable
sealed class SwipeActionSerializable {
    @Serializable data class LaunchApp(val packageName: String) : SwipeActionSerializable()
    @Serializable
    data class LaunchShortcut(
        val packageName: String,
        val shortcutId: String
    ) : SwipeActionSerializable()

    @Serializable data class OpenUrl(val url: String) : SwipeActionSerializable()
    @Serializable data class OpenFile(
        val uri: String,
        val mimeType: String? = null
    ) : SwipeActionSerializable()
    @Serializable object NotificationShade : SwipeActionSerializable()
    @Serializable object ControlPanel : SwipeActionSerializable()
    @Serializable object OpenAppDrawer : SwipeActionSerializable()
    @Serializable object  OpenDragonLauncherSettings: SwipeActionSerializable()
    @Serializable object Lock: SwipeActionSerializable()
    @Serializable object ReloadApps: SwipeActionSerializable()

    @Serializable object OpenRecentApps: SwipeActionSerializable()
    @Serializable data class OpenCircleNest(val nestId: Int): SwipeActionSerializable()
}

// Gson type adapter for sealed class
class SwipeActionAdapter : JsonSerializer<SwipeActionSerializable>, JsonDeserializer<SwipeActionSerializable> {
    override fun serialize(
        src: SwipeActionSerializable?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if (src == null) return JsonNull.INSTANCE
        val obj = JsonObject()
        when (src) {
            is SwipeActionSerializable.LaunchApp -> {
                obj.addProperty("type", "LaunchApp")
                obj.addProperty("packageName", src.packageName)
            }
            is SwipeActionSerializable.OpenUrl -> {
                obj.addProperty("type", "OpenUrl")
                obj.addProperty("url", src.url)
            }
            is SwipeActionSerializable.OpenFile -> {
                obj.addProperty("type", "OpenFile")
                obj.addProperty("uri", src.uri)
                obj.addProperty("mimeType", src.mimeType)
            }
            is SwipeActionSerializable.NotificationShade -> { obj.addProperty("type", "NotificationShade") }
            is SwipeActionSerializable.ControlPanel -> { obj.addProperty("type", "ControlPanel") }
            is SwipeActionSerializable.OpenAppDrawer -> { obj.addProperty("type", "OpenAppDrawer") }
            is SwipeActionSerializable.OpenDragonLauncherSettings -> { obj.addProperty("type", "OpenDragonLauncherSettings") }
            is SwipeActionSerializable.Lock -> { obj.addProperty("type", "Lock") }
            is SwipeActionSerializable.ReloadApps -> { obj.addProperty("type", "ReloadApps") }
            is SwipeActionSerializable.OpenRecentApps -> { obj.addProperty("type", "OpenRecentApps") }
            is SwipeActionSerializable.LaunchShortcut -> {
                obj.addProperty("type", "LaunchShortcut")
                obj.addProperty("packageName", src.packageName)
                obj.addProperty("shortcutId", src.shortcutId)
            }

            is SwipeActionSerializable.OpenCircleNest -> {
                obj.addProperty("type", "OpenCircleNest")
                obj.addProperty("nestId", src.nestId)
            }
        }
        return obj
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SwipeActionSerializable? {
        if (json == null || !json.isJsonObject) return null
        val obj = json.asJsonObject
        return when (obj.get("type").asString) {
            "LaunchApp" -> SwipeActionSerializable.LaunchApp(obj.get("packageName").asString)
            "OpenUrl" -> SwipeActionSerializable.OpenUrl(obj.get("url").asString)
            "OpenFile" -> SwipeActionSerializable.OpenFile(
                obj.get("uri").asString,
                obj.get("mimeType")?.asString
            )
            "NotificationShade" -> SwipeActionSerializable.NotificationShade
            "ControlPanel" -> SwipeActionSerializable.ControlPanel
            "OpenAppDrawer" -> SwipeActionSerializable.OpenAppDrawer
            "OpenDragonLauncherSettings" -> SwipeActionSerializable.OpenDragonLauncherSettings
            "Lock" -> SwipeActionSerializable.Lock
            "ReloadApps" -> SwipeActionSerializable.ReloadApps
            "OpenRecentApps" -> SwipeActionSerializable.OpenRecentApps
            "LaunchShortcut" -> SwipeActionSerializable.LaunchShortcut(
                obj.get("packageName").asString,
                obj.get("shortcutId").asString
            )
            "OpenCircleNest" -> SwipeActionSerializable.OpenCircleNest(
                obj.get("nestId").asInt
            )
            else -> null
        }
    }
}

object SwipeJson {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(SwipeActionSerializable::class.java, SwipeActionAdapter())
        .create()

    private val gsonPretty: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(SwipeActionSerializable::class.java, SwipeActionAdapter())
        .create()

    private val pointsType = object : TypeToken<List<SwipePointSerializable>>() {}.type
    private val nestsType = object : TypeToken<List<CircleNest>>() {}.type


    /* ---------- Old format, keep it for legacy support ---------- */
    private val listType = object : TypeToken<List<SwipePointSerializable>>() {}.type

    fun decode(jsonString: String): List<SwipePointSerializable> {
        if (jsonString.isBlank()) return emptyList()
        return try {
            gson.fromJson(jsonString, listType)
        } catch (e: Throwable) {
            Log.e("SwipeJson", "Decode failed: ${e.message}", e)
            emptyList()
        }
    }

    /* ---------- Points ---------- */

    fun encodePoints(points: List<SwipePointSerializable>): String =
        gson.toJson(points, pointsType)

    fun encodePointsPretty(points: List<SwipePointSerializable>): String =
        gsonPretty.toJson(points, pointsType)

    fun decodePoints(json: String): List<SwipePointSerializable> =
        decodeSafe(json, pointsType)

    /* ---------- Nests ---------- */

    fun encodeNests(nests: List<CircleNest>): String =
        gson.toJson(nests, nestsType)

    fun encodeNestsPretty(nests: List<CircleNest>): String =
        gsonPretty.toJson(nests, nestsType)

    fun decodeNests(json: String): List<CircleNest> =
        decodeSafe(json, nestsType)


    // Kinda hacky but its the best way I managed to make it work
    fun encodeAction(action: SwipeActionSerializable): String = when (action) {
        is SwipeActionSerializable.LaunchApp ->
            """{"type":"LaunchApp","packageName":"${action.packageName}"}"""
        is SwipeActionSerializable.LaunchShortcut ->
            """{"type":"LaunchShortcut","packageName":"${action.packageName}","shortcutId":"${action.shortcutId}"}"""
        is SwipeActionSerializable.OpenUrl ->
            """{"type":"OpenUrl","url":"${action.url}"}"""
        is SwipeActionSerializable.OpenFile ->
            """{"type":"OpenFile","uri":"${action.uri}","mimeType":${action.mimeType?.let { "\"$it\"" } ?: "null"}}"""
        is SwipeActionSerializable.NotificationShade ->
            """{"type":"NotificationShade"}"""
        is SwipeActionSerializable.ControlPanel ->
            """{"type":"ControlPanel"}"""
        is SwipeActionSerializable.OpenAppDrawer ->
            """{"type":"OpenAppDrawer"}"""
        is SwipeActionSerializable.OpenDragonLauncherSettings ->
            """{"type":"OpenDragonLauncherSettings"}"""
        is SwipeActionSerializable.Lock ->
            """{"type":"Lock"}"""
        is SwipeActionSerializable.ReloadApps ->
            """{"type":"ReloadApps"}"""
        is SwipeActionSerializable.OpenRecentApps ->
            """{"type":"OpenRecentApps"}"""
        is SwipeActionSerializable.OpenCircleNest ->
            """{"type":"OpenCircleNest","nestId":"${action.nestId}"}"""
    }

    fun decodeAction(jsonString: String): SwipeActionSerializable? {
        if (jsonString.isBlank() || jsonString == "{}") return null
        return try {
            gson.fromJson(jsonString, SwipeActionSerializable::class.java)
        } catch (_: Throwable) {
            null
        }
    }


    private fun <T> decodeSafe(json: String, type: Type): List<T> {
        if (json.isBlank()) return emptyList()
        return try {
            gson.fromJson(json, type)
        } catch (e: Throwable) {
            Log.e("SwipeJson", "Decode failed: ${e.message}", e)
            emptyList()
        }
    }
}


fun SwipeActionSerializable.targetPackage(): String? = when (this) {
    is SwipeActionSerializable.LaunchApp -> packageName
    is SwipeActionSerializable.LaunchShortcut -> packageName
    else -> null
}
