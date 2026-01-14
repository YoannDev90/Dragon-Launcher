package org.elnix.dragonlauncher.common.theme

import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.utils.colors.blendWith

object AmoledDefault : ThemeColors {
    override val Primary = Color(0xFF6650a4)
    override val OnPrimary = Color(0xFFCECECE)
    override val Secondary = Color(0xFF934EB2)
    override val OnSecondary = OnPrimary
    override val Tertiary = Color(0xFFCB84EC)
    override val OnTertiary = OnPrimary
    override val Background = Color.Black
    override val OnBackground = OnPrimary
    override val Surface = Primary.blendWith(Background, 0.7f)
    override val OnSurface = OnBackground
    override val Error = Color.Red
    override val OnError = OnPrimary
    override val Outline = OnPrimary
    override val AngleLineColor = Color.Red
    override val CircleColor = Color(0xFF960000)
    override val LaunchAppColor = Color(0xFF55AAFF)
    override val OpenUrlColor = Color(0xFF66DD77)
    override val NotificationShadeColor = Color(0xFFFFBB44)
    override val ControlPanelColor = Color(0xFFFF6688)
    override val OpenAppDrawerColor = Color(0xFFDD55FF)
    override val LauncherSettingsColor = Color.Red
    override val LockColor = Color(0xFF555555)
    override val OpenFileColor = Color(0xFF00FFF7)
    override val ReloadColor = Color(0xFF886300)
    override val OpenRecentAppsColor = Color(0xFF880081)
    override val OpenCircleNestColor: Color
        get() = Color(0xFF1BEE14)
    override val GoParentNestColor: Color
        get() = Color(0xFF1BEE14)
}

object DarkDefault : ThemeColors {
    override val Primary = Color(0xFF9842B7)
    override val OnPrimary = Color.Black

    override val Secondary = Color(0xFFA06CB7)
    override val OnSecondary = OnPrimary

    override val Tertiary = Color(0xFFB784C9)
    override val OnTertiary = OnPrimary

    override val Background = Color(0xFF1A1624)
    override val OnBackground = Color(0xFFE6E6E6)

    override val Surface = Primary.blendWith(Background, 0.75f)
    override val OnSurface = OnBackground

    override val Error = Color.Red
    override val OnError = OnPrimary

    override val Outline = Color.White.copy(alpha = 0.8f)

    override val AngleLineColor = Color.Red
    override val CircleColor = Color(0xFF960000)
    override val LaunchAppColor = Color(0xFF55AAFF)
    override val OpenUrlColor = Color(0xFF66DD77)
    override val NotificationShadeColor = Color(0xFFFFBB44)
    override val ControlPanelColor = Color(0xFFFF6688)
    override val OpenAppDrawerColor = Color(0xFFDD55FF)
    override val LauncherSettingsColor = Color.Red
    override val LockColor = Color(0xFF555555)
    override val OpenFileColor = Color(0xFF00FFF7)
    override val ReloadColor = Color(0xFF886300)
    override val OpenRecentAppsColor = Color(0xFF880081)
    override val OpenCircleNestColor: Color
        get() = Color(0xFF1BEE14)
    override val GoParentNestColor: Color
        get() = Color(0xFF1BEE14)
}


object LightDefault : ThemeColors {
    override val Primary = Color(0xFFA351E7)
    override val OnPrimary = Color.Black
    override val Secondary = Color(0xFF772C93)
    override val OnSecondary = OnPrimary
    override val Tertiary = Color(0xFF530D72)
    override val OnTertiary = OnPrimary
    override val Background = Color.White
    override val OnBackground = OnPrimary
    override val Surface = Primary.blendWith(Background, 0.7f)
    override val OnSurface = OnPrimary
    override val Error = Color.Red
    override val OnError = OnPrimary
    override val Outline = Color.Black.copy(alpha = 0.8f)
    override val AngleLineColor = Color.Red
    override val CircleColor = Color(0xFF960000)
    override val LaunchAppColor = Color(0xFF55AAFF)
    override val OpenUrlColor = Color(0xFF66DD77)
    override val NotificationShadeColor = Color(0xFFFFBB44)
    override val ControlPanelColor = Color(0xFFFF6688)
    override val OpenAppDrawerColor = Color(0xFFDD55FF)
    override val LauncherSettingsColor = Color.Red
    override val LockColor = Color(0xFF555555)
    override val OpenFileColor = Color(0xFF00FFF7)
    override val ReloadColor = Color(0xFF886300)
    override val OpenRecentAppsColor = Color(0xFF880081)
    override val OpenCircleNestColor: Color
        get() = Color(0xFF1BEE14)
    override val GoParentNestColor: Color
        get() = Color(0xFF1BEE14)

}


val copyColor = Color(0xFFE19807)
val moveColor = Color(0xFF14E7EE)
val addRemoveCirclesColor = Color(0xFF00D950)
