package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.foundation.clickable
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
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.TextDivider
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.statusbar.StatusBar
import org.elnix.dragonlauncher.utils.colors.AppObjectsColors
import org.elnix.dragonlauncher.utils.isValidDateFormat
import org.elnix.dragonlauncher.utils.isValidTimeFormat
import org.elnix.dragonlauncher.utils.openUrl

@Composable
fun StatusBarTab(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val systemInsets = WindowInsets.systemBars.asPaddingValues()

    val isRealFullscreen = systemInsets.calculateTopPadding() == 0.dp

    val showStatusBar by StatusBarSettingsStore.getShowStatusBar(ctx)
        .collectAsState(initial = true)

    val statusBarBackground by StatusBarSettingsStore.getBarBackgroundColor(ctx)
        .collectAsState(initial = Color.Transparent)

    val statusBarText by StatusBarSettingsStore.getBarTextColor(ctx)
        .collectAsState(initial = MaterialTheme.colorScheme.onBackground)

    val showTime by StatusBarSettingsStore.getShowTime(ctx)
        .collectAsState(initial = true)

    val showDate by StatusBarSettingsStore.getShowDate(ctx)
        .collectAsState(initial = false)

    val timeFormatter by StatusBarSettingsStore.getTimeFormatter(ctx)
        .collectAsState("HH:mm:ss")

    val dateFormatter by StatusBarSettingsStore.getDateFormatter(ctx)
        .collectAsState("MMM dd")

    val showNotifications by StatusBarSettingsStore.getShowNotifications(ctx)
        .collectAsState(initial = false)

    val showBattery by StatusBarSettingsStore.getShowBattery(ctx)
        .collectAsState(initial = true)

    val showConnectivity by StatusBarSettingsStore.getShowConnectivity(ctx)
        .collectAsState(initial = false)

    val showNextAlarm by StatusBarSettingsStore.getShowNextAlarm(ctx)
        .collectAsState(true)



    Column{

        if (showStatusBar && isRealFullscreen) {
            StatusBar(
                backgroundColor = statusBarBackground,
                textColor = statusBarText,
                showTime = showTime,
                showDate = showDate,
                timeFormatter = timeFormatter,
                dateFormatter = dateFormatter,
                showNotifications = showNotifications,
                showBattery = showBattery,
                showConnectivity = showConnectivity,
                showNextAlarm = showNextAlarm
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
                    modifier = Modifier.clickable { ctx.openUrl("https://discord.gg/XXKXQeXpvF") },
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
                        StatusBarSettingsStore.setShowStatusBar(ctx, it)
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
                        StatusBarSettingsStore.setBarBackgroundColor(ctx, it)
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
                        StatusBarSettingsStore.setBarTextColor(ctx, it)
                    }
                }
            }

            item {
                SwitchRow(
                    state = showTime,
                    text = stringResource(R.string.show_time)
                ) {
                    scope.launch { StatusBarSettingsStore.setShowTime(ctx, it) }
                }
            }

            item {
                Column {
                    Text(
                        text = stringResource(R.string.time_format_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.time_format_examples),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = timeFormatter,
                        onValueChange = { newValue ->
                            scope.launch {
                                StatusBarSettingsStore.setTimeFormatter(ctx, newValue)
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
                                        StatusBarSettingsStore.setTimeFormatter(ctx, "HH:mm:ss")
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
                    scope.launch { StatusBarSettingsStore.setShowDate(ctx, it) }
                }
            }


            item {
                Column {
                    Text(
                        text = stringResource(R.string.date_format_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.date_format_examples),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = dateFormatter,
                        onValueChange = { newValue ->
                            scope.launch {
                                StatusBarSettingsStore.setDateFormatter(ctx, newValue)
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
                                        StatusBarSettingsStore.setDateFormatter(ctx, "MMM dd")
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
                    scope.launch { StatusBarSettingsStore.setShowNextAlarm(ctx, it) }
                }
            }

            item {
                SwitchRow(
                    state = showNotifications,
                    enabled = false,
                    text = stringResource(R.string.show_notifications),
                    subText = "Not implemented"
                ) {
                    scope.launch { StatusBarSettingsStore.setShowNotifications(ctx, it) }
                }
            }

            item {
                SwitchRow(
                    state = showBattery,
                    text = stringResource(R.string.show_battery)
                ) {
                    scope.launch { StatusBarSettingsStore.setShowBattery(ctx, it) }
                }
            }

            item {
                SwitchRow(
                    state = showConnectivity,
                    text = stringResource(R.string.show_connectivity),
                    subText = "Kinda buggy RN, so working so well, but you can try"
                ) {
                    scope.launch { StatusBarSettingsStore.setShowConnectivity(ctx, it) }
                }
            }
        }
    }
}
