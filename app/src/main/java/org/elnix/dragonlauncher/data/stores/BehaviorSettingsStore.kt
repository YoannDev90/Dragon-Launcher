package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.BaseSettingsStore
import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.data.SwipeJson
import org.elnix.dragonlauncher.data.behaviorDataStore

object BehaviorSettingsStore : BaseSettingsStore() {
    override val name: String = "Behavior"

    private data class UiSettingsBackup(
        val backAction: SwipeActionSerializable? = null,
        val doubleClickAction: SwipeActionSerializable? = null,
        val keepScreenOn: Boolean = false,
        val leftPadding: Int = 0,
        val rightPadding: Int = 0,
        val upPadding: Int = 0,
        val downPadding: Int = 0
    )

    private val defaults = UiSettingsBackup()

    private object Keys {
        val BACK_ACTION = stringPreferencesKey("backAction")
        val DOUBLE_CLICK_ACTION = stringPreferencesKey("doubleClickAction")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keepScreenOn")
        val LEFT_PADDING = intPreferencesKey("leftPadding")
        val RIGHT_PADDING = intPreferencesKey("rightPadding")
        val UP_PADDING = intPreferencesKey("upPadding")
        val DOWN_PADDING = intPreferencesKey("downPadding")
        val ALL = listOf(
            BACK_ACTION,
            DOUBLE_CLICK_ACTION,
            KEEP_SCREEN_ON,
            LEFT_PADDING,
            RIGHT_PADDING,
            UP_PADDING,
            DOWN_PADDING
        )
    }

    fun getBackAction(ctx: Context): Flow<SwipeActionSerializable?> =
        ctx.behaviorDataStore.data.map {
            it[Keys.BACK_ACTION]?.takeIf { s -> s.isNotBlank() }?.let(SwipeJson::decodeAction)
        }

    fun getDoubleClickAction(ctx: Context): Flow<SwipeActionSerializable?> =
        ctx.behaviorDataStore.data.map {
            it[Keys.DOUBLE_CLICK_ACTION]?.takeIf { s -> s.isNotBlank() }?.let(SwipeJson::decodeAction)
        }

    suspend fun setBackAction(ctx: Context, value: SwipeActionSerializable?) {
        ctx.behaviorDataStore.edit {
            if (value != null) it[Keys.BACK_ACTION] = SwipeJson.encodeAction(value)
            else it.remove(Keys.BACK_ACTION)
        }
    }

    suspend fun setDoubleClickAction(ctx: Context, value: SwipeActionSerializable?) {
        ctx.behaviorDataStore.edit {
            if (value != null) it[Keys.DOUBLE_CLICK_ACTION] = SwipeJson.encodeAction(value)
            else it.remove(Keys.DOUBLE_CLICK_ACTION)
        }
    }

    fun getKeepScreenOn(ctx: Context): Flow<Boolean> =
        ctx.behaviorDataStore.data.map { it[Keys.KEEP_SCREEN_ON] ?: defaults.keepScreenOn }

    suspend fun setKeepScreenOn(ctx: Context, value: Boolean) {
        ctx.behaviorDataStore.edit { it[Keys.KEEP_SCREEN_ON] = value }
    }

    fun getLeftPadding(ctx: Context): Flow<Int> =
        ctx.behaviorDataStore.data.map { it[Keys.LEFT_PADDING] ?: defaults.leftPadding }

    fun getRightPadding(ctx: Context): Flow<Int> =
        ctx.behaviorDataStore.data.map { it[Keys.RIGHT_PADDING] ?: defaults.rightPadding }

    fun getUpPadding(ctx: Context): Flow<Int> =
        ctx.behaviorDataStore.data.map { it[Keys.UP_PADDING] ?: defaults.upPadding }

    fun getDownPadding(ctx: Context): Flow<Int> =
        ctx.behaviorDataStore.data.map { it[Keys.DOWN_PADDING] ?: defaults.downPadding }

    suspend fun setLeftPadding(ctx: Context, value: Int) {
        ctx.behaviorDataStore.edit { it[Keys.LEFT_PADDING] = value }
    }

    suspend fun setRightPadding(ctx: Context, value: Int) {
        ctx.behaviorDataStore.edit { it[Keys.RIGHT_PADDING] = value }
    }

    suspend fun setUpPadding(ctx: Context, value: Int) {
        ctx.behaviorDataStore.edit { it[Keys.UP_PADDING] = value }
    }

    suspend fun setDownPadding(ctx: Context, value: Int) {
        ctx.behaviorDataStore.edit { it[Keys.DOWN_PADDING] = value }
    }

    override suspend fun resetAll(ctx: Context) {
        ctx.behaviorDataStore.edit { prefs ->
            Keys.ALL.forEach { prefs.remove(it) }
        }
    }

    suspend fun getAll(ctx: Context): Map<String, Any> {
        val prefs = ctx.behaviorDataStore.data.first()

        return buildMap {
            fun putIfChanged(key: Preferences.Key<Boolean>, default: Boolean) {
                val v = prefs[key]
                if (v != null && v != default) put(key.name, v)
            }

            fun putIfChanged(key: Preferences.Key<String>, default: String?) {
                val v = prefs[key]
                if (v != null && v != default) put(key.name, v)
            }

            fun putIfChanged(key: Preferences.Key<Int>, default: Int) {
                val v = prefs[key]
                if (v != null && v != default) put(key.name, v)
            }

            putIfChanged(Keys.BACK_ACTION, null)
            putIfChanged(Keys.DOUBLE_CLICK_ACTION, null)
            putIfChanged(Keys.KEEP_SCREEN_ON, defaults.keepScreenOn)
            putIfChanged(Keys.LEFT_PADDING, defaults.leftPadding)
            putIfChanged(Keys.RIGHT_PADDING, defaults.rightPadding)
            putIfChanged(Keys.UP_PADDING, defaults.upPadding)
            putIfChanged(Keys.DOWN_PADDING, defaults.downPadding)
        }
    }

    suspend fun setAll(ctx: Context, backup: Map<String, Any?>) {
        ctx.behaviorDataStore.edit { prefs ->
            fun applyString(key: Preferences.Key<String>) {
                val raw = backup[key.name] ?: return
                prefs[key] = raw.toString()
            }

            fun applyBoolean(key: Preferences.Key<Boolean>) {
                val raw = backup[key.name] ?: return
                val v = when (raw) {
                    is Boolean -> raw
                    is String -> raw.toBooleanStrictOrNull() ?: return
                    else -> return
                }
                prefs[key] = v
            }

            fun applyInt(key: Preferences.Key<Int>) {
                val raw = backup[key.name] ?: return
                val v = when (raw) {
                    is Int -> raw
                    is String -> raw.toIntOrNull() ?: return
                    else -> return
                }
                prefs[key] = v
            }

            applyString(Keys.BACK_ACTION)
            applyString(Keys.DOUBLE_CLICK_ACTION)
            applyBoolean(Keys.KEEP_SCREEN_ON)
            applyInt(Keys.LEFT_PADDING)
            applyInt(Keys.RIGHT_PADDING)
            applyInt(Keys.UP_PADDING)
            applyInt(Keys.DOWN_PADDING)
        }
    }
}
