package org.elnix.dragonlauncher.common.serializables

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class CacheKeyAdapter : JsonSerializer<CacheKey>, JsonDeserializer<CacheKey> {

    override fun serialize(
        src: CacheKey,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.cacheKey)
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): CacheKey {
        return CacheKey(json.asString)
    }
}
