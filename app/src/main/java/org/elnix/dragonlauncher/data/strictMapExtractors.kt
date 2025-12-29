package org.elnix.dragonlauncher.data

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



fun MutableMap<String, Any>.putIfNonDefault(
    key: Preferences.Key<*>,
    value: Any?,
    def: Any
) {
    if (value != null && value != def) {
        put(key.name, value.toString())
    }
}
