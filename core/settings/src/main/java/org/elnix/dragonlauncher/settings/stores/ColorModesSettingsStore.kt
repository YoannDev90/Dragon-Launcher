package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.enumsui.ColorCustomisationMode
import org.elnix.dragonlauncher.enumsui.ColorPickerMode
import org.elnix.dragonlauncher.enumsui.DefaultThemes
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object ColorModesSettingsStore : MapSettingsStore() {

    override val name: String = "Color Modes"
    override val dataStoreName = DataStoreName.COLOR_MODE


    override val ALL: List<SettingObject<*>>
        get() = listOf(
            colorPickerMode,
            colorCustomisationMode,
            defaultTheme
        )


    val colorPickerMode = SettingObject(
        key = "colorPickerMode",
        dataStoreName = dataStoreName,
        default = ColorPickerMode.DEFAULTS,
        type = SettingType.Enum(ColorPickerMode::class.java)
    )

    val colorCustomisationMode = SettingObject(
        key = "colorCustomisationMode",
        dataStoreName = dataStoreName,
        default = ColorCustomisationMode.DEFAULT,
        type = SettingType.Enum(ColorCustomisationMode::class.java)
    )

    val defaultTheme = SettingObject(
        key = "defaultTheme",
        dataStoreName = dataStoreName,
        default = DefaultThemes.AMOLED,
        type = SettingType.Enum(DefaultThemes::class.java)
    )
}
