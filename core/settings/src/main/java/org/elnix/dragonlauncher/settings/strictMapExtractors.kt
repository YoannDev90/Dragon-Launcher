package org.elnix.dragonlauncher.settings


fun getBooleanStrict(
    raw: Map<String, Any?>,
    key: String,
    def: Boolean
): Boolean {
    val v = raw[key] ?: return def
    return when (v) {
        is Boolean -> v
        is Number -> v.toInt() != 0
        is String -> when (v.trim().lowercase()) {
            "true", "1", "yes", "y", "on" -> true
            "false", "0", "no", "n", "off" -> false
            else -> throw BackupTypeException(key, "Boolean", "String", v)
        }
        else -> throw BackupTypeException(key, "Boolean", v::class.simpleName, v)
    }
}

fun getIntStrict(
    raw: Map<String, Any?>,
    key: String,
    def: Int
): Int {
    val v = raw[key] ?: return def
    return when (v) {
        is Int -> v
        is Number -> v.toInt()
        is String -> v.toIntOrNull()
            ?: throw BackupTypeException(key, "Int", "String", v)
        else -> throw BackupTypeException(key, "Int", v::class.simpleName, v)
    }
}
fun getFloatStrict(
    raw: Map<String, Any?>,
    key: String,
    def: Float
): Float {
    val v = raw[key] ?: return def
    return when (v) {
        is Float -> v
        is Number -> v.toFloat()
        is String -> v.toFloatOrNull()
            ?: throw BackupTypeException(key, "Float", "String", v)
        else -> throw BackupTypeException(key, "Float", v::class.simpleName, v)
    }
}

fun getLongStrict(
    raw: Map<String, Any?>,
    key: String,
    def: Long
): Long {
    val v = raw[key] ?: return def
    return when (v) {
        is Long -> v
        is Number -> v.toLong()
        is String -> v.toLongOrNull()
            ?: throw BackupTypeException(key, "Long", "String", v)
        else -> throw BackupTypeException(key, "Long", v::class.simpleName, v)
    }
}

fun getStringStrict(
    raw: Map<String, Any?>,
    key: String,
    def: String
): String {
    val v = raw[key] ?: return def
    return when (v) {
        is String -> v
        else -> throw BackupTypeException(key, "String", v::class.simpleName, v)
    }
}

fun getStringSetStrict(
    raw: Map<String, Any?>,
    key: String,
    def: Set<String>
): Set<String> {
    val v = raw[key] ?: return def

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
        else -> throw BackupTypeException(key, "String Set", v.javaClass.name, v)
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
    key: String,
    def: E
): E {
    val v = raw[key] ?: return def

    if (v !is String) {
        throw BackupTypeException(
            key,
            "String (Enum name)",
            v::class.simpleName,
            v
        )
    }

    return enumValues<E>().firstOrNull { it.name == v }
        ?: throw BackupTypeException(
            key,
            "one of ${enumValues<E>().joinToString { it.name }}",
            "String",
            v
        )
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
