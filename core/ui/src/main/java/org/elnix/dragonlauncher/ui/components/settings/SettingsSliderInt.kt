@file:Suppress("AssignedValueIsNeverRead", "unused")

package org.elnix.dragonlauncher.ui.components.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel

@Composable
fun SettingsSlider(
    setting: BaseSettingObject<Int, Int>,
    title: String,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    showValue: Boolean = true,
    decimals: Int = 2,
    enabled: Boolean = true
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by setting.flow(ctx).collectAsState(setting.default)

    var tempState by remember { mutableIntStateOf(state) }

    SliderWithLabel(
        modifier = modifier,
        label = title,
        value = state,
        valueRange = valueRange,
        color = color,
        enabled = enabled,
        backgroundColor = backgroundColor,
        showValue = showValue,
        onReset = {
            scope.launch {
                setting.reset(ctx)
            }
        },
        onDragStateChange = {
            scope.launch { setting.set(ctx, tempState) }
        }
    ) { tempState = it }
}

@Composable
fun SettingsSlider(
    setting: BaseSettingObject<Int, Int>,
    title: String,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    showValue: Boolean = true,
    decimals: Int = 2,
    enabled: Boolean = true,
    instantUiUpdate: Boolean,
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by setting.flow(ctx).collectAsState(setting.default)

    SliderWithLabel(
        modifier = modifier,
        label = title,
        value = state,
        valueRange = valueRange,
        color = color,
        enabled = enabled,
        backgroundColor = backgroundColor,
        showValue = showValue,
        onReset = {
            scope.launch {
                setting.reset(ctx)
            }
        },
        onDragStateChange = {}
    ) {
        scope.launch { setting.set(ctx, it) }
    }
}
