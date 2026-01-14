package org.elnix.dragonlauncher.common.serializables

import android.content.ComponentName
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import org.elnix.dragonlauncher.common.logging.logE
import java.lang.reflect.Type
import java.util.UUID


fun dummySwipePoint(
    action: SwipeActionSerializable? = null,
    id: String? = null
) =
    SwipePointSerializable(
        circleNumber = 0,
        angleDeg = 0.0,
        action = action ?: SwipeActionSerializable.OpenDragonLauncherSettings,
        id = id ?: UUID.randomUUID().toString(),
        nestId = 0
    )

val defaultSwipePointsValues = dummySwipePoint(null).copy(
    borderStroke = 4f,
    borderStrokeSelected = 8f,
    opacity = 1f,
    cornerRadius = null,
    paddingDp = 0,
    haptic = false
)




/**
 * Swipe Actions Serializable, the core of the main gesture idea
 * Holds all the different actions the user can do
 */
sealed class SwipeActionSerializable {
    data class LaunchApp(val packageName: String) : SwipeActionSerializable()
        data class LaunchShortcut(
        val packageName: String,
        val shortcutId: String
    ) : SwipeActionSerializable()

    data class OpenUrl(val url: String) : SwipeActionSerializable()
    data class OpenFile(
        val uri: String,
        val mimeType: String? = null
    ) : SwipeActionSerializable()
    object NotificationShade : SwipeActionSerializable()
    object ControlPanel : SwipeActionSerializable()
    object OpenAppDrawer : SwipeActionSerializable()
    object  OpenDragonLauncherSettings: SwipeActionSerializable()
    object Lock: SwipeActionSerializable()
    object ReloadApps: SwipeActionSerializable()

    object OpenRecentApps: SwipeActionSerializable()
    data class OpenCircleNest(val nestId: Int): SwipeActionSerializable()
    object GoParentNest: SwipeActionSerializable()
    data class OpenWidget(
        val widgetId: Int,
        val provider: ComponentName
    ): SwipeActionSerializable()
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

            // Those with more parameters
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
            is SwipeActionSerializable.LaunchShortcut -> {
                obj.addProperty("type", "LaunchShortcut")
                obj.addProperty("packageName", src.packageName)
                obj.addProperty("shortcutId", src.shortcutId)
            }

            is SwipeActionSerializable.OpenCircleNest -> {
                obj.addProperty("type", "OpenCircleNest")
                obj.addProperty("nestId", src.nestId)
            }


            is SwipeActionSerializable.OpenWidget -> {
                obj.addProperty("type", "OpenWidget")
                obj.addProperty("widgetId", "${src.widgetId}")
                obj.addProperty("provider", "${src.provider.packageName}:${src.provider.className}")

            }

            // Those with only the name as param
            is SwipeActionSerializable.NotificationShade -> { obj.addProperty("type", "NotificationShade") }
            is SwipeActionSerializable.ControlPanel -> { obj.addProperty("type", "ControlPanel") }
            is SwipeActionSerializable.OpenAppDrawer -> { obj.addProperty("type", "OpenAppDrawer") }
            is SwipeActionSerializable.OpenDragonLauncherSettings -> { obj.addProperty("type", "OpenDragonLauncherSettings") }
            is SwipeActionSerializable.Lock -> { obj.addProperty("type", "Lock") }
            is SwipeActionSerializable.ReloadApps -> { obj.addProperty("type", "ReloadApps") }
            is SwipeActionSerializable.OpenRecentApps -> { obj.addProperty("type", "OpenRecentApps") }
            is SwipeActionSerializable.GoParentNest -> { obj.addProperty("type", "GoParentNest") }

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
            "GoParentNest" -> SwipeActionSerializable.GoParentNest
            "OpenWidget" -> {
                val providerStr = obj.get("provider")?.asString ?: ""

                val provider = ComponentName.unflattenFromString(providerStr)
                    ?: ComponentName("", "")

                val widgetId = obj.get("widgetId")?.asInt ?: 0
                SwipeActionSerializable.OpenWidget(widgetId, provider)
            }
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
            logE("SwipeJson", "Decode failed: ${e.message}", e)
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


    fun encodeAction(action: SwipeActionSerializable?): String =
        gson.toJson(action, SwipeActionSerializable::class.java)


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
            logE("SwipeJson", "Decode failed: ${e.message}", e)
            emptyList()
        }
    }
}


/**
 * Used to reach the same package name, just since they are different instances, the compiler
 * complains about accessing directly .packageName
 */
fun SwipeActionSerializable.targetPackage(): String? = when (this) {
    is SwipeActionSerializable.LaunchApp -> packageName
    is SwipeActionSerializable.LaunchShortcut -> packageName
    else -> null
}
