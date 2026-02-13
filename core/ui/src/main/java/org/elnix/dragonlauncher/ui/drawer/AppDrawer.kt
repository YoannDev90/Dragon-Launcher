package org.elnix.dragonlauncher.ui.drawer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.WorkspaceType
import org.elnix.dragonlauncher.common.serializables.dummySwipePoint
import org.elnix.dragonlauncher.common.utils.PrivateSpaceUtils
import org.elnix.dragonlauncher.common.utils.openSearch
import org.elnix.dragonlauncher.enumsui.DrawerActions
import org.elnix.dragonlauncher.enumsui.DrawerActions.CLEAR
import org.elnix.dragonlauncher.enumsui.DrawerActions.CLOSE
import org.elnix.dragonlauncher.enumsui.DrawerActions.CLOSE_KB
import org.elnix.dragonlauncher.enumsui.DrawerActions.DISABLED
import org.elnix.dragonlauncher.enumsui.DrawerActions.NONE
import org.elnix.dragonlauncher.enumsui.DrawerActions.OPEN_FIRST_APP
import org.elnix.dragonlauncher.enumsui.DrawerActions.OPEN_KB
import org.elnix.dragonlauncher.enumsui.DrawerActions.SEARCH_WEB
import org.elnix.dragonlauncher.enumsui.DrawerActions.TOGGLE_KB
import org.elnix.dragonlauncher.enumsui.PrivateSpaceLoadingState
import org.elnix.dragonlauncher.enumsui.isUsed
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.dialogs.AppAliasesDialog
import org.elnix.dragonlauncher.ui.dialogs.AppLongPressDialog
import org.elnix.dragonlauncher.ui.dialogs.IconEditorDialog
import org.elnix.dragonlauncher.ui.dialogs.RenameAppDialog
import org.elnix.dragonlauncher.ui.helpers.AppGrid
import org.elnix.dragonlauncher.ui.helpers.WallpaperDim
import org.elnix.dragonlauncher.ui.modifiers.settingsGroup

