package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.uiDatastore

object UiSettingsStore {

    private data class UiSettingsBackup(
        val rgbLoading: Boolean = true,
        val rgbLine: Boolean = true,
        val showLaunchingAppLabel: Boolean = true,
        val showLaunchingAppIcon: Boolean = true,
        val showAppLaunchPreviewCircle: Boolean = true,
        val fullscreen: Boolean = true,
        val showAppCirclePreview: Boolean = true,
        val showAppLinePreview: Boolean = true,
    )

    private val defaults = UiSettingsBackup()

    private val RGB_LOADING = booleanPreferencesKey(defaults::rgbLoading.name)
    fun getRGBLoading(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[RGB_LOADING] ?: defaults.rgbLoading }
    suspend fun setRGBLoading(ctx: Context, enabled: Boolean) {
        ctx.uiDatastore.edit { it[RGB_LOADING] = enabled }
    }

    private val RGB_LINE = booleanPreferencesKey(defaults::rgbLine.name)
    fun getRGBLine(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[RGB_LINE] ?: defaults.rgbLine }
    suspend fun setRGBLine(ctx: Context, enabled: Boolean) {
        ctx.uiDatastore.edit { it[RGB_LINE] = enabled }
    }

    private val SHOW_LAUNCHING_APP_LABEL = booleanPreferencesKey(defaults::showLaunchingAppLabel.name)
    fun getShowLaunchingAppLabel(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[SHOW_LAUNCHING_APP_LABEL] ?: defaults.showLaunchingAppLabel }
    suspend fun setShowLaunchingAppLabel(ctx: Context, enabled: Boolean) {
        ctx.uiDatastore.edit { it[SHOW_LAUNCHING_APP_LABEL] = enabled }
    }

    private val SHOW_LAUNCHING_APP_ICON = booleanPreferencesKey(defaults::showLaunchingAppIcon.name)
    fun getShowLaunchingAppIcon(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[SHOW_LAUNCHING_APP_ICON] ?: defaults.showLaunchingAppIcon }
    suspend fun setShowLaunchingAppIcon(ctx: Context, enabled: Boolean) {
        ctx.uiDatastore.edit { it[SHOW_LAUNCHING_APP_ICON] = enabled }
    }

    private val SHOW_APP_LAUNCH_PREVIEW = booleanPreferencesKey(defaults::showAppLaunchPreviewCircle.name)
    fun getShowAppLaunchPreview(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[SHOW_APP_LAUNCH_PREVIEW] ?: defaults.showAppLaunchPreviewCircle }
    suspend fun setShowAppLaunchPreview(ctx: Context, enabled: Boolean) {
        ctx.uiDatastore.edit { it[SHOW_APP_LAUNCH_PREVIEW] = enabled }
    }

    private val FULLSCREEN = booleanPreferencesKey(defaults::fullscreen.name)
    fun getFullscreen(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[FULLSCREEN] ?: defaults.fullscreen }
    suspend fun setFullscreen(ctx: Context, enabled: Boolean) {
        ctx.uiDatastore.edit { it[FULLSCREEN] = enabled }
    }

    private val SHOW_CIRCLE_PREVIEW = booleanPreferencesKey(defaults::showAppCirclePreview.name)
    fun getShowCirclePreview(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[SHOW_CIRCLE_PREVIEW] ?: defaults.showAppCirclePreview }
    suspend fun setShowCirclePreview(ctx: Context, enabled: Boolean) {
        ctx.uiDatastore.edit { it[SHOW_CIRCLE_PREVIEW] = enabled }
    }

    private val SHOW_LINE_PREVIEW = booleanPreferencesKey(defaults::showAppLinePreview.name)
    fun getShowLinePreview(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[SHOW_LINE_PREVIEW] ?: defaults.showAppLinePreview }
    suspend fun setShowLinePreview(ctx: Context, enabled: Boolean) {
        ctx.uiDatastore.edit { it[SHOW_LINE_PREVIEW] = enabled }
    }

    suspend fun resetAll(ctx: Context) {
        ctx.uiDatastore.edit { prefs ->
            prefs.remove(RGB_LOADING)
            prefs.remove(RGB_LINE)
            prefs.remove(SHOW_LAUNCHING_APP_LABEL)
            prefs.remove(SHOW_LAUNCHING_APP_ICON)
            prefs.remove(SHOW_APP_LAUNCH_PREVIEW)
            prefs.remove(FULLSCREEN)
            prefs.remove(SHOW_CIRCLE_PREVIEW)
            prefs.remove(SHOW_LINE_PREVIEW)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.uiDatastore.data.first()

        return buildMap {

            fun putIfNonDefault(key: String, value: Any?, default: Any?) {
                if (value != null && value != default) {
                    put(key, value.toString())
                }
            }

            putIfNonDefault(defaults::rgbLoading.name, prefs[RGB_LOADING], defaults.rgbLoading)
            putIfNonDefault(defaults::rgbLine.name, prefs[RGB_LINE], defaults.rgbLine)
            putIfNonDefault(defaults::showLaunchingAppLabel.name, prefs[SHOW_LAUNCHING_APP_LABEL], defaults.showLaunchingAppLabel)
            putIfNonDefault(defaults::showLaunchingAppIcon.name, prefs[SHOW_LAUNCHING_APP_ICON], defaults.showLaunchingAppIcon)
            putIfNonDefault(defaults::showAppLaunchPreviewCircle.name, prefs[SHOW_APP_LAUNCH_PREVIEW], defaults.showAppLaunchPreviewCircle)
            putIfNonDefault(defaults::fullscreen.name, prefs[FULLSCREEN], defaults.fullscreen)
            putIfNonDefault(defaults::showAppCirclePreview.name, prefs[SHOW_CIRCLE_PREVIEW], defaults.showAppCirclePreview)
            putIfNonDefault(defaults::showAppLinePreview.name, prefs[SHOW_LINE_PREVIEW], defaults.showAppLinePreview)
        }
    }

    suspend fun setAll(ctx: Context, backup: Map<String, String>) {
        ctx.uiDatastore.edit { prefs ->

            backup[defaults::rgbLoading.name]?.let {
                prefs[RGB_LOADING] = it.toBoolean()
            }

            backup[defaults::rgbLine.name]?.let {
                prefs[RGB_LINE] = it.toBoolean()
            }

            backup[defaults::showLaunchingAppLabel.name]?.let {
                prefs[SHOW_LAUNCHING_APP_LABEL] = it.toBoolean()
            }

            backup[defaults::showLaunchingAppIcon.name]?.let {
                prefs[SHOW_LAUNCHING_APP_ICON] = it.toBoolean()
            }

            backup[defaults::showAppLaunchPreviewCircle.name]?.let {
                prefs[SHOW_APP_LAUNCH_PREVIEW] = it.toBoolean()
            }

            backup[defaults::fullscreen.name]?.let {
                prefs[FULLSCREEN] = it.toBoolean()
            }

            backup[defaults::showAppCirclePreview.name]?.let {
                prefs[SHOW_CIRCLE_PREVIEW] = it.toBoolean()
            }

            backup[defaults::showAppLinePreview.name]?.let {
                prefs[SHOW_LINE_PREVIEW] = it.toBoolean()
            }
        }
    }
}
