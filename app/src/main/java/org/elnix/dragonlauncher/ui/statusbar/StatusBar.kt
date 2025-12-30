package org.elnix.dragonlauncher.ui.statusbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusBar(
    backgroundColor: Color,
    textColor: Color,
    showTime: Boolean,
    showDate: Boolean,
    timeFormatter: String,
    dateFormatter: String,
    showNotifications: Boolean,
    showBattery: Boolean,
    showConnectivity: Boolean,
    showNextAlarm: Boolean,
    leftPadding: Int,
    rightPadding: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(
                start = leftPadding.dp,
                top = 2.dp,
                end = rightPadding.dp,
                bottom = 2.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        StatusBarClock(
            showTime = showTime,
            showDate = showDate,
            timeFormatter = timeFormatter,
            dateFormatter = dateFormatter,
            textColor = textColor
        )

        Spacer(modifier = Modifier.weight(1f))

        if (showNextAlarm) {
            StatusBarNextAlarm(textColor = textColor)
            Spacer(modifier = Modifier.width(6.dp))
        }

        if (showNotifications) {
            StatusBarNotifications()
            Spacer(modifier = Modifier.width(6.dp))
        }

        if (showConnectivity) {
            StatusBarConnectivity(textColor)
            Spacer(modifier = Modifier.width(6.dp))
        }

        if (showBattery) {
            StatusBarBattery(textColor)
        }
    }
}
