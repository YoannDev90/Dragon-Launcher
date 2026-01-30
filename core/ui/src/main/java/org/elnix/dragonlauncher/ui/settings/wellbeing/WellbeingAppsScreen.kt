package org.elnix.dragonlauncher.ui.settings.wellbeing

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader


/**
 * Screen for selecting which apps should trigger the digital pause.
 */
@Composable
fun WellbeingAppsScreen(
    appsViewModel: AppsViewModel,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val allApps by appsViewModel.allApps.collectAsState()
    val icons by appsViewModel.icons.collectAsState()
    val pausedApps by WellbeingSettingsStore.getPausedAppsFlow(ctx)
        .collectAsState(initial = emptySet())

    var searchQuery by remember { mutableStateOf("") }

    val filteredApps by remember(allApps, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                allApps.sortedBy { it.name.lowercase() }
            } else {
                allApps.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.packageName.contains(searchQuery, ignoreCase = true)
                }.sortedBy { it.name.lowercase() }
            }
        }
    }

    SettingsLazyHeader(
        title = stringResource(R.string.select_paused_apps),
        onBack = onBack,
        helpText = stringResource(R.string.manage_paused_apps_desc),
        onReset = {
            scope.launch {
                WellbeingSettingsStore.setPausedApps(ctx, emptySet())
            }
        },
        lazyContent = {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text(stringResource(R.string.search_apps_to_pause)) },
                    singleLine = true
                )
            }

            items(
                count = filteredApps.size,
                key = { filteredApps[it].packageName }
            ) { index ->
                val app = filteredApps[index]
                val isChecked = app.packageName in pausedApps
                val isSocialMedia = app.packageName in WellbeingSettingsStore.knownSocialMediaApps
                val icon = icons[app.packageName]

                AppCheckboxItem(
                    appName = app.name,
                    packageName = app.packageName,
                    icon = icon,
                    isChecked = isChecked,
                    isSocialMedia = isSocialMedia,
                    onCheckedChange = { checked ->
                        scope.launch {
                            if (checked) {
                                WellbeingSettingsStore.addPausedApp(ctx, app.packageName, pausedApps)
                            } else {
                                WellbeingSettingsStore.removePausedApp(ctx, app.packageName, pausedApps)
                            }
                        }
                    }
                )
            }
        }
    )
}


@Composable
private fun AppCheckboxItem(
    appName: String,
    packageName: String,
    icon: ImageBitmap?,
    isChecked: Boolean,
    isSocialMedia: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Image(
                bitmap = it,
                contentDescription = appName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = appName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )

            if (isSocialMedia) {
                Text(
                    text = "📱",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
