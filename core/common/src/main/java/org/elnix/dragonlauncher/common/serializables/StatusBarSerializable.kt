package org.elnix.dragonlauncher.common.serializables

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
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.utils.Constants.Logging.STATUS_BAR_TAG
import java.lang.reflect.Type


sealed class StatusBarSerializable {

    data class Time(
        val formatter: String = "HH:mm:ss",
        val action: SwipeActionSerializable? = null
    ) : StatusBarSerializable()

    data class Date(
        val formatter: String = "MMM dd",
        val action: SwipeActionSerializable? = null
    ) : StatusBarSerializable()

    data object Bandwidth : StatusBarSerializable()

    data class Notifications(
        val maxIcons: Int = 8
    ) : StatusBarSerializable()

    data object Connectivity : StatusBarSerializable()

    data class Spacer(
        val width: Int = -1
    ) : StatusBarSerializable()

    data class Battery(
        val showIcon: Boolean = false,
        val showPercentage: Boolean = true
    ) : StatusBarSerializable()

    data class NextAlarm(
        val formatter: String = "HH:mm"
    ) : StatusBarSerializable()
}


val allStatusBarSerializable = listOf(
    StatusBarSerializable.Time(),
    StatusBarSerializable.Date(),
    StatusBarSerializable.Bandwidth,
    StatusBarSerializable.Notifications(),
    StatusBarSerializable.Connectivity,
    StatusBarSerializable.Battery(),
    StatusBarSerializable.NextAlarm(),
    StatusBarSerializable.Spacer()
)

// Gson type adapter for sealed class
class StatusBarAdapter : JsonSerializer<StatusBarSerializable>, JsonDeserializer<StatusBarSerializable> {
    override fun serialize(
        src: StatusBarSerializable?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if (src == null) return JsonNull.INSTANCE
        val obj = JsonObject()
        when (src) {

            is StatusBarSerializable.Time -> {
                obj.addProperty("type", "Time")
                obj.addProperty("action", SwipeJson.encodeAction(src.action))
                obj.addProperty("formatter", src.formatter)
            }

            is StatusBarSerializable.Date -> {
                obj.addProperty("type", "Date")
                obj.addProperty("action", SwipeJson.encodeAction(src.action))
                obj.addProperty("formatter", src.formatter)
            }

            is StatusBarSerializable.Bandwidth -> {
                obj.addProperty("type", "Bandwidth")
            }

            is StatusBarSerializable.Notifications -> {
                obj.addProperty("type", "Notifications")
                obj.addProperty("maxIcons", src.maxIcons)
            }

            is StatusBarSerializable.Connectivity -> {
                obj.addProperty("type", "Connectivity")
            }

            is StatusBarSerializable.Spacer -> {
                obj.addProperty("type", "Spacer")
                obj.addProperty("width", src.width)
            }

            is StatusBarSerializable.Battery -> {
                obj.addProperty("type", "Battery")
                obj.addProperty("showIcon", src.showIcon)
                obj.addProperty("showPercentage", src.showPercentage)
            }

            is StatusBarSerializable.NextAlarm -> {
                obj.addProperty("type", "NextAlarm")
                obj.addProperty("formatter", src.formatter)
            }
        }
        return obj
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): StatusBarSerializable? {
        if (json == null || !json.isJsonObject) return null
        val obj = json.asJsonObject
        return try {
            when (obj.get("type").asString) {
                "Time" -> StatusBarSerializable.Time(
                    formatter = obj.get("formatter").asString,
                    action = SwipeJson.decodeAction(obj.get("action").asString)
                )

                "Date" -> StatusBarSerializable.Date(
                    formatter = obj.get("formatter").asString,
                    action = SwipeJson.decodeAction(obj.get("action").asString)
                )

                "Bandwidth" -> StatusBarSerializable.Bandwidth


                "Notifications" -> StatusBarSerializable.Notifications(
                    maxIcons = obj.get("maxIcons")?.asInt ?: 0
                )

                "Connectivity" -> StatusBarSerializable.Connectivity

                "Spacer" -> StatusBarSerializable.Spacer(
                    width = obj.get("width")?.asInt ?: -1,
                )
                "Battery" -> StatusBarSerializable.Battery(
                    showIcon = obj.get("showIcon")?.asBoolean ?: true,
                    showPercentage =obj.get("showPercentage")?.asBoolean ?: true,
                )
                "NextAlarm" -> StatusBarSerializable.NextAlarm(
                    formatter = obj.get("formatter")?.asString ?: "HH:mm"
                )
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

object StatusBarJson {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(StatusBarSerializable::class.java, StatusBarAdapter())
        .create()

    private val type = object : TypeToken<List<StatusBarSerializable>>() {}.type



    /* ───────────── List encoders / decoders ───────────── */

    fun encodeStatusBarElements(elements: List<StatusBarSerializable>): String =
        gson.toJson(elements, type)


    fun decodeStatusBarElements(json: String): List<StatusBarSerializable> {
        if (json.isBlank() || json == "{}") return emptyList()

        logD(STATUS_BAR_TAG, json)
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            logE(STATUS_BAR_TAG, "Decode failed: ${e.message}", e)
            emptyList()
        }
    }


//    /* ───────────── Individuals encoders / decoders ───────────── */
//
//    fun encodeStatusBarAction(obj: StatusBarSerializable): String =
//        gson.toJson(obj, StatusBarSerializable::class.java)
//
//
//    fun decodeStatusBarAction(jsonString: String): StatusBarSerializable? {
//        if (!jsonString.isNotBlankJson) return null
//        return try {
//            gson.fromJson(jsonString, StatusBarSerializable::class.java)
//        } catch (_: Exception) {
//            null
//        }
//    }
}
