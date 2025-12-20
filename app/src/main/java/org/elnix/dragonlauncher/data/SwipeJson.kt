package org.elnix.dragonlauncher.data

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
    @SerializedName("d") val id: String? = null
)

// Use sealed class for actions
@Serializable
sealed class SwipeActionSerializable {
    @Serializable data class LaunchApp(val packageName: String) : SwipeActionSerializable()
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

    private val listType = object : TypeToken<List<SwipePointSerializable>>() {}.type

    fun encode(points: List<SwipePointSerializable>): String = gson.toJson(points, listType)

    fun encodePretty(points: List<SwipePointSerializable>): String = gsonPretty.toJson(points, listType)

    fun decode(jsonString: String): List<SwipePointSerializable> {
        if (jsonString.isBlank()) return emptyList()
        return try {
            gson.fromJson(jsonString, listType)
        } catch (_: Throwable) {
            emptyList()
        }
    }


    // Kinda hacky but its the best way I managed to make it work
    fun encodeAction(action: SwipeActionSerializable): String = when (action) {
        is SwipeActionSerializable.LaunchApp ->
            """{"type":"LaunchApp","packageName":"${action.packageName}"}"""
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
    }
    fun decodeAction(jsonString: String): SwipeActionSerializable? {
        if (jsonString.isBlank() || jsonString == "{}") return null
        return try {
            gson.fromJson(jsonString, SwipeActionSerializable::class.java)
        } catch (_: Throwable) {
            null
        }
    }
}
