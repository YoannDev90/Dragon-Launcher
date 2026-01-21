package org.elnix.dragonlauncher.ui.drawer

import android.content.Intent
import android.provider.Settings
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.dummySwipePoint
import org.elnix.dragonlauncher.common.utils.openSearch
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.enumsui.DrawerActions
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.actions.launchSwipeAction
import org.elnix.dragonlauncher.ui.dialogs.AppLongPressDialog
import org.elnix.dragonlauncher.ui.dialogs.IconEditorDialog
import org.elnix.dragonlauncher.ui.dialogs.RenameAppDialog
import org.elnix.dragonlauncher.ui.helpers.AppGrid

@Suppress("AssignedValueIsNeverRead")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppDrawerScreen(
    appsViewModel: AppsViewModel,
    appLifecycleViewModel: AppLifecycleViewModel,
//    wallpaperViewModel: WallpaperViewModel,
    showIcons: Boolean,
    showLabels: Boolean,
    autoShowKeyboard: Boolean,
    gridSize: Int,
    searchBarBottom: Boolean,
    leftAction: DrawerActions,
    leftWidth: Float,
    rightAction: DrawerActions,
    rightWidth: Float,
    onClose: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()



//    val activity = LocalContext.current as? Activity
//    val window = activity?.window
//
//    val drawerBlurRadius by wallpaperViewModel.blurRadiusDrawerScreen.collectAsState(0)
//
//    LaunchedEffect(Unit, drawerBlurRadius) {
//        if (Build.VERSION.SDK_INT >= 31) {
//            window?.setBackgroundBlurRadius(drawerBlurRadius)
//        }
//    }

//    /* ───────────── Reload all apps asynchronously on entering drawer (icons + apps) ───────────── */
//    LaunchedEffect(Unit) {
//        scope.launch{ appsViewModel.reloadApps() }
//    }


    val homeActionDetected by appLifecycleViewModel.homeActionDetected.collectAsState()

    val drawerHomeAction by DrawerSettingsStore.getDrawerHomeAction(ctx)
        .collectAsState(initial = DrawerActions.CLOSE)


    val workspaceState by appsViewModel.enabledState.collectAsState()
    val workspaces = workspaceState.workspaces
    val overrides = workspaceState.appOverrides

    val selectedWorkspaceId by appsViewModel.selectedWorkspaceId.collectAsState()
    val initialIndex = workspaces.indexOfFirst { it.id == selectedWorkspaceId }
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (workspaces.size - 1).coerceAtLeast(0)),
        pageCount = { workspaces.size }
    )

    val icons by appsViewModel.icons.collectAsState()

    val autoLaunchSingleMatch by DrawerSettingsStore
        .getAutoLaunchSingleMatch(ctx)
        .collectAsState(initial = true)

    val clickEmptySpaceToRaiseKeyboard by DrawerSettingsStore
        .getClickEmptySpaceToRaiseKeyboard(ctx)
        .collectAsState(initial = false)

    val drawerEnterAction by DrawerSettingsStore.getDrawerEnterAction(ctx)
        .collectAsState(initial = DrawerActions.CLEAR)

    val scrollDownToCloseDrawerOnTop by DrawerSettingsStore.getScrollDownToCloseDrawerOnTop(ctx)
        .collectAsState(initial = true)




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


    fun toggleKeyboard() {
        if (isSearchFocused) {
            focusManager.clearFocus()
            keyboardController?.hide()
        } else {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    fun launchDrawerAction(action: DrawerActions) {
        when (action) {
            DrawerActions.CLOSE -> onClose()
            DrawerActions.TOGGLE_KB -> toggleKeyboard()

            DrawerActions.CLEAR -> searchQuery = ""
            DrawerActions.SEARCH_WEB -> {
                if (searchQuery.isNotBlank()) ctx.openSearch(searchQuery)
            }
            DrawerActions.OPEN_FIRST_APP -> haveToLaunchFirstApp = true
            DrawerActions.NONE, DrawerActions.DISABLED -> {}
        }
    }

    LaunchedEffect(homeActionDetected) {
        if (homeActionDetected) launchDrawerAction(drawerHomeAction)
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
        try {
            launchSwipeAction(ctx, action)
            onClose()
        } catch (e: Exception) {
            onClose()
            ctx.showToast("Error: ${e.message}")
        }
    }

//    if (useWallpaper) {
//        wallpaper?.let { bmp ->
//            Image(
//                bitmap = bmp.asImageBitmap(),
//                contentDescription = null,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .background(Color.Transparent)

//            .then(
//                if (!useWallpaper) Modifier.background(MaterialTheme.colorScheme.background)
//                else Modifier
//            )
            .clickable(
                enabled = clickEmptySpaceToRaiseKeyboard,
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

            if (leftAction != DrawerActions.DISABLED) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(leftWidth)
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
                            else apps.filter {
                                it.name.contains(searchQuery, ignoreCase = true)
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
                        txtColor = MaterialTheme.colorScheme.onSurface,
                        showIcons = showIcons,
                        showLabels = showLabels,
                        onLongClick = { dialogApp = it },
                        onClose = if (scrollDownToCloseDrawerOnTop) onClose else null
                    ) {
                        launchApp(it.action)
                    }
                }
            }

            if (rightAction != DrawerActions.DISABLED) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(rightWidth)
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
            onChangeAppIcon = {
                appTarget = app
            }
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

//        if (iconOverride == null) {
//            scope.launch { appsViewModel.reloadPointIcon(tempPoint) }
//        }

        IconEditorDialog(
            point = tempPoint,
            appsViewModel = appsViewModel,
            onReset = {
                appsViewModel.updateSingleIcon(app, false)
            },
            onDismiss = { appTarget = null }
        ) {

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
            }
            appTarget = null
        }
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
        placeholder = { Text(
            text = stringResource(R.string.search_apps),
            color = MaterialTheme.colorScheme.onBackground
        ) },
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
