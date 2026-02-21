package org.elnix.dragonlauncher.ui.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.elnix.dragonlauncher.base.theme.AmoledDragonColorScheme
import org.elnix.dragonlauncher.base.theme.DarkDragonColorScheme
import org.elnix.dragonlauncher.base.theme.LightDragonColorScheme
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.enumsui.DefaultThemes
import org.elnix.dragonlauncher.enumsui.DefaultThemes.AMOLED
import org.elnix.dragonlauncher.enumsui.DefaultThemes.CUSTOM
import org.elnix.dragonlauncher.enumsui.DefaultThemes.DARK
import org.elnix.dragonlauncher.enumsui.DefaultThemes.LIGHT
import org.elnix.dragonlauncher.enumsui.DefaultThemes.SYSTEM
import org.elnix.dragonlauncher.settings.stores.ColorModesSettingsStore
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.remembers.rememberCustomColorScheme
import org.elnix.dragonlauncher.ui.remembers.rememberExtraColors


@Composable
fun getSystemColorScheme(
    defaultTheme: DefaultThemes,
    dynamicColor: Boolean
): ColorScheme {
    val darkTheme = isSystemInDarkTheme()
    return when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> {
            if (defaultTheme == AMOLED) AmoledDragonColorScheme
            else DarkDragonColorScheme
        }

        else -> LightDragonColorScheme
    }
}

@Composable
fun getCustomColorScheme(dynamicColor: Boolean): ColorScheme =
    rememberCustomColorScheme(getSystemColorScheme(SYSTEM, dynamicColor))


@Composable
private fun getDefaultColorScheme(
    defaultTheme: DefaultThemes,
    dynamicColor: Boolean
): ColorScheme =
    when (defaultTheme) {
        LIGHT -> LightDragonColorScheme
        DARK -> DarkDragonColorScheme
        AMOLED -> AmoledDragonColorScheme
        SYSTEM -> getSystemColorScheme(defaultTheme, dynamicColor)
        CUSTOM -> getCustomColorScheme(dynamicColor)
    }

@Composable
fun DragonLauncherTheme(
    content: @Composable () -> Unit
) {
    val dynamicColor by ColorModesSettingsStore.dynamicColor.asState()
    val defaultTheme by ColorModesSettingsStore.defaultTheme.asState()

    val colorScheme = getDefaultColorScheme(defaultTheme, dynamicColor)
    val extraColors = rememberExtraColors()

    CompositionLocalProvider(
        LocalExtraColors provides extraColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
