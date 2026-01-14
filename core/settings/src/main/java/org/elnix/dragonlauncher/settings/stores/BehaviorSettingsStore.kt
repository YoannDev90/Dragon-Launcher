package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipeJson
import org.elnix.dragonlauncher.settings.BaseSettingsStore
import org.elnix.dragonlauncher.settings.behaviorDataStore
import org.elnix.dragonlauncher.settings.getBooleanStrict
import org.elnix.dragonlauncher.settings.getIntStrict
import org.elnix.dragonlauncher.settings.getStringStrict
import org.elnix.dragonlauncher.settings.putIfNonDefault
import kotlin.text.isNotBlank

object BehaviorSettingsStore : BaseSettingsStore<Map<String, Any?>>() {
    override val name: String = "Behavior"

    private data class UiSettingsBackup(
        val backAction: SwipeActionSerializable? = null,
        val doubleClickAction: SwipeActionSerializable? = null,
        val keepScreenOn: Boolean = false,
        val leftPadding: Int = 60,
        val rightPadding: Int = 60,
        val upPadding: Int = 80,
        val downPadding: Int = 100,
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

    suspend fun setBackAction(ctx: Context, value: SwipeActionSerializable?) {
        ctx.behaviorDataStore.edit {
            if (value != null) it[Keys.BACK_ACTION] = SwipeJson.encodeAction(value)
            else it.remove(Keys.BACK_ACTION)
        }
    }

    fun getDoubleClickAction(ctx: Context): Flow<SwipeActionSerializable?> =
        ctx.behaviorDataStore.data.map {
            it[Keys.DOUBLE_CLICK_ACTION]?.takeIf { s -> s.isNotBlank() }?.let(SwipeJson::decodeAction)
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

    suspend fun setLeftPadding(ctx: Context, value: Int) {
        ctx.behaviorDataStore.edit { it[Keys.LEFT_PADDING] = value }
    }

    fun getLeftPadding(ctx: Context): Flow<Int> =
        ctx.behaviorDataStore.data.map { it[Keys.LEFT_PADDING] ?: defaults.leftPadding }

    suspend fun setRightPadding(ctx: Context, value: Int) {
        ctx.behaviorDataStore.edit { it[Keys.RIGHT_PADDING] = value }
    }

    fun getRightPadding(ctx: Context): Flow<Int> =
        ctx.behaviorDataStore.data.map { it[Keys.RIGHT_PADDING] ?: defaults.rightPadding }

    suspend fun setUpPadding(ctx: Context, value: Int) {
        ctx.behaviorDataStore.edit { it[Keys.UP_PADDING] = value }
    }

    fun getUpPadding(ctx: Context): Flow<Int> =
        ctx.behaviorDataStore.data.map { it[Keys.UP_PADDING] ?: defaults.upPadding }

    suspend fun setDownPadding(ctx: Context, value: Int) {
        ctx.behaviorDataStore.edit { it[Keys.DOWN_PADDING] = value }
    }

    fun getDownPadding(ctx: Context): Flow<Int> =
        ctx.behaviorDataStore.data.map { it[Keys.DOWN_PADDING] ?: defaults.downPadding }


    override suspend fun resetAll(ctx: Context) {
        ctx.behaviorDataStore.edit { prefs ->
            Keys.ALL.forEach { prefs.remove(it) }
        }
    }

    override suspend fun getAll(ctx: Context): Map<String, Any> {
        val prefs = ctx.behaviorDataStore.data.first()

        return buildMap {

            putIfNonDefault(
                Keys.BACK_ACTION,
                prefs[Keys.BACK_ACTION],
                defaults.backAction
            )

            putIfNonDefault(
                Keys.DOUBLE_CLICK_ACTION,
                prefs[Keys.DOUBLE_CLICK_ACTION],
                defaults.doubleClickAction
            )

            putIfNonDefault(
                Keys.KEEP_SCREEN_ON,
                prefs[Keys.KEEP_SCREEN_ON],
                defaults.keepScreenOn
            )

            putIfNonDefault(
                Keys.LEFT_PADDING,
                prefs[Keys.LEFT_PADDING],
                defaults.leftPadding
            )

            putIfNonDefault(
                Keys.RIGHT_PADDING,
                prefs[Keys.RIGHT_PADDING],
                defaults.rightPadding
            )

            putIfNonDefault(
                Keys.UP_PADDING,
                prefs[Keys.UP_PADDING],
                defaults.upPadding
            )

            putIfNonDefault(
                Keys.DOWN_PADDING,
                prefs[Keys.DOWN_PADDING],
                defaults.downPadding
            )
        }
    }


    override suspend fun setAll(ctx: Context, value: Map<String, Any?>) {
        ctx.behaviorDataStore.edit { prefs ->

            prefs[Keys.BACK_ACTION] =
                getStringStrict(
                    value,
                    Keys.BACK_ACTION,
                    defaults.backAction.toString()
                )

            prefs[Keys.DOUBLE_CLICK_ACTION] =
                getStringStrict(
                    value,
                    Keys.DOUBLE_CLICK_ACTION,
                    defaults.doubleClickAction.toString()
                )

            prefs[Keys.KEEP_SCREEN_ON] =
                getBooleanStrict(
                    value,
                    Keys.KEEP_SCREEN_ON,
                    defaults.keepScreenOn
                )

            prefs[Keys.LEFT_PADDING] =
                getIntStrict(
                    value,
                    Keys.LEFT_PADDING,
                    defaults.leftPadding
                )

            prefs[Keys.RIGHT_PADDING] =
                getIntStrict(
                    value,
                    Keys.RIGHT_PADDING,
                    defaults.rightPadding
                )

            prefs[Keys.UP_PADDING] =
                getIntStrict(
                    value,
                    Keys.UP_PADDING,
                    defaults.upPadding
                )

            prefs[Keys.DOWN_PADDING] =
                getIntStrict(
                    value,
                    Keys.DOWN_PADDING,
                    defaults.downPadding
                )
        }
    }
}
