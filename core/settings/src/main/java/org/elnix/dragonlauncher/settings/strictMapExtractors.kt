package org.elnix.dragonlauncher.settings

import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipeJson


fun getBooleanStrict(
    raw: Any?,
    def: Boolean
): Boolean {
    return when (raw) {
        is Boolean -> raw
        is Number -> raw.toInt() != 0
        is String -> when (raw.trim().lowercase()) {
            "true", "1", "yes", "y", "on" -> true
            "false", "0", "no", "n", "off" -> false
            else -> null
        }
        else -> null
    } ?: def
}

fun getIntStrict(
    raw: Any?,
    def: Int
): Int {
    return when (raw) {
        is Int -> raw
        is Number -> raw.toInt()
        is String -> raw.toIntOrNull()
        else -> null
    } ?: def
}
fun getFloatStrict(
    raw: Any?,
    def: Float
): Float {
    return when (raw) {
        is Float -> raw
        is Number -> raw.toFloat()
        is String -> raw.toFloatOrNull()
        else -> null
    } ?: def
}

fun getLongStrict(
    raw: Any?,
    def: Long
): Long {
    return when (raw) {
        is Long -> raw
        is Number -> raw.toLong()
        is String -> raw.toLongOrNull()
        else -> null
    } ?: def
}


fun getDoubleStrict(
    raw: Any?,
    def: Double
): Double {
    return when (raw) {
        is Double -> raw
        is Number -> raw.toDouble()
        is String -> raw.toDoubleOrNull()
        else -> null
    } ?: def
}

fun getStringStrict(
    raw: Any?,
    def: String
): String {
    return when (raw) {
        is String -> raw
        null -> def
        else -> raw.toString()
    }
}


fun getSwipeActionSerializableStrict(
    raw: Any?,
    def: SwipeActionSerializable
): SwipeActionSerializable {
    return when (raw) {
        is String -> SwipeJson.decodeAction(raw)
        else -> null
    } ?: def
}

fun getStringSetStrict(
    raw: Any?,
    def: Set<String>
): Set<String> {
    return when (raw) {
        is Set<*> -> raw.flattenStrings().toSet()
        is List<*> -> raw.flattenStrings().toSet()
        is String -> {
            // Parse "[a,b,c]" → ["a","b","c"]
            try {
                // Extract content between [ ] and split by comma
                val clean = raw.trim().removeSurrounding("[", "]")
                if (clean.isBlank()) return emptySet()

                clean.split(",")
                    .map { it.trim().trim('"').trim('\'') }
                    .filter { it.isNotBlank() }
                    .toSet()
            } catch (_: Exception) {
                setOf(raw)
            }
        }
        else -> null
    } ?: def
}



private fun Collection<*>.flattenStrings(): List<String> = flatMap { item ->
    when (item) {
        is String -> listOf(item)
        is Collection<*> -> item.flattenStrings()
        else -> emptyList()
    }
}.filter { it.isNotBlank() }

fun <E : Enum<E>> getEnumStrict(
    raw: Any?,
    def: E,
    enumClass: Class<E>
): E {
    return enumClass.enumConstants
        ?.firstOrNull { it.name == raw }
        ?: def
}




fun getColorStrict(
    raw: Any?,
    def: Color
): Color {
    return when (raw) {
        null -> null
        is Int -> Color(raw)
        is Number -> Color(raw.toInt())
        is String -> Color (raw.toInt())
        else -> null
    } ?: def
}

fun MutableMap<String, Any>.putIfNonDefault(
    key: String,
    value: Any?,
    def: Any?
) {
    if (value != null && value != def) {
        put(key, value.toString())
    }
}
