package org.elnix.dragonlauncher.ui.settings.debug

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.allStores
import org.elnix.dragonlauncher.data.stores.WallpaperSettingsStore
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.json.JSONObject

@Composable
fun SettingsDebugTab(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    var settingsJson by remember { mutableStateOf<JSONObject?>(null) }


    fun loadSettings() {
        scope.launch {
            val json = JSONObject()

            allStores.filter { it.store != WallpaperSettingsStore }.forEach { store ->
                store.store.exportForBackup(ctx)?.let {
                    json.put(store.backupKey, it)
                }
            }
            settingsJson = json
        }
    }
    LaunchedEffect(Unit) {
        loadSettings()
    }
    SettingsLazyHeader(
        title = stringResource(R.string.debug),
        onBack = onBack,
        helpText = "settings json",
        onReset = null,
        resetText = null
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = { loadSettings() }
                ) {
                    Icon(Icons.Default.Loop, null)
                }
            }
        }
        item {
            settingsJson?.let {
                Text(
                    text = it.toString(2)
                )
            }
        }
    }
}
