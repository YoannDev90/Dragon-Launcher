package org.elnix.dragonlauncher.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import org.json.JSONObject


abstract class BaseSettingsStore<T> {

    abstract val name: String
    abstract val dataStoreName: DataStoreName

    @Suppress("PropertyName")
    abstract val ALL: List<SettingObject<*>>

    suspend fun resetAll(ctx: Context) {
        ALL.forEach { it.reset(ctx) }
    }

    abstract suspend fun getAll(ctx: Context): T
    abstract suspend fun setAll(ctx: Context, value: T)

    abstract suspend fun exportForBackup(ctx: Context): JSONObject?
    abstract suspend fun importFromBackup(ctx: Context, json: JSONObject)
}


class TypedSettingObject<T>(
    key: String,
    dataStoreName: DataStoreName,
    default: T,
    private val type: SettingType
) : SettingObject<T>(key, dataStoreName, default, type) {

    @Suppress("UNCHECKED_CAST")
    override val preferenceKey: Preferences.Key<T> =
        when (type) {
            SettingType.Boolean -> booleanPreferencesKey(key)
            SettingType.Int -> intPreferencesKey(key)
            SettingType.Float -> floatPreferencesKey(key)
            SettingType.Long -> longPreferencesKey(key)
//            SettingType.Double -> doublePreferencesKey(key)
            SettingType.String -> stringPreferencesKey(key)
            SettingType.StringSet -> stringSetPreferencesKey(key)
        } as Preferences.Key<T>

    @Suppress("UNCHECKED_CAST")
    override fun decode(raw: Any?): T =
        when (type) {
            SettingType.Boolean ->
                getBooleanStrict(mapOf(key to raw), key, default as Boolean) as T

            SettingType.Int ->
                getIntStrict(mapOf(key to raw), key, default as Int) as T

            SettingType.Float ->
                getFloatStrict(mapOf(key to raw), key, default as Float) as T

            SettingType.String ->
                getStringStrict(mapOf(key to raw), key, default as String) as T

            SettingType.StringSet ->
                getStringSetStrict(mapOf(key to raw), key, default as Set<String>) as T

            SettingType.Long ->
                getLongStrict(mapOf(key to raw), key, default as Long) as T

//            SettingType.Double ->
//                (raw as? Number)?.toDouble() ?: default
        }
}



abstract class MapSettingsStore :
    BaseSettingsStore<Map<String, Any?>>() {

    override suspend fun getAll(ctx: Context): Map<String, Any?> =
        buildMap {
            ALL.forEach { setting ->
                put(setting.key, setting.get(ctx))
            }
        }

    override suspend fun setAll(ctx: Context, value: Map<String, Any?>) {
        ALL.forEach { setting ->
            val raw = value[setting.key]
            @Suppress("UNCHECKED_CAST")
            (setting as SettingObject<Any?>)
                .set(ctx, setting.decode(raw))
        }
    }

    override suspend fun exportForBackup(ctx: Context): JSONObject =
        JSONObject(getAll(ctx))

    override suspend fun importFromBackup(ctx: Context, json: JSONObject) {
        json.keys().forEach { key ->
            ALL.find { it.key == key }?.let { setting ->
                @Suppress("UNCHECKED_CAST")
                (setting as SettingObject<Any?>).set(ctx, json.opt(key))
            }
        }
    }
}


abstract class JsonSettingsStore :
    BaseSettingsStore<JSONObject>() {

    abstract val jsonSetting: TypedSettingObject<String>

    override suspend fun getAll(ctx: Context): JSONObject =
        JSONObject(jsonSetting.get(ctx))

    override suspend fun setAll(ctx: Context, value: JSONObject) {
        jsonSetting.set(ctx, value.toString())
    }

    override suspend fun exportForBackup(ctx: Context): JSONObject =
        getAll(ctx)

    override suspend fun importFromBackup(ctx: Context, json: JSONObject) {
        setAll(ctx, json)
    }
}




//abstract class BaseSettingsStore<T> {
//    abstract val name: String
//    abstract val dataStoreName: DataStoreName
//
//    @Suppress("PropertyName")
//    abstract val ALL: List<SettingObject<*>>
//
//    suspend fun resetAll(ctx: Context) {
//        ALL.forEach {
//            it.reset(ctx)
//        }
//    }
//
//    suspend fun getAll(ctx: Context): T {
//        return buildMap {
//            ALL.forEach {
//                it.get(ctx)
//            }
//        }
//    }
//    abstract suspend fun setAll(ctx: Context, value: T)
//
//    /**
//     * Exports the store as JSONObject for backup.
//     * Adapts automatically for Map<String, Any> or JSONObject types.
//     */
//    open suspend fun exportForBackup(ctx: Context): JSONObject? {
//        val value = getAll(ctx) ?: return null
//        return when (value) {
//            is JSONObject -> value
//            is Map<*, *> -> JSONObject().apply {
//                value.forEach { (k, v) ->
//                    put(k.toString(), v)
//                }
//            }
//            else -> null
//        }
//    }
//
//    /**
//     * Imports the store from a JSONObject backup.
//     * Adapts automatically for Map<String, Any> or JSONObject types.
//     */
//    open suspend fun importFromBackup(ctx: Context, json: JSONObject) {
//        val value = getAll(ctx)
//        @Suppress("UNCHECKED_CAST")
//        when (value) {
//            is JSONObject -> setAll(ctx, json as T)
//            is Map<*, *> -> {
//                val map = buildMap<String, Any?> {
//                    json.keys().forEach { key ->
//                        put(key, json.opt(key))
//                    }
//                }
//                @Suppress("UNCHECKED_CAST")
//                setAll(ctx, map as T)
//            }
//            else -> return
//        }
//    }
//}
