package org.elnix.dragonlauncher.data.stores


import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.BackupTypeException
import org.elnix.dragonlauncher.data.BaseSettingsStore
import org.elnix.dragonlauncher.data.ColorCustomisationMode
import org.elnix.dragonlauncher.data.DefaultThemes
import org.elnix.dragonlauncher.data.colorDatastore
import org.elnix.dragonlauncher.data.getDefaultColorScheme
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setAngleLineColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setBackground
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setCircleColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setControlPanelColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setError
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setLaunchAppColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setLauncherSettingsColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setLockColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setNotificationShadeColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setOnBackground
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setOnError
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setOnPrimary
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setOnSecondary
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setOnSurface
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setOnTertiary
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setOpenAppDrawerColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setOpenFileColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setOpenUrlColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setOutline
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setPrimary
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setReloadColor
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setSecondary
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setSurface
import org.elnix.dragonlauncher.data.stores.ColorSettingsStore.setTertiary
import org.elnix.dragonlauncher.ui.theme.AmoledDefault
import org.elnix.dragonlauncher.ui.theme.ThemeColors
import org.elnix.dragonlauncher.utils.colors.randomColor

object ColorSettingsStore : BaseSettingsStore() {
    override val name: String = "Colors"

    private val PRIMARY_COLOR = intPreferencesKey("primary_color")
    private val ON_PRIMARY_COLOR = intPreferencesKey("on_primary_color")
    private val SECONDARY_COLOR = intPreferencesKey("secondary_color")
    private val ON_SECONDARY_COLOR = intPreferencesKey("on_secondary_color")
    private val TERTIARY_COLOR = intPreferencesKey("tertiary_color")
    private val ON_TERTIARY_COLOR = intPreferencesKey("on_tertiary_color")
    private val BACKGROUND_COLOR = intPreferencesKey("background_color")
    private val ON_BACKGROUND_COLOR = intPreferencesKey("on_background_color")
    private val SURFACE_COLOR = intPreferencesKey("surface_color")
    private val ON_SURFACE_COLOR = intPreferencesKey("on_surface_color")
    private val ERROR_COLOR = intPreferencesKey("error_color")
    private val ON_ERROR_COLOR = intPreferencesKey("on_error_color")
    private val OUTLINE_COLOR = intPreferencesKey("outline_color")
    private val ANGLE_LINE_COLOR = intPreferencesKey("delete_color")
    private val CIRCLE_COLOR = intPreferencesKey("circle_color")
    private val LAUNCH_APP_COLOR = intPreferencesKey("launch_app_color")
    private val OPEN_URL_COLOR = intPreferencesKey("open_url_color")
    private val NOTIFICATION_SHADE_COLOR = intPreferencesKey("notification_shade_color")
    private val CONTROL_PANEL_COLOR = intPreferencesKey("control_panel_color")
    private val OPEN_APP_DRAWER_COLOR = intPreferencesKey("open_app_drawer_color")
    private val LAUNCHER_SETTINGS_COLOR = intPreferencesKey("launcher_settings_color")
    private val LOCK_COLOR = intPreferencesKey("lock_color")
    private val OPEN_FILE_COLOR = intPreferencesKey("open_file_color")
    private val RELOAD_COLOR = intPreferencesKey("reload_color")
    private val OPEN_RECENT_APPS = intPreferencesKey("open_recent_apps")

    // ------------------------------------------
    //            NORMAL COLORS
    // ------------------------------------------

    fun getPrimary(ctx: Context) =
        ctx.colorDatastore.data.map { it[PRIMARY_COLOR]?.let { color -> Color(color) } }

