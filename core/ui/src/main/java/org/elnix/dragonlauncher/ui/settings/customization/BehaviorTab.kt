@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.components.ExpandableSection
import org.elnix.dragonlauncher.ui.components.dragon.DragonColumnGroup
import org.elnix.dragonlauncher.ui.components.settings.SettingsSlider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.helpers.CustomActionSelector
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader


@Composable
fun BehaviorTab(
    appsViewModel: AppsViewModel,
    appLifecycleViewModel: AppLifecycleViewModel,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val backAction by BehaviorSettingsStore.backAction.asState()
    val doubleClickAction by BehaviorSettingsStore.doubleClickAction.asState()
    val homeAction by BehaviorSettingsStore.homeAction.asState()
    val leftPadding by BehaviorSettingsStore.leftPadding.asState()
    val rightPadding by BehaviorSettingsStore.rightPadding.asState()
    val topPadding by BehaviorSettingsStore.topPadding.asState()
    val bottomPadding by BehaviorSettingsStore.bottomPadding.asState()

    var isPaddingBlockExtended by remember { mutableStateOf(false) }

    var isDragging by remember { mutableStateOf(false) }

    suspend fun brieflyShowIsDragging() {
        isDragging = true
        delay(200)
        isDragging = false
    }

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
            SettingsSwitchRow(
                setting = BehaviorSettingsStore.keepScreenOn,
                title = stringResource(R.string.keep_screen_on),
                description = stringResource(R.string.keep_screen_on_desc)
            )
        }

        item {
            SettingsSwitchRow(
                setting = BehaviorSettingsStore.disableHapticFeedbackGlobally,
                title = stringResource(R.string.disable_haptic_globally),
                description = stringResource(R.string.disable_haptic_globally_desc)
            )
        }

        item {
            SettingsSwitchRow(
                setting = BehaviorSettingsStore.pointsActionSnapsToOuterCircle,
                title = stringResource(R.string.point_action_snaps_to_outer_circle),
                description = stringResource(R.string.point_action_snaps_to_outer_circle_desc)
            )
        }

        item {
            CustomActionSelector(
                appsViewModel = appsViewModel,
                appLifecycleViewModel = appLifecycleViewModel,
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
                appLifecycleViewModel = appLifecycleViewModel,
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
                appLifecycleViewModel = appLifecycleViewModel,
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
            DragonColumnGroup {
                SettingsSlider(
                    setting = BehaviorSettingsStore.longCLickSettingsDuration,
                    title = stringResource(R.string.long_click_settings_duration),
                    description = stringResource(R.string.long_click_settings_duration_desc),
                    valueRange = 200..5000
                )

                SettingsSlider(
                    setting = BehaviorSettingsStore.holdDelayBeforeStartingLongClickSettings,
                    title = stringResource(R.string.hold_delay_before_starting_long_click_settings),
                    description = stringResource(R.string.hold_delay_before_starting_long_click_settings_desc),
                    valueRange = 200..2000
                )
            }
        }

        item {
            ExpandableSection(
                expanded = { isPaddingBlockExtended },
                title = stringResource(R.string.drag_zone_padding),
                onExpand = { isPaddingBlockExtended = !isPaddingBlockExtended }
            ) {
                SliderWithLabel(
                    label = stringResource(R.string.left_padding),
                    value = leftPadding,
                    valueRange = 0..300,
                    color = MaterialTheme.colorScheme.primary,
                    showValue = true,
                    onReset = {
                        scope.launch {
                            BehaviorSettingsStore.leftPadding.reset(ctx)
                            brieflyShowIsDragging()
                        }
                    },
                    onChange = {
                        scope.launch {
                            BehaviorSettingsStore.leftPadding.set(ctx, it)
                        }
                    },
                    onDragStateChange = { dragging ->
                        isDragging = dragging
                    }
                )

                SliderWithLabel(
                    label = stringResource(R.string.right_padding),
                    value = rightPadding,
                    valueRange = 0..300,
                    color = MaterialTheme.colorScheme.primary,
                    showValue = true,
                    onReset = {
                        scope.launch {
                            BehaviorSettingsStore.rightPadding.reset(ctx)
                            brieflyShowIsDragging()
                        }
                    },
                    onChange = {
                        scope.launch {
                            BehaviorSettingsStore.rightPadding.set(ctx, it)
                        }
                    },
                    onDragStateChange = { dragging ->
                        isDragging = dragging
                    }
                )

                SliderWithLabel(
                    label = stringResource(R.string.top_padding),
                    value = topPadding,
                    valueRange = 0..300,
                    color = MaterialTheme.colorScheme.primary,
                    showValue = true,
                    onReset = {
                        scope.launch {
                            BehaviorSettingsStore.topPadding.reset(ctx)
                            brieflyShowIsDragging()
                        }
                    },
                    onChange = {
                        scope.launch {
                            BehaviorSettingsStore.topPadding.set(ctx, it)
                        }
                    },
                    onDragStateChange = { dragging ->
                        isDragging = dragging
                    }
                )

                SliderWithLabel(
                    label = stringResource(R.string.bottom_padding),
                    value = bottomPadding,
                    valueRange = 0..300,
                    color = MaterialTheme.colorScheme.primary,
                    showValue = true,
                    onReset = {
                        scope.launch {
                            BehaviorSettingsStore.bottomPadding.reset(ctx)
                            brieflyShowIsDragging()
                        }
                    },
                    onChange = {
                        scope.launch {
                            BehaviorSettingsStore.bottomPadding.set(ctx, it)
                        }
                    },
                    onDragStateChange = { dragging ->
                        isDragging = dragging
                    }
                )
            }
        }

        item {
            DragonColumnGroup {
                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.superWarningMode,
                    title = stringResource(R.string.super_warning_mode),
                    description = stringResource(R.string.super_warning_mode_desc),
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.vibrateOnError,
                    title = stringResource(R.string.vibrate_on_error),
                    description = stringResource(R.string.vibrate_on_error_desc),
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.alarmSound,
                    title = stringResource(R.string.alarm_sound),
                    description = stringResource(R.string.super_warning_mode_desc),
                )

                SettingsSwitchRow(
                    setting = BehaviorSettingsStore.metalPipesSound,
                    title = stringResource(R.string.metal_pipes_sound),
                    description = stringResource(R.string.metal_pipes_sound_desc),
                )

                SettingsSlider(
                    setting = BehaviorSettingsStore.superWarningModeSound,
                    title = stringResource(R.string.super_warning_mode_sound),
                    description = stringResource(R.string.super_warning_mode_sound_desc),
                    valueRange = 0..100
                )
            }
        }
    }


    if (isDragging) {
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
