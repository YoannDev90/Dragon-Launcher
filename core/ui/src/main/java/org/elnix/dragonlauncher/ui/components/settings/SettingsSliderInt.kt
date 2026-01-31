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
    description: String? = null,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    showValue: Boolean = true,
    enabled: Boolean = true,
    allowTextEditValue: Boolean = true,
    onDragStateChange: ((Boolean) -> Unit)? = null,
    onChange: ((Int) -> Unit)? = null,
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by setting.flow(ctx).collectAsState(setting.default)

    var tempState by remember { mutableIntStateOf(state) }

    SliderWithLabel(
        modifier = modifier,
        label = title,
        description = description,
        value = state,
        valueRange = valueRange,
        color = color,
        enabled = enabled,
        allowTextEditValue = allowTextEditValue,
        backgroundColor = backgroundColor,
        showValue = showValue,
        onReset = {
            scope.launch {
                setting.reset(ctx)
            }
        },
        onDragStateChange = {
            scope.launch { setting.set(ctx, tempState) }
            onDragStateChange?.invoke(it)
        }
    ) {
        tempState = it
        onChange?.invoke(it)
    }
}

@Composable
fun SettingsSlider(
    setting: BaseSettingObject<Int, Int>,
    title: String,
    description: String? = null,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    showValue: Boolean = true,
    enabled: Boolean = true,
    allowTextEditValue: Boolean = true,
    instantUiUpdate: Boolean,
    onDragStateChange: ((Boolean) -> Unit)? = null,
    onChange: ((Int) -> Unit)? = null
    ) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by setting.flow(ctx).collectAsState(setting.default)

    SliderWithLabel(
        modifier = modifier,
        label = title,
        description = description,
        value = state,
        valueRange = valueRange,
        color = color,
        enabled = enabled,
        allowTextEditValue = allowTextEditValue,
        backgroundColor = backgroundColor,
        showValue = showValue,
        onReset = {
            scope.launch {
                setting.reset(ctx)
            }
        },
        onDragStateChange = onDragStateChange
    ) {
        scope.launch { setting.set(ctx, it) }
        onChange?.invoke(it)
    }
}
