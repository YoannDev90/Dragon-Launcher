package org.elnix.dragonlauncher.base.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/* ───────────────────────── AMOLED ───────────────────────── */

val AmoledDragonColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF6650A4),
    onPrimary = Color(0xFFCECECE),

    secondary = Color(0xFF934EB2),
    onSecondary = Color(0xFFCECECE),

    tertiary = Color(0xFFCB84EC),
    onTertiary = Color(0xFFCECECE),

    background = Color.Black,
    onBackground = Color(0xFFCECECE),

    surface = Color(0xFF1A1328),
    onSurface = Color(0xFFCECECE),

    surfaceVariant = Color(0xFF241C36),
    onSurfaceVariant = Color(0xFFCECECE),

    error = Color(0xFFCF6679),
    onError = Color.Black,

    outline = Color(0xFFCECECE),
    outlineVariant = Color(0xFF3A3350),

    scrim = Color.Black,
    inverseSurface = Color(0xFFE6E6E6),
    inverseOnSurface = Color.Black,
    inversePrimary = Color(0xFF4E3A8F)
)

/* ───────────────────────── DARK ───────────────────────── */

val DarkDragonColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF9842B7),
    onPrimary = Color.Black,

    secondary = Color(0xFFA06CB7),
    onSecondary = Color.Black,

    tertiary = Color(0xFFB784C9),
    onTertiary = Color.Black,

    background = Color(0xFF1A1624),
    onBackground = Color(0xFFE6E6E6),

    surface = Color(0xFF261F36),
    onSurface = Color(0xFFE6E6E6),

    surfaceVariant = Color(0xFF322948),
    onSurfaceVariant = Color(0xFFD6CCE6),

    error = Color(0xFFCF6679),
    onError = Color.Black,

    outline = Color(0xCCFFFFFF),
    outlineVariant = Color(0xFF4A3F63),

    scrim = Color.Black,
    inverseSurface = Color(0xFFE6E6E6),
    inverseOnSurface = Color.Black,
    inversePrimary = Color(0xFF6E2A8A)
)

/* ───────────────────────── LIGHT ───────────────────────── */

val LightDragonColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFFA351E7),
    onPrimary = Color.Black,

    secondary = Color(0xFF772C93),
    onSecondary = Color.White,

    tertiary = Color(0xFF530D72),
    onTertiary = Color.White,

    background = Color.White,
    onBackground = Color.Black,

    surface = Color(0xFFF4EEFA),
    onSurface = Color.Black,

    surfaceVariant = Color(0xFFE6DBF0),
    onSurfaceVariant = Color(0xFF3A2B4A),

    error = Color(0xFFB3261E),
    onError = Color.White,

    outline = Color(0xCC000000),
    outlineVariant = Color(0xFFB8A9C8),

    scrim = Color.Black,
    inverseSurface = Color(0xFF2A2436),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFF7B2EB8)
)

/* ───────────────────────── Shared action colors ───────────────────────── */

val copyColor = Color(0xFFE19807)
val moveColor = Color(0xFF14E7EE)
val addRemoveCirclesColor = Color(0xFF00D950)
