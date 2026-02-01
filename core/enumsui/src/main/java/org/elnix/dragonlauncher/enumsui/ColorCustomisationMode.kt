package org.elnix.dragonlauncher.enumsui

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.enumsui.CustomColorModeEditing.NORMAL
import org.elnix.dragonlauncher.enumsui.DefaultThemes.AMOLED
import org.elnix.dragonlauncher.enumsui.DefaultThemes.CUSTOM
import org.elnix.dragonlauncher.enumsui.DefaultThemes.DARK
import org.elnix.dragonlauncher.enumsui.DefaultThemes.LIGHT
import org.elnix.dragonlauncher.enumsui.DefaultThemes.SYSTEM

enum class DefaultThemes { LIGHT, DARK, AMOLED, SYSTEM, CUSTOM }

@Composable
fun defaultThemeName(mode: DefaultThemes) = when (mode) {
    LIGHT -> stringResource(R.string.light_theme)
    DARK -> stringResource(R.string.dark_theme)
    AMOLED -> stringResource(R.string.amoled_theme)
    SYSTEM -> stringResource(R.string.system_theme)
    CUSTOM -> stringResource(R.string.custom_theme)
}


enum class ColorPickerButtonAction { RANDOM, RESET, COPY, PASTE, NONE }

@Composable
fun colorPickerButtonIcon(mode: ColorPickerButtonAction) = when (mode) {
    ColorPickerButtonAction.RANDOM -> Icons.Default.Shuffle
    ColorPickerButtonAction.RESET ->  Icons.Default.Restore
    ColorPickerButtonAction.COPY -> Icons.Default.ContentCopy
    ColorPickerButtonAction.PASTE ->  Icons.Default.ContentPaste
    ColorPickerButtonAction.NONE ->  Icons.Default.FilterNone
}

enum class CustomColorModeEditing { NORMAL, CUSTOM }

fun customColorModeEditingName(ctx: Context, mode: CustomColorModeEditing) = when (mode) {
    NORMAL -> ctx.getString(R.string.normal_colors)
    CustomColorModeEditing.CUSTOM -> ctx.getString(R.string.custom_colors)
}
@Composable
fun customColorModeEditingIcon(mode: CustomColorModeEditing) = when (mode) {
    NORMAL -> Icons.Default.Settings
    CustomColorModeEditing.CUSTOM -> Icons.Default.SettingsSuggest
}
