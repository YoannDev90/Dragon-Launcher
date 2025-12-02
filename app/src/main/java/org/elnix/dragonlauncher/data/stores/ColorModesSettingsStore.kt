package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.ColorCustomisationMode
import org.elnix.dragonlauncher.data.DefaultThemes
import org.elnix.dragonlauncher.data.colorModeDatastore
import org.elnix.dragonlauncher.data.helpers.ColorPickerMode

object ColorModesSettingsStore {


    private data class ColorModesSettingsBackup(
        val colorPickerMode: ColorPickerMode = ColorPickerMode.SLIDERS,
        val colorCustomisationMode: ColorCustomisationMode = ColorCustomisationMode.DEFAULT,
        val defaultTheme: DefaultThemes = DefaultThemes.AMOLED,
    )
    private val defaults = ColorModesSettingsBackup()


    private val COLOR_PICKER_MODE = stringPreferencesKey(defaults::colorPickerMode.name)
    fun getColorPickerMode(ctx: Context): Flow<ColorPickerMode> =
        ctx.colorModeDatastore.data.map { prefs ->
            prefs[COLOR_PICKER_MODE]?.let { ColorPickerMode.valueOf(it) }
                ?: ColorPickerMode.SLIDERS
        }
    suspend fun setColorPickerMode(ctx: Context, state: ColorPickerMode) {
        ctx.colorModeDatastore.edit { it[COLOR_PICKER_MODE] = state.name}
    }


    private val COLOR_CUSTOMISATION_MODE = stringPreferencesKey(defaults::colorCustomisationMode.name)
    fun getColorCustomisationMode(ctx: Context): Flow<ColorCustomisationMode> =
        ctx.colorModeDatastore.data.map { prefs ->
            prefs[COLOR_CUSTOMISATION_MODE]?.let { ColorCustomisationMode.valueOf(it) }
                ?: ColorCustomisationMode.DEFAULT
        }
    suspend fun setColorCustomisationMode(ctx: Context, state: ColorCustomisationMode) {
        ctx.colorModeDatastore.edit { it[COLOR_CUSTOMISATION_MODE] = state.name }
    }

    private val DEFAULT_THEME = stringPreferencesKey(defaults::defaultTheme.name)
    fun getDefaultTheme(ctx: Context): Flow<DefaultThemes> =
        ctx.colorModeDatastore.data.map { prefs ->
            prefs[DEFAULT_THEME]?.let { DefaultThemes.valueOf(it) }
                ?: DefaultThemes.AMOLED
        }
    suspend fun setDefaultTheme(ctx: Context, state: DefaultThemes) {
        ctx.colorModeDatastore.edit { it[DEFAULT_THEME] = state.name }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.colorModeDatastore.edit { prefs ->
            prefs.remove(COLOR_PICKER_MODE)
            prefs.remove(COLOR_CUSTOMISATION_MODE)
            prefs.remove(DEFAULT_THEME)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.colorModeDatastore.data.first()
        return buildMap {

            fun putIfNonDefault(key: String, value: Any?, default: Any?) {
                if (value != null && value != default) {
                    put(key, value.toString())
                }
            }

            putIfNonDefault(defaults::colorPickerMode.name, prefs[COLOR_PICKER_MODE], defaults::colorPickerMode)
            putIfNonDefault(defaults::colorCustomisationMode.name, prefs[COLOR_CUSTOMISATION_MODE], defaults::colorPickerMode)
            putIfNonDefault(defaults::defaultTheme.name, prefs[DEFAULT_THEME], defaults::defaultTheme)
        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, String>) {
        ctx.colorModeDatastore.edit { prefs ->
            data[defaults::colorPickerMode.name]?.let { prefs[COLOR_PICKER_MODE] = it }
            data[defaults::colorCustomisationMode.name]?.let { prefs[COLOR_CUSTOMISATION_MODE] = it }
            data[defaults::defaultTheme.name]?.let { prefs[DEFAULT_THEME] = it }
        }
    }
}