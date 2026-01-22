package org.elnix.dragonlauncher.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

abstract class SettingObject<T>(
    key: String,
    dataStoreName: DataStoreName,
    default: T,
    type: SettingType
) : BaseSettingObject<T>(key, dataStoreName, default) {

    @Suppress("UNCHECKED_CAST")
    override val preferenceKey: Preferences.Key<T> =
        when (type) {
            is SettingType.Int -> androidx.datastore.preferences.core.intPreferencesKey(key)
            is SettingType.Long -> androidx.datastore.preferences.core.longPreferencesKey(key)
            is SettingType.Float -> androidx.datastore.preferences.core.floatPreferencesKey(key)
//            is SettingType.Double -> androidx.datastore.preferences.core.doublePreferencesKey(key)
            is SettingType.Boolean -> androidx.datastore.preferences.core.booleanPreferencesKey(key)
            is SettingType.String -> androidx.datastore.preferences.core.stringPreferencesKey(key)
            is SettingType.StringSet -> androidx.datastore.preferences.core.stringSetPreferencesKey(key)
        } as Preferences.Key<T>


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
