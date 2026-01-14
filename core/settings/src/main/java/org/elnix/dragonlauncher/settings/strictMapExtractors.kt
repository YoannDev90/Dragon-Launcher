package org.elnix.dragonlauncher.settings

import androidx.datastore.preferences.core.Preferences


fun getBooleanStrict(
    raw: Map<String, Any?>,
    key: Preferences.Key<Boolean>,
    def: Boolean
): Boolean {
    val v = raw[key.name] ?: return def
    return when (v) {
        is Boolean -> v
        is Number -> v.toInt() != 0
        is String -> when (v.trim().lowercase()) {
            "true", "1", "yes", "y", "on" -> true
            "false", "0", "no", "n", "off" -> false
            else -> throw BackupTypeException(key.name, "Boolean", "String", v)
        }
        else -> throw BackupTypeException(key.name, "Boolean", v::class.simpleName, v)
    }
}

fun getIntStrict(
    raw: Map<String, Any?>,
    key: Preferences.Key<Int>,
    def: Int
): Int {
    val v = raw[key.name] ?: return def
    return when (v) {
        is Int -> v
        is Number -> v.toInt()
        is String -> v.toIntOrNull()
            ?: throw BackupTypeException(key.name, "Int", "String", v)
        else -> throw BackupTypeException(key.name, "Int", v::class.simpleName, v)
    }
}
fun getFloatStrict(
    raw: Map<String, Any?>,
    key: Preferences.Key<Float>,
    def: Float
): Float {
    val v = raw[key.name] ?: return def
    return when (v) {
        is Float -> v
        is Number -> v.toFloat()
        is String -> v.toFloatOrNull()
            ?: throw BackupTypeException(key.name, "Float", "String", v)
        else -> throw BackupTypeException(key.name, "Float", v::class.simpleName, v)
    }
}

fun getStringStrict(
    raw: Map<String, Any?>,
    key: Preferences.Key<String>,
    def: String
): String {
    val v = raw[key.name] ?: return def
    return when (v) {
        is String -> v
        else -> throw BackupTypeException(key.name, "String", v::class.simpleName, v)
    }
}

fun getStringSetStrict(
    raw: Map<String, Any?>,
    key: Preferences.Key<Set<String>>,
    def: Set<String>
): Set<String> {
    val v = raw[key.name] ?: return def

    return when (v) {
        is Set<*> -> v.flattenStrings().toSet()
        is List<*> -> v.flattenStrings().toSet()
        is String -> {
            // Parse "[a,b,c]" → ["a","b","c"]
            try {
                // Extract content between [ ] and split by comma
                val clean = v.trim().removeSurrounding("[", "]")
                if (clean.isBlank()) return emptySet()

                clean.split(",")
                    .map { it.trim().trim('"').trim('\'') }
                    .filter { it.isNotBlank() }
                    .toSet()
            } catch (_: Exception) {
                setOf(v)
            }
        }
        else -> throw BackupTypeException(key.name, "String Set", v.javaClass.name, v)
    }
}



private fun Collection<*>.flattenStrings(): List<String> = flatMap { item ->
    when (item) {
        is String -> listOf(item)
        is Collection<*> -> item.flattenStrings()
        else -> emptyList()
    }
}.filter { it.isNotBlank() }


inline fun <reified E : Enum<E>> getEnumStrict(
    raw: Map<String, Any?>,
    key: Preferences.Key<String>,
    def: E
): E {
    val v = raw[key.name] ?: return def

    if (v !is String) {
        throw BackupTypeException(
            key.name,
            "String (Enum name)",
            v::class.simpleName,
            v
        )
    }

    return enumValues<E>().firstOrNull { it.name == v }
        ?: throw BackupTypeException(
            key.name,
            "one of ${enumValues<E>().joinToString { it.name }}",
            "String",
            v
        )
}


fun MutableMap<String, Any>.putIfNonDefault(
    key: Preferences.Key<*>,
    value: Any?,
    def: Any?
) {
    if (value != null && value != def) {
        put(key.name, value.toString())
    }
}
