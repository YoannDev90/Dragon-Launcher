package org.elnix.dragonlauncher.settings.stores

import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object UiSettingsStore : MapSettingsStore() {

    override val name: String = "Ui"
    override val dataStoreName: DataStoreName = DataStoreName.UI

    val rgbLoading = SettingObject(
        key = "rgbLoading",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val rgbLine = SettingObject(
        key = "rgbLine",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showLaunchingAppLabel = SettingObject(
        key = "showLaunchingAppLabel",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showLaunchingAppIcon = SettingObject(
        key = "showLaunchingAppIcon",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showAppLaunchingPreview = SettingObject(
        key = "showAppLaunchPreview",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val fullScreen = SettingObject(
        key = "fullscreen",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showCirclePreview = SettingObject(
        key = "showCirclePreview",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showLinePreview = SettingObject(
        key = "showLinePreview",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showAnglePreview = SettingObject(
        key = "showAnglePreview",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val snapPoints = SettingObject(
        key = "snapPoints",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val autoSeparatePoints = SettingObject(
        key = "autoSeparatePoints",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showAppPreviewIconCenterStartPosition = SettingObject(
        key = "showAppPreviewIconCenterStartPosition",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val linePreviewSnapToAction = SettingObject(
        key = "linePreviewSnapToAction",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val minAngleFromAPointToActivateIt = SettingObject(
        key = "minAngleFromAPointToActivateIt",
        dataStoreName = dataStoreName,
        default = 0,
        type = SettingType.Int
    )

    val showAllActionsOnCurrentCircle = SettingObject(
        key = "showAllActionsOnCurrentCircle",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val selectedIconPack = SettingObject(
        key = "selected_icon_pack",
        dataStoreName = dataStoreName,
        default = "",
        type = SettingType.String
    )

    val iconPackTint = SettingObject(
        key = "icon_pack_tint",
        dataStoreName = dataStoreName,
        default = Color.Unspecified,
        type = SettingType.Color
    )

    val appLabelIconOverlayTopPadding = SettingObject(
        key = "appLabelIconOverlayTopPadding",
        dataStoreName = dataStoreName,
        default = 30,
        type = SettingType.Int
    )

    val appLabelOverlaySize = SettingObject(
        key = "appLabelOverlaySize",
        dataStoreName = dataStoreName,
        default = 18,
        type = SettingType.Int
    )

    val appIconOverlaySize = SettingObject(
        key = "appIconOverlaySize",
        dataStoreName = dataStoreName,
        default = 22,
        type = SettingType.Int
    )

    override val ALL: List<SettingObject<*>> = listOf(
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
        minAngleFromAPointToActivateIt,
        showAllActionsOnCurrentCircle,
        selectedIconPack,
        iconPackTint,
        appLabelIconOverlayTopPadding,
        appLabelOverlaySize,
        appIconOverlaySize
    )
}
