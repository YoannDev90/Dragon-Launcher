package org.elnix.dragonlauncher.ui.statusbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore

@Composable
fun StatusBar(
    onClockAction: (SwipeActionSerializable) -> Unit,
    onDateAction: (SwipeActionSerializable) -> Unit
) {
    val ctx = LocalContext.current

    val statusBarBackground by StatusBarSettingsStore.barBackgroundColor.flow(ctx)
        .collectAsState(initial = Color.Transparent)

    val statusBarText by StatusBarSettingsStore.barTextColor.flow(ctx)
        .collectAsState(initial = MaterialTheme.colorScheme.onBackground)

    val showTime by StatusBarSettingsStore.showTime.flow(ctx)
        .collectAsState(initial = false)

    val showDate by StatusBarSettingsStore.showDate.flow(ctx)
        .collectAsState(initial = false)

    val timeFormatter by StatusBarSettingsStore.timeFormatter.flow(ctx)
        .collectAsState("HH:mm")

    val dateFormatter by StatusBarSettingsStore.dateFormater.flow(ctx)
        .collectAsState("MMM dd")

    val showNotifications by StatusBarSettingsStore.showNotifications.flow(ctx)
        .collectAsState(initial = false)

    val showBattery by StatusBarSettingsStore.showBattery.flow(ctx)
        .collectAsState(initial = false)

    val showConnectivity by StatusBarSettingsStore.showConnectivity.flow(ctx)
        .collectAsState(initial = false)

    val showNextAlarm by StatusBarSettingsStore.showNextAlarm.flow(ctx)
        .collectAsState(false)

    val leftStatusBarPadding by StatusBarSettingsStore.leftPadding.flow(ctx)
        .collectAsState(initial = 5)

    val rightStatusBarPadding by StatusBarSettingsStore.rightPadding.flow(ctx)
        .collectAsState(initial = 5)

    val topStatusBarPadding by StatusBarSettingsStore.topPadding.flow(ctx)
        .collectAsState(initial = 2)

    val bottomStatusBarPadding by StatusBarSettingsStore.bottomPadding.flow(ctx)
        .collectAsState(initial = 2)

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
