@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.wellbeing

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.utils.hasUsageStatsPermission
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.components.DragonIconButton
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.resolveShape
import org.elnix.dragonlauncher.ui.components.settings.SettingsSlider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.dialogs.AppPickerDialog
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsItem
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader

@Composable
fun WellbeingTab(
    appsViewModel: AppsViewModel,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val icons by appsViewModel.icons.collectAsState()

    val socialMediaPauseEnabled by WellbeingSettingsStore.socialMediaPauseEnabled.flow(ctx)
        .collectAsState(initial = false)

    val guiltModeEnabled by WellbeingSettingsStore.guiltModeEnabled.flow(ctx)
        .collectAsState(initial = false)

    val pauseDuration by WellbeingSettingsStore.pauseDurationSeconds.flow(ctx)
        .collectAsState(initial = 10)

    val pausedApps by WellbeingSettingsStore.getPausedAppsFlow(ctx)
        .collectAsState(initial = emptySet())

    val gridSize by DrawerSettingsStore.gridSize.flow(ctx)
        .collectAsState(initial = 1)
    val showIcons by DrawerSettingsStore.showAppIconsInDrawer.flow(ctx)
        .collectAsState(initial = true)
    val showLabels by DrawerSettingsStore.showAppLabelInDrawer.flow(ctx)
        .collectAsState(initial = true)
    val iconsShape by DrawerSettingsStore.iconsShape.flow(ctx)
        .collectAsState(DrawerSettingsStore.iconsShape.default)

    val allApps by appsViewModel.allApps.collectAsState()

    var showAppPicker by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showOverlayPermissionDialog by remember { mutableStateOf(false) }

    val reminderEnabled by WellbeingSettingsStore.reminderEnabled.flow(ctx)
        .collectAsState(initial = false)
    val reminderInterval by WellbeingSettingsStore.reminderIntervalMinutes.flow(ctx)
        .collectAsState(initial = 5)
    val reminderMode by WellbeingSettingsStore.reminderMode.flow(ctx)
        .collectAsState(initial = "overlay")
    val returnToLauncherEnabled by WellbeingSettingsStore.returnToLauncherEnabled.flow(ctx)
        .collectAsState(initial = false)

    LaunchedEffect(reminderEnabled, reminderMode) {
        if (reminderEnabled && reminderMode == "overlay" && !Settings.canDrawOverlays(ctx)) {
            WellbeingSettingsStore.reminderEnabled.set(ctx, false)
            showOverlayPermissionDialog = true
        }
    }

    SettingsLazyHeader(
        title = stringResource(R.string.wellbeing),
        onBack = onBack,
        helpText = stringResource(R.string.wellbeing_help),
        resetTitle = stringResource(R.string.reset_default_settings),
        resetText = stringResource(R.string.reset_settings_in_this_tab),
        onReset = {
            scope.launch {
                WellbeingSettingsStore.resetAll(ctx)
                WellbeingSettingsStore.setPausedApps(ctx, emptySet())
            }
        }
    ) {
        item {
            WellbeingIntroCard(
                title = stringResource(R.string.wellbeing),
                description = stringResource(R.string.wellbeing_help),
                reminderEnabled = reminderEnabled,
                reminderModeLabel = if (reminderMode == "overlay") {
                    stringResource(R.string.reminder_mode_overlay)
                } else {
                    stringResource(R.string.reminder_mode_notification)
                }
            )
        }

        // Pause Screen
        item {
            SettingsSwitchRow(
                setting = WellbeingSettingsStore.socialMediaPauseEnabled,
                title = stringResource(R.string.social_media_pause),
                description = stringResource(R.string.social_media_pause_description)
            )
        }

        item {
            SwitchRow(
                state = guiltModeEnabled,
                text = stringResource(R.string.guilt_mode),
                subText = stringResource(R.string.guilt_mode_description),
                enabled = socialMediaPauseEnabled,
            ) { newValue ->
                if (newValue && !hasUsageStatsPermission(ctx)) {
                    showPermissionDialog = true
                } else {
                    scope.launch {
                        WellbeingSettingsStore.guiltModeEnabled.set(ctx, newValue)
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DragonShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, DragonShape)
                    .padding(16.dp)
            ) {
                SettingsSlider(
                    setting = WellbeingSettingsStore.pauseDurationSeconds,
                    title = stringResource(R.string.pause_duration),
                    description = stringResource(R.string.pause_duration_description, pauseDuration),
                    valueRange = 3..60,
                    allowTextEditValue = false,
                    showValue = false,
                    enabled = socialMediaPauseEnabled
                )
            }
        }

        // Reminders
        item {
            TextDivider(stringResource(R.string.reminder_mode_title))
        }

        item {
            SwitchRow(
                state = reminderEnabled,
                text = stringResource(R.string.reminder_mode_title),
                subText = stringResource(R.string.reminder_mode_description),
                enabled = socialMediaPauseEnabled,
            ) { newValue ->
                if (newValue && reminderMode == "overlay" && !Settings.canDrawOverlays(ctx)) {
                    showOverlayPermissionDialog = true
                } else {
                    scope.launch {
                        WellbeingSettingsStore.reminderEnabled.set(ctx, newValue)
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DragonShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, DragonShape)
                    .padding(16.dp)
            ) {
                SettingsSlider(
                    setting = WellbeingSettingsStore.reminderIntervalMinutes,
                    title = stringResource(R.string.reminder_interval),
                    description = stringResource(R.string.reminder_interval_description, reminderInterval),
                    valueRange = 1..30,
                    allowTextEditValue = false,
                    showValue = false,
                    enabled = socialMediaPauseEnabled && reminderEnabled
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsItem(
                    title = stringResource(R.string.reminder_mode_notification),
                    icon = Icons.Outlined.Notifications,
                    modifier = Modifier.weight(1f),
                    enabled = socialMediaPauseEnabled && reminderEnabled,
                    backgroundColor = if (reminderMode == "notification") {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ) {
                    scope.launch {
                        WellbeingSettingsStore.reminderMode.set(ctx, "notification")
                    }
                }

                SettingsItem(
                    title = stringResource(R.string.reminder_mode_overlay),
                    icon = Icons.Outlined.Layers,
                    modifier = Modifier.weight(1f),
                    enabled = socialMediaPauseEnabled && reminderEnabled,
                    backgroundColor = if (reminderMode == "overlay") {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ) {
                    if (!Settings.canDrawOverlays(ctx)) {
                        showOverlayPermissionDialog = true
                    } else {
                        scope.launch {
                            WellbeingSettingsStore.reminderMode.set(ctx, "overlay")
                        }
                    }
                }
            }
        }

        if (reminderMode == "overlay" && socialMediaPauseEnabled && reminderEnabled) {
            item {
                TextDivider(stringResource(R.string.popup_display_title))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DragonShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, DragonShape)
                        .padding(vertical = 4.dp)
                ) {
                    val popupShowSessionTime by WellbeingSettingsStore.popupShowSessionTime.flow(ctx)
                        .collectAsState(initial = true)
                    val popupShowTodayTime by WellbeingSettingsStore.popupShowTodayTime.flow(ctx)
                        .collectAsState(initial = true)
                    val popupShowRemainingTime by WellbeingSettingsStore.popupShowRemainingTime.flow(ctx)
                        .collectAsState(initial = true)

                    SwitchRow(
                        state = popupShowSessionTime,
                        text = stringResource(R.string.popup_show_session_time),
                        subText = stringResource(R.string.popup_show_session_time_desc),
                        enabled = true
                    ) { newValue ->
                        scope.launch {
                            WellbeingSettingsStore.popupShowSessionTime.set(ctx, newValue)
                        }
                    }

                    SwitchRow(
                        state = popupShowTodayTime,
                        text = stringResource(R.string.popup_show_today_time),
                        subText = stringResource(R.string.popup_show_today_time_desc),
                        enabled = true
                    ) { newValue ->
                        scope.launch {
                            WellbeingSettingsStore.popupShowTodayTime.set(ctx, newValue)
                        }
                    }

                    SwitchRow(
                        state = popupShowRemainingTime,
                        text = stringResource(R.string.popup_show_remaining_time),
                        subText = stringResource(R.string.popup_show_remaining_time_desc),
                        enabled = true
                    ) { newValue ->
                        scope.launch {
                            WellbeingSettingsStore.popupShowRemainingTime.set(ctx, newValue)
                        }
                    }
                }
            }
        }

        // Return to Launcher
        item {
            TextDivider(stringResource(R.string.return_to_launcher_title))
        }

        item {
            SwitchRow(
                state = returnToLauncherEnabled,
                text = stringResource(R.string.return_to_launcher_title),
                subText = stringResource(R.string.return_to_launcher_description),
                enabled = socialMediaPauseEnabled,
            ) { newValue ->
                scope.launch {
                    WellbeingSettingsStore.returnToLauncherEnabled.set(ctx, newValue)
                }
            }
        }

        // Paused Apps
        item {
            val countText = if (pausedApps.isNotEmpty()) {
                "${stringResource(R.string.paused_apps)} (${pausedApps.size})"
            } else {
                stringResource(R.string.paused_apps)
            }
            TextDivider(countText)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsItem(
                    title = stringResource(R.string.add_app),
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f),
                    enabled = socialMediaPauseEnabled
                ) {
                    showAppPicker = true
                }

                SettingsItem(
                    title = stringResource(R.string.add_social_media),
                    icon = Icons.Default.Apps,
                    modifier = Modifier.weight(1f),
                    enabled = socialMediaPauseEnabled
                ) {
                    scope.launch {
                        val installedPackages = allApps.map { it.packageName }.toSet()
                        val socialApps = WellbeingSettingsStore.knownSocialMediaApps.filter {
                            it in installedPackages
                        }
                        WellbeingSettingsStore.setPausedApps(ctx, pausedApps + socialApps)
                    }
                }
            }
        }

        if (pausedApps.isNotEmpty()) {
            item {
                SettingsItem(
                    title = stringResource(R.string.clear_all),
                    icon = Icons.Default.Clear,
                    enabled = socialMediaPauseEnabled
                ) {
                    scope.launch {
                        WellbeingSettingsStore.setPausedApps(ctx, emptySet())
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(pausedApps.toList()) { packageName ->
                val app = allApps.find { it.packageName == packageName }
                val appName = app?.name ?: packageName

                PausedAppItem(
                    appName = appName,
                    packageName = packageName,
                    appIcon = icons[app?.packageName],
                    iconsShape = iconsShape,
                    onRemove = {
                        scope.launch {
                            WellbeingSettingsStore.removePausedApp(ctx, packageName, pausedApps)
                        }
                    }
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DragonShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, DragonShape)
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = stringResource(R.string.no_paused_apps),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (showAppPicker) {
        AppPickerDialog(
            appsViewModel = appsViewModel,
            gridSize = gridSize,
            iconShape = iconsShape,
            showIcons = showIcons,
            showLabels = showLabels,
            onDismiss = { showAppPicker = false }
        ) {
            scope.launch {
                WellbeingSettingsStore.addPausedApp(ctx, it.packageName, pausedApps)
            }
            showAppPicker = false
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(stringResource(R.string.usage_permission_required)) },
            text = { Text(stringResource(R.string.usage_permission_description)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        ctx.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }
                ) {
                    Text(stringResource(R.string.open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showOverlayPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showOverlayPermissionDialog = false },
            title = { Text(stringResource(R.string.overlay_permission_required)) },
            text = { Text(stringResource(R.string.overlay_permission_description)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOverlayPermissionDialog = false
                        ctx.startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${ctx.packageName}")
                            ).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                    }
                ) {
                    Text(stringResource(R.string.open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showOverlayPermissionDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun WellbeingIntroCard(
    title: String,
    description: String,
    reminderEnabled: Boolean,
    reminderModeLabel: String
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DragonShape)
            .background(gradient)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, DragonShape)
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (reminderEnabled) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = reminderModeLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (reminderEnabled) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PausedAppItem(
    appName: String,
    packageName: String,
    appIcon: ImageBitmap?,
    iconsShape: IconShape,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DragonShape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (appIcon != null) {
                Image(
                    bitmap = appIcon,
                    contentDescription = appName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(resolveShape(iconsShape)),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(resolveShape(iconsShape))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        DragonIconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
