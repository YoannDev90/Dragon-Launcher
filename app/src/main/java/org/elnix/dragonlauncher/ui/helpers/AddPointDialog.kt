package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.data.SwipeActionSerializable

@Composable
fun AddPointDialog(
    onDismiss: () -> Unit,
    onActionSelected: (SwipeActionSerializable) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Choose action") },
        text = {
            Column {

                Button(onClick = {
                    onActionSelected(SwipeActionSerializable.OpenAppDrawer)
                }) { Text("Open App Drawer") }

                Button(onClick = {
                    onActionSelected(SwipeActionSerializable.NotificationShade)
                }) { Text("Notification Shade") }

                Button(onClick = {
                    onActionSelected(SwipeActionSerializable.ControlPanel)
                }) { Text("Control Panel") }

                Spacer(Modifier.height(12.dp))

                // Example: URL
                Button(onClick = {
                    onActionSelected(SwipeActionSerializable.OpenUrl("https://www.google.com"))
                }) { Text("Open URL") }

                Spacer(Modifier.height(12.dp))

                // Example: App picker (you can replace with real picker later)
                Button(onClick = {
                    onActionSelected(SwipeActionSerializable.LaunchApp("com.android.chrome"))
                }) { Text("Launch Chrome") }
            }
        },
        containerColor = Color.Black,
    )
}
