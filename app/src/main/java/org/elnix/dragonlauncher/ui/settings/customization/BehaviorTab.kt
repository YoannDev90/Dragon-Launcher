package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.data.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.helpers.CustomActionSelector
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.utils.models.AppDrawerViewModel
import org.elnix.dragonlauncher.utils.models.WorkspaceViewModel


@Composable
fun BehaviorTab(
    appsViewModel: AppDrawerViewModel,
    workspaceViewModel: WorkspaceViewModel,
    onBack: () -> Unit
) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val icons by appsViewModel.icons.collectAsState()


    val backAction by BehaviorSettingsStore.getBackAction(ctx)
        .collectAsState(initial = null)

    val doubleClickAction by BehaviorSettingsStore.getDoubleClickAction(ctx)
        .collectAsState(initial = null)

    val keepScreenOn by BehaviorSettingsStore.getKeepScreenOn(ctx)
        .collectAsState(initial = false)

    SettingsLazyHeader(
        title = stringResource(R.string.behavior),
        onBack = onBack,
        helpText = stringResource(R.string.behavior_help),
        onReset = {
            scope.launch {
                UiSettingsStore.resetAll(ctx)
            }
        }
    ) {
        item {
            SwitchRow(
                keepScreenOn,
                stringResource(R.string.keep_screen_on),
            ) {
                scope.launch { BehaviorSettingsStore.setKeepScreenOn(ctx, it) }
            }
        }

        item {
            CustomActionSelector(
                appsViewModel = appsViewModel,
                workspaceViewModel = workspaceViewModel,
                icons = icons,
                currentAction = backAction,
                label = stringResource(R.string.back_action),
                onToggle = {
                    scope.launch {
                        BehaviorSettingsStore.setBackAction(ctx, null)
                    }
                }
            ) {
                scope.launch {
                    BehaviorSettingsStore.setBackAction(ctx, it)
                }
            }
        }

        item {
            CustomActionSelector(
                appsViewModel = appsViewModel,
                workspaceViewModel = workspaceViewModel,
                icons = icons,
                currentAction = doubleClickAction,
                label = stringResource(R.string.double_click_action),
                onToggle = {
                    scope.launch {
                        BehaviorSettingsStore.setDoubleClickAction(ctx, null)
                    }
                }
            ) {
                scope.launch {
                    BehaviorSettingsStore.setDoubleClickAction(ctx, it)
                }
            }
        }
    }
}
