package org.elnix.dragonlauncher.common.serializables

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object IconShapeGson {

    private const val TYPE = "type"

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(IconShape::class.java, Adapter())
        .create()

    fun encode(value: IconShape): String =
        gson.toJson(value, IconShape::class.java)

    fun decode(raw: Any?, fallback: IconShape): IconShape =
        runCatching {
            val stringRaw = raw?.toString() ?: ""
            gson.fromJson(stringRaw, IconShape::class.java)
        }.getOrElse { fallback }

    private class Adapter : JsonSerializer<IconShape>, JsonDeserializer<IconShape> {

        override fun serialize(
            src: IconShape,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            val obj = JsonObject()

            when (src) {
                is IconShape.Custom -> {
                    obj.addProperty(TYPE, "Custom")
                    obj.add("shape", context.serialize(src.shape))
                }
                else -> {
                    obj.addProperty(TYPE, src.javaClass.simpleName)
                }
            }
            return obj
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): IconShape {
            val obj = json.asJsonObject
            return when (obj[TYPE].asString) {
                "PlatformDefault" -> IconShape.PlatformDefault
                "Circle" -> IconShape.Circle
                "Square" -> IconShape.Square
                "RoundedSquare" -> IconShape.RoundedSquare
                "Triangle" -> IconShape.Triangle
                "Squircle" -> IconShape.Squircle
                "Hexagon" -> IconShape.Hexagon
                "Pentagon" -> IconShape.Pentagon
                "Teardrop" -> IconShape.Teardrop
                "Pebble" -> IconShape.Pebble
                "EasterEgg" -> IconShape.EasterEgg
                "Random" -> IconShape.Random
                "Custom" -> IconShape.Custom(
                    shape = context.deserialize(
                        obj.getAsJsonObject("shape"),
                        CustomIconShapeSerializable::class.java
                    )
                )

                // in case of error of decoding,  use the platform default
                else -> IconShape.PlatformDefault
            }
        }
    }
}
