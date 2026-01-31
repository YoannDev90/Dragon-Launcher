package org.elnix.dragonlauncher.ui.settings.wellbeing

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsItem
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader

@Composable
fun WellbeingTab(
    appsViewModel: AppsViewModel,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val socialMediaPauseEnabled by WellbeingSettingsStore.socialMediaPauseEnabled.flow(ctx)
        .collectAsState(initial = false)

    val guiltModeEnabled by WellbeingSettingsStore.guiltModeEnabled.flow(ctx)
        .collectAsState(initial = false)

    val pauseDuration by WellbeingSettingsStore.pauseDurationSeconds.flow(ctx)
        .collectAsState(initial = 10)

    val pausedApps by WellbeingSettingsStore.getPausedAppsFlow(ctx)
        .collectAsState(initial = emptySet())

    val allApps by appsViewModel.allApps.collectAsState()

    var showAppSelector by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    BackHandler { onBack() }

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
            SettingsSwitchRow(
                setting = WellbeingSettingsStore.socialMediaPauseEnabled,
                title = stringResource(R.string.social_media_pause),
                description = stringResource(R.string.social_media_pause_description)
            )
        }

        // Guilt mode with usage permission check
        item {
            GuiltModeRow(
                enabled = socialMediaPauseEnabled,
                checked = guiltModeEnabled,
                onCheckedChange = { newValue ->
                    if (newValue && !hasUsageStatsPermission(ctx)) {
                        showPermissionDialog = true
                    } else {
                        scope.launch {
                            WellbeingSettingsStore.guiltModeEnabled.set(ctx, newValue)
                        }
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.pause_duration),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.pause_duration_description, pauseDuration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = pauseDuration.toFloat(),
                    onValueChange = { newValue ->
                        scope.launch {
                            WellbeingSettingsStore.pauseDurationSeconds.set(ctx, newValue.toInt())
                        }
                    },
                    valueRange = 3f..60f,
                    steps = 56,
                    enabled = socialMediaPauseEnabled
                )
            }
        }

        item {
            TextDivider(stringResource(R.string.paused_apps))
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
                    showAppSelector = true
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

        // Clear all button when there are apps
        if (pausedApps.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
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
        }

        if (pausedApps.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(pausedApps.toList()) { packageName ->
                val app = allApps.find { it.packageName == packageName }
                val appName = app?.name ?: packageName
                val appIcon = remember(packageName) {
                    try {
                        ctx.packageManager.getApplicationIcon(packageName).toBitmap().asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }

                PausedAppItem(
                    appName = appName,
                    packageName = packageName,
                    appIcon = appIcon,
                    onRemove = {
                        scope.launch {
                            WellbeingSettingsStore.removePausedApp(ctx, packageName, pausedApps)
                        }
                    }
                )
            }
        } else {
            item {
                Text(
                    text = stringResource(R.string.no_paused_apps),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    if (showAppSelector) {
        AppSelectorDialog(
            apps = allApps.filter { it.packageName !in pausedApps },
            onAppSelected = { packageName ->
                scope.launch {
                    WellbeingSettingsStore.addPausedApp(ctx, packageName, pausedApps)
                }
                showAppSelector = false
            },
            onDismiss = { showAppSelector = false }
        )
    }

    // Usage permission dialog
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
}

/**
 * Check if the app has usage stats permission
 */
private fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

@Composable
private fun GuiltModeRow(
    enabled: Boolean,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.guilt_mode),
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface 
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = stringResource(R.string.guilt_mode_description),
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
private fun PausedAppItem(
    appName: String,
    packageName: String,
    appIcon: ImageBitmap?,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
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
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }

        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AppSelectorDialog(
    apps: List<org.elnix.dragonlauncher.common.serializables.AppModel>,
    onAppSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val ctx = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_app)) },
        text = {
            Column(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                androidx.compose.foundation.lazy.LazyColumn {
                    items(apps.sortedBy { it.name }) { app ->
                        val appIcon = remember(app.packageName) {
                            try {
                                ctx.packageManager.getApplicationIcon(app.packageName)
                                    .toBitmap().asImageBitmap()
                            } catch (e: Exception) {
                                null
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onAppSelected(app.packageName) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (appIcon != null) {
                                Image(
                                    bitmap = appIcon,
                                    contentDescription = app.name,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Text(
                                text = app.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