@SuppressLint("LocalContextGetResourceValueCall")
@Suppress("AssignedValueIsNeverRead")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppDrawerScreen(
    appsViewModel: AppsViewModel,
    appLifecycleViewModel: AppLifecycleViewModel,
    showIcons: Boolean,
    showLabels: Boolean,
    autoShowKeyboard: Boolean,
    gridSize: Int,
    searchBarBottom: Boolean,
    leftAction: DrawerActions,
    leftWeight: Float,
    rightAction: DrawerActions,
    rightWeight: Float,
    onUnlockPrivateSpace: () -> Unit,
    onLaunchAction: (SwipeActionSerializable) -> Unit,
    onClose: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val privateSpaceState by appsViewModel.privateSpaceState.collectAsState()

    val workspaceState by appsViewModel.enabledState.collectAsState()
    val visibleWorkspaces = workspaceState.workspaces
    val overrides = workspaceState.appOverrides
    val aliases = workspaceState.appAliases


    val selectedWorkspaceId by appsViewModel.selectedWorkspaceId.collectAsState()
    val initialIndex = visibleWorkspaces.indexOfFirst { it.id == selectedWorkspaceId }
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (visibleWorkspaces.size - 1).coerceAtLeast(0)),
        pageCount = { visibleWorkspaces.size }
    )

    val icons by appsViewModel.icons.collectAsState()

    val autoLaunchSingleMatch by DrawerSettingsStore.autoOpenSingleMatch.asState()
    val useCategory by DrawerSettingsStore.useCategory.asState()

    /* ───────────── Actions ───────────── */
    val tapEmptySpaceToRaiseKeyboard by DrawerSettingsStore
        .tapEmptySpaceAction.asState()

    val drawerEnterAction by DrawerSettingsStore.drawerEnterAction.asState()
    val drawerBackAction by DrawerSettingsStore.backDrawerAction.asState()
    val drawerHomeAction by DrawerSettingsStore.drawerHomeAction.asState()
    val drawerScrollDownAction by DrawerSettingsStore.scrollDownDrawerAction.asState()
    val drawerScrollUpAction by DrawerSettingsStore.scrollUpDrawerAction.asState()

    val iconsShape by DrawerSettingsStore.iconsShape.asState()

    /* ───────────── Recently Used Apps ───────────── */
    val showRecentlyUsedApps by DrawerSettingsStore.showRecentlyUsedApps.asState()
    val recentlyUsedAppsCount by DrawerSettingsStore.recentlyUsedAppsCount.asState()
    val recentApps by appsViewModel.getRecentApps(recentlyUsedAppsCount)
        .collectAsStateWithLifecycle(emptyList())


    var haveToLaunchFirstApp by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var dialogApp by remember { mutableStateOf<AppModel?>(null) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isSearchFocused by remember { mutableStateOf(false) }

    var showRenameAppDialog by remember { mutableStateOf(false) }
    var renameTargetPackage by remember { mutableStateOf<String?>(null) }
    var renameText by remember { mutableStateOf("") }

    var showAliasDialog by remember { mutableStateOf<AppModel?>(null) }

    var workspaceId by remember { mutableStateOf<String?>(null) }


    var appTarget by remember { mutableStateOf<AppModel?>(null) }


    LaunchedEffect(autoShowKeyboard) {
        if (autoShowKeyboard) {
            yield()
            focusRequester.requestFocus()
        }
    }


    /**
     * Updates the visible workspace
     */
    LaunchedEffect(visibleWorkspaces, selectedWorkspaceId) {
        if (visibleWorkspaces.isEmpty()) return@LaunchedEffect

        val selectedVisible = visibleWorkspaces.any { it.id == selectedWorkspaceId }
        val targetId = if (selectedVisible) selectedWorkspaceId else visibleWorkspaces.first().id
        val targetIndex = visibleWorkspaces.indexOfFirst { it.id == targetId }

        if (!selectedVisible) {
            appsViewModel.selectWorkspace(targetId)
        }

        if (targetIndex >= 0 && pagerState.currentPage != targetIndex) {
            pagerState.scrollToPage(targetIndex)
        }
    }

    /**
     * Fires on workspace state change
     * launch the private space unlocking prompt if workspace type if private space
     */
    LaunchedEffect(pagerState.currentPage) {
        val newWorkspaceId =
            visibleWorkspaces.getOrNull(pagerState.currentPage)?.id ?: return@LaunchedEffect
        val newWorkspace =
            visibleWorkspaces.getOrNull(pagerState.currentPage) ?: return@LaunchedEffect

        // Check if switching to Private Space (Android 15+)
        if (PrivateSpaceUtils.isPrivateSpaceSupported() &&
            newWorkspace.type == WorkspaceType.PRIVATE &&
            privateSpaceState != PrivateSpaceLoadingState.Available
        ) {
            onUnlockPrivateSpace()
        }

        workspaceId = newWorkspaceId
        appsViewModel.selectWorkspace(newWorkspaceId)
    }


    fun closeKeyboard() {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    fun openKeyboard() {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    fun toggleKeyboard() {
        if (isSearchFocused) {
            closeKeyboard()
        } else {
            openKeyboard()
        }
    }


    fun launchDrawerAction(action: DrawerActions) {
        when (action) {
            CLOSE -> onClose()
            TOGGLE_KB -> toggleKeyboard()
            CLOSE_KB -> closeKeyboard()
            OPEN_KB -> openKeyboard()

            CLEAR -> searchQuery = ""
            SEARCH_WEB -> {
                if (searchQuery.isNotBlank()) ctx.openSearch(searchQuery)
            }

            OPEN_FIRST_APP -> haveToLaunchFirstApp = true
            NONE, DISABLED -> {}
        }
    }


    LaunchedEffect(Unit) {
        appLifecycleViewModel.homeEvents.collect {
            launchDrawerAction(drawerHomeAction)
        }
    }

    @Composable
    fun DrawerTextInput() {
        AppDrawerSearch(
            searchQuery = searchQuery,
            onSearchChanged = { searchQuery = it },
            modifier = Modifier.focusRequester(focusRequester),
            onEnterPressed = { launchDrawerAction(drawerEnterAction) },
            onFocusStateChanged = { isSearchFocused = it }
        )
    }


    /* Dim wallpaper system */
    val drawerDimRadius by UiSettingsStore.wallpaperDimDrawerScreen.flow(ctx)
        .collectAsState(UiSettingsStore.wallpaperDimDrawerScreen.default)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        WallpaperDim(drawerDimRadius)
    }


    BackHandler {
        launchDrawerAction(drawerBackAction)
    }

    val topPadding = if (!searchBarBottom) 60.dp else 0.dp
    val bottomPadding = if (searchBarBottom) 60.dp else 0.dp

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.ime))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding, bottom = bottomPadding)
                .clickable(
                    enabled = tapEmptySpaceToRaiseKeyboard.isUsed(),
                    indication = null,
                    interactionSource = null
                ) {
                    toggleKeyboard()
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {

                if (leftAction != DISABLED) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(leftWeight.coerceIn(0.001f, 1f))
                            .clickable(
                                indication = null,
                                interactionSource = null
                            ) { launchDrawerAction(leftAction) }
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    /* ───────────── Recently Used Apps section ───────────── */
                    if (showRecentlyUsedApps && searchQuery.isBlank() && recentApps.isNotEmpty()) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .settingsGroup(border = false)
                        ) {
                            AppGrid(
                                apps = recentApps,
                                icons = icons,
                                gridSize = gridSize,
                                iconShape = iconsShape,
                                txtColor = MaterialTheme.colorScheme.onBackground,
                                showIcons = showIcons,
                                showLabels = showLabels,
                                fillMaxSize = false,
                                onLongClick = { dialogApp = it },
                                onScrollDown = null,
                                onScrollUp = null
                            ) {
                                onLaunchAction(it.action)
                            }
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        key = { it.hashCode() }
                    ) { pageIndex ->

                        val workspace = visibleWorkspaces[pageIndex]

                        val apps by appsViewModel
                            .appsForWorkspace(workspace, overrides)
                            .collectAsStateWithLifecycle(emptyList())

                        val filteredApps by remember(searchQuery, apps) {
                            derivedStateOf {
                                if (searchQuery.isBlank()) apps
                                else apps.filter { app ->
                                    app.name.contains(searchQuery, ignoreCase = true) ||

                                            // Also search for aliases
                                            aliases[app.packageName]?.any {
                                                it.contains(
                                                    searchQuery,
                                                    ignoreCase = true
                                                )
                                            } ?: false
                                }
                            }
                        }

                        LaunchedEffect(haveToLaunchFirstApp, filteredApps) {
                            if ((autoLaunchSingleMatch && filteredApps.size == 1 && searchQuery.isNotEmpty()) || haveToLaunchFirstApp) {
                                onLaunchAction(filteredApps.first().action)
                            }
                        }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            // If the current workspace is a private space and locked, display a lock icon
                            if (privateSpaceState != PrivateSpaceLoadingState.Available && (workspace.type == WorkspaceType.PRIVATE)) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    DragonIconButton(
                                        onClick = onUnlockPrivateSpace,
                                        modifier = Modifier.padding(15.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = stringResource(R.string.private_space_locked)
                                        )
                                    }
                                }
                            } else {
                                AppGrid(
                                    apps = filteredApps,
                                    icons = icons,
                                    gridSize = gridSize,
                                    iconShape = iconsShape,
                                    txtColor = MaterialTheme.colorScheme.onBackground,
                                    showIcons = showIcons,
                                    showLabels = showLabels,
                                    useCategory = useCategory,
                                    onReload = {
                                        scope.launch {
                                            if (workspace.type == WorkspaceType.PRIVATE) appsViewModel.reloadPrivateSpace()
                                            else appsViewModel.reloadApps()
                                        }
                                    },
                                    onLongClick = { dialogApp = it },
                                    onScrollDown = { launchDrawerAction(drawerScrollDownAction) },
                                    onScrollUp = { launchDrawerAction(drawerScrollUpAction) }
                                ) {
                                    onLaunchAction(it.action)
                                }
                            }
                        }
                    }
                }

                if (rightAction != DISABLED) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(rightWeight.coerceIn(0.001f, 1f))
                            .clickable(
                                indication = null,
                                interactionSource = null
                            ) { launchDrawerAction(rightAction) }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentAlignment = if (searchBarBottom) Alignment.BottomCenter else Alignment.TopCenter
        ) {
            DrawerTextInput()
        }
    }

    if (dialogApp != null) {
        val app = dialogApp!!

        AppLongPressDialog(
            app = app,
            onDismiss = { dialogApp = null },
            onOpen = { onLaunchAction(app.action) },
            onSettings = {
                ctx.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = "package:${app.packageName}".toUri()
                    }
                )
                onClose()
            },
            onUninstall = {
                ctx.startActivity(
                    Intent(Intent.ACTION_DELETE).apply {
                        data = "package:${app.packageName}".toUri()
                    }
                )
                onClose()
            },
            onRemoveFromWorkspace = {
                workspaceId?.let {
                    scope.launch {
                        appsViewModel.removeAppFromWorkspace(
                            it,
                            app.packageName
                        )
                    }
                }
            },
            onRenameApp = {
                renameText = app.name
                renameTargetPackage = app.packageName
                showRenameAppDialog = true
            },
            onChangeAppIcon = { appTarget = app },
            onAliases = { showAliasDialog = app }
        )
    }

    RenameAppDialog(
        visible = showRenameAppDialog,
        title = stringResource(R.string.rename_app),
        name = renameText,
        onNameChange = { renameText = it },
        onConfirm = {
            val pkg = renameTargetPackage ?: return@RenameAppDialog

            scope.launch {
                appsViewModel.renameApp(
                    packageName = pkg,
                    name = renameText
                )
            }

            showRenameAppDialog = false
            renameTargetPackage = null
        },
        onReset = {
            val pkg = renameTargetPackage ?: return@RenameAppDialog

            scope.launch {
                appsViewModel.resetAppName(pkg)
            }
            showRenameAppDialog = false
            renameTargetPackage = null
        },
        onDismiss = { showRenameAppDialog = false }
    )

    if (appTarget != null) {

        val app = appTarget!!
        val pkg = app.packageName

        val iconOverride =
            overrides[pkg]?.customIcon


        val tempPoint =
            dummySwipePoint(
                SwipeActionSerializable.LaunchApp(
                    pkg,
                    app.isPrivateProfile,
                    app.userId
                ), pkg
            ).copy(
                customIcon = iconOverride
            )


        IconEditorDialog(
            point = tempPoint,
            appsViewModel = appsViewModel,
            onReset = {
                appsViewModel.updateSingleIcon(app, false)
            },
            onDismiss = { appTarget = null }
        ) {

            /* ───────────── Reload icon once firstly ───────────── */
            scope.launch {
                if (it != null) {
                    appsViewModel.setAppIcon(
                        pkg,
                        it
                    )
                } else {
                    appsViewModel.resetAppIcon(pkg)
                }
                appsViewModel.updateSingleIcon(app, true)

                /* ───────────── Reload all points upon icon change to synchronize with points ───────────── */
                withContext(Dispatchers.IO) {
                    appsViewModel.reloadApps()
                }
            }

            appTarget = null
        }
    }

    if (showAliasDialog != null) {
        val app = showAliasDialog!!

        AppAliasesDialog(
            appsViewModel = appsViewModel,
            app = app,
            onDismiss = { showAliasDialog = null }
        )
    }
}


@Composable
private fun AppDrawerSearch(
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    onEnterPressed: () -> Unit = {},
    onFocusStateChanged: (Boolean) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = searchQuery,
        onValueChange = onSearchChanged,
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(DragonShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .onFocusChanged { focusState ->
                val focused = focusState.isFocused
                onFocusStateChanged(focused) // Notify parent of focus change
                if (focused) {
                    keyboardController?.show() // Show keyboard when TextField gains focus
                }
                // Keyboard hiding on focus loss is handled by system, IME actions, or explicit calls elsewhere (e.g., scroll logic)
            },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_apps),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search_apps),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onEnterPressed() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}
