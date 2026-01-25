package org.elnix.dragonlauncher.settings.bases

import android.content.Context
import org.elnix.dragonlauncher.settings.DataStoreName
import org.json.JSONObject

/**
 * Base abstract class for a collection of related settings in a [androidx.datastore.core.DataStore].
 *
 * A `BaseSettingsStore` represents a **group of settings** (e.g., all color settings,
 * all user preferences, etc.) and provides a consistent API to:
 *   - access all settings in the store,
 *   - reset them,
 *   - get/set the entire store as a single object,
 *   - import/export for backup.
 *
 * @param T The aggregate type representing the values of the entire store.
 *
 * ### Responsibilities
 * - Defines a list of all contained settings via [ALL].
 * - Provides utility methods to reset all settings to their defaults.
 * - Requires concrete implementations to provide methods for reading/writing all settings
 *   at once ([getAll], [setAll]).
 * - Supports backup and restore via JSON ([exportForBackup], [importFromBackup]).
 *
 * ### Example Usage
 * ```
 * class ColorSettingsStore(
 *     override val dataStoreName: DataStoreName
 * ) : BaseSettingsStore<ColorSettingsState>() {
 *
 *     override val name = "ColorSettings"
 *
 *     val primaryColor = Settings.color("primary_color", dataStoreName, AmoledDefault.Primary)
 *     val secondaryColor = Settings.color("secondary_color", dataStoreName, AmoledDefault.Secondary)
 *
 *     override val ALL: List<BaseSettingObject<*, *>> = listOf(
 *         primaryColor, secondaryColor
 *     )
 *
 *     override suspend fun getAll(ctx: Context): ColorSettingsState { ... }
 *     override suspend fun setAll(ctx: Context, value: ColorSettingsState) { ... }
 *     override suspend fun exportForBackup(ctx: Context): JSONObject? { ... }
 *     override suspend fun importFromBackup(ctx: Context, json: JSONObject) { ... }
 * }
 * ```
 *
 * ### Notes
 * - Each element in [ALL] should be a concrete subclass of [BaseSettingObject].
 * - `resetAll(ctx)` iterates over all settings in [ALL] and restores their default values.
 * - The type [T] allows a store to represent the **combined state** of all settings, useful for
 *   loading, saving, and backup in one operation.
 */
abstract class BaseSettingsStore<T> {

    /** Human-readable name for this settings store, e.g., "ColorSettings". */
    abstract val name: String

    /** Identifier of the [androidx.datastore.core.DataStore] used to persist these settings. */
    abstract val dataStoreName: DataStoreName

    /**
     * List of all individual settings in this store.
     *
     * Each item must be a concrete instance of [BaseSettingObject].
     * This list is used for operations like [resetAll].
     */
    @Suppress("PropertyName")
    abstract val ALL: List<BaseSettingObject<*, *>>

    /**
     * Resets all settings in this store to their default values.
     *
     * @param ctx The Android [Context] required to access the underlying DataStore.
     */
    suspend fun resetAll(ctx: Context) {
        ALL.forEach { it.reset(ctx) }
    }

    /**
     * Reads the current state of all settings in this store.
     *
     * @param ctx The Android [Context] required to access the underlying DataStore.
     * @return The aggregate state of type [T].
     */
    abstract suspend fun getAll(ctx: Context): T

    /**
     * Writes the given aggregate state to all settings in this store.
     *
     * @param ctx The Android [Context] required to access the underlying DataStore.
     * @param value The new state to write to all settings.
     */
    abstract suspend fun setAll(ctx: Context, value: T)

    /**
     * Exports the current state of all settings as a [JSONObject] for backup purposes.
     *
     * @param ctx The Android [Context] required to access the underlying DataStore.
     * @return A [JSONObject] representing all settings, or `null` if nothing to export.
     */
    abstract suspend fun exportForBackup(ctx: Context): JSONObject?

    /**
     * Imports settings from a [JSONObject] backup.
     *
     * @param ctx The Android [Context] required to access the underlying DataStore.
     * @param json The [JSONObject] containing backup values.
     */
    abstract suspend fun importFromBackup(ctx: Context, json: JSONObject?)
}
