package org.elnix.dragonlauncher.ui.theme


import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.theme.AmoledDefault


fun generateColorScheme(
    primary: Color,
    onPrimary: Color,
    secondary: Color,
    onSecondary: Color,
    tertiary: Color,
    onTertiary: Color,
    background: Color,
    onBackground: Color,
    surface: Color,
    onSurface: Color,
    error: Color,
    onError: Color,
    outline: Color
): ColorScheme {

    val base = darkColorScheme()

    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        tertiary = tertiary,
        onTertiary = onTertiary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        error = error,
        onError = onError,
        outline = outline
    )
}

@Composable
fun DragonLauncherTheme(
    customPrimary: Color? = null,
    customOnPrimary: Color? = null,
    customSecondary: Color? = null,
    customOnSecondary: Color? = null,
    customTertiary: Color? = null,
    customOnTertiary: Color? = null,
    customBackground: Color? = null,
    customOnBackground: Color? = null,
    customSurface: Color? = null,
    customOnSurface: Color? = null,
    customError: Color? = null,
    customOnError: Color? = null,
    customOutline: Color? = null,
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
    val primary = customPrimary ?: AmoledDefault.Primary
    val onPrimary = customOnPrimary ?: AmoledDefault.OnPrimary

    val secondary = customSecondary ?: AmoledDefault.Secondary
    val onSecondary = customOnSecondary ?: AmoledDefault.OnSecondary

    val tertiary = customTertiary ?: AmoledDefault.Tertiary
    val onTertiary = customOnTertiary ?: AmoledDefault.OnTertiary

    val background = customBackground ?: AmoledDefault.Background
    val onBackground = customOnBackground ?: AmoledDefault.OnBackground

    val surface = customSurface ?: AmoledDefault.Surface
    val onSurface = customOnSurface ?: AmoledDefault.OnSurface

    val error = customError ?: AmoledDefault.Error
    val onError = customOnError ?: AmoledDefault.OnError

    val outline = customOutline ?: AmoledDefault.Outline

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

    val colorScheme = generateColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        tertiary = tertiary,
        onTertiary = onTertiary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        error = error,
        onError = onError,
        outline = outline,
    )

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
