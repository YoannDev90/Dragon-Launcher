package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.data.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.helpers.CustomActionSelector
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
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

    val backAction by BehaviorSettingsStore.getBackAction(ctx).collectAsState(initial = null)
    val doubleClickAction by BehaviorSettingsStore.getDoubleClickAction(ctx).collectAsState(initial = null)
    val keepScreenOn by BehaviorSettingsStore.getKeepScreenOn(ctx).collectAsState(initial = false)
    val leftPadding by BehaviorSettingsStore.getLeftPadding(ctx).collectAsState(initial = 0)
    val rightPadding by BehaviorSettingsStore.getRightPadding(ctx).collectAsState(initial = 0)
    val upPadding by BehaviorSettingsStore.getUpPadding(ctx).collectAsState(initial = 0)
    val downPadding by BehaviorSettingsStore.getDownPadding(ctx).collectAsState(initial = 0)

    val isDragging = remember { mutableStateOf(false) }

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

        item {
            SliderWithLabel(
                label = stringResource(R.string.left_padding),
                value = leftPadding,
                valueRange = 0f..50f,
                color = MaterialTheme.colorScheme.primary,
                showValue = true,
                onReset = {
                    scope.launch {
                        BehaviorSettingsStore.setLeftPadding(ctx, 0)
                    }
                },
                onChange = {
                    scope.launch {
                        BehaviorSettingsStore.setLeftPadding(ctx, it)
                    }
                },
                onDragStateChange = { dragging ->
                    isDragging.value = dragging
                }
            )
        }

        item {
            SliderWithLabel(
                label = stringResource(R.string.right_padding),
                value = rightPadding,
                valueRange = 0f..50f,
                color = MaterialTheme.colorScheme.primary,
                showValue = true,
                onReset = {
                    scope.launch {
                        BehaviorSettingsStore.setRightPadding(ctx, 0)
                    }
                },
                onChange = {
                    scope.launch {
                        BehaviorSettingsStore.setRightPadding(ctx, it)
                    }
                },
                onDragStateChange = { dragging ->
                    isDragging.value = dragging
                }
            )
        }

        item {
            SliderWithLabel(
                label = stringResource(R.string.up_padding),
                value = upPadding,
                valueRange = 0f..50f,
                color = MaterialTheme.colorScheme.primary,
                showValue = true,
                onReset = {
                    scope.launch {
                        BehaviorSettingsStore.setUpPadding(ctx, 0)
                    }
                },
                onChange = {
                    scope.launch {
                        BehaviorSettingsStore.setUpPadding(ctx, it)
                    }
                },
                onDragStateChange = { dragging ->
                    isDragging.value = dragging
                }
            )
        }

        item {
            SliderWithLabel(
                label = stringResource(R.string.down_padding),
                value = downPadding,
                valueRange = 0f..50f,
                color = MaterialTheme.colorScheme.primary,
                showValue = true,
                onReset = {
                    scope.launch {
                        BehaviorSettingsStore.setDownPadding(ctx, 0)
                    }
                },
                onChange = {
                    scope.launch {
                        BehaviorSettingsStore.setDownPadding(ctx, it)
                    }
                },
                onDragStateChange = { dragging ->
                    isDragging.value = dragging
                }
            )
        }
    }

    DragPreviewOverlay(
        isVisible = isDragging.value,
        leftPadding = leftPadding.dp,
        rightPadding = rightPadding.dp,
        upPadding = upPadding.dp,
        downPadding = downPadding.dp
    )
}





@Composable
fun DragPreviewOverlay(
    isVisible: Boolean,
    leftPadding: androidx.compose.ui.unit.Dp,
    rightPadding: androidx.compose.ui.unit.Dp,
    upPadding: androidx.compose.ui.unit.Dp,
    downPadding: androidx.compose.ui.unit.Dp
) {
    if (!isVisible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                while (true) {
                    awaitPointerEventScope {
                        awaitPointerEvent()
                    }
                }
            }
            .padding(
                start = leftPadding,
                top = upPadding,
                end = rightPadding,
                bottom = downPadding
            )
            .background(Color(0x55FF0000))

    ) { }
}
