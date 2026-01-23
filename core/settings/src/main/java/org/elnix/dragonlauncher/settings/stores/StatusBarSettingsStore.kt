package org.elnix.dragonlauncher.settings.stores

import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.theme.AmoledDefault
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object StatusBarSettingsStore : MapSettingsStore() {

    override val name: String = "Status Bar"
    override val dataStoreName: DataStoreName = DataStoreName.STATUS_BAR

    val showStatusBar = SettingObject(
        key = "showStatusBar",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val barBackgroundColor = SettingObject(
        key = "barBackgroundColor",
        dataStoreName = dataStoreName,
        default = Color.Transparent,
        type = SettingType.Color
    )

    val barTextColor = SettingObject(
        key = "barTextColor",
        dataStoreName = dataStoreName,
        default = AmoledDefault.OnBackground,
        type = SettingType.Color
    )

    val showTime = SettingObject(
        key = "showTime",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showDate = SettingObject(
        key = "showDate",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val timeFormatter = SettingObject(
        key = "timeFormatter",
        dataStoreName = dataStoreName,
        default = "HH:mm:ss",
        type = SettingType.String
    )

    val dateFormater = SettingObject(
        key = "dateFormatter",
        dataStoreName = dataStoreName,
        default = "MMM dd",
        type = SettingType.String
    )

    val showNotifications = SettingObject(
        key = "showNotifications",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val showBattery = SettingObject(
        key = "showBattery",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showConnectivity = SettingObject(
        key = "showConnectivity",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val showNextAlarm = SettingObject(
        key = "showNextAlarm",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val leftPadding = SettingObject(
        key = "leftPadding",
        dataStoreName = dataStoreName,
        default = 5,
        type = SettingType.Int
    )

    val rightPadding = SettingObject(
        key = "rightPadding",
        dataStoreName = dataStoreName,
        default = 5,
        type = SettingType.Int
    )

    val topPadding = SettingObject(
        key = "topPadding",
        dataStoreName = dataStoreName,
        default = 2,
        type = SettingType.Int
    )

    val bottomPadding = SettingObject(
        key = "bottomPadding",
        dataStoreName = dataStoreName,
        default = 2,
        type = SettingType.Int
    )

    val clockAction = SettingObject<SwipeActionSerializable?>(
        key = "clockAction",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.SwipeActionSerializable
    )

    val dateAction = SettingObject<SwipeActionSerializable?>(
        key = "dateAction",
        dataStoreName = dataStoreName,
        default = null,
        type = SettingType.SwipeActionSerializable
    )

    override val ALL: List<SettingObject<*>> = listOf(
        showStatusBar,
        barBackgroundColor,
        barTextColor,
        showTime,
        showDate,
        timeFormatter,
        dateFormater,
        showNotifications,
        showBattery,
        showConnectivity,
        showNextAlarm,
        leftPadding,
        rightPadding,
        topPadding,
        bottomPadding,
        clockAction,
        dateAction
    )
}
