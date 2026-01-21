package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.helpers.AppGrid
import org.elnix.dragonlauncher.ui.helpers.iconPackListContent
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader

@Composable
fun IconPackTab(
    appsViewModel: AppsViewModel,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val apps by appsViewModel.userApps.collectAsState(initial = emptyList())
    val icons by appsViewModel.icons.collectAsState()

    val selectedPack by appsViewModel.selectedIconPack.collectAsState()
    val packs by appsViewModel.iconPacksList.collectAsState()

    val iconPackTint by UiSettingsStore.getIconPackTintFLow(ctx).collectAsState(null)
    // Load packs
    LaunchedEffect(Unit) {
        appsViewModel.loadIconsPacks()
        appsViewModel.loadSavedIconPack()
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
                        apps = apps.shuffled().take(6),
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

        item {
            ColorPickerRow(
                label = stringResource(R.string.icon_pack_tint),
                defaultColor = Color.Unspecified,
                currentColor = iconPackTint ?: Color.Unspecified
            ) {
                val newColorInt = if (it == Color.Unspecified) null else it.toArgb()
                scope.launch { appsViewModel.setIconPackTint(newColorInt) }
            }
        }

        iconPackListContent(
            packs = packs,
            icons = icons,
            selectedPackPackage = selectedPack?.packageName,
            showClearOption = true,
            onReloadPacks = {
                appsViewModel.loadIconsPacks()
            },
            onPackClick = { pack ->
                scope.launch {
                    appsViewModel.selectIconPack(pack)
                }
            },
            onClearClick = {
                scope.launch {
                    appsViewModel.clearIconPack()
                }
            }
        )

    }
}
