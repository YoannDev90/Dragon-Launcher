package org.elnix.dragonlauncher.settings.stores

import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object UiSettingsStore : MapSettingsStore() {

    override val name: String = "Ui"
    override val dataStoreName: DataStoreName = DataStoreName.UI

    val rgbLoading = Settings.boolean(
        key = "rgbLoading",
        dataStoreName = dataStoreName,
        default = true
    )

    val rgbLine = Settings.boolean(
        key = "rgbLine",
        dataStoreName = dataStoreName,
        default = true
    )

    val showLaunchingAppLabel = Settings.boolean(
        key = "showLaunchingAppLabel",
        dataStoreName = dataStoreName,
        default = true,
        )

    val showLaunchingAppIcon = Settings.boolean(
        key = "showLaunchingAppIcon",
        dataStoreName = dataStoreName,
        default = true
    )

    val showAppLaunchingPreview = Settings.boolean(
        key = "showAppLaunchPreview",
        dataStoreName = dataStoreName,
        default = true
    )

    val fullScreen = Settings.boolean(
        key = "fullscreen",
        dataStoreName = dataStoreName,
        default = true
    )

    val showCirclePreview = Settings.boolean(
        key = "showCirclePreview",
        dataStoreName = dataStoreName,
        default = true
    )

    val showLinePreview = Settings.boolean(
        key = "showLinePreview",
        dataStoreName = dataStoreName,
        default = true
    )

    val showAnglePreview = Settings.boolean(
        key = "showAnglePreview",
        dataStoreName = dataStoreName,
        default = true
    )

    val snapPoints = Settings.boolean(
        key = "snapPoints",
        dataStoreName = dataStoreName,
        default = true
    )

    val autoSeparatePoints = Settings.boolean(
        key = "autoSeparatePoints",
        dataStoreName = dataStoreName,
        default = true
    )

    val showAppPreviewIconCenterStartPosition = Settings.boolean(
        key = "showAppPreviewIconCenterStartPosition",
        dataStoreName = dataStoreName,
        default = false
    )

    val linePreviewSnapToAction = Settings.boolean(
        key = "linePreviewSnapToAction",
        dataStoreName = dataStoreName,
        default = false
    )

    val showAllActionsOnCurrentCircle = Settings.boolean(
        key = "showAllActionsOnCurrentCircle",
        dataStoreName = dataStoreName,
        default = false
    )

    val selectedIconPack = Settings.string(
        key = "selected_icon_pack",
        dataStoreName = dataStoreName,
        default = ""
    )

    val iconPackTint = Settings.color(
        key = "icon_pack_tint",
        dataStoreName = dataStoreName,
        default = Color.Unspecified
    )

    val appLabelIconOverlayTopPadding = Settings.int(
        key = "appLabelIconOverlayTopPadding",
        dataStoreName = dataStoreName,
        default = 30
    )

    val appLabelOverlaySize = Settings.int(
        key = "appLabelOverlaySize",
        dataStoreName = dataStoreName,
        default = 18
    )

    val appIconOverlaySize = Settings.int(
        key = "appIconOverlaySize",
        dataStoreName = dataStoreName,
        default = 22
    )

    val wallpaperDimMainScreen = Settings.float(
        key = "wallpaperDimMainScreen",
        dataStoreName = dataStoreName,
        default = 0f
    )

    val wallpaperDimDrawerScreen = Settings.float(
        key = "wallpaperDimDrawerScreen",
        dataStoreName = dataStoreName,
        default = 0f
    )


    override val ALL: List<BaseSettingObject<*,*>> = listOf(
        rgbLoading,
        rgbLine,
        showLaunchingAppLabel,
        showLaunchingAppIcon,
        showAppLaunchingPreview,
        fullScreen,
        showCirclePreview,
        showLinePreview,
        showAnglePreview,
        snapPoints,
        autoSeparatePoints,
        showAppPreviewIconCenterStartPosition,
        linePreviewSnapToAction,
        showAllActionsOnCurrentCircle,
        selectedIconPack,
        iconPackTint,
        appLabelIconOverlayTopPadding,
        appLabelOverlaySize,
        appIconOverlaySize,
        wallpaperDimMainScreen,
        wallpaperDimDrawerScreen
    )
}
