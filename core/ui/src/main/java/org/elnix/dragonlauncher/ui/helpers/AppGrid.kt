package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.actions.appIcon
import org.elnix.dragonlauncher.ui.components.resolveShape
import org.elnix.dragonlauncher.ui.drawer.AppItem

@Composable
fun AppGrid(
    apps: List<AppModel>,
    icons: Map<String, ImageBitmap>,
    iconShape: IconShape,
    gridSize: Int,
    txtColor: Color,
    showIcons: Boolean,
    showLabels: Boolean,
    onLongClick: ((AppModel) -> Unit)? = null,
    onScrollDown: (() -> Unit)? = null,
    onScrollUp: (() -> Unit)? = null,
    onClick: (AppModel) -> Unit
) {
    val ctx = LocalContext.current

    val maxIconSize by DrawerSettingsStore.maxIconSize.flow(ctx)
        .collectAsState(DrawerSettingsStore.maxIconSize.default)

    val iconsSpacingVertical by DrawerSettingsStore.iconsSpacingVertical.flow(ctx)
        .collectAsState(DrawerSettingsStore.iconsSpacingVertical.default)

    val iconsSpacingHorizontal by DrawerSettingsStore.iconsSpacingHorizontal.flow(ctx)
        .collectAsState(DrawerSettingsStore.iconsSpacingHorizontal.default)


    val listState = rememberLazyListState()
    val nestedConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (
                    listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
                ) {

                    /* Launches onScrollDown on any down drag */
                    if (available.y > 15) {
                        onScrollDown?.invoke()
                    }

                    /* Launches onScrollUp on any up drag */
                    if (available.y < 15) {
                        onScrollUp?.invoke()
                    }
                }
                return Offset.Zero
            }
        }
    }

    if (gridSize == 1) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (onScrollDown != null) Modifier.nestedScroll(nestedConnection)
                    else Modifier
                )
        ) {
            items(apps, key = { it.packageName }) { app ->
                AppItem(
                    app = app,
                    showIcons = showIcons,
                    showLabels = showLabels,
                    txtColor = txtColor,
                    icons = icons,
                    iconsSpacing = iconsSpacingVertical,
                    shape = resolveShape(iconShape),
                    onClick = { onClick(app) },
                    onLongClick = if (onLongClick != null) { { onLongClick(app) } } else null
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridSize),
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (onScrollDown != null) Modifier.nestedScroll(nestedConnection)
                    else Modifier
                ),
//            verticalArrangement = Arrangement.spacedBy(iconsSpacing.dp),
//            horizontalArrangement = Arrangement.spacedBy(iconsSpacing.dp)
        ) {
            items(apps.size) { index ->
                val app = apps[index]

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DragonShape)
                        .combinedClickable(
                            onLongClick = if (onLongClick != null) {
                                { onLongClick(app) }
                            } else null
                        ) { onClick(app) }
                        .padding(iconsSpacingHorizontal.dp, iconsSpacingVertical.dp)
                ) {
                    if (showIcons) {
                        Image(
                            painter = appIcon(app.packageName, icons),
                            contentDescription = app.name,
                            modifier = Modifier
                                .sizeIn(maxWidth = maxIconSize.dp)
                                .aspectRatio(1f)
                                .clip(resolveShape(iconShape)),
                            contentScale = ContentScale.Fit
                        )
                    }

                    if (showLabels) {
                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = app.name,
                            color = txtColor,
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
