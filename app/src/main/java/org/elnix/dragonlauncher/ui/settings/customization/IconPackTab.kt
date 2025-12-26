package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.ui.helpers.AppGrid
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.utils.models.AppDrawerViewModel

@Composable
fun IconPackTab(
    appsViewModel: AppDrawerViewModel = viewModel(),
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val apps by appsViewModel.userApps.collectAsState(initial = emptyList())
    val icons by appsViewModel.icons.collectAsState()

    val selectedPack by appsViewModel.selectedIconPack.collectAsState()
    val packs = appsViewModel.findIconPacks()

    // Load packs
    LaunchedEffect(Unit) {
        appsViewModel.loadSavedIconPack(ctx)
    }

    SettingsLazyHeader(
        title = stringResource(R.string.icon_pack),
        onBack = onBack,
        helpText = stringResource(R.string.icon_pack_help),
        onReset = {
            scope.launch {
                appsViewModel.clearIconPack()
            }
        },
        titleContent = {
            item {
                Box(Modifier.height(80.dp)){
                    AppGrid(
                        apps = apps.take(6),
                        icons = icons,
                        txtColor = MaterialTheme.colorScheme.onBackground,
                        gridSize = 6,
                        showIcons = true,
                        showLabels = false
                    ) { }
                }
            }
        }
    ) {

        if (packs.isEmpty()) {
            item {
                Text(
                    text=  stringResource(R.string.no_icon_pack_found),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {

            item {
                Text(
                    text = "${packs.size} icon packs found",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(packs) { pack ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            scope.launch {
                                appsViewModel.selectIconPack(pack)
                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val packAppIcon = icons[pack.packageName]
                        Box(modifier = Modifier.size(40.dp)) {
                            if (packAppIcon != null) {
                                Image(
                                    bitmap = packAppIcon,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Column {
                            Text(
                                text = pack.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = pack.packageName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (selectedPack?.packageName == pack.packageName) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        item {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        scope.launch {
                            appsViewModel.clearIconPack()
                        }
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp)
                    )

                    Column {
                        Text(
                            text = stringResource(R.string.default_no_icon_pack),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.use_original_app_icon),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (selectedPack == null) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
