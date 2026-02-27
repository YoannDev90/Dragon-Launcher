package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.backupableStores
import org.elnix.dragonlauncher.settings.bases.BaseSettingsStore
import org.elnix.dragonlauncher.common.utils.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.json.JSONObject

@Composable
fun ImportSettingsDialog(
    backupJson: JSONObject,
    onDismiss: () -> Unit,
    onConfirm: (selectedStores: Map<DataStoreName, BaseSettingsStore<*,*>>) -> Unit
) {

    // Filter stores that exist in backup JSON
    val availableStores = backupableStores.filter {
        backupJson.has(it.key.backupKey) ||
        backupJson.has("actions") // Old actions store, for legacy support
    }

    val selected = remember(availableStores) {
        mutableStateMapOf<DataStoreName, Boolean>().apply {
            availableStores.forEach { put(it.key, true) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(availableStores.filter { selected[it.key] == true })
                },
                colors = AppObjectsColors.buttonColors()
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = AppObjectsColors.cancelButtonColors()
            ) { Text("Cancel") }
        },
        title = { Text("Select settings to import") },
        text = {
            LazyColumn {
                items(availableStores.entries.toList()) { entry ->

                    val dataStoreName = entry.key
                    val settingsStore = entry.value
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .toggleable(
                                value = selected[dataStoreName] ?: true,
                                onValueChange = { selected[dataStoreName] = it }
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(settingsStore.name)
                        Checkbox(
                            checked = selected[dataStoreName] ?: true,
                            onCheckedChange = null
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = DragonShape
    )
}
