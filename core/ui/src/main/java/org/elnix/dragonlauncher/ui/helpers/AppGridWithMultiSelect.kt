package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.actions.appIcon
import org.elnix.dragonlauncher.ui.components.resolveShape

/**
 * App grid/list with multi-select support.
 * Long press to enter multi-select mode, tap to toggle selection.
 */
@Composable
fun AppGridWithMultiSelect(
    apps: List<AppModel>,
    icons: Map<String, ImageBitmap>,
    iconShape: IconShape,
    gridSize: Int,
    txtColor: Color,
    showIcons: Boolean,
    showLabels: Boolean,
    isMultiSelectMode: Boolean,
    selectedPackages: List<String>,
    onEnterMultiSelect: (AppModel) -> Unit,
    onToggleSelect: (AppModel) -> Unit,
    onAppClick: (AppModel) -> Unit
) {
    val ctx = LocalContext.current

    val maxIconSize by DrawerSettingsStore.maxIconSize.flow(ctx)
        .collectAsState(DrawerSettingsStore.maxIconSize.default)

    val iconsSpacingVertical by DrawerSettingsStore.iconsSpacingVertical.flow(ctx)
        .collectAsState(DrawerSettingsStore.iconsSpacingVertical.default)

    val iconsSpacingHorizontal by DrawerSettingsStore.iconsSpacingHorizontal.flow(ctx)
        .collectAsState(DrawerSettingsStore.iconsSpacingHorizontal.default)

    val shape = resolveShape(iconShape)

    if (gridSize == 1) {
        // List mode
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(apps, key = { it.packageName }) { app ->
                val isSelected = app.packageName in selectedPackages

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DragonShape)
                        .then(
                            if (isSelected)
                                Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                            else Modifier
                        )
                        .combinedClickable(
                            onClick = {
                                if (isMultiSelectMode) {
                                    onToggleSelect(app)
                                } else {
                                    onAppClick(app)
                                }
                            },
                            onLongClick = {
                                if (!isMultiSelectMode) {
                                    onEnterMultiSelect(app)
                                }
                            }
                        )
                        .padding(vertical = iconsSpacingVertical.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showIcons) {
                        Box {
                            val icon = icons[app.packageName]
                            if (icon != null) {
                                Image(
                                    bitmap = icon,
                                    contentDescription = app.name,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(shape),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.ic_app_default),
                                    contentDescription = app.name,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(shape),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.BottomEnd)
                                        .offset(x = 4.dp, y = 4.dp)
                                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                    }

                    if (showLabels) {
                        Text(
                            text = app.name,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else txtColor
                        )
                    }
                }
            }
        }
    } else {
        // Grid mode
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridSize),
            modifier = Modifier.fillMaxSize()
        ) {
            items(apps.size) { index ->
                val app = apps[index]
                val isSelected = app.packageName in selectedPackages

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DragonShape)
                        .then(
                            if (isSelected)
                                Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.primary,
                                        DragonShape
                                    )
                            else Modifier
                        )
                        .combinedClickable(
                            onClick = {
                                if (isMultiSelectMode) {
                                    onToggleSelect(app)
                                } else {
                                    onAppClick(app)
                                }
                            },
                            onLongClick = {
                                if (!isMultiSelectMode) {
                                    onEnterMultiSelect(app)
                                }
                            }
                        )
                        .padding(iconsSpacingHorizontal.dp, iconsSpacingVertical.dp)
                ) {
                    if (showIcons) {
                        Box {
                            Image(
                                painter = appIcon(app.packageName, icons),
                                contentDescription = app.name,
                                modifier = Modifier
                                    .sizeIn(maxWidth = maxIconSize.dp)
                                    .aspectRatio(1f)
                                    .clip(resolveShape(iconShape)),
                                contentScale = ContentScale.Fit
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.BottomEnd)
                                        .offset(x = 4.dp, y = 4.dp)
                                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                                )
                            }
                        }
                    }

                    if (showLabels) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = app.name,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else txtColor,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
