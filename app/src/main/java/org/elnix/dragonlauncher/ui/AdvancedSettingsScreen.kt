package org.elnix.dragonlauncher.ui


import androidx.activity.compose.BackHandler
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.data.datastore.SettingsStore
import org.elnix.dragonlauncher.ui.helpers.SwitchRow


@Composable
fun AdvancedSettingsScreen(
    onBack: (() -> Unit)
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val rgbLoading by SettingsStore.getRGBLoading(ctx)
        .collectAsState(initial = true)

    val angleLineColor by SettingsStore.getAngleLineColor(ctx)
        .collectAsState(initial = null)

    BackHandler { onBack() }

    SwitchRow(
        rgbLoading,
        "RGB loading settings",
    ) { scope.launch { SettingsStore.setRGBLoading(ctx, it) } }

    OutlinedTextField(
        value = angleLineColor?.toArgb()?.toString() ?: "",
        onValueChange = { newText: String ->
            scope.launch {
                val intValue = newText.toIntOrNull()
                SettingsStore.setAngleLineColor(ctx, intValue?.let { Color(it) })
            }
        },
        label = { Text("Angle line color") },
        modifier = Modifier
    )


}
