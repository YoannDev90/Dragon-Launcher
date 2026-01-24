package org.elnix.dragonlauncher.settings

import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipeJson
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject

/**
 * Factory object for creating typed [org.elnix.dragonlauncher.settings.bases.BaseSettingObject] instances backed by DataStore.
 *
 * This object provides convenient functions to create strongly-typed settings without
 * manually specifying generic parameters or creating dedicated subclasses for every type.
 *
 * Supported types include:
 * - Primitive types: [Boolean], [Int], [Long], [Float], [Double], [String], [Set<String>]
 * - Enum types: any [Enum] using its name as the stored string
 * - Complex types: [Color] and [SwipeActionSerializable], with proper encode/decode handling
 *
 * Each function returns a [org.elnix.dragonlauncher.settings.bases.BaseSettingObject] which can be used to get/set values, reset
 * the setting, or observe changes via flows.
 *
 * Example usage:
 * ```
 * val primaryColor = Settings.color(
 *     key = "primary_color",
 *     dataStoreName = dataStoreName,
 *     default = AmoledDefault.Primary
 * )
 *
 * val swipeAction = Settings.swipeAction(
 *     key = "main_swipe_action",
 *     dataStoreName = dataStoreName,
 *     default = SwipeActionSerializable.OpenDragonLauncherSettings
 * )
 * ```
 */
object Settings {

    fun boolean(
        key: String,
        dataStoreName: DataStoreName,
        default: Boolean
    ): BaseSettingObject<Boolean, Boolean> =
        BaseSettingObject(
            key = key,
            dataStoreName = dataStoreName,
            default = default,
            preferenceKey = booleanPreferencesKey(key),
            encode = { it },
            decode = { raw ->
                getBooleanStrict(raw, key, default)
            }
        )

    fun int(
        key: String,
        dataStoreName: DataStoreName,
        default: Int
    ): BaseSettingObject<Int, Int> =
        BaseSettingObject(
            key = key,
            dataStoreName = dataStoreName,
            default = default,
            preferenceKey = intPreferencesKey(key),
            encode = { it },
            decode = { raw ->
                getIntStrict(raw, key, default)
            }
        )


    fun float(
        key: String,
        dataStoreName: DataStoreName,
        default: Float
    ): BaseSettingObject<Float, Float> =
        BaseSettingObject(
            key = key,
            dataStoreName = dataStoreName,
            default = default,
            preferenceKey = floatPreferencesKey(key),
            encode = { it },
            decode = { raw ->
                getFloatStrict(raw, key, default)
            }
        )

    fun long(
        key: String,
        dataStoreName: DataStoreName,
        default: Long
    ): BaseSettingObject<Long, Long> =
        BaseSettingObject(
            key = key,
            dataStoreName = dataStoreName,
            default = default,
            preferenceKey = longPreferencesKey(key),
            encode = { it },
            decode = { raw ->
                getLongStrict(raw, key, default)
            }
        )

    fun double(
        key: String,
        dataStoreName: DataStoreName,
        default: Double
    ): BaseSettingObject<Double, Double> =
        BaseSettingObject(
            key = key,
            dataStoreName = dataStoreName,
            default = default,
            preferenceKey = doublePreferencesKey(key),
            encode = { it },
            decode = { raw ->
                getDoubleStrict(raw, key, default)
            }
        )


    fun string(
        key: String,
        dataStoreName: DataStoreName,
        default: String
    ): BaseSettingObject<String, String> =
        BaseSettingObject(
            key = key,
            dataStoreName = dataStoreName,
            default = default,
            preferenceKey = stringPreferencesKey(key),
            encode = { it },
            decode = { raw ->
                getStringStrict(raw, key, default)
            }
        )

    fun stringSet(
        key: String,
        dataStoreName: DataStoreName,
        default: Set<String>
    ): BaseSettingObject<Set<String>, Set<String>> =
        BaseSettingObject(
            key = key,
            dataStoreName = dataStoreName,
            default = default,
            preferenceKey = stringSetPreferencesKey(key),
            encode = { it },
            decode = { raw ->
                getStringSetStrict(raw, key, default)
            }
        )

    fun <E : Enum<E>> enum(
        key: String,
        dataStoreName: DataStoreName,
        default: E,
        enumClass: Class<E>
    ): BaseSettingObject<E, String> =
        BaseSettingObject(
            key = key,
            dataStoreName = dataStoreName,
            default = default,
            preferenceKey = stringPreferencesKey(key),
            encode = { it.name },
            decode = { raw ->
                getEnumStrict(raw, key, default, enumClass)

            }
        )

    fun color(
        key: String,
        dataStoreName: DataStoreName,
        default: Color
    ): BaseSettingObject<Color, Int> =
        BaseSettingObject(
            key = key,
            dataStoreName = dataStoreName,
            default = default,
            preferenceKey = intPreferencesKey(key),
            encode = { it.value.toInt() },
            decode = { raw ->
                getColorStrict(raw, key, default)
            }
        )

    fun swipeAction(
        key: String,
        dataStoreName: DataStoreName,
        default: SwipeActionSerializable
    ): BaseSettingObject<SwipeActionSerializable, String> =
        BaseSettingObject(
            key = key,
            dataStoreName = dataStoreName,
            default = default,
            preferenceKey = stringPreferencesKey(key),
            encode = { SwipeJson.encodeAction(it) },
            decode = { raw ->
                getSwipeActionSerializableStrict(raw, key, default)
            }
        )
}