    suspend fun setPrimary(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[PRIMARY_COLOR] = color.toArgb() }
    }

    fun getOnPrimary(ctx: Context) =
        ctx.colorDatastore.data.map { it[ON_PRIMARY_COLOR]?.let { color -> Color(color) } }

    suspend fun setOnPrimary(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[ON_PRIMARY_COLOR] = color.toArgb() }
    }

    fun getSecondary(ctx: Context) =
        ctx.colorDatastore.data.map { it[SECONDARY_COLOR]?.let { color -> Color(color) } }

    suspend fun setSecondary(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[SECONDARY_COLOR] = color.toArgb() }
    }

    fun getOnSecondary(ctx: Context) =
        ctx.colorDatastore.data.map { it[ON_SECONDARY_COLOR]?.let { color -> Color(color) } }

    suspend fun setOnSecondary(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[ON_SECONDARY_COLOR] = color.toArgb() }
    }

    fun getTertiary(ctx: Context) =
        ctx.colorDatastore.data.map { it[TERTIARY_COLOR]?.let { color -> Color(color) } }

    suspend fun setTertiary(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[TERTIARY_COLOR] = color.toArgb() }
    }

    fun getOnTertiary(ctx: Context) =
        ctx.colorDatastore.data.map { it[ON_TERTIARY_COLOR]?.let { color -> Color(color) } }

    suspend fun setOnTertiary(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[ON_TERTIARY_COLOR] = color.toArgb() }
    }

    fun getBackground(ctx: Context) =
        ctx.colorDatastore.data.map { it[BACKGROUND_COLOR]?.let { color -> Color(color) } }

    suspend fun setBackground(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[BACKGROUND_COLOR] = color.toArgb() }
    }

    fun getOnBackground(ctx: Context) =
        ctx.colorDatastore.data.map { it[ON_BACKGROUND_COLOR]?.let { color -> Color(color) } }

    suspend fun setOnBackground(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[ON_BACKGROUND_COLOR] = color.toArgb() }
    }

    fun getSurface(ctx: Context) =
        ctx.colorDatastore.data.map { it[SURFACE_COLOR]?.let { color -> Color(color) } }

    suspend fun setSurface(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[SURFACE_COLOR] = color.toArgb() }
    }

    fun getOnSurface(ctx: Context) =
        ctx.colorDatastore.data.map { it[ON_SURFACE_COLOR]?.let { color -> Color(color) } }

    suspend fun setOnSurface(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[ON_SURFACE_COLOR] = color.toArgb() }
    }

    fun getError(ctx: Context) =
        ctx.colorDatastore.data.map { it[ERROR_COLOR]?.let { color -> Color(color) } }

    suspend fun setError(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[ERROR_COLOR] = color.toArgb() }
    }

    fun getOnError(ctx: Context) =
        ctx.colorDatastore.data.map { it[ON_ERROR_COLOR]?.let { color -> Color(color) } }

    suspend fun setOnError(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[ON_ERROR_COLOR] = color.toArgb() }
    }

    fun getOutline(ctx: Context) =
        ctx.colorDatastore.data.map { it[OUTLINE_COLOR]?.let { color -> Color(color) } }

    suspend fun setOutline(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[OUTLINE_COLOR] = color.toArgb() }
    }

    // ------------------------------------------
    //            CUSTOM COLORS
    // ------------------------------------------

    fun getAngleLineColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[ANGLE_LINE_COLOR]?.let { color -> Color(color) } }

    suspend fun setAngleLineColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[ANGLE_LINE_COLOR] = color.toArgb() }
    }

    fun getCircleColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[CIRCLE_COLOR]?.let { color -> Color(color) } }

    suspend fun setCircleColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[CIRCLE_COLOR] = color.toArgb() }
    }

    fun getLaunchAppColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[LAUNCH_APP_COLOR]?.let { c -> Color(c) } }

    suspend fun setLaunchAppColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[LAUNCH_APP_COLOR] = color.toArgb() }
    }

    fun getOpenUrlColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[OPEN_URL_COLOR]?.let { c -> Color(c) } }

    suspend fun setOpenUrlColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[OPEN_URL_COLOR] = color.toArgb() }
    }

    fun getNotificationShadeColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[NOTIFICATION_SHADE_COLOR]?.let { c -> Color(c) } }

    suspend fun setNotificationShadeColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[NOTIFICATION_SHADE_COLOR] = color.toArgb() }
    }

    fun getControlPanelColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[CONTROL_PANEL_COLOR]?.let { c -> Color(c) } }

    suspend fun setControlPanelColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[CONTROL_PANEL_COLOR] = color.toArgb() }
    }

    fun getOpenAppDrawerColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[OPEN_APP_DRAWER_COLOR]?.let { c -> Color(c) } }

    suspend fun setOpenAppDrawerColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[OPEN_APP_DRAWER_COLOR] = color.toArgb() }
    }

    fun getLauncherSettingsColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[LAUNCHER_SETTINGS_COLOR]?.let { c -> Color(c) } }

    suspend fun setLauncherSettingsColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[LAUNCHER_SETTINGS_COLOR] = color.toArgb() }
    }

    fun getLockColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[LOCK_COLOR]?.let { c -> Color(c) } }

    suspend fun setLockColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[LOCK_COLOR] = color.toArgb() }
    }

    fun getOpenFileColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[OPEN_FILE_COLOR]?.let { c -> Color(c) } }

    suspend fun setOpenFileColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[OPEN_FILE_COLOR] = color.toArgb() }
    }

    fun getReloadColor(ctx: Context) =
        ctx.colorDatastore.data.map { it[RELOAD_COLOR]?.let { c -> Color(c) } }

    suspend fun setReloadColor(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[RELOAD_COLOR] = color.toArgb() }
    }

    fun getOpenRecentApps(ctx: Context) =
        ctx.colorDatastore.data.map { it[OPEN_RECENT_APPS]?.let { c -> Color(c) } }

    suspend fun setOpenRecentApps(ctx: Context, color: Color) {
        ctx.colorDatastore.edit { it[OPEN_RECENT_APPS] = color.toArgb() }
    }



    suspend fun resetColors(
        ctx: Context,
        selectedColorCustomisationMode: ColorCustomisationMode,
        selectedMode: DefaultThemes
    ) {

        val themeColors: ThemeColors = when (selectedColorCustomisationMode) {
            ColorCustomisationMode.DEFAULT -> getDefaultColorScheme(ctx, selectedMode)
            ColorCustomisationMode.NORMAL, ColorCustomisationMode.ALL -> AmoledDefault
        }

        applyThemeColors(ctx, themeColors)
    }


    suspend fun setAllRandomColors(ctx: Context) {
        val random = { randomColor() }

        setPrimary(ctx, random())
        setOnPrimary(ctx, random())
        setSecondary(ctx, random())
        setOnSecondary(ctx, random())
        setTertiary(ctx, random())
        setOnTertiary(ctx, random())
        setBackground(ctx, random())
        setOnBackground(ctx, random())
        setSurface(ctx, random())
        setOnSurface(ctx, random())
        setError(ctx, random())
        setOnError(ctx, random())
        setOutline(ctx, random())
        setAngleLineColor(ctx, random())
        setCircleColor(ctx, random())

        setLaunchAppColor(ctx, random())
        setOpenUrlColor(ctx, random())
        setNotificationShadeColor(ctx, random())
        setControlPanelColor(ctx, random())
        setOpenAppDrawerColor(ctx, random())
        setLauncherSettingsColor(ctx, random())
        setLockColor(ctx, random())
        setOpenFileColor(ctx, random())
        setReloadColor(ctx, random())
        setOpenRecentApps(ctx, random())
    }

    override suspend fun resetAll(ctx: Context) {
        ctx.colorDatastore.edit { prefs ->
            prefs.remove(PRIMARY_COLOR)
            prefs.remove(ON_PRIMARY_COLOR)
            prefs.remove(SECONDARY_COLOR)
            prefs.remove(ON_SECONDARY_COLOR)
            prefs.remove(TERTIARY_COLOR)
            prefs.remove(ON_TERTIARY_COLOR)
            prefs.remove(BACKGROUND_COLOR)
            prefs.remove(ON_BACKGROUND_COLOR)
            prefs.remove(SURFACE_COLOR)
            prefs.remove(ON_SURFACE_COLOR)
            prefs.remove(ERROR_COLOR)
            prefs.remove(ON_ERROR_COLOR)
            prefs.remove(OUTLINE_COLOR)
            prefs.remove(ANGLE_LINE_COLOR)
            prefs.remove(CIRCLE_COLOR)

            prefs.remove(LAUNCH_APP_COLOR)
            prefs.remove(OPEN_URL_COLOR)
            prefs.remove(NOTIFICATION_SHADE_COLOR)
            prefs.remove(CONTROL_PANEL_COLOR)
            prefs.remove(OPEN_APP_DRAWER_COLOR)
            prefs.remove(LAUNCHER_SETTINGS_COLOR)
            prefs.remove(LOCK_COLOR)
            prefs.remove(OPEN_FILE_COLOR)
            prefs.remove(RELOAD_COLOR)
            prefs.remove(OPEN_RECENT_APPS)
        }
    }

    suspend fun getAll(ctx: Context): Map<String, Int> {
        val prefs = ctx.colorDatastore.data.first()
        val colorMode = ColorModesSettingsStore.getColorCustomisationMode(ctx).first()
        val defaultTheme = ColorModesSettingsStore.getDefaultTheme(ctx).first()

        val default = if (colorMode == ColorCustomisationMode.DEFAULT) getDefaultColorScheme(ctx, defaultTheme)
                     else AmoledDefault
        return buildMap {
            fun putIfNonDefault(key: String, value: Int?, default: Color) {
                if (value != null && value != default.toArgb()) {
                    put(key, value)
                }
            }

            putIfNonDefault(PRIMARY_COLOR.name,        prefs[PRIMARY_COLOR],        default.Primary)
            putIfNonDefault(ON_PRIMARY_COLOR.name,     prefs[ON_PRIMARY_COLOR],     default.OnPrimary)
            putIfNonDefault(SECONDARY_COLOR.name,      prefs[SECONDARY_COLOR],      default.Secondary)
            putIfNonDefault(ON_SECONDARY_COLOR.name,   prefs[ON_SECONDARY_COLOR],   default.OnSecondary)
            putIfNonDefault(TERTIARY_COLOR.name,       prefs[TERTIARY_COLOR],       default.Tertiary)
            putIfNonDefault(ON_TERTIARY_COLOR.name,    prefs[ON_TERTIARY_COLOR],    default.OnTertiary)
            putIfNonDefault(BACKGROUND_COLOR.name,     prefs[BACKGROUND_COLOR],     default.Background)
            putIfNonDefault(ON_BACKGROUND_COLOR.name,  prefs[ON_BACKGROUND_COLOR],  default.OnBackground)
            putIfNonDefault(SURFACE_COLOR.name,        prefs[SURFACE_COLOR],        default.Surface)
            putIfNonDefault(ON_SURFACE_COLOR.name,     prefs[ON_SURFACE_COLOR],     default.OnSurface)
            putIfNonDefault(ERROR_COLOR.name,          prefs[ERROR_COLOR],          default.Error)
            putIfNonDefault(ON_ERROR_COLOR.name,       prefs[ON_ERROR_COLOR],       default.OnError)
            putIfNonDefault(OUTLINE_COLOR.name,        prefs[OUTLINE_COLOR],        default.Outline)
            putIfNonDefault(ANGLE_LINE_COLOR.name,     prefs[ANGLE_LINE_COLOR],     default.AngleLineColor)
            putIfNonDefault(CIRCLE_COLOR.name,         prefs[CIRCLE_COLOR],         default.CircleColor)

            putIfNonDefault(LAUNCH_APP_COLOR.name,        prefs[LAUNCH_APP_COLOR],        default.LaunchAppColor)
            putIfNonDefault(OPEN_URL_COLOR.name,          prefs[OPEN_URL_COLOR],          default.OpenUrlColor)
            putIfNonDefault(NOTIFICATION_SHADE_COLOR.name,prefs[NOTIFICATION_SHADE_COLOR],default.NotificationShadeColor)
            putIfNonDefault(CONTROL_PANEL_COLOR.name,     prefs[CONTROL_PANEL_COLOR],     default.ControlPanelColor)
            putIfNonDefault(OPEN_APP_DRAWER_COLOR.name,   prefs[OPEN_APP_DRAWER_COLOR],   default.OpenAppDrawerColor)
            putIfNonDefault(LAUNCHER_SETTINGS_COLOR.name, prefs[LAUNCHER_SETTINGS_COLOR], default.LauncherSettingsColor)
            putIfNonDefault(LOCK_COLOR.name,              prefs[LOCK_COLOR],              default.LockColor)
            putIfNonDefault(OPEN_FILE_COLOR.name,         prefs[OPEN_FILE_COLOR],         default.OpenFileColor)
            putIfNonDefault(RELOAD_COLOR.name,            prefs[RELOAD_COLOR],            default.ReloadColor)
            putIfNonDefault(OPEN_RECENT_APPS.name,        prefs[OPEN_RECENT_APPS],        default.OpenRecentAppsColor)

        }
    }

    suspend fun setAll(ctx: Context, data: Map<String, Any?>) {
        ctx.colorDatastore.edit { prefs ->

            fun setInt(key: Preferences.Key<Int>, raw: Any?) {
                if (raw == null) return
                val intVal = raw as? Int
                    ?: throw BackupTypeException(key.name, "Int", raw::class.simpleName,raw)
                prefs[key] = intVal
            }

            data.forEach { (name, value) ->
                when (name) {
                    PRIMARY_COLOR.name        -> setInt(PRIMARY_COLOR, value)
                    ON_PRIMARY_COLOR.name     -> setInt(ON_PRIMARY_COLOR, value)
                    SECONDARY_COLOR.name      -> setInt(SECONDARY_COLOR, value)
                    ON_SECONDARY_COLOR.name   -> setInt(ON_SECONDARY_COLOR, value)
                    TERTIARY_COLOR.name       -> setInt(TERTIARY_COLOR, value)
                    ON_TERTIARY_COLOR.name    -> setInt(ON_TERTIARY_COLOR, value)
                    BACKGROUND_COLOR.name     -> setInt(BACKGROUND_COLOR, value)
                    ON_BACKGROUND_COLOR.name  -> setInt(ON_BACKGROUND_COLOR, value)
                    SURFACE_COLOR.name        -> setInt(SURFACE_COLOR, value)
                    ON_SURFACE_COLOR.name     -> setInt(ON_SURFACE_COLOR, value)
                    ERROR_COLOR.name          -> setInt(ERROR_COLOR, value)
                    ON_ERROR_COLOR.name       -> setInt(ON_ERROR_COLOR, value)
                    OUTLINE_COLOR.name        -> setInt(OUTLINE_COLOR, value)
                    ANGLE_LINE_COLOR.name     -> setInt(ANGLE_LINE_COLOR, value)
                    CIRCLE_COLOR.name         -> setInt(CIRCLE_COLOR, value)

                    LAUNCH_APP_COLOR.name         -> setInt(LAUNCH_APP_COLOR, value)
                    OPEN_URL_COLOR.name           -> setInt(OPEN_URL_COLOR, value)
                    NOTIFICATION_SHADE_COLOR.name -> setInt(NOTIFICATION_SHADE_COLOR, value)
                    CONTROL_PANEL_COLOR.name      -> setInt(CONTROL_PANEL_COLOR, value)
                    OPEN_APP_DRAWER_COLOR.name    -> setInt(OPEN_APP_DRAWER_COLOR, value)
                    LAUNCHER_SETTINGS_COLOR.name  -> setInt(LAUNCHER_SETTINGS_COLOR, value)
                    LOCK_COLOR.name               -> setInt(LOCK_COLOR, value)
                    OPEN_FILE_COLOR.name          -> setInt(OPEN_FILE_COLOR, value)
                    RELOAD_COLOR.name             -> setInt(RELOAD_COLOR, value)
                    OPEN_RECENT_APPS.name         -> setInt(OPEN_RECENT_APPS, value)
                }
            }
        }
    }
}


