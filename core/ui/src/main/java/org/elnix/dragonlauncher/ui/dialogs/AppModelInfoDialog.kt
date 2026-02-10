package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape

@Composable
fun AppModelInfoDialog(
    app: AppModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        text = {
            Column {
                Text(text = "name = ${app.name}")
                Text(text = "packageName = ${app.packageName}")
                Text(text = "isEnabled = ${app.isEnabled}")
                Text(text = "isSystem = ${app.isSystem}")
                Text(text = "isWorkProfile = ${app.isWorkProfile}")
                Text(text = "isPrivateProfile = ${app.isPrivateProfile}")
                Text(text = "isLaunchable = ${app.isLaunchable}")
                Text(text = "settings = ${app.settings}")
                Text(text = "userId = ${app.userId}")
            }
        },
        dismissButton = {},
        confirmButton = {},
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = DragonShape
    )
}
