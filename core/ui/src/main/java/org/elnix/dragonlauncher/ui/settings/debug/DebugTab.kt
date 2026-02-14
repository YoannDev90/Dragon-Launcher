@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.debug

import android.os.Build
import android.system.Os.kill
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.serializables.dummySwipePoint
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.detectSystemLauncher
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.services.SystemControl
import org.elnix.dragonlauncher.services.SystemControl.activateDeviceAdmin
import org.elnix.dragonlauncher.services.SystemControl.isDeviceAdminActive
import org.elnix.dragonlauncher.settings.allStores
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.dialogs.IconEditorDialog
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsItem
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.wellbeing.OverlayReminderService

@Composable
fun DebugTab(
    navController: NavController,
    appsViewModel: AppsViewModel,
    onShowWelcome: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val isDebugModeEnabled by DebugSettingsStore.debugEnabled.asState()

    val debugInfos by DebugSettingsStore.debugInfos.asState()
    val settingsDebugInfos by DebugSettingsStore.settingsDebugInfo.asState()
    val widgetsDebugInfos by DebugSettingsStore.widgetsDebugInfo.asState()
    val workspaceDebugInfos by DebugSettingsStore.workspacesDebugInfo.asState()

    val useAccessibilityInsteadOfContextToExpandActionPanel by DebugSettingsStore
        .useAccessibilityInsteadOfContextToExpandActionPanel.asState()

    val hasInitialized by PrivateSettingsStore.hasInitialized.asState()
    val showSetDefaultLauncherBanner by PrivateSettingsStore.showSetDefaultLauncherBanner.asState()

    val forceAppLanguageSelector by DebugSettingsStore.forceAppLanguageSelector.asState()

    val forceAppWidgetsSelector by DebugSettingsStore.forceAppWidgetsSelector.asState()

    val doNotRemindMeAgainNotificationsBehavior by PrivateSettingsStore.showMethodAsking.asState()

    val systemLauncherPackageName by DebugSettingsStore.systemLauncherPackageName.asState()
    val autoRaiseDragonOnSystemLauncher by DebugSettingsStore.autoRaiseDragonOnSystemLauncher.asState()

    var pendingSystemLauncher by remember { mutableStateOf<String?>(null) }

    var showEditAppOverrides by remember { mutableStateOf(false) }



    SettingsLazyHeader(
        title = stringResource(R.string.debug),
        onBack = onBack,
        helpText = "Debug, too busy to make a translated explanation",
        onReset = null,
        resetText = null
    ) {

        item {
            SwitchRow(
                state = isDebugModeEnabled,
                text = "Activate Debug Mode",
                defaultValue = true
            ) {
                scope.launch {
                    DebugSettingsStore.debugEnabled.set(ctx, it)
                }
                navController.popBackStack()
            }
        }

        item { TextDivider("Debug things") }

        item {
            SettingsItem(
                title = "Logs",
                icon = Icons.AutoMirrored.Filled.Notes
            ) {
                navController.navigate(SETTINGS.LOGS)
            }
        }

        item {
            SettingsItem(
                title = "Settings debug json",
                icon = Icons.Default.Settings
            ) {
                navController.navigate(SETTINGS.SETTINGS_JSON)
            }
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    kill(9,9)
                }
            ) {
                Text("☠\uFE0F Kill app")
            }
        }

        item {
            SwitchRow(
                state = debugInfos,
                text = "Show debug infos",
                defaultValue = false
            ) {
                scope.launch {
                    DebugSettingsStore.debugInfos.set(ctx, it)
                }
            }
        }

        item {
            SwitchRow(
                state = settingsDebugInfos,
                text = "Show debug infos in settings page",
                defaultValue = false
            ) {
                scope.launch {
                    DebugSettingsStore.settingsDebugInfo.set(ctx, it)
                }
            }
        }

        item {
            SwitchRow(
                state = widgetsDebugInfos,
                text = "Show debug infos in widgets page",
                defaultValue = false
            ) {
                scope.launch {
                    DebugSettingsStore.widgetsDebugInfo.set(ctx, it)
                }
            }
        }

        item {
            SwitchRow(
                state = workspaceDebugInfos,
                text = "Show debug infos in workspace page",
                defaultValue = false
            ) {
                scope.launch {
                    DebugSettingsStore.workspacesDebugInfo.set(ctx, it)
                }
            }
        }

        item {
            SettingsSwitchRow(
                setting = DebugSettingsStore.privateSpaceDebugInfo,
                title = "Private space debug info",
                description = "Display private space state debug infos on top of everything"
            )
        }

        item {
            Button(
                onClick = { onShowWelcome() },
                colors = AppObjectsColors.buttonColors(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Show welcome screen",
                )
            }
        }


        item {
            Button(
                onClick = {
                    scope.launch { PrivateSettingsStore.lastSeenVersionCode.set(ctx, 0) }
                },
                colors = AppObjectsColors.buttonColors(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Show what's new sheet",
                )
            }
        }

        item { TextDivider("Wellbeing tests") }



        item {
            Button(
                onClick = {
                    if (!android.provider.Settings.canDrawOverlays(ctx)) {
                        ctx.showToast("Overlay permission not granted")
                        return@Button
                    }
                    OverlayReminderService.show(ctx, "TikTok", "15 min", "42 min", "10 min", true, "reminder")
                },
                colors = AppObjectsColors.buttonColors(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Test: Reminder overlay popup")
            }
        }

        item {
            Button(
                onClick = {
                    if (!android.provider.Settings.canDrawOverlays(ctx)) {
                        ctx.showToast("Overlay permission not granted")
                        return@Button
                    }
                    OverlayReminderService.show(ctx, "TikTok", "25 min", "58 min", "5 min", true, "time_warning")
                },
                colors = AppObjectsColors.buttonColors(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Test: Time almost up overlay")
            }
        }

        item {
            SwitchRow(
                state = hasInitialized,
                text = "Has initialized"
            ) {
                scope.launch {
                    PrivateSettingsStore.hasInitialized.set(ctx, it)
                }
            }
        }

        item {
            SwitchRow(
                state = !showSetDefaultLauncherBanner,
                text = "Hide set default launcher banner",
                defaultValue = true
            ) {
                scope.launch {
                    PrivateSettingsStore.showSetDefaultLauncherBanner.set(ctx, !it)
                }
            }
        }

        item {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Text(
                    text = "Check this to force the app's language selector instead of the android's one",
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Text(
                    text = "Since you're under android 13, or code name TIRAMISU you can't use the android language selector and you're blocked with the app custom one.",
                    color = MaterialTheme.colorScheme.onBackground
                )

            }

            SwitchRow(
                state = forceAppLanguageSelector,
                text = "Force app language selector",
                enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ) { scope.launch { DebugSettingsStore.forceAppLanguageSelector.set(ctx, it) } }
        }

        item {
            SwitchRow(
                state = forceAppWidgetsSelector,
                text = "Force app widgets selector"
            ) {
                scope.launch {
                    DebugSettingsStore.forceAppWidgetsSelector.set(
                        ctx,
                        it
                    )
                }
            }
        }

        item {
            SwitchRow(
                state = useAccessibilityInsteadOfContextToExpandActionPanel,
                text = "useAccessibilityInsteadOfContextToExpandActionPanel"
            ) {
                scope.launch {
                    DebugSettingsStore.useAccessibilityInsteadOfContextToExpandActionPanel.set(
                        ctx,
                        it
                    )
                }
            }
        }

        item {
            SwitchRow(
                state = doNotRemindMeAgainNotificationsBehavior,
                text = "Ask me each times for the notifications / quick settings behavior"
            ) {
                scope.launch {
                    PrivateSettingsStore.showMethodAsking.set(ctx, it)
                }
            }
        }

        item {
            TextDivider(
                text = "Reset",
                lineColor = MaterialTheme.colorScheme.error,
                textColor = MaterialTheme.colorScheme.error,
            )
        }

        items(allStores.entries.toList()) { entry ->
            val settingsStore = entry.value

            OutlinedButton(
                onClick = { scope.launch { settingsStore.resetAll(ctx) } },
                colors = AppObjectsColors.cancelButtonColors(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Reset ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            ),
                        ) {
                            append(settingsStore.name)
                        }
                        append(" SettingsStore")
                    }
                )
            }
        }

        item {
            TextButton(
                onClick = { SystemControl.openServiceSettings((ctx)) }
            ) {
                Text("Open Service settings")
            }
            ActivateDeviceAdminButton()

        }


        item {
            SwitchRow(
                state = autoRaiseDragonOnSystemLauncher,
                text = "Auto launch Dragon on system launcher (needs accessibility enabled)",
            ) {
                scope.launch {
                    DebugSettingsStore.autoRaiseDragonOnSystemLauncher.set(ctx, it)
                }
            }
        }


        item {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Button(
                        onClick = {
                            pendingSystemLauncher = detectSystemLauncher(ctx)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Detect System launcher")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                DebugSettingsStore.systemLauncherPackageName.set(
                                    ctx,
                                    pendingSystemLauncher
                                )
                            }
                        },
                        enabled = pendingSystemLauncher != null
                    ) {
                        Text("Set")
                    }
                }

                if (pendingSystemLauncher != null) {
                    Text(
                        buildAnnotatedString {
                            append("Your system launcher: ")
                            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append(pendingSystemLauncher)
                            }
                        }
                    )
                } else {
                    Text("No system launcher detected")
                }
            }
        }
        item {
            OutlinedTextField(
                label = {
                    Text(
                        text = "Your system launcher package name",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                value = systemLauncherPackageName,
                onValueChange = { newValue ->
                    scope.launch {
                        DebugSettingsStore.systemLauncherPackageName.set(ctx, newValue)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = AppObjectsColors.outlinedTextFieldColors()
            )
        }

        item {
            TextButton(
                onClick = {
                    showEditAppOverrides = true
                }
            ) {
                Text(
                    text = "Edit ALL app overrides \uD83D\uDE08",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
    if (showEditAppOverrides) {
        IconEditorDialog(
            appsViewModel = appsViewModel,
            point = dummySwipePoint(),
            onDismiss = { showEditAppOverrides = false }
        ) { newIcon ->
            appsViewModel.applyIconToApps(
                icon = newIcon
            )
            showEditAppOverrides = false
        }
    }
}


@Composable
fun ActivateDeviceAdminButton() {
    val ctx = LocalContext.current
    val isActive = remember { mutableStateOf(isDeviceAdminActive(ctx)) }

    TextButton(
        onClick = {
            ctx.logD("Compose", "Button clicked - context: ${ctx.packageName}")
            activateDeviceAdmin(ctx)
            isActive.value = isDeviceAdminActive(ctx)
        }
    ) {
        Text(
            if (isActive.value) "Device Admin ✓ Active"
            else "Activate Device Admin"
        )
    }
}
