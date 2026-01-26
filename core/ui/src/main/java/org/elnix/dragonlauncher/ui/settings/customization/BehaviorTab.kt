package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.helpers.CustomActionSelector
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader


@Composable
fun BehaviorTab(
    appsViewModel: AppsViewModel,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val backAction by BehaviorSettingsStore.backAction.flow(ctx).collectAsState(initial = null)
    val doubleClickAction by BehaviorSettingsStore.doubleClickAction.flow(ctx).collectAsState(initial = null)
    val homeAction by BehaviorSettingsStore.homeAction.flow(ctx).collectAsState(initial = null)
    val keepScreenOn by BehaviorSettingsStore.keepScreenOn.flow(ctx).collectAsState(initial = false)
    val leftPadding by BehaviorSettingsStore.leftPadding.flow(ctx).collectAsState(initial = 0)
    val rightPadding by BehaviorSettingsStore.rightPadding.flow(ctx).collectAsState(initial = 0)
    val topPadding by BehaviorSettingsStore.topPadding.flow(ctx).collectAsState(initial = 0)
    val bottomPadding by BehaviorSettingsStore.bottomPadding.flow(ctx).collectAsState(initial = 0)

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
                scope.launch { BehaviorSettingsStore.keepScreenOn.set(ctx, it) }
            }
        }

        item {
            CustomActionSelector(
                appsViewModel = appsViewModel,
                currentAction = backAction,
                label = stringResource(R.string.back_action),
                onToggle = {
                    scope.launch {
                        BehaviorSettingsStore.backAction.reset(ctx)
                    }
                }
            ) {
                scope.launch {
                    BehaviorSettingsStore.backAction.set(ctx, it)
                }
            }
        }

        item {
            CustomActionSelector(
                appsViewModel = appsViewModel,
                currentAction = doubleClickAction,
                label = stringResource(R.string.double_click_action),
                onToggle = {
                    scope.launch {
                        BehaviorSettingsStore.doubleClickAction.reset(ctx)
                    }
                }
            ) {
                scope.launch {
                    BehaviorSettingsStore.doubleClickAction.set(ctx, it)
                }
            }
        }

        item {
            CustomActionSelector(
                appsViewModel = appsViewModel,
                currentAction = homeAction,
                label = stringResource(R.string.home_action),
                onToggle = {
                    scope.launch {
                        BehaviorSettingsStore.homeAction.reset(ctx)
                    }
                }
            ) {
                scope.launch {
                    BehaviorSettingsStore.homeAction.set(ctx, it)
                }
            }
        }

        item {
            SliderWithLabel(
                label = stringResource(R.string.left_padding),
                value = leftPadding,
                valueRange = 0..300,
                color = MaterialTheme.colorScheme.primary,
                showValue = true,
                onReset = {
                    scope.launch {
                        BehaviorSettingsStore.leftPadding.reset(ctx)
                    }
                },
                onChange = {
                    scope.launch {
                        BehaviorSettingsStore.leftPadding.set(ctx, it)
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
                valueRange = 0..300,
                color = MaterialTheme.colorScheme.primary,
                showValue = true,
                onReset = {
                    scope.launch {
                        BehaviorSettingsStore.rightPadding.reset(ctx)
                    }
                },
                onChange = {
                    scope.launch {
                        BehaviorSettingsStore.rightPadding.set(ctx, it)
                    }
                },
                onDragStateChange = { dragging ->
                    isDragging.value = dragging
                }
            )
        }

        item {
            SliderWithLabel(
                label = stringResource(R.string.top_padding),
                value = topPadding,
                valueRange = 0..300,
                color = MaterialTheme.colorScheme.primary,
                showValue = true,
                onReset = {
                    scope.launch {
                        BehaviorSettingsStore.topPadding.reset(ctx)
                    }
                },
                onChange = {
                    scope.launch {
                        BehaviorSettingsStore.topPadding.set(ctx, it)
                    }
                },
                onDragStateChange = { dragging ->
                    isDragging.value = dragging
                }
            )
        }

        item {
            SliderWithLabel(
                label = stringResource(R.string.bottom_padding),
                value = bottomPadding,
                valueRange = 0..300,
                color = MaterialTheme.colorScheme.primary,
                showValue = true,
                onReset = {
                    scope.launch {
                        BehaviorSettingsStore.bottomPadding.reset(ctx)
                    }
                },
                onChange = {
                    scope.launch {
                        BehaviorSettingsStore.bottomPadding.set(ctx, it)
                    }
                },
                onDragStateChange = { dragging ->
                    isDragging.value = dragging
                }
            )
        }
    }


    if (isDragging.value){
        Canvas(Modifier.fillMaxSize()) {
            drawRect(
                color = Color(0x55FF0000),
                topLeft = Offset(
                    leftPadding.toFloat(),
                    topPadding.toFloat()
                ),
                size = Size(
                    size.width - leftPadding - rightPadding.toFloat(),
                    size.height - topPadding - bottomPadding.toFloat()
                )
            )
        }
    }
}
