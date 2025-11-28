package org.elnix.dragonlauncher.ui


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

    val rgbLine by SettingsStore.getRGBLine(ctx)
        .collectAsState(initial = true)


    val debugInfos by SettingsStore.getDebugInfos(ctx)
        .collectAsState(initial = true)


//    val angleLineColor by SettingsStore.getAngleLineColor(ctx)
//        .collectAsState(initial = null)

//    var hexText by remember { mutableStateOf(toHexWithAlpha(angleLineColor ?: Color.Red)) }

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        }

        SwitchRow(
            debugInfos,
            "Debug Infos",
        ) { scope.launch { SettingsStore.setDebugInfos(ctx, it) } }


        SwitchRow(
            rgbLoading,
            "RGB loading settings",
        ) { scope.launch { SettingsStore.setRGBLoading(ctx, it) } }

        SwitchRow(
            rgbLine,
            "RGB line selector",
        ) { scope.launch { SettingsStore.setRGBLine(ctx, it) } }



//        OutlinedTextField(
//            value = hexText,
//            onValueChange = {
//                hexText = it
//                runCatching {
//                    if (it.startsWith("#")) {
//                        scope.launch {
//                            val intValue = hexText.toIntOrNull()
//                            SettingsStore.setAngleLineColor(ctx, intValue?.let { Color(hexText) })
//                        }
//                        angleLineColor = Color(hexText.toColorInt())
////                        alpha = parseAlpha(it)
//                    }
//                }
//            },
//            label = { Text("HEX") },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
//        OutlinedTextField(
//            value = angleLineColor?.toArgb()?.toString() ?: "",
//            onValueChange = { newText: String ->
//                scope.launch {
//                    val intValue = newText.toIntOrNull()
//                    SettingsStore.setAngleLineColor(ctx, intValue?.let { Color(it) })
//                }
//            },
//            label = { Text("Angle line color") },
//            modifier = Modifier.fillMaxWidth()
//        )
    }
}
