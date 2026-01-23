package org.elnix.dragonlauncher.settings

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject

class SettingObject<T>(
    key: String,
    dataStoreName: DataStoreName,
    default: T,
    val type: SettingType
) : BaseSettingObject<T>(key, dataStoreName, default) {

    @Suppress("UNCHECKED_CAST")
    override val preferenceKey: Preferences.Key<T> =
        when (type) {
            is SettingType.Int -> intPreferencesKey(key)
            is SettingType.Long -> longPreferencesKey(key)
            is SettingType.Float -> floatPreferencesKey(key)
            is SettingType.Double -> doublePreferencesKey(key)
            is SettingType.Boolean -> booleanPreferencesKey(key)
            is SettingType.String -> stringPreferencesKey(key)
            is SettingType.StringSet -> stringSetPreferencesKey(key)
            is SettingType.SwipeActionSerializable -> stringPreferencesKey(key)
            is SettingType.Enum<*> -> stringPreferencesKey(key)
            is SettingType.Color -> intPreferencesKey(key)
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

            SettingType.Double ->
                getDoubleStrict(mapOf(key to raw), key, default as Double) as T

            SettingType.SwipeActionSerializable ->
                getSwipeActionSerializableStrict(mapOf(key to raw), key, default as SwipeActionSerializable) as T

            is SettingType.Enum<*> ->
                decodeEnumStrict(
                    raw = raw,
                    key = key,
                    def = default as Enum<*>,
                    enumClass = type.enumClass
                ) as T

            SettingType.Color ->
                getColorStrict(mapOf(key to raw), key, default as Color) as T
        }

    override suspend fun get(ctx: Context): T {
        return ctx.resolveDataStore(dataStoreName)
            .data
            .first()[preferenceKey] ?: default
    }

    override fun flow(ctx: Context): Flow<T> {
        return ctx.resolveDataStore(dataStoreName)
            .data
            .map { it[preferenceKey] ?: default }
    }


    override suspend fun set(ctx: Context, value: T?) {
        ctx.resolveDataStore(dataStoreName).edit {
            if (value != null) {
                it[preferenceKey] = value
            } else {
                it.remove(preferenceKey)
            }
        }
    }
}
