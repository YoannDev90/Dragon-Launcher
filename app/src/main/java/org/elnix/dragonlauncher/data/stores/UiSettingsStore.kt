package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.BackupTypeException
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
        val snapPoints: Boolean = true
    )

    private val defaults = UiSettingsBackup()

    private object Keys {
        val RGB_LOADING = booleanPreferencesKey(UiSettingsBackup::rgbLoading.name)
        val RGB_LINE = booleanPreferencesKey(UiSettingsBackup::rgbLine.name)
        val SHOW_LAUNCHING_APP_LABEL = booleanPreferencesKey(UiSettingsBackup::showLaunchingAppLabel.name)
        val SHOW_LAUNCHING_APP_ICON = booleanPreferencesKey(UiSettingsBackup::showLaunchingAppIcon.name)
        val SHOW_APP_LAUNCH_PREVIEW = booleanPreferencesKey(UiSettingsBackup::showAppLaunchPreviewCircle.name)
        val FULLSCREEN = booleanPreferencesKey(UiSettingsBackup::fullscreen.name)
        val SHOW_CIRCLE_PREVIEW = booleanPreferencesKey(UiSettingsBackup::showAppCirclePreview.name)
        val SHOW_LINE_PREVIEW = booleanPreferencesKey(UiSettingsBackup::showAppLinePreview.name)
        val SNAP_POINTS = booleanPreferencesKey(UiSettingsBackup::snapPoints.name)

        val ALL = listOf(
            RGB_LOADING,
            RGB_LINE,
            SHOW_LAUNCHING_APP_LABEL,
            SHOW_LAUNCHING_APP_ICON,
            SHOW_APP_LAUNCH_PREVIEW,
            FULLSCREEN,
            SHOW_CIRCLE_PREVIEW,
            SHOW_LINE_PREVIEW,
            SNAP_POINTS
        )
    }

    fun getRGBLoading(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[Keys.RGB_LOADING] ?: defaults.rgbLoading }

    suspend fun setRGBLoading(ctx: Context, value: Boolean) {
        ctx.uiDatastore.edit { it[Keys.RGB_LOADING] = value }
    }

    fun getRGBLine(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[Keys.RGB_LINE] ?: defaults.rgbLine }

    suspend fun setRGBLine(ctx: Context, value: Boolean) {
        ctx.uiDatastore.edit { it[Keys.RGB_LINE] = value }
    }

    fun getShowLaunchingAppLabel(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[Keys.SHOW_LAUNCHING_APP_LABEL] ?: defaults.showLaunchingAppLabel }

    suspend fun setShowLaunchingAppLabel(ctx: Context, value: Boolean) {
        ctx.uiDatastore.edit { it[Keys.SHOW_LAUNCHING_APP_LABEL] = value }
    }

    fun getShowLaunchingAppIcon(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[Keys.SHOW_LAUNCHING_APP_ICON] ?: defaults.showLaunchingAppIcon }

    suspend fun setShowLaunchingAppIcon(ctx: Context, value: Boolean) {
        ctx.uiDatastore.edit { it[Keys.SHOW_LAUNCHING_APP_ICON] = value }
    }

    fun getShowAppLaunchPreview(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[Keys.SHOW_APP_LAUNCH_PREVIEW] ?: defaults.showAppLaunchPreviewCircle }

    suspend fun setShowAppLaunchPreview(ctx: Context, value: Boolean) {
        ctx.uiDatastore.edit { it[Keys.SHOW_APP_LAUNCH_PREVIEW] = value }
    }

    fun getFullscreen(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[Keys.FULLSCREEN] ?: defaults.fullscreen }

    suspend fun setFullscreen(ctx: Context, value: Boolean) {
        ctx.uiDatastore.edit { it[Keys.FULLSCREEN] = value }
    }

    fun getShowCirclePreview(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[Keys.SHOW_CIRCLE_PREVIEW] ?: defaults.showAppCirclePreview }

    suspend fun setShowCirclePreview(ctx: Context, value: Boolean) {
        ctx.uiDatastore.edit { it[Keys.SHOW_CIRCLE_PREVIEW] = value }
    }

    fun getShowLinePreview(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[Keys.SHOW_LINE_PREVIEW] ?: defaults.showAppLinePreview }

    suspend fun setShowLinePreview(ctx: Context, value: Boolean) {
        ctx.uiDatastore.edit { it[Keys.SHOW_LINE_PREVIEW] = value }
    }

    fun getSnapPoints(ctx: Context): Flow<Boolean> =
        ctx.uiDatastore.data.map { it[Keys.SNAP_POINTS] ?: defaults.snapPoints }

    suspend fun setSnapPoints(ctx: Context, value: Boolean) {
        ctx.uiDatastore.edit { it[Keys.SNAP_POINTS] = value }
    }


    suspend fun resetAll(ctx: Context) {
        ctx.uiDatastore.edit { prefs ->
            Keys.ALL.forEach { prefs.remove(it) }
        }
    }

    suspend fun getAll(ctx: Context): Map<String, Any> {
        val prefs = ctx.uiDatastore.data.first()

        return buildMap {

            fun putIfChanged(key: Preferences.Key<Boolean>, default: Boolean) {
                val v = prefs[key]
                if (v != null && v != default) put(key.name, v)
            }

            putIfChanged(Keys.RGB_LOADING, defaults.rgbLoading)
            putIfChanged(Keys.RGB_LINE, defaults.rgbLine)
            putIfChanged(Keys.SHOW_LAUNCHING_APP_LABEL, defaults.showLaunchingAppLabel)
            putIfChanged(Keys.SHOW_LAUNCHING_APP_ICON, defaults.showLaunchingAppIcon)
            putIfChanged(Keys.SHOW_APP_LAUNCH_PREVIEW, defaults.showAppLaunchPreviewCircle)
            putIfChanged(Keys.FULLSCREEN, defaults.fullscreen)
            putIfChanged(Keys.SHOW_CIRCLE_PREVIEW, defaults.showAppCirclePreview)
            putIfChanged(Keys.SHOW_LINE_PREVIEW, defaults.showAppLinePreview)
            putIfChanged(Keys.SNAP_POINTS, defaults.snapPoints)
        }
    }


    suspend fun setAll(ctx: Context, backup: Map<String, Any?>) {
        ctx.uiDatastore.edit { prefs ->

            fun applyBoolean(key: Preferences.Key<Boolean>) {
                val raw = backup[key.name] ?: return

                val boolValue = when (raw) {
                    is Boolean -> raw
                    is String -> raw.toBooleanStrictOrNull()
                        ?: throw BackupTypeException(
                            key.name,
                            expected = "Boolean",
                            actual = "String",
                            value = raw
                        )
                    else -> throw BackupTypeException(
                        key.name,
                        expected = "Boolean",
                        actual = raw::class.simpleName,
                        value = raw
                    )
                }

                prefs[key] = boolValue
            }

            applyBoolean(Keys.RGB_LOADING)
            applyBoolean(Keys.RGB_LINE)
            applyBoolean(Keys.SHOW_LAUNCHING_APP_LABEL)
            applyBoolean(Keys.SHOW_LAUNCHING_APP_ICON)
            applyBoolean(Keys.SHOW_APP_LAUNCH_PREVIEW)
            applyBoolean(Keys.FULLSCREEN)
            applyBoolean(Keys.SHOW_CIRCLE_PREVIEW)
            applyBoolean(Keys.SHOW_LINE_PREVIEW)
            applyBoolean(Keys.SNAP_POINTS)
        }
    }
}
