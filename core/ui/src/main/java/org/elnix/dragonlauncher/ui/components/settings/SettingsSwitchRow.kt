@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.components.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.ui.helpers.SwitchRow

@Composable
fun SettingsSwitchRow(
    setting: BaseSettingObject<Boolean, Boolean>,
    title: String,
    description: String,
    enabled: Boolean = true,
    onCheck: ((Boolean) -> Unit)? = null
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by setting.flow(ctx).collectAsState(setting.default)

    var tempState by remember { mutableStateOf(state) }

    LaunchedEffect(state) {
        tempState = state
    }

    SwitchRow(
        state = tempState,
        text = title,
        subText = description,
        enabled = enabled
    ) {
        tempState = it
        scope.launch {
            setting.set(ctx, it)
        }
        onCheck?.invoke(it)
    }
}
