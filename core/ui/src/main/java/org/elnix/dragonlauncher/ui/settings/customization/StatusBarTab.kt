package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.isValidDateFormat
import org.elnix.dragonlauncher.common.utils.isValidTimeFormat
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.services.DragonNotificationListenerService
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.ExpandableSection
import org.elnix.dragonlauncher.ui.components.settings.SettingsColorPicker
import org.elnix.dragonlauncher.ui.components.settings.SettingsSlider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.helpers.CustomActionSelector
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.statusbar.StatusBar

@Composable
fun StatusBarTab(
    appsViewModel: AppsViewModel,
    appLifecycleViewModel: AppLifecycleViewModel,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val systemInsets = WindowInsets.systemBars.asPaddingValues()
    val isRealFullscreen = systemInsets.calculateTopPadding() == 0.dp

    val showStatusBar by StatusBarSettingsStore.showStatusBar.asState()
    val showTime by StatusBarSettingsStore.showTime.asState()
    val showDate by StatusBarSettingsStore.showDate.asState()
    val timeFormatter by StatusBarSettingsStore.timeFormatter.asState()
    val dateFormatter by StatusBarSettingsStore.dateFormater.asState()
    val clockAction by StatusBarSettingsStore.clockAction.asState()
    val dateAction by StatusBarSettingsStore.dateAction.asState()

    var timeSectionExpanded by remember { mutableStateOf(false) }
    var dateSectionExpanded by remember { mutableStateOf(false) }
    var displaySectionExpanded by remember { mutableStateOf(false) }
    var paddingsSectionExpanded by remember { mutableStateOf(false) }

    Column {
        AnimatedVisibility(showStatusBar && isRealFullscreen) {
            StatusBar(
                onDateAction = null,
                onClockAction = null
            )
        }

        SettingsLazyHeader(
            title = stringResource(R.string.status_bar),
            onBack = onBack,
            helpText = stringResource(R.string.status_bar_tab_text),
            onReset = {
                scope.launch {
                    StatusBarSettingsStore.resetAll(ctx)
                }
            }
        ) {
            item {
                SettingsSwitchRow(
                    setting = StatusBarSettingsStore.showStatusBar,
                    title = stringResource(R.string.show_status_bar),
                    description = stringResource(R.string.show_status_bar_desc),
                ) {
                    scope.launch {
                        StatusBarSettingsStore.showStatusBar.set(ctx, it)
                    }
                }
            }

            item {
                AnimatedVisibility(showStatusBar) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ExpandableSection(
                            expanded = { timeSectionExpanded },
                            title = stringResource(R.string.time_display_settings),
                            onExpand = { timeSectionExpanded = !timeSectionExpanded }
                        ) {
                            SettingsSwitchRow(
                                setting = StatusBarSettingsStore.showTime,
                                title = stringResource(R.string.show_time),
                                description = stringResource(R.string.show_time_desc)
                            )

                            CustomActionSelector(
                                appsViewModel = appsViewModel,
                                appLifecycleViewModel = appLifecycleViewModel,
                                currentAction = clockAction,
                                label = stringResource(R.string.clock_action),
                                nullText = stringResource(R.string.opens_alarm_clock_app),
                                enabled = showTime,
                                switchEnabled = showTime,
                                onToggle = {
                                    scope.launch {
                                        StatusBarSettingsStore.clockAction.set(ctx, null)
                                    }
                                }
                            ) {
                                scope.launch {
                                    StatusBarSettingsStore.clockAction.set(ctx, it)
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    text = stringResource(R.string.time_format_examples),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                OutlinedTextField(
                                    enabled = showTime,
                                    label = { Text(stringResource(R.string.time_format_title)) },
                                    value = timeFormatter,
                                    onValueChange = { newValue ->
                                        scope.launch {
                                            StatusBarSettingsStore.timeFormatter.set(ctx, newValue)
                                        }
                                    },
                                    singleLine = true,
                                    isError = !isValidTimeFormat(timeFormatter),
                                    supportingText = if (!isValidTimeFormat(timeFormatter)) {
                                        { Text(stringResource(R.string.invalid_format)) }
                                    } else null,
                                    placeholder = { Text("HH:mm:ss") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Restore,
                                            contentDescription = stringResource(R.string.reset),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable {
                                                scope.launch {
                                                    StatusBarSettingsStore.timeFormatter.reset(ctx)
                                                }
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = AppObjectsColors.outlinedTextFieldColors()
                                )
                            }
                        }

                        ExpandableSection(
                            expanded = { dateSectionExpanded },
                            title = stringResource(R.string.date_display_settings),
                            onExpand = { dateSectionExpanded = !dateSectionExpanded }
                        ) {
                            SettingsSwitchRow(
                                setting = StatusBarSettingsStore.showDate,
                                title = stringResource(R.string.show_date),
                                description = stringResource(R.string.show_date_desc)
                            )

                            CustomActionSelector(
                                appsViewModel = appsViewModel,
                                appLifecycleViewModel = appLifecycleViewModel,
                                currentAction = dateAction,
                                label = stringResource(R.string.date_action),
                                nullText = stringResource(R.string.opens_calendar_app),
                                enabled = showDate,
                                switchEnabled = showDate,
                                onToggle = {
                                    scope.launch {
                                        StatusBarSettingsStore.dateAction.reset(ctx)
                                    }
                                }
                            ) {
                                scope.launch {
                                    StatusBarSettingsStore.dateAction.set(ctx, it)
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    text = stringResource(R.string.date_format_examples),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                OutlinedTextField(
                                    enabled = showDate,
                                    label = {
                                        Text(stringResource(R.string.date_format_title))
                                    },
                                    value = dateFormatter,
                                    onValueChange = { newValue ->
                                        scope.launch {
                                            StatusBarSettingsStore.dateFormater.set(ctx, newValue)
                                        }
                                    },
                                    singleLine = true,
                                    isError = !isValidDateFormat(dateFormatter),
                                    supportingText = if (!isValidDateFormat(dateFormatter)) {
                                        { Text(stringResource(R.string.invalid_format)) }
                                    } else null,
                                    placeholder = { Text("MMM dd") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Restore,
                                            contentDescription = stringResource(R.string.reset),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable {
                                                scope.launch {
                                                    StatusBarSettingsStore.dateFormater.reset(ctx)
                                                }
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = AppObjectsColors.outlinedTextFieldColors()
                                )
                            }
                        }


                        ExpandableSection(
                            expanded = { displaySectionExpanded },
                            title = stringResource(R.string.display_options_settings),
                            onExpand = { displaySectionExpanded = !displaySectionExpanded }
                        ) {
                            SettingsColorPicker(
                                settingObject = StatusBarSettingsStore.barBackgroundColor,
                                label = stringResource(R.string.status_bar_background),
                                defaultColor = Color.Transparent
                            )

                            SettingsColorPicker(
                                settingObject = StatusBarSettingsStore.barTextColor,
                                label = stringResource(R.string.status_bar_text_color),
                                defaultColor = Color.White
                            )

                            SettingsSwitchRow(
                                setting = StatusBarSettingsStore.showNextAlarm,
                                title = stringResource(R.string.show_next_alarm),
                                description = stringResource(R.string.show_next_alarm_desc)
                            )

                            SettingsSwitchRow(
                                setting = StatusBarSettingsStore.showNotifications,
                                title = stringResource(R.string.show_notifications),
                                description = stringResource(R.string.show_notifications_desc)
                            )

                            val showNotifications by StatusBarSettingsStore.showNotifications.asState()
                            var hasNotificationPermission by remember {
                                mutableStateOf(DragonNotificationListenerService.isPermissionGranted(ctx))
                            }
                            LaunchedEffect(showNotifications) {
                                if (!showNotifications) return@LaunchedEffect
                                while (isActive) {
                                    hasNotificationPermission =
                                        DragonNotificationListenerService.isPermissionGranted(ctx)
                                    if (hasNotificationPermission) break
                                    delay(2_000L)
                                }
                            }
                            AnimatedVisibility(showNotifications && !hasNotificationPermission) {
                                Text(
                                    text = stringResource(R.string.grant_notification_access),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            DragonNotificationListenerService.openNotificationSettings(ctx)
                                        }
                                )
                            }
                            AnimatedVisibility(showNotifications) {
                                SettingsSlider(
                                    setting = StatusBarSettingsStore.maxNotificationIcons,
                                    title = stringResource(R.string.max_notification_icons),
                                    valueRange = 1..10,
                                )
                            }

                            SettingsSwitchRow(
                                setting = StatusBarSettingsStore.showBattery,
                                title = stringResource(R.string.show_battery),
                                description = stringResource(R.string.show_battery_desc)
                            )

                            SettingsSwitchRow(
                                setting = StatusBarSettingsStore.showConnectivity,
                                title = stringResource(R.string.show_connectivity),
                                description = stringResource(R.string.show_connectivity_desc)
                            )

                            SettingsSwitchRow(
                                setting = StatusBarSettingsStore.showBandwidth,
                                title = stringResource(R.string.show_bandwidth),
                                description = stringResource(R.string.show_bandwidth_desc)
                            )
                        }

                        ExpandableSection(
                            expanded = { paddingsSectionExpanded },
                            title = stringResource(R.string.padding),
                            onExpand = { paddingsSectionExpanded = !paddingsSectionExpanded }
                        ) {
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
        }
    }
}
