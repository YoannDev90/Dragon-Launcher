package org.elnix.dragonlauncher.settings



/**
 * Describes the physical storage and decoding type of a setting.
 *
 * `SettingType` defines:
 * - which `Preferences.Key<T>` implementation is used in DataStore
 * - how raw values (e.g. from JSON backups or legacy stores) are decoded
 *
 * It is intentionally separated from the Kotlin generic type `T` to allow:
 * - uniform handling of heterogeneous settings
 * - runtime decoding during import / migration
 * - type-safe DataStore access without reflection
 *
 * Each object corresponds to exactly one supported DataStore value type.
 */
sealed interface SettingType {

    /**
     * Boolean preference.
     *
     * Stored using `booleanPreferencesKey`.
     * Typical use: feature flags, toggles.
     */
    data object Boolean : SettingType

    /**
     * Integer preference.
     *
     * Stored using `intPreferencesKey`.
     * Typical use: sizes, paddings, counters.
     */
    data object Int : SettingType

    /**
     * Floating-point preference (32-bit).
     *
     * Stored using `floatPreferencesKey`.
     * Typical use: UI scaling factors.
     */
    data object Float : SettingType

    /**
     * Long integer preference (64-bit).
     *
     * Stored using `longPreferencesKey`.
     * Typical use: timestamps, durations.
     */
    data object Long : SettingType

    /**
     * Double-precision floating-point preference (64-bit).
     *
     * Stored using `doublePreferencesKey`.
     * Typical use: precise numeric values.
     */
    data object Double : SettingType

    /**
     * String preference.
     *
     * Stored using `stringPreferencesKey`.
     * Typical use: identifiers, modes, serialized data.
     */
    data object String : SettingType

    /**
     * Set of strings preference.
     *
     * Stored using `stringSetPreferencesKey`.
     * Typical use: collections of IDs or flags.
     */
    data object StringSet : SettingType

    /**
     * Serialized [SwipeActionSerializable] preference.
     *
     * Stored as a JSON string using `stringPreferencesKey`,
     * then decoded into a `SwipeActionSerializable` instance.
     *
     * This allows complex structured data to be stored while
     * keeping DataStore strictly primitive.
     */
    data object SwipeActionSerializable : SettingType

    data class Enum<E : kotlin.Enum<E>>(
        val enumClass: Class<E>
    ) : SettingType
}
