package org.elnix.dragonlauncher.ui.statusbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.ui.components.settings.asState

@Composable
fun StatusBar(
    onClockAction: (SwipeActionSerializable) -> Unit,
    onDateAction: (SwipeActionSerializable) -> Unit
) {
    val statusBarBackground by StatusBarSettingsStore.barBackgroundColor.asState()
    val statusBarText by StatusBarSettingsStore.barTextColor.asState()

    val showTime by StatusBarSettingsStore.showTime.asState()
    val showDate by StatusBarSettingsStore.showDate.asState()
    val timeFormatter by StatusBarSettingsStore.timeFormatter.asState()
    val dateFormatter by StatusBarSettingsStore.dateFormater.asState()
    val showNotifications by StatusBarSettingsStore.showNotifications.asState()
    val showBattery by StatusBarSettingsStore.showBattery.asState()
    val showConnectivity by StatusBarSettingsStore.showConnectivity.asState()
    val showNextAlarm by StatusBarSettingsStore.showNextAlarm.asState()
    val leftStatusBarPadding by StatusBarSettingsStore.leftPadding.asState()
    val rightStatusBarPadding by StatusBarSettingsStore.rightPadding.asState()
    val topStatusBarPadding by StatusBarSettingsStore.topPadding.asState()
    val bottomStatusBarPadding by StatusBarSettingsStore.bottomPadding.asState()
    val clockAction by StatusBarSettingsStore.clockAction.asState()
    val dateAction by StatusBarSettingsStore.dateAction.asState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(statusBarBackground)
            .padding(
                start = leftStatusBarPadding.dp,
                top = topStatusBarPadding.dp,
                end = rightStatusBarPadding.dp,
                bottom = bottomStatusBarPadding.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        StatusBarClock(
            showTime = showTime,
            showDate = showDate,
            timeFormatter = timeFormatter,
            dateFormatter = dateFormatter,
            clockAction = clockAction,
            dateAction = dateAction,
            textColor = statusBarText,
            onClockAction = onClockAction,
            onDateAction = onDateAction
        )

        Spacer(modifier = Modifier.weight(1f))

        if (showNextAlarm) {
            StatusBarNextAlarm(statusBarText)
            Spacer(modifier = Modifier.width(6.dp))
        }

        if (showNotifications) {
            StatusBarNotifications()
            Spacer(modifier = Modifier.width(6.dp))
        }

        if (showConnectivity) {
            StatusBarConnectivity(statusBarText)
            Spacer(modifier = Modifier.width(6.dp))
        }

        if (showBattery) {
            StatusBarBattery(statusBarText)
        }
    }
}
