package org.elnix.dragonlauncher.ui.components.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow

@Composable
fun SettingsColorPicker(
    settingObject: BaseSettingObject<Color, Int>,
    defaultColor: Color,
    label: String
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by settingObject.asState()

    var tempState by remember { mutableStateOf(state) }

    LaunchedEffect(state) { tempState = state }


    ColorPickerRow(
        label = label,
        currentColor = tempState
    ) {
        tempState = it ?: defaultColor
        scope.launch {
            settingObject.set(ctx, it)
        }
    }
}
