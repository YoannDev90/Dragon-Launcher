package org.elnix.dragonlauncher.data.datastore

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsStore by preferencesDataStore("settingsStore")


object SettingsStore {
    private val ANGLE_LINE_COLOR = intPreferencesKey("angle_line_color")
    fun getAngleLineColor(ctx: Context): Flow<Color?> =
        ctx.settingsStore.data.map { it[ANGLE_LINE_COLOR]?.let { Color(it) }
        }
    suspend fun setAngleLineColor(ctx: Context, color: Color?) {
        ctx.settingsStore.edit { it[ANGLE_LINE_COLOR] = color?.toArgb() ?: 0 }
    }

    private val RGB_LOADING = booleanPreferencesKey("rgb_loading")
    fun getRGBLoading(ctx: Context): Flow<Boolean> =
        ctx.settingsStore.data.map { it[RGB_LOADING] ?: true }
    suspend fun setRGBLoading(ctx: Context, enabled: Boolean) {
        ctx.settingsStore.edit { it[RGB_LOADING] = enabled }
    }

    private val RGB_LINE = booleanPreferencesKey("rgb_line")
    fun getRGBLine(ctx: Context): Flow<Boolean> =
        ctx.settingsStore.data.map { it[RGB_LINE] ?: true }
    suspend fun setRGBLine(ctx: Context, enabled: Boolean) {
        ctx.settingsStore.edit { it[RGB_LINE] = enabled }
    }

    private val DEBUG_INFOS = booleanPreferencesKey("debug_infos")
    fun getDebugInfos(ctx: Context): Flow<Boolean> =
        ctx.settingsStore.data.map { it[DEBUG_INFOS] ?: false }
    suspend fun setDebugInfos(ctx: Context, enabled: Boolean) {
        ctx.settingsStore.edit { it[DEBUG_INFOS] = enabled }
    }


    private val SHOW_LAUNCHING_APP_LABEL = booleanPreferencesKey("show_launching_app_label")
    fun getShowLaunchingAppLabel(ctx: Context): Flow<Boolean> =
        ctx.settingsStore.data.map { it[SHOW_LAUNCHING_APP_LABEL] ?: true }
    suspend fun setShowLaunchingAppLabel(ctx: Context, enabled: Boolean) {
        ctx.settingsStore.edit { it[SHOW_LAUNCHING_APP_LABEL] = enabled }
    }

    private val SHOW_LAUNCHING_APP_ICON = booleanPreferencesKey("show_launching_app_icon")
    fun getShowLaunchingAppIcon(ctx: Context): Flow<Boolean> =
        ctx.settingsStore.data.map { it[SHOW_LAUNCHING_APP_ICON] ?: true }
    suspend fun setShowLaunchingAppIcon(ctx: Context, enabled: Boolean) {
        ctx.settingsStore.edit { it[SHOW_LAUNCHING_APP_ICON] = enabled }
    }
}