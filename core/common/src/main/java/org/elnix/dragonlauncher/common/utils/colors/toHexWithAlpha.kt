package org.elnix.dragonlauncher.common.utils.colors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

// --- Utility: convert color → #RRGGBBAA ---
//fun toHexWithAlpha(color: Color): String {
//    val argb = color.toArgb()
//    val rgb = argb and 0xFFFFFF
//    val alpha = (color.alpha * 255).toInt().coerceIn(0, 255)
//    return "#${"%06X".format(rgb)}${"%02X".format(alpha)}"
//}

// --- Utility: convert color → #AARRGGBB ---
fun Color?.toHexWithAlpha(): String = "#%08X".format(this?.toArgb())
