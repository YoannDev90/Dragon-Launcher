package org.elnix.dragonlauncher.ui.helpers

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.AppCategory
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.iconCacheKey
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.drawer.AppItemGrid
import org.elnix.dragonlauncher.ui.drawer.AppItemHorizontal
import org.elnix.dragonlauncher.ui.modifiers.shapedClickable
import kotlin.math.min

@Composable
fun AppGrid(
    apps: List<AppModel>,
    icons: Map<String, ImageBitmap>,
    iconShape: IconShape,
    gridSize: Int,
    txtColor: Color,
    showIcons: Boolean,
    showLabels: Boolean,
    useCategory: Boolean = false,
    fillMaxSize: Boolean = true,

    // Multi select things
    isMultiSelectMode: Boolean = false,
    selectedPackages: List<String> = emptyList(),
    onEnterMultiSelect: ((AppModel) -> Unit)? = null,
    onToggleSelect: ((AppModel) -> Unit)? = null,

    onReload: (() -> Unit)? = null,
    onLongClick: ((AppModel) -> Unit)? = null,
    onScrollDown: (() -> Unit)? = null,
    onScrollUp: (() -> Unit)? = null,
    onClick: (AppModel) -> Unit
) {
    val maxIconSize by DrawerSettingsStore.maxIconSize.asState()
    val iconsSpacingVertical by DrawerSettingsStore.iconsSpacingVertical.asState()
    val iconsSpacingHorizontal by DrawerSettingsStore.iconsSpacingHorizontal.asState()

    val categoryGridSize by DrawerSettingsStore.categoryGridWidth.asState()
    val categoryGridCells by DrawerSettingsStore.categoryGridCells.asState()
    val showCategoryName by DrawerSettingsStore.showCategoryName.asState()

    var openedCategory by remember { mutableStateOf<AppCategory?>(null) }

    val visibleApps by remember(apps) {
        derivedStateOf {
            // Only display the apps that belongs to the selected category, if enabled
            apps.filter {
                if (useCategory) openedCategory?.let { cat -> cat == it.category } ?: true
                else true
            }
        }
    }

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

    val gridState = rememberLazyGridState()
    val nestedConnectionGrid = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (
                    gridState.firstVisibleItemIndex == 0 &&
                    gridState.firstVisibleItemScrollOffset == 0
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


    BackHandler(openedCategory != null) {
        openedCategory = null
    }


    val modifier = if (fillMaxSize) Modifier.fillMaxSize() else Modifier

    when {
        visibleApps.isEmpty() -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text(
                        text = stringResource(R.string.no_apps),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (onReload != null) {
                        DragonIconButton(
                            onClick = onReload
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.reload_apps)
                            )
                        }
                    }
                }
            }
        }

        // Can't use categories with multi-select mode cause it's too annoying to implement
        useCategory && openedCategory == null && !isMultiSelectMode -> {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(categoryGridSize),
                modifier = modifier
                    .then(
                        if (onScrollDown != null) Modifier.nestedScroll(nestedConnectionGrid)
                        else Modifier
                    ),
                verticalArrangement = Arrangement.spacedBy(iconsSpacingVertical.dp),
                horizontalArrangement = Arrangement.spacedBy(iconsSpacingHorizontal.dp)
            ) {

                AppCategory.entries.forEach { category ->
                    val categoryApps = visibleApps.filter { it.category == category }

                    categoryApps
                        .takeIf { it.isNotEmpty() }
                        ?.let {
                            item {
                                CategoryGrid(
                                    category = category,
                                    apps = categoryApps,
                                    icons = icons,
                                    iconShape = iconShape,
                                    maxIconSize = maxIconSize,
                                    txtColor = txtColor,
                                    showIcons = showIcons,
                                    onLongClick = onLongClick,
                                    onClick = onClick,
                                    showCategoryName = showCategoryName,
                                    gridCells = categoryGridCells,
                                ) {
                                    openedCategory = category
                                }
                            }
                        }
                }
            }
        }

        gridSize == 1 -> {
            LazyColumn(
                state = listState,
                modifier = modifier
                    .then(
                        if (onScrollDown != null) Modifier.nestedScroll(nestedConnection)
                        else Modifier
                    ),
                verticalArrangement = Arrangement.spacedBy(iconsSpacingVertical.dp),
            ) {
                items(visibleApps, key = { it.iconCacheKey() }) { app ->
                    val selected = app.packageName in selectedPackages

                    AppItemHorizontal(
                        app = app,
                        selected = selected,
                        showIcons = showIcons,
                        showLabels = showLabels,
                        txtColor = txtColor,
                        icons = icons,
                        iconShape = iconShape,
                        onLongClick = {
                            if (!isMultiSelectMode && onEnterMultiSelect != null) {
                                onEnterMultiSelect(app)
                            }  else if (isMultiSelectMode && onToggleSelect != null) {
                                onToggleSelect(app)
                            } else {
                                onLongClick?.invoke(app)
                            }
                        },
                        onClick = {
                            if (isMultiSelectMode && onToggleSelect != null) {
                                onToggleSelect(app)
                            } else{
                                onClick(app)
                            }
                        }
                    )
                }
            }
        }

        else -> {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(gridSize),
                modifier = modifier
                    .then(
                        if (onScrollDown != null) Modifier.nestedScroll(nestedConnectionGrid)
                        else Modifier
                    ),
                verticalArrangement = Arrangement.spacedBy(iconsSpacingVertical.dp),
                horizontalArrangement = Arrangement.spacedBy(iconsSpacingHorizontal.dp)
            ) {
                items(visibleApps, key = { it.iconCacheKey() }) { app ->
                    val selected = app.packageName in selectedPackages


                    AppItemGrid(
                        app = app,
                        selected = selected,
                        icons = icons,
                        showIcons = showIcons,
                        maxIconSize = maxIconSize,
                        iconShape = iconShape,
                        showLabels = showLabels,
                        txtColor = txtColor,
                        onLongClick = {
                            if (!isMultiSelectMode && onEnterMultiSelect != null) {
                                onEnterMultiSelect(app)
                            } else if (isMultiSelectMode && onToggleSelect != null) {
                                onToggleSelect(app)
                            } else onLongClick?.invoke(app)
                        },
                        onClick = {
                            if (isMultiSelectMode && onToggleSelect != null) {
                                onToggleSelect(app)
                            } else onClick(app)
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun CategoryGrid(
    category: AppCategory,
    apps: List<AppModel>,

    icons: Map<String, ImageBitmap>,
    iconShape: IconShape,
    maxIconSize: Int,
    txtColor: Color,
    showIcons: Boolean,

    gridCells: Int,
    showCategoryName: Boolean,
    modifier: Modifier = Modifier,
    onLongClick: ((AppModel) -> Unit)? = null,
    onClick: (AppModel) -> Unit,
    onOpenCategory: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .shapedClickable { onOpenCategory() }
                .padding(10.dp)
        ) {
            AppDefinedGrid(
                apps = apps,
                icons = icons,
                iconShape = iconShape,
                maxIconSize = maxIconSize,
                txtColor = txtColor,
                showIcons = showIcons,
                onLongClick = onLongClick,
                onClick = onClick,
                gridCells = gridCells,
            )
        }

        if (showCategoryName) {
            Text(
                text = category.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun AppDefinedGrid(
    apps: List<AppModel>,

    icons: Map<String, ImageBitmap>,
    iconShape: IconShape,
    maxIconSize: Int,
    txtColor: Color,
    showIcons: Boolean,

    gridCells: Int,
    modifier: Modifier = Modifier,
    onLongClick: ((AppModel) -> Unit)? = null,
    onClick: (AppModel) -> Unit,
) {
    var appIndex = 0

    val appNumber = apps.size
    val maxAppNumber = gridCells * gridCells - 1
    val sanitizedAppNumber = min(appNumber, maxAppNumber)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        repeat(gridCells) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                repeat(gridCells) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (appIndex < sanitizedAppNumber) {
                            AppItemGrid(
                                app = apps[appIndex],
                                icons = icons,
                                showIcons = showIcons,
                                maxIconSize = maxIconSize,
                                iconShape = iconShape,
                                showLabels = false,
                                txtColor = txtColor,
                                onLongClick = onLongClick,
                                onClick = onClick
                            )
                        } else if (appNumber > maxAppNumber) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ){
                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = "More",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }
                    appIndex ++
                }
            }
        }
    }
}
