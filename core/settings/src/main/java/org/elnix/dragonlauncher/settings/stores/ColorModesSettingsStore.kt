package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.MapSettingsStore
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.TypedSettingObject

object ColorModesSettingsStore : MapSettingsStore() {

    override val name: String = "Color Modes"
    override val dataStoreName = DataStoreName.COLOR_MODE


    val COLOR_PICKER_MODE = TypedSettingObject(
        key = "colorPickerMode",
        dataStoreName = dataStoreName,
        default = "",
        type = SettingType.String
    )

    val COLOR_CUSTOMISATION_MODE = TypedSettingObject(
        key = "colorCustomisationMode",
        dataStoreName = dataStoreName,
        default = "",
        type = SettingType.String
    )

    val DEFAULT_THEME = TypedSettingObject(
        key = "defaultTheme",
        dataStoreName = dataStoreName,
        default = "",
        type = SettingType.String
    )


    override val ALL = listOf(
        COLOR_PICKER_MODE,
        COLOR_CUSTOMISATION_MODE,
        DEFAULT_THEME

    )


//    // -------------------------------------------------------------------------
//    // Accessors + Mutators
//    // -------------------------------------------------------------------------
//    fun getColorPickerMode(ctx: Context): Flow<ColorPickerMode> =
//        ctx.colorModeDatastore.data.map { prefs ->
//            prefs[COLOR_PICKER_MODE]?.let { ColorPickerMode.valueOf(it) }
//                ?: defaults.colorPickerMode
//        }
//
//    suspend fun setColorPickerMode(ctx: Context, mode: ColorPickerMode) {
//        ctx.colorModeDatastore.edit { it[COLOR_PICKER_MODE] = mode.name }
//    }
//
//    fun getColorCustomisationMode(ctx: Context): Flow<ColorCustomisationMode> =
//        ctx.colorModeDatastore.data.map { prefs ->
//            prefs[COLOR_CUSTOMISATION_MODE]?.let { ColorCustomisationMode.valueOf(it) }
//                ?: defaults.colorCustomisationMode
//        }
//
//    suspend fun setColorCustomisationMode(ctx: Context, mode: ColorCustomisationMode) {
//        ctx.colorModeDatastore.edit { it[COLOR_CUSTOMISATION_MODE] = mode.name }
//    }
//
//    fun getDefaultTheme(ctx: Context): Flow<DefaultThemes> =
//        ctx.colorModeDatastore.data.map { prefs ->
//            prefs[DEFAULT_THEME]?.let { DefaultThemes.valueOf(it) }
//                ?: defaults.defaultTheme
//        }
//
//    suspend fun setDefaultTheme(ctx: Context, mode: DefaultThemes) {
//        ctx.colorModeDatastore.edit { it[DEFAULT_THEME] = mode.name }
//    }
//
//    // -------------------------------------------------------------------------
//    // Reset
//    // -------------------------------------------------------------------------
//    override suspend fun resetAll(ctx: Context) {
//        ctx.colorModeDatastore.edit { prefs ->
//            prefs.remove(COLOR_PICKER_MODE)
//            prefs.remove(COLOR_CUSTOMISATION_MODE)
//            prefs.remove(DEFAULT_THEME)
//        }
//    }
//
//    // -------------------------------------------------------------------------
//    // Backup export
//    // -------------------------------------------------------------------------
//    override suspend fun getAll(ctx: Context): Map<String, Any> {
//        val prefs = ctx.colorModeDatastore.data.first()
//
//        return buildMap {
//
//            putIfNonDefault(
//                COLOR_PICKER_MODE,
//                prefs[COLOR_PICKER_MODE],
//                defaults.colorPickerMode.name
//            )
//
//            putIfNonDefault(
//                COLOR_CUSTOMISATION_MODE,
//                prefs[COLOR_CUSTOMISATION_MODE],
//                defaults.colorCustomisationMode.name
//            )
//
//            putIfNonDefault(
//                DEFAULT_THEME,
//                prefs[DEFAULT_THEME],
//                defaults.defaultTheme.name
//            )
//        }
//    }
//
//
//    override suspend fun setAll(ctx: Context, value: Map<String, Any?>) {
//
//        val backup = ColorModesSettingsBackup(
//            colorPickerMode = getEnumStrict(
//                value,
//                COLOR_PICKER_MODE,
//                defaults.colorPickerMode
//            ),
//            colorCustomisationMode = getEnumStrict(
//                value,
//                COLOR_CUSTOMISATION_MODE,
//                defaults.colorCustomisationMode
//            ),
//            defaultTheme = getEnumStrict(
//                value,
//                DEFAULT_THEME,
//                defaults.defaultTheme
//            )
//        )
//
//        ctx.colorModeDatastore.edit { prefs ->
//            prefs[COLOR_PICKER_MODE] = backup.colorPickerMode.name
//            prefs[COLOR_CUSTOMISATION_MODE] = backup.colorCustomisationMode.name
//            prefs[DEFAULT_THEME] = backup.defaultTheme.name
//        }
//
//        // Apply colorscheme
//        applyDefaultThemeColors(ctx, backup.defaultTheme)
//    }
}
