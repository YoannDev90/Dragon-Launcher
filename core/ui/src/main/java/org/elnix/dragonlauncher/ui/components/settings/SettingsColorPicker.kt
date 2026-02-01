package org.elnix.dragonlauncher.ui.components.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject

@Composable
fun SettingsColorPicker(
    settingObject: BaseSettingObject<Color, Int>,
    label: String
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by settingObject.flow(ctx).collectAsState(settingObject.default)

    var tempState by remember { mutableStateOf(state) }

    LaunchedEffect(state) { tempState = state }


//    ColorPickerRow(
//        label = label,
//        defaultColor =
//    ) { }
}
