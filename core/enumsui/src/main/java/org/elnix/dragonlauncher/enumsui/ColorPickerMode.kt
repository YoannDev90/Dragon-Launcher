package org.elnix.dragonlauncher.enumsui

import androidx.compose.runtime.Composable



enum class ColorPickerMode {
    DEFAULTS,
    SLIDERS,
    GRADIENT
}

@Composable
fun colorPickerText(mode: ColorPickerMode): String {
    return when(mode){
        ColorPickerMode.SLIDERS -> "Sliders"
        ColorPickerMode.GRADIENT -> "Gradient"
        ColorPickerMode.DEFAULTS -> "Default"
    }
}
