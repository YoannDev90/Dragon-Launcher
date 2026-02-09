package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.helpers.AppGrid
import org.elnix.dragonlauncher.ui.helpers.AppGridWithMultiSelect

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppPickerDialog(
    appsViewModel: AppsViewModel,
    gridSize: Int,
    iconShape: IconShape,
    showIcons: Boolean,
    showLabels: Boolean,
    multiSelectEnabled: Boolean = false,
    onDismiss: () -> Unit,
    onAppSelected: (AppModel) -> Unit,
    onMultipleAppsSelected: ((List<AppModel>, Boolean) -> Unit)? = null
) {
    val workspaceState by appsViewModel.enabledState.collectAsState()
    val workspaces = workspaceState.workspaces
    val overrides = workspaceState.appOverrides

    val icons by appsViewModel.icons.collectAsState()

    val selectedWorkspaceId by appsViewModel.selectedWorkspaceId.collectAsState()
    val initialIndex = workspaces.indexOfFirst { it.id == selectedWorkspaceId }
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (workspaces.size - 1).coerceAtLeast(0)),
        pageCount = { workspaces.size }
    )

    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchBarEnabled by remember { mutableStateOf(false) }

    // Multi-select state
    var isMultiSelectMode by remember { mutableStateOf(false) }
    val selectedApps = remember { mutableStateListOf<String>() }

    LaunchedEffect(pagerState.currentPage) {
        val workspaceId = workspaces.getOrNull(pagerState.currentPage)?.id ?: return@LaunchedEffect
        appsViewModel.selectWorkspace(workspaceId)
    }

    CustomAlertDialog(
        alignment = Alignment.Center,
        modifier = Modifier.padding(15.dp).height(700.dp),
        onDismissRequest = {
            if (isMultiSelectMode) {
                isMultiSelectMode = false
                selectedApps.clear()
            } else {
                onDismiss()
            }
        },
        scroll = false,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(75.dp)
                ) {
                    if (!isSearchBarEnabled) {
                        Text(
                            text = if (isMultiSelectMode)
                                stringResource(R.string.multi_select_count, selectedApps.size)
                            else
                                stringResource(R.string.select_app),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (isMultiSelectMode) {
                            DragonIconButton(
                                onClick = {
                                    isMultiSelectMode = false
                                    selectedApps.clear()
                                },
                                colors = AppObjectsColors.iconButtonColors()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Deselect,
                                    contentDescription = stringResource(R.string.deselect_all)
                                )
                            }
                        }

                        DragonIconButton(
                            onClick = { isSearchBarEnabled = true },
                            colors = AppObjectsColors.iconButtonColors()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_apps)
                            )
                        }

                        DragonIconButton(
                            onClick = { scope.launch { appsViewModel.reloadApps() } },
                            colors = AppObjectsColors.iconButtonColors()
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = stringResource(R.string.reload_apps)
                            )
                        }
                    } else {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.clickable {
                                        searchQuery = ""
                                        isSearchBarEnabled = false
                                    }
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.search_apps),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            colors = AppObjectsColors.outlinedTextFieldColors(backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f), removeBorder = true),
                            modifier = Modifier
                                .clip(CircleShape),
                            maxLines = 1
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    workspaces.forEachIndexed { index, workspace ->
                        val selected = pagerState.currentPage == index

                        TextButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            shape = DragonShape,
                            colors = AppObjectsColors.buttonColors(
                                if (!selected) MaterialTheme.colorScheme.surface else null
                            ),
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Text(
                                text = workspace.name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Multi-select hint
                if (multiSelectEnabled && !isMultiSelectMode) {
                    Text(
                        text = stringResource(R.string.multi_select_drawer_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { pageIndex ->

                    val workspace = workspaces[pageIndex]

                    val apps by appsViewModel
                        .appsForWorkspace(workspace, overrides)
                        .collectAsState(initial = emptyList())

                    val filteredApps = if (isSearchBarEnabled)
                        apps.filter { it.name.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true) }
                    else apps

                    if (multiSelectEnabled) {
                        AppGridWithMultiSelect(
                            apps = filteredApps,
                            icons = icons,
                            iconShape = iconShape,
                            gridSize = gridSize,
                            txtColor = MaterialTheme.colorScheme.onSurface,
                            showIcons = showIcons,
                            showLabels = showLabels,
                            isMultiSelectMode = isMultiSelectMode,
                            selectedPackages = selectedApps,
                            onEnterMultiSelect = { app ->
                                isMultiSelectMode = true
                                if (!selectedApps.contains(app.packageName)) {
                                    selectedApps.add(app.packageName)
                                }
                            },
                            onToggleSelect = { app ->
                                if (selectedApps.contains(app.packageName)) {
                                    selectedApps.remove(app.packageName)
                                } else {
                                    selectedApps.add(app.packageName)
                                }
                                if (selectedApps.isEmpty()) {
                                    isMultiSelectMode = false
                                }
                            },
                            onAppClick = { app ->
                                onAppSelected(app)
                                onDismiss()
                            }
                        )
                    } else {
                        AppGrid(
                            apps = filteredApps,
                            icons = icons,
                            iconShape = iconShape,
                            gridSize = gridSize,
                            txtColor = MaterialTheme.colorScheme.onSurface,
                            showIcons = showIcons,
                            showLabels = showLabels
                        ) {
                            onAppSelected(it)
                            onDismiss()
                        }
                    }
                }

                // Multi-select action bar
                if (isMultiSelectMode && selectedApps.isNotEmpty() && onMultipleAppsSelected != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                            .clip(DragonShape)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Resolve picked apps to AppModel list
                        Button(
                            onClick = {
                                val workspaceApps = workspaces.flatMap { ws ->
                                    val flow = appsViewModel.appsForWorkspace(ws, overrides)
                                    // we'll resolve via getAllApps from ViewModel
                                    emptyList<AppModel>()
                                }
                                // We'll use the allApps state reference
                                val allApps = appsViewModel.allApps.value
                                val pickedApps = allApps.filter { it.packageName in selectedApps }
                                onMultipleAppsSelected(pickedApps, true)
                                onDismiss()
                            },
                            shape = DragonShape,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlaylistAddCheck, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.add_all_auto))
                        }

                        Button(
                            onClick = {
                                val allApps = appsViewModel.allApps.value
                                val pickedApps = allApps.filter { it.packageName in selectedApps }
                                onMultipleAppsSelected(pickedApps, false)
                                onDismiss()
                            },
                            shape = DragonShape,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlaylistAdd, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.add_all_manual))
                        }
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = MaterialTheme.colorScheme.surface,
        shape = DragonShape
    )
}
