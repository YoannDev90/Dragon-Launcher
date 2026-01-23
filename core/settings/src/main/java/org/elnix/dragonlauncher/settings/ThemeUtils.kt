package org.elnix.dragonlauncher.settings


import android.content.Context
import android.content.res.Configuration
import org.elnix.dragonlauncher.common.theme.AmoledDefault
import org.elnix.dragonlauncher.common.theme.DarkDefault
import org.elnix.dragonlauncher.common.theme.LightDefault
import org.elnix.dragonlauncher.enumsui.DefaultThemes
import org.elnix.dragonlauncher.settings.stores.applyThemeColors

suspend fun applyDefaultThemeColors(ctx: Context, theme: DefaultThemes) {
    applyThemeColors(ctx, getDefaultColorScheme(ctx, theme))
}

fun getDefaultColorScheme(ctx: Context, theme: DefaultThemes) = when (theme) {
    DefaultThemes.LIGHT -> LightDefault
    DefaultThemes.DARK -> DarkDefault
    DefaultThemes.AMOLED -> AmoledDefault
    DefaultThemes.SYSTEM -> {
        val nightModeFlags = ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            DarkDefault
        } else {
            LightDefault
        }
    }
}
