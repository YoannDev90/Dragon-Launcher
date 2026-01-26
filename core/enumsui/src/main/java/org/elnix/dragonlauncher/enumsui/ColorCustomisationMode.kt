package org.elnix.dragonlauncher.enumsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.common.R


enum class ColorCustomisationMode { DEFAULT, NORMAL, ALL }

@Composable
fun colorCustomizationModeName(mode: ColorCustomisationMode) = when (mode) {
    ColorCustomisationMode.DEFAULT -> stringResource(org.elnix.dragonlauncher.common.R.string.color_mode_default)
    ColorCustomisationMode.NORMAL -> stringResource(R.string.color_mode_normal)
    ColorCustomisationMode.ALL -> stringResource(R.string.color_mode_all)
}



enum class DefaultThemes { LIGHT, DARK, AMOLED, SYSTEM }

@Composable
fun defaultThemeName(mode: DefaultThemes) = when (mode) {
    DefaultThemes.LIGHT -> stringResource(R.string.light_theme)
    DefaultThemes.DARK -> stringResource(R.string.dark_theme)
    DefaultThemes.AMOLED -> stringResource(R.string.amoled_theme)
    DefaultThemes.SYSTEM -> stringResource(R.string.system_theme)
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
