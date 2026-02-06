package org.elnix.dragonlauncher.ui.drawer

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import org.elnix.dragonlauncher.common.serializables.dummySwipePoint
import org.elnix.dragonlauncher.common.utils.openSearch
import org.elnix.dragonlauncher.common.utils.showToast
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
import org.elnix.dragonlauncher.enumsui.isUsed
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore
import org.elnix.dragonlauncher.ui.actions.launchAppDirectly
import org.elnix.dragonlauncher.ui.actions.launchSwipeAction
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.dialogs.AppAliasesDialog
import org.elnix.dragonlauncher.ui.dialogs.AppLongPressDialog
import org.elnix.dragonlauncher.ui.dialogs.IconEditorDialog
import org.elnix.dragonlauncher.ui.dialogs.RenameAppDialog
import org.elnix.dragonlauncher.ui.helpers.AppGrid
import org.elnix.dragonlauncher.ui.helpers.WallpaperDim
import org.elnix.dragonlauncher.ui.wellbeing.DigitalPauseActivity

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
    onClose: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()


    val workspaceState by appsViewModel.enabledState.collectAsState()
    val workspaces = workspaceState.workspaces
    val overrides = workspaceState.appOverrides
    val aliases = workspaceState.appAliases

    val selectedWorkspaceId by appsViewModel.selectedWorkspaceId.collectAsState()
    val initialIndex = workspaces.indexOfFirst { it.id == selectedWorkspaceId }
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (workspaces.size - 1).coerceAtLeast(0)),
        pageCount = { workspaces.size }
    )

    val icons by appsViewModel.icons.collectAsState()

    val autoLaunchSingleMatch by DrawerSettingsStore
        .autoOpenSingleMatch.asState()


    /* ───────────── Actions ───────────── */
    val tapEmptySpaceToRaiseKeyboard by DrawerSettingsStore
        .tapEmptySpaceAction.asState()

    val drawerEnterAction by DrawerSettingsStore.drawerEnterAction.asState()
    val drawerBackAction by DrawerSettingsStore.drawerEnterAction.asState()
    val drawerHomeAction by DrawerSettingsStore.drawerHomeAction.asState()
    val drawerScrollDownAction by DrawerSettingsStore.scrollDownDrawerAction.asState()
    val drawerScrollUpAction by DrawerSettingsStore.scrollUpDrawerAction.asState()

    val iconsShape by DrawerSettingsStore.iconsShape.asState()


    /*  ─────────────  Wellbeing Settings  ─────────────  */
    val socialMediaPauseEnabled by WellbeingSettingsStore.socialMediaPauseEnabled.asState()
    val guiltModeEnabled by WellbeingSettingsStore.guiltModeEnabled.asState()
    val pauseDuration by WellbeingSettingsStore.pauseDurationSeconds.asState()
    val pausedApps by WellbeingSettingsStore.getPausedAppsFlow(ctx)
        .collectAsState(initial = emptySet())

    var pendingPackageToLaunch by remember { mutableStateOf<String?>(null) }

    val digitalPauseLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == DigitalPauseActivity.RESULT_PROCEED && pendingPackageToLaunch != null) {
            try {
                launchAppDirectly(ctx, pendingPackageToLaunch!!)
                onClose()
            } catch (e: Exception) {
                ctx.showToast("Error: ${e.message}")
            }
        }
        pendingPackageToLaunch = null
    }


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

    LaunchedEffect(pagerState.currentPage) {
        workspaceId = workspaces.getOrNull(pagerState.currentPage)?.id ?: return@LaunchedEffect
        appsViewModel.selectWorkspace(workspaceId!!)
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

    fun launchApp(action: SwipeActionSerializable) {
        // Store package for potential pause callback
        if (action is SwipeActionSerializable.LaunchApp) {
            pendingPackageToLaunch = action.packageName
        }

        try {
            launchSwipeAction(
                ctx = ctx,
                action = action,
                pausedApps = pausedApps,
                socialMediaPauseEnabled = socialMediaPauseEnabled,
                guiltModeEnabled = guiltModeEnabled,
                pauseDuration = pauseDuration,
                digitalPauseLauncher = digitalPauseLauncher
            )
            // Only close if not paused (pause closes after user decision)
            if (!socialMediaPauseEnabled ||
                action !is SwipeActionSerializable.LaunchApp ||
                action.packageName !in pausedApps) {
                onClose()
            }
        } catch (e: Exception) {
            onClose()
            ctx.showToast("Error: ${e.message}")
        }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = tapEmptySpaceToRaiseKeyboard.isUsed(),
                indication = null,
                interactionSource = null
            ) {
                toggleKeyboard()
            }
            .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.ime))
    ) {

        if (!searchBarBottom) {
            DrawerTextInput()
        }

        Row(modifier = Modifier.fillMaxSize()) {

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
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                HorizontalPager(
                    state = pagerState,
                    key = { it.hashCode() }
                ) { pageIndex ->

                    val workspace = workspaces[pageIndex]

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
                            launchApp(filteredApps.first().action)
                        }
                    }


                    AppGrid(
                        apps = filteredApps,
                        icons = icons,
                        gridSize = gridSize,
                        iconShape = iconsShape,
                        txtColor = MaterialTheme.colorScheme.onSurface,
                        showIcons = showIcons,
                        showLabels = showLabels,
                        onLongClick = { dialogApp = it },
                        onScrollDown = { launchDrawerAction(drawerScrollDownAction) },
                        onScrollUp = { launchDrawerAction(drawerScrollUpAction) }
                    ) {
                        launchApp(it.action)
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

    if (dialogApp != null) {
        val app = dialogApp!!

        AppLongPressDialog(
            app = app,
            onDismiss = { dialogApp = null },
            onOpen = { launchApp(app.action) },
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
            dummySwipePoint(SwipeActionSerializable.LaunchApp(pkg), pkg).copy(
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
            .onFocusChanged { focusState ->
                val focused = focusState.isFocused
                onFocusStateChanged(focused) // Notify parent of focus change
                if (focused) {
                    keyboardController?.show() // Show keyboard when TextField gains focus
                }
                // Keyboard hiding on focus loss is handled by system, IME actions, or explicit calls elsewhere (e.g., scroll logic)
            },
        placeholder = {
            Text(
                text = stringResource(R.string.search_apps),
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            // Don't hide the keyboard on enter, just clear the search
//            keyboardController?.hide() // Hide keyboard on IME "Search" action
            onEnterPressed()
        }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}
