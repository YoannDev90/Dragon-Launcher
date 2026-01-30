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
import org.elnix.dragonlauncher.enumsui.DrawerActions
import org.elnix.dragonlauncher.enumsui.drawerActionsLabel
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.ui.components.generic.ActionSelectorRow

@Composable
fun DrawerActionSelector(
    settingObject: BaseSettingObject<DrawerActions, String>,
    label: String
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by settingObject.flow(ctx).collectAsState(settingObject.default)

    var tempState by remember { mutableStateOf(state) }

    LaunchedEffect(state) { tempState = state }

    LaunchedEffect(tempState) {
        scope.launch {
            settingObject.set(ctx, tempState)
        }
    }

    val stateNotDisabled = tempState != DrawerActions.DISABLED

    ActionSelectorRow(
        options = DrawerActions.entries.filter { it != DrawerActions.DISABLED },
        selected = tempState,
        label = label,
        optionLabel = { drawerActionsLabel(ctx, it) },
        onToggle = {

            tempState = if (stateNotDisabled) {
                DrawerActions.TOGGLE_KB
            } else {
                DrawerActions.TOGGLE_KB
            }
        },
        toggled = stateNotDisabled
    ) {
        tempState = it
    }
}
