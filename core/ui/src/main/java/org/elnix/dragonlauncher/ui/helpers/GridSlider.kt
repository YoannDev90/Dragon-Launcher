package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.resolveShape

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
    var sliderValue by remember { mutableFloatStateOf(gridSize.toFloat()) }

    val iconsShape by DrawerSettingsStore.iconsShape.flow(ctx)
        .collectAsState(DrawerSettingsStore.iconsShape.default)

    LaunchedEffect(gridSize) {
        sliderValue = gridSize.toFloat()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Grid Size: ${sliderValue.toInt()}",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(15.dp))

            Icon(
                imageVector = Icons.Default.Restore,
                contentDescription = stringResource(R.string.reset),
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .clickable {
                        scope.launch{
                            DrawerSettingsStore.gridSize.reset(ctx)
                        }
                    }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
        ) {
            AppGrid(
                apps = apps.take(if (gridSize == 1) 3 else gridSize),
                icons = icons,
                shape = resolveShape(iconsShape),
                txtColor = MaterialTheme.colorScheme.onBackground,
                gridSize = gridSize,
                showIcons = showIcons,
                showLabels = showLabels
            ) { }
        }

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = {
                scope.launch { DrawerSettingsStore.gridSize.set(ctx, sliderValue.toInt()) }
            },
            valueRange = 1f..10f,
            steps = 8,
            colors = AppObjectsColors.sliderColors()
        )
    }
}
