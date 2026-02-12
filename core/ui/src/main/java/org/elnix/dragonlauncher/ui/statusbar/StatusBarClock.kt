package org.elnix.dragonlauncher.ui.statusbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.utils.openAlarmApp
import org.elnix.dragonlauncher.common.utils.openCalendar
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun StatusBarClock(
    showTime: Boolean,
    showDate: Boolean,
    textColor: Color,
    timeFormatter: String,
    dateFormatter: String,
    clockAction: SwipeActionSerializable?,
    dateAction: SwipeActionSerializable?,
    onClockAction: ((SwipeActionSerializable) -> Unit)?,
    onDateAction: ((SwipeActionSerializable) -> Unit)?
) {
    val ctx = LocalContext.current


    val timeFormat = remember(timeFormatter) {
        try {
            DateTimeFormatter.ofPattern(timeFormatter)
        } catch (e: Exception) {
            println("⚠️ Invalid time format '$timeFormatter': ${e.message}")
            DateTimeFormatter.ofPattern("HH:mm:ss")
        }
    }

    val dateFormat = remember(dateFormatter) {
        try {
            DateTimeFormatter.ofPattern(dateFormatter)
        } catch (e: Exception) {
            println("⚠️ Invalid date format '$dateFormatter': ${e.message}")
            DateTimeFormatter.ofPattern("MMM dd")
        }
    }

    var time by remember { mutableStateOf(LocalTime.now()) }
    var date by remember { mutableStateOf(LocalDate.now()) }

    // Update every second if timeFormatter contains 'ss', else every minute
    val updateInterval = if ("ss" in timeFormatter) 1_000L else 60_000L

    LaunchedEffect(timeFormatter, dateFormatter) {
        while (true) {
            time = LocalTime.now()
            date = LocalDate.now()
            delay(updateInterval)
        }
    }

    val timeText = try {
        time.format(timeFormat)
    } catch (e: Exception) {
        println("⚠️ Time formatting failed: ${e.message}")
        time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    val dateText = try {
        date.format(dateFormat)
    } catch (e: Exception) {
        println("⚠️ Date formatting failed: ${e.message}")
        date.format(DateTimeFormatter.ofPattern("MMM dd"))
    }

    Row {
        if (showTime) {
            Text(
                text = timeText,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable(onClockAction != null) {
                    clockAction?.let { onClockAction!!(it) } ?: openAlarmApp(ctx)
                }
            )
        }

        if (showDate) {
            Text(
                text = dateText,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable(onDateAction != null) {
                    dateAction?.let { onDateAction!!(it) } ?: openCalendar(ctx)
                }
            )
        }
    }
}
