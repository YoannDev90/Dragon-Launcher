package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.ui.components.ExpandableSection
import org.elnix.dragonlauncher.ui.components.settings.SettingsColorPicker
import org.elnix.dragonlauncher.ui.components.settings.SettingsSlider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.remembers.LocalShowStatusBar
import org.elnix.dragonlauncher.ui.remembers.rememberExpandableSection
import org.elnix.dragonlauncher.ui.statusbar.EditStatusBar
import org.elnix.dragonlauncher.ui.statusbar.StatusBar

@Composable
fun StatusBarTab(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val showStatusBar = LocalShowStatusBar.current
    val scope = rememberCoroutineScope()

    val systemInsets = WindowInsets.systemBars.asPaddingValues()
    val isRealFullscreen = systemInsets.calculateTopPadding() == 0.dp

    val paddingsSectionState = rememberExpandableSection(stringResource(R.string.padding))

    Column {
        AnimatedVisibility(showStatusBar && isRealFullscreen) {
            StatusBar(null)
        }

        SettingsLazyHeader(
            title = stringResource(R.string.status_bar),
            onBack = onBack,
            helpText = stringResource(R.string.status_bar_tab_text),
            onReset = {
                scope.launch {
                    StatusBarSettingsStore.resetAll(ctx)
                }
            },
            content = {
                SettingsSwitchRow(
                    setting = StatusBarSettingsStore.showStatusBar,
                    title = stringResource(R.string.show_status_bar),
                    description = stringResource(R.string.show_status_bar_desc),
                ) {
                    scope.launch {
                        StatusBarSettingsStore.showStatusBar.set(ctx, it)
                    }
                }


                AnimatedVisibility(showStatusBar) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {

                        SettingsColorPicker(
                            settingObject = StatusBarSettingsStore.barBackgroundColor,
                            label = stringResource(R.string.status_bar_background),
                            defaultColor = Color.Transparent
                        )

                        SettingsColorPicker(
                            settingObject = StatusBarSettingsStore.barTextColor,
                            label = stringResource(R.string.status_bar_text_color),
                            defaultColor = MaterialTheme.colorScheme.primary
                        )

                        EditStatusBar()

//                        ExpandableSection(timeSectionState) {
//                            SettingsSwitchRow(
//                                setting = StatusBarSettingsStore.showTime,
//                                title = stringResource(R.string.show_time),
//                                description = stringResource(R.string.show_time_desc)
//                            )
//

//

//                        }
//
//                        ExpandableSection(dateSectionState) {
//                            SettingsSwitchRow(
//                                setting = StatusBarSettingsStore.showDate,
//                                title = stringResource(R.string.show_date),
//                                description = stringResource(R.string.show_date_desc)
//                            )
//
//                            CustomActionSelector(
//                                appsViewModel = appsViewModel,
//                                appLifecycleViewModel = appLifecycleViewModel,
//                                currentAction = dateAction,
//                                label = stringResource(R.string.date_action),
//                                nullText = stringResource(R.string.opens_calendar_app),
//                                enabled = showDate,
//                                switchEnabled = showDate,
//                                onToggle = {
//                                    scope.launch {
//                                        StatusBarSettingsStore.dateAction.reset(ctx)
//                                    }
//                                }
//                            ) {
//                                scope.launch {
//                                    StatusBarSettingsStore.dateAction.set(ctx, it)
//                                }
//                            }
//

//                        }


//                        ExpandableSection(displaySectionState) {

//
//                            SettingsSwitchRow(
//                                setting = StatusBarSettingsStore.showNextAlarm,
//                                title = stringResource(R.string.show_next_alarm),
//                                description = stringResource(R.string.show_next_alarm_desc)
//                            )
//
//                            SettingsSwitchRow(
//                                setting = StatusBarSettingsStore.showNotifications,
//                                title = stringResource(R.string.show_notifications),
//                                description = stringResource(R.string.show_notifications_desc)
//                            ) { enabled ->
//                                if (enabled && !DragonNotificationListenerService.isPermissionGranted(ctx)) {
//                                    DragonNotificationListenerService.openNotificationSettings(ctx)
//                                }
//                            }
//
//                            val showNotifications by StatusBarSettingsStore.showNotifications.asState()
//                            var hasNotificationPermission by remember {
//                                mutableStateOf(DragonNotificationListenerService.isPermissionGranted(ctx))
//                            }
//                            LaunchedEffect(showNotifications) {
//                                if (!showNotifications) return@LaunchedEffect
//                                while (isActive) {
//                                    hasNotificationPermission =
//                                        DragonNotificationListenerService.isPermissionGranted(ctx)
//                                    if (hasNotificationPermission) break
//                                    delay(2_000L)
//                                }
//                            }
//                            AnimatedVisibility(showNotifications && !hasNotificationPermission) {
//                                Text(
//                                    text = stringResource(R.string.grant_notification_access),
//                                    color = MaterialTheme.colorScheme.primary,
//                                    style = MaterialTheme.typography.labelMedium,
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .clickable {
//                                            DragonNotificationListenerService.openNotificationSettings(ctx)
//                                        }
//                                )
//                            }
//                            AnimatedVisibility(showNotifications) {
//                                SettingsSlider(
//                                    setting = StatusBarSettingsStore.maxNotificationIcons,
//                                    title = stringResource(R.string.max_notification_icons),
//                                    valueRange = 1..10,
//                                )
//                            }
//
//                            SettingsSwitchRow(
//                                setting = StatusBarSettingsStore.showBattery,
//                                title = stringResource(R.string.show_battery),
//                                description = stringResource(R.string.show_battery_desc)
//                            )
//
//                            SettingsSwitchRow(
//                                setting = StatusBarSettingsStore.showConnectivity,
//                                title = stringResource(R.string.show_connectivity),
//                                description = stringResource(R.string.show_connectivity_desc)
//                            )
//
//                            SettingsSwitchRow(
//                                setting = StatusBarSettingsStore.showBandwidth,
//                                title = stringResource(R.string.show_bandwidth),
//                                description = stringResource(R.string.show_bandwidth_desc)
//                            )
//                        }

                        ExpandableSection(paddingsSectionState) {
                            SettingsSlider(
                                setting = StatusBarSettingsStore.leftPadding,
                                title = stringResource(R.string.left_padding),
                                valueRange = 0..200,
                            )

                            SettingsSlider(
                                setting = StatusBarSettingsStore.rightPadding,
                                title = stringResource(R.string.right_padding),
                                valueRange = 0..200,
                            )
                            SettingsSlider(
                                setting = StatusBarSettingsStore.topPadding,
                                title = stringResource(R.string.top_padding),
                                valueRange = 0..200,
                            )
                            SettingsSlider(
                                setting = StatusBarSettingsStore.bottomPadding,
                                title = stringResource(R.string.bottom_padding),
                                valueRange = 0..200,
                            )
                        }
                    }
                }
            }
        )
    }
}
