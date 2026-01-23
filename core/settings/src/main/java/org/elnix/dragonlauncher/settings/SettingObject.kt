package org.elnix.dragonlauncher.settings
//
//import androidx.compose.ui.graphics.Color
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.booleanPreferencesKey
//import androidx.datastore.preferences.core.doublePreferencesKey
//import androidx.datastore.preferences.core.floatPreferencesKey
//import androidx.datastore.preferences.core.intPreferencesKey
//import androidx.datastore.preferences.core.longPreferencesKey
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.core.stringSetPreferencesKey
//import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
//import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
//
//class SettingObject<T, R> internal constructor(
//    key: String,
//    dataStoreName: DataStoreName,
//    default: T,
//    private val type: SettingType,
//    private val preferenceKey: Preferences.Key<R>,
//    private val encode: (T) -> R,
//    private val decode: (R) -> T
//) : BaseSettingObject<T, R>(key, dataStoreName, default) {
//
////    @Suppress("UNCHECKED_CAST")
////    override val preferenceKey: Preferences.Key<R> =
////        when (type) {
////            SettingType.Int -> intPreferencesKey(key)
////            SettingType.Long -> longPreferencesKey(key)
////            SettingType.Float -> floatPreferencesKey(key)
////            SettingType.Double -> doublePreferencesKey(key)
////            SettingType.Boolean -> booleanPreferencesKey(key)
////            SettingType.String -> stringPreferencesKey(key)
////            SettingType.StringSet -> stringSetPreferencesKey(key)
////            SettingType.SwipeActionSerializable -> stringPreferencesKey(key)
////            is SettingType.Enum<*> -> stringPreferencesKey(key)
////            else -> error("Color must use ColorSettingObject")
////        } as Preferences.Key<R>
////
////    @Suppress("UNCHECKED_CAST")
////    override fun decode(raw: R): T =
////        when (type) {
////            SettingType.Boolean ->
////                getBooleanStrict(mapOf(key to raw), key, default as Boolean) as T
////            SettingType.Int ->
////                getIntStrict(mapOf(key to raw), key, default as Int) as T
////            SettingType.Float ->
////                getFloatStrict(mapOf(key to raw), key, default as Float) as T
////            SettingType.String ->
////                getStringStrict(mapOf(key to raw), key, default as String) as T
////            SettingType.StringSet ->
////                getStringSetStrict(mapOf(key to raw), key, default as Set<String>) as T
////            SettingType.Long ->
////                getLongStrict(mapOf(key to raw), key, default as Long) as T
////            SettingType.Double ->
////                getDoubleStrict(mapOf(key to raw), key, default as Double) as T
////            SettingType.SwipeActionSerializable ->
////                getSwipeActionSerializableStrict(
////                    mapOf(key to raw), key, default as SwipeActionSerializable
////                ) as T
////            is SettingType.Enum<*> ->
////                decodeEnumStrict(
////                    raw = raw,
////                    key = key,
////                    def = default as Enum<*>,
////                    enumClass = type.enumClass
////                ) as T
////            SettingType.Color ->
////                getColorStrict(mapOf(key to raw), key, default as Color) as T
////        }
////
////    @Suppress("UNCHECKED_CAST")
////    override fun encode(value: T): R = when (type) {
////        SettingType.Boolean -> value as Bo
////        SettingType.Color -> TODO()
////        SettingType.Double -> TODO()
////        is SettingType.Enum<*> -> TODO()
////        SettingType.Float -> TODO()
////        SettingType.Int -> TODO()
////        SettingType.Long -> TODO()
////        SettingType.String -> TODO()
////        SettingType.StringSet -> TODO()
////        SettingType.SwipeActionSerializable -> TODO()
////    }
//}
//
//
//
//
//
///*  Implementation of more specific settings objects that has specialized types */
//
//
////class ColorSettingObject(
////    key: String,
////    dataStoreName: DataStoreName,
////    default: Color
////) : BaseSettingObject<Color, Int>(key, dataStoreName, default) {
////
////    override val preferenceKey: Preferences.Key<Int> =
////        intPreferencesKey(key)
////
////    override fun encode(value: Color): Int =
////        value.value.toInt()
////
////    override fun decode(raw: Int): Color =
////        getColorStrict(mapOf(key to raw), key, default)
////}
////
////
////
////class SwipeActionSerializableSettingsObject(
////    key: String,
////    dataStoreName: DataStoreName,
////    default: SwipeActionSerializable
////) : BaseSettingObject<SwipeActionSerializable, String>(key, dataStoreName, default) {
////
////    override val preferenceKey: Preferences.Key<String> =
////        stringPreferencesKey(key)
////
////    override fun encode(value: SwipeActionSerializable): String =
////        value.value.toInt()
////
////    override fun decode(raw: String): SwipeActionSerializable =
////        getSwipeActionSerializableStrict(
////            mapOf(key to raw), key, default
////        )
////}
