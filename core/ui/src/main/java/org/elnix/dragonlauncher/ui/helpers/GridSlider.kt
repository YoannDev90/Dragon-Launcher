package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore

@Composable
fun GridSizeSlider(
    apps: List<AppModel>,
    icons: Map<String, ImageBitmap>,
    showIcons: Boolean,
    showLabels: Boolean
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val gridSize by DrawerSettingsStore.gridSize.flow(ctx).collectAsState(initial = 1)

    val iconsShape by DrawerSettingsStore.iconsShape.flow(ctx)
        .collectAsState(DrawerSettingsStore.iconsShape.default)


    var tempGridSize by remember { mutableStateOf(gridSize) }

    LaunchedEffect(gridSize) { tempGridSize = gridSize }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        SliderWithLabel(
            label = stringResource(R.string.grid_size),
            value = tempGridSize,
            valueRange = 1..10,
            onReset = {
                scope.launch { DrawerSettingsStore.gridSize.reset(ctx) }
            },
            onDragStateChange = {
                scope.launch { DrawerSettingsStore.gridSize.set(ctx, tempGridSize) }
            }
        ) {
            tempGridSize = it
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
        ) {
            AppGrid(
                apps = apps.shuffled().take(if (tempGridSize == 1) 3 else tempGridSize * 2),
                icons = icons,
                iconShape = iconsShape,
                txtColor = MaterialTheme.colorScheme.onBackground,
                gridSize = tempGridSize,
                showIcons = showIcons,
                showLabels = showLabels
            ) { }
        }
    }
}
