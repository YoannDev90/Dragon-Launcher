package org.elnix.dragonlauncher.ui.settings.customization

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.discordInviteLink
import org.elnix.dragonlauncher.common.utils.isValidDateFormat
import org.elnix.dragonlauncher.common.utils.isValidTimeFormat
import org.elnix.dragonlauncher.common.utils.openUrl
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.helpers.CustomActionSelector
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.TextDivider
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.statusbar.StatusBar

@Composable
fun StatusBarTab(
    appsViewModel: AppsViewModel,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val systemInsets = WindowInsets.systemBars.asPaddingValues()

    val isRealFullscreen = systemInsets.calculateTopPadding() == 0.dp

    val showStatusBar by StatusBarSettingsStore.showStatusBar.flow(ctx)
        .collectAsState(initial = false)

    val statusBarBackground by StatusBarSettingsStore.barBackgroundColor.flow(ctx)
        .collectAsState(initial = Color.Transparent)

    val statusBarText by StatusBarSettingsStore.barTextColor.flow(ctx)
        .collectAsState(initial = MaterialTheme.colorScheme.onBackground)

    val showTime by StatusBarSettingsStore.showTime.flow(ctx)
        .collectAsState(initial = false)

    val showDate by StatusBarSettingsStore.showDate.flow(ctx)
        .collectAsState(initial = false)

    val timeFormatter by StatusBarSettingsStore.timeFormatter.flow(ctx)
        .collectAsState("HH:mm:ss")

    val dateFormatter by StatusBarSettingsStore.dateFormater.flow(ctx)
        .collectAsState("MMM dd")

    val showNotifications by StatusBarSettingsStore.showNotifications.flow(ctx)
        .collectAsState(initial = false)

    val showBattery by StatusBarSettingsStore.showBattery.flow(ctx)
        .collectAsState(initial = false)

    val showConnectivity by StatusBarSettingsStore.showConnectivity.flow(ctx)
        .collectAsState(initial = false)

    val showNextAlarm by StatusBarSettingsStore.showNextAlarm.flow(ctx)
        .collectAsState(false)

    val leftPadding by StatusBarSettingsStore.leftPadding.flow(ctx)
        .collectAsState(initial = 5)

    val rightPadding by StatusBarSettingsStore.rightPadding.flow(ctx)
        .collectAsState(initial = 5)

    val topPadding by StatusBarSettingsStore.topPadding.flow(ctx)
        .collectAsState(initial = 2)

    val bottomPadding by StatusBarSettingsStore.bottomPadding.flow(ctx)
        .collectAsState(initial = 2)

    val clockAction by StatusBarSettingsStore.clockAction.flow(ctx)
        .collectAsState(null)

    val dateAction by StatusBarSettingsStore.dateAction.flow(ctx)
        .collectAsState(null)


    Column{

        if (showStatusBar && isRealFullscreen) {
            StatusBar(
                onDateAction = {},
                onClockAction = {}
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
                Text(
                    text = "For the little nerds that uses my app, I would like a lot of feedback on this status bar, the rule I fixed to me is that Dragon will never have any network access (btw to show the connectivity icons it's annoying). You can feedback me on github issues or tell me on discord",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Text(
                    text = "Github Issues",
                    modifier = Modifier.clickable { ctx.openUrl("https://github.com/Elnix90/Dragon-Launcher/issues/new") },
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = TextDecoration.Underline

                )
            }

            item {
                Text(
                    text = "Discord",
                    modifier = Modifier.clickable { ctx.openUrl(discordInviteLink) },
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = TextDecoration.Underline
                )
            }

            item {
                SwitchRow(
                    state = showStatusBar,
                    text = stringResource(R.string.show_status_bar)
                ) {
                    scope.launch {
                        StatusBarSettingsStore.showStatusBar.set(ctx, it)
                    }
                }
            }

            item { TextDivider(stringResource(R.string.display)) }

            item {
                ColorPickerRow(
                    label = stringResource(R.string.status_bar_background),
                    defaultColor = Color.Transparent,
                    currentColor = statusBarBackground,
                ) {
                    scope.launch {
                        StatusBarSettingsStore.barBackgroundColor.set(ctx, it)
                    }
                }
            }

            item {
                ColorPickerRow(
                    label = stringResource(R.string.status_bar_text_color),
                    defaultColor = MaterialTheme.colorScheme.onBackground,
                    currentColor = statusBarText,
                ) {
                    scope.launch {
                        StatusBarSettingsStore.barTextColor.set(ctx, it)
                    }
                }
            }

            item {
                SwitchRow(
                    state = showTime,
                    text = stringResource(R.string.show_time)
                ) {
                    scope.launch { StatusBarSettingsStore.showTime.set(ctx, it) }
                }
            }

            item {
                CustomActionSelector(
                    appsViewModel = appsViewModel,
                    currentAction = clockAction,
                    label = stringResource(R.string.clock_action),
                    nullText = stringResource(R.string.opens_alarmclock_app),
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
            }

            item {
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


            item {
                SwitchRow(
                    state = showDate,
                    text = stringResource(R.string.show_date)
                ) {
                    scope.launch { StatusBarSettingsStore.showDate.set(ctx, it) }
                }
            }

            item {
                CustomActionSelector(
                    appsViewModel = appsViewModel,
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
            }


            item {
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

            item {
                SwitchRow(
                    state = showNextAlarm,
                    text = stringResource(R.string.show_next_alarm),
                    subText = "Requires exact alarm permission"
                ) {
                    scope.launch { StatusBarSettingsStore.showNextAlarm.set(ctx, it) }
                }
            }

            item {
                SwitchRow(
                    state = showNotifications,
                    enabled = false,
                    text = stringResource(R.string.show_notifications),
                    subText = "Not implemented"
                ) {
                    scope.launch { StatusBarSettingsStore.showNotifications.set(ctx, it) }
                }
            }

            item {
                SwitchRow(
                    state = showBattery,
                    text = stringResource(R.string.show_battery)
                ) {
                    scope.launch { StatusBarSettingsStore.showBattery.set(ctx, it) }
                }
            }

            item {
                SwitchRow(
                    state = showConnectivity,
                    text = stringResource(R.string.show_connectivity),
                    subText = "Kinda buggy RN, so working so well, but you can try"
                ) {
                    scope.launch { StatusBarSettingsStore.showConnectivity.set(ctx, it) }
                }
            }

            item { TextDivider(stringResource(R.string.padding)) }


            item {
                SliderWithLabel(
                    label = stringResource(R.string.left_padding),
                    value = leftPadding,
                    showValue = true,
                    valueRange = 0..200,
                    color = MaterialTheme.colorScheme.primary,
                    onReset = { scope.launch { StatusBarSettingsStore.leftPadding.reset(ctx) } }
                ) {
                    scope.launch{ StatusBarSettingsStore.leftPadding.set(ctx, it ) }
                }
            }

            item {
                SliderWithLabel(
                    label = stringResource(R.string.right_padding),
                    value = rightPadding,
                    showValue = true,
                    valueRange = 0..200,
                    color = MaterialTheme.colorScheme.primary,
                    onReset = { scope.launch { StatusBarSettingsStore.rightPadding.reset(ctx) } }
                ) {
                    scope.launch{ StatusBarSettingsStore.rightPadding.set(ctx, it ) }
                }
            }

            item {
                SliderWithLabel(
                    label = stringResource(R.string.top_padding),
                    value = topPadding,
                    showValue = true,
                    valueRange = 0..200,
                    color = MaterialTheme.colorScheme.primary,
                    onReset = { scope.launch { StatusBarSettingsStore.topPadding.reset(ctx) } }
                ) {
                    scope.launch{ StatusBarSettingsStore.topPadding.set(ctx, it ) }
                }
            }

            item {
                SliderWithLabel(
                    label = stringResource(R.string.bottom_padding),
                    value = bottomPadding,
                    showValue = true,
                    valueRange = 0..200,
                    color = MaterialTheme.colorScheme.primary,
                    onReset = { scope.launch { StatusBarSettingsStore.bottomPadding.reset(ctx) } }
                ) {
                    scope.launch{ StatusBarSettingsStore.bottomPadding.set(ctx, it ) }
                }
            }
        }
    }
}
