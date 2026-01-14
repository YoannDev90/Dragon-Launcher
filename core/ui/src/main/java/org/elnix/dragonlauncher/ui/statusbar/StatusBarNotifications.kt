package org.elnix.dragonlauncher.ui.statusbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun StatusBarNotifications() {
    Text(
        text = "🔔",
        style = MaterialTheme.typography.bodyMedium
    )
}