private suspend fun applyThemeColors(ctx: Context, colors: ThemeColors) {
    setPrimary(ctx, colors.Primary)
    setOnPrimary(ctx, colors.OnPrimary)
    setSecondary(ctx, colors.Secondary)
    setOnSecondary(ctx, colors.OnSecondary)
    setTertiary(ctx, colors.Tertiary)
    setOnTertiary(ctx, colors.OnTertiary)
    setBackground(ctx, colors.Background)
    setOnBackground(ctx, colors.OnBackground)
    setSurface(ctx, colors.Surface)
    setOnSurface(ctx, colors.OnSurface)
    setError(ctx, colors.Error)
    setOnError(ctx, colors.OnError)
    setOutline(ctx, colors.Outline)
    setAngleLineColor(ctx, colors.AngleLineColor)
    setCircleColor(ctx, colors.CircleColor)

    setLaunchAppColor(ctx, colors.LaunchAppColor)
    setOpenUrlColor(ctx, colors.OpenUrlColor)
    setNotificationShadeColor(ctx, colors.NotificationShadeColor)
    setControlPanelColor(ctx, colors.ControlPanelColor)
    setOpenAppDrawerColor(ctx, colors.OpenAppDrawerColor)
    setLauncherSettingsColor(ctx, colors.LauncherSettingsColor)
    setLockColor(ctx, colors.LockColor)
    setOpenFileColor(ctx, colors.OpenFileColor)
    setReloadColor(ctx, colors.ReloadColor)
}
