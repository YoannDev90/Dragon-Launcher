package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.enumsui.ColorCustomisationMode
import org.elnix.dragonlauncher.enumsui.ColorPickerButtonAction
import org.elnix.dragonlauncher.enumsui.ColorPickerMode
import org.elnix.dragonlauncher.enumsui.DefaultThemes
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object ColorModesSettingsStore : MapSettingsStore() {

    override val name: String = "Color Modes"
    override val dataStoreName = DataStoreName.COLOR_MODE


    override val ALL: List<BaseSettingObject <*, *> >
        get() = listOf(
            colorPickerMode,
            colorCustomisationMode,
            defaultTheme
        )


    val colorPickerMode = Settings.enum(
        key = "colorPickerMode",
        dataStoreName = dataStoreName,
        default = ColorPickerMode.DEFAULTS,
        enumClass = ColorPickerMode::class.java,
    )

    val colorCustomisationMode = Settings.enum(
        key = "colorCustomisationMode",
        dataStoreName = dataStoreName,
        default = ColorCustomisationMode.DEFAULT,
        enumClass =  ColorCustomisationMode::class.java
    )

    val defaultTheme = Settings.enum(
        key = "defaultTheme",
        dataStoreName = dataStoreName,
        default = DefaultThemes.AMOLED,
        enumClass = DefaultThemes::class.java
    )

    val colorPickerButton = Settings.enum(
        key = "colorPickerButton",
        dataStoreName = dataStoreName,
        default = ColorPickerButtonAction.RANDOM,
        enumClass = ColorPickerButtonAction::class.java
    )
}
