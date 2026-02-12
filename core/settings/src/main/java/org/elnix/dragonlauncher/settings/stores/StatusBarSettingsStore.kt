package org.elnix.dragonlauncher.settings.stores

import androidx.compose.ui.graphics.Color
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object StatusBarSettingsStore : MapSettingsStore() {

    override val name: String = "Status Bar"
    override val dataStoreName: DataStoreName = DataStoreName.STATUS_BAR

    val showStatusBar = Settings.boolean(
        key = "showStatusBar",
        dataStoreName = dataStoreName,
        default = true
    )

    val barBackgroundColor = Settings.color(
        key = "barBackgroundColor",
        dataStoreName = dataStoreName,
        default = Color.Transparent
    )

    val barTextColor = Settings.color(
        key = "barTextColor",
        dataStoreName = dataStoreName,
        default = Color.White
    )

    val showTime = Settings.boolean(
        key = "showTime",
        dataStoreName = dataStoreName,
        default = true
    )

    val showDate = Settings.boolean(
        key = "showDate",
        dataStoreName = dataStoreName,
        default = false
    )

    val timeFormatter = Settings.string(
        key = "timeFormatter",
        dataStoreName = dataStoreName,
        default = "HH:mm:ss | "
    )

    val dateFormater = Settings.string(
        key = "dateFormatter",
        dataStoreName = dataStoreName,
        default = "MMM dd"
    )

    val showNotifications = Settings.boolean(
        key = "showNotifications",
        dataStoreName = dataStoreName,
        default = false
    )

    val showBattery = Settings.boolean(
        key = "showBattery",
        dataStoreName = dataStoreName,
        default = true
    )

    val showConnectivity = Settings.boolean(
        key = "showConnectivity",
        dataStoreName = dataStoreName,
        default = false
    )

    val showNextAlarm = Settings.boolean(
        key = "showNextAlarm",
        dataStoreName = dataStoreName,
        default = true
    )

    val leftPadding = Settings.int(
        key = "leftPadding",
        dataStoreName = dataStoreName,
        default = 5,
        allowedRange = 0..300
    )

    val rightPadding = Settings.int(
        key = "rightPadding",
        dataStoreName = dataStoreName,
        default = 5,
        allowedRange = 0..300
    )

    val topPadding = Settings.int(
        key = "topPadding",
        dataStoreName = dataStoreName,
        default = 2,
        allowedRange = 0..300
    )

    val bottomPadding = Settings.int(
        key = "bottomPadding",
        dataStoreName = dataStoreName,
        default = 2,
        allowedRange = 0..300
    )

    val clockAction = Settings.swipeAction(
        key = "clockAction",
        dataStoreName = dataStoreName,
        default = SwipeActionSerializable.None
    )

    val dateAction = Settings.swipeAction(
        key = "dateAction",
        dataStoreName = dataStoreName,
        default = SwipeActionSerializable.None
    )

    override val ALL: List<BaseSettingObject<*,*>> = listOf(
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
