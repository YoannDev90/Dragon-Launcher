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
        ctx.settingsStore.data.map { prefs ->
            prefs[ANGLE_LINE_COLOR]?.let { Color(it) }
        }
    suspend fun setAngleLineColor(ctx: Context, color: Color?) {
        ctx.settingsStore.edit { it[ANGLE_LINE_COLOR] = color?.toArgb() ?: 0 }
    }

    private val RGB_LOADING = booleanPreferencesKey("rgb_loading")
    fun getRGBLoading(ctx: Context): Flow<Boolean> =
        ctx.settingsStore.data.map { prefs ->
            prefs[RGB_LOADING] ?: true }
    suspend fun setRGBLoading(ctx: Context, enabled: Boolean) {
        ctx.settingsStore.edit { it[RGB_LOADING] = enabled }
    }
}