package org.elnix.dragonlauncher.ui.settings.wellbeing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader


/**
 * Wellbeing settings screen with Social Media Pause, Guilt Mode, and app management.
 */
@Composable
fun WellbeingTab(
    appsViewModel: AppsViewModel,
    onManageApps: () -> Unit,
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

    SettingsLazyHeader(
        title = stringResource(R.string.wellbeing),
        onBack = onBack,
        helpText = stringResource(R.string.wellbeing_help),
        onReset = {
            scope.launch {
                WellbeingSettingsStore.resetAll(ctx)
                WellbeingSettingsStore.setPausedApps(ctx, emptySet())
            }
        }
    ) {

        /*  ─────────────  Main Toggle  ─────────────  */
        item {
            SwitchRow(
                state = socialMediaPauseEnabled,
                text = stringResource(R.string.social_media_pause),
                subText = stringResource(R.string.social_media_pause_desc)
            ) {
                scope.launch { WellbeingSettingsStore.socialMediaPauseEnabled.set(ctx, it) }
            }
        }

        /*  ─────────────  Guilt Mode Toggle  ─────────────  */
        item {
            SwitchRow(
                state = guiltModeEnabled,
                text = stringResource(R.string.guilt_mode),
                subText = stringResource(R.string.guilt_mode_desc),
                enabled = socialMediaPauseEnabled
            ) {
                scope.launch { WellbeingSettingsStore.guiltModeEnabled.set(ctx, it) }
            }
        }

        /*  ─────────────  Pause Duration Slider  ─────────────  */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.pause_duration),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (socialMediaPauseEnabled) 1f else 0.5f
                    )
                )
                Text(
                    text = stringResource(R.string.pause_duration_desc, pauseDuration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (socialMediaPauseEnabled) 1f else 0.5f
                    )
                )
                SliderWithLabel(
                    value = pauseDuration.toFloat(),
                    valueRange = 3f..30f,
                    color = MaterialTheme.colorScheme.primary,
                    enabled = socialMediaPauseEnabled
                ) { newValue ->
                    scope.launch {
                        WellbeingSettingsStore.pauseDurationSeconds.set(ctx, newValue.toInt())
                    }
                }
            }
        }

        /*  ─────────────  Paused Apps Count  ─────────────  */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.manage_paused_apps),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (pausedApps.isEmpty()) {
                        stringResource(R.string.no_paused_apps)
                    } else {
                        stringResource(R.string.paused_apps_count, pausedApps.size)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        /*  ─────────────  Action Buttons  ─────────────  */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Manage Apps Button
                Button(
                    onClick = onManageApps,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = socialMediaPauseEnabled
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.manage_paused_apps))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }

                // Add All Social Media Button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val installedSocialApps = allApps
                                .map { it.packageName }
                                .filter { it in WellbeingSettingsStore.knownSocialMediaApps }
                                .toSet()
                            WellbeingSettingsStore.setPausedApps(
                                ctx,
                                pausedApps + installedSocialApps
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = socialMediaPauseEnabled
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.add_social_media_apps))
                        Icon(Icons.Default.AddCircle, contentDescription = null)
                    }
                }

                // Clear All Button
                if (pausedApps.isNotEmpty()) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                WellbeingSettingsStore.setPausedApps(ctx, emptySet())
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.clear_paused_apps))
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
