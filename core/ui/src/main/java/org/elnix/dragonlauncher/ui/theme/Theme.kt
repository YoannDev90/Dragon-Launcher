package org.elnix.dragonlauncher.ui.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.elnix.dragonlauncher.base.theme.AmoledDragonColorScheme
import org.elnix.dragonlauncher.base.theme.DarkDragonColorScheme
import org.elnix.dragonlauncher.base.theme.LightDragonColorScheme
import org.elnix.dragonlauncher.common.theme.AmoledDefault
import org.elnix.dragonlauncher.enumsui.DefaultThemes
import org.elnix.dragonlauncher.enumsui.DefaultThemes.AMOLED
import org.elnix.dragonlauncher.enumsui.DefaultThemes.CUSTOM
import org.elnix.dragonlauncher.enumsui.DefaultThemes.DARK
import org.elnix.dragonlauncher.enumsui.DefaultThemes.LIGHT
import org.elnix.dragonlauncher.enumsui.DefaultThemes.SYSTEM


@Composable
fun getDefaultColorScheme(
    defaultTheme: DefaultThemes,
    dynamicColor: Boolean
): ColorScheme? =
    when (defaultTheme) {
        LIGHT -> LightDragonColorScheme
        DARK -> DarkDragonColorScheme
        AMOLED -> AmoledDragonColorScheme
        SYSTEM -> {
            val darkTheme = isSystemInDarkTheme()
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                }

                darkTheme -> DarkDragonColorScheme
                else -> LightDragonColorScheme
            }
        }

        CUSTOM -> null
    }

@Composable
fun DragonLauncherTheme(
    defaultTheme: DefaultThemes,
    dynamicColor: Boolean,
    customColorScheme: ColorScheme?,

    customAngleLineColor: Color? = null,
    customCircleColor: Color? = null,
    customLaunchAppColor: Color? = null,
    customOpenUrlColor: Color? = null,
    customNotificationShadeColor: Color? = null,
    customControlPanelColor: Color? = null,
    customOpenAppDrawerColor: Color? = null,
    customLauncherSettingsColor: Color? = null,
    customLockColor: Color? = null,
    customOpenFileColor: Color? = null,
    customReloadAppsColor: Color? = null,
    customOpenRecentAppsColor: Color? = null,
    customOpenCircleNest: Color? = null,
    customGoParentNest: Color? = null,

    content: @Composable () -> Unit
) {

    val angleLine = customAngleLineColor ?: AmoledDefault.AngleLineColor
    val circle = customCircleColor ?: AmoledDefault.CircleColor

    val launchApp = customLaunchAppColor ?: AmoledDefault.LaunchAppColor
    val openUrl = customOpenUrlColor ?: AmoledDefault.OpenUrlColor
    val notificationShade = customNotificationShadeColor ?: AmoledDefault.NotificationShadeColor
    val controlPanel = customControlPanelColor ?: AmoledDefault.ControlPanelColor
    val openAppDrawer = customOpenAppDrawerColor ?: AmoledDefault.OpenAppDrawerColor
    val launcherSettings = customLauncherSettingsColor ?: AmoledDefault.LauncherSettingsColor
    val lock = customLockColor ?: AmoledDefault.LockColor
    val openFile = customOpenFileColor ?: AmoledDefault.OpenFileColor
    val reloadApps = customReloadAppsColor ?: AmoledDefault.ReloadColor
    val openRecentApps = customOpenRecentAppsColor ?: AmoledDefault.OpenRecentAppsColor
    val openCircleNest = customOpenCircleNest ?: AmoledDefault.OpenCircleNestColor
    val goParentNest = customGoParentNest ?: AmoledDefault.GoParentNestColor

    val extraColors = ExtraColors(
        angleLine,
        circle,
        launchApp,
        openUrl,
        notificationShade,
        controlPanel,
        openAppDrawer,
        launcherSettings,
        lock,
        openFile,
        reloadApps,
        openRecentApps,
        openCircleNest,
        goParentNest
    )

    val colorScheme = getDefaultColorScheme(defaultTheme, dynamicColor) ?: customColorScheme ?: AmoledDragonColorScheme

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
