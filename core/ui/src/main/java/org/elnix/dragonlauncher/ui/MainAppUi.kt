package org.elnix.dragonlauncher.ui

import android.R.attr.versionCode
import android.content.ComponentName
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.FloatingAppObject
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.ROUTES
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.WidgetHostProvider
import org.elnix.dragonlauncher.common.utils.getVersionCode
import org.elnix.dragonlauncher.common.utils.hasUriReadWritePermission
import org.elnix.dragonlauncher.common.utils.isDefaultLauncher
import org.elnix.dragonlauncher.common.utils.loadChangelogs
import org.elnix.dragonlauncher.enumsui.DrawerActions
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.models.BackupViewModel
import org.elnix.dragonlauncher.models.FloatingAppsViewModel
import org.elnix.dragonlauncher.settings.stores.BackupSettingsStore
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.ui.dialogs.UserValidation
import org.elnix.dragonlauncher.ui.dialogs.WidgetPickerDialog
import org.elnix.dragonlauncher.ui.drawer.AppDrawerScreen
import org.elnix.dragonlauncher.ui.helpers.ReselectAutoBackupBanner
import org.elnix.dragonlauncher.ui.helpers.SetDefaultLauncherBanner
import org.elnix.dragonlauncher.ui.settings.backup.BackupTab
import org.elnix.dragonlauncher.ui.settings.customization.AppearanceTab
import org.elnix.dragonlauncher.ui.settings.customization.BehaviorTab
import org.elnix.dragonlauncher.ui.settings.customization.ColorSelectorTab
import org.elnix.dragonlauncher.ui.settings.customization.DrawerTab
import org.elnix.dragonlauncher.ui.settings.customization.FloatingAppsTab
import org.elnix.dragonlauncher.ui.settings.customization.IconPackTab
import org.elnix.dragonlauncher.ui.settings.customization.StatusBarTab
import org.elnix.dragonlauncher.ui.settings.customization.ThemesTab
import org.elnix.dragonlauncher.ui.settings.customization.WallpaperTab
import org.elnix.dragonlauncher.ui.settings.debug.DebugTab
import org.elnix.dragonlauncher.ui.settings.debug.LogsTab
import org.elnix.dragonlauncher.ui.settings.debug.SettingsDebugTab
import org.elnix.dragonlauncher.ui.settings.language.LanguageTab
import org.elnix.dragonlauncher.ui.settings.workspace.WorkspaceDetailScreen
import org.elnix.dragonlauncher.ui.settings.workspace.WorkspaceListScreen
import org.elnix.dragonlauncher.ui.welcome.WelcomeScreen
import org.elnix.dragonlauncher.ui.whatsnew.ChangelogsScreen
import org.elnix.dragonlauncher.ui.whatsnew.WhatsNewBottomSheet


@Suppress("AssignedValueIsNeverRead")
@Composable
fun MainAppUi(
    backupViewModel: BackupViewModel,
    appsViewModel: AppsViewModel,
    floatingAppsViewModel: FloatingAppsViewModel,
    navController: NavHostController,
    widgetHostProvider: WidgetHostProvider,
    onBindCustomWidget: (Int, ComponentName, nestId: Int) -> Unit,
    onLaunchSystemWidgetPicker: (nestId: Int) -> Unit,
    onResetWidgetSize: (id: Int, widgetId: Int) -> Unit,
    onRemoveFloatingApp: (FloatingAppObject) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val result by backupViewModel.result.collectAsState()

    // Changelogs system
    val lastSeenVersionCode by PrivateSettingsStore.getLastSeenVersionCode(ctx)
        .collectAsState(initial = Int.MAX_VALUE)

    val currentVersionCode = getVersionCode(ctx)
    var showWhatsNewBottomSheet by remember { mutableStateOf(false) }

    var showWidgetPicker by remember { mutableStateOf<Int?>(null) }

    val updates by produceState(initialValue = emptyList()) {
        value = loadChangelogs(ctx, versionCode)
    }

    val showAppIconsInDrawer by DrawerSettingsStore.getShowAppIconsInDrawer(ctx)
        .collectAsState(initial = true)

    val showAppLabelsInDrawer by DrawerSettingsStore.getShowAppLabelsInDrawer(ctx)
        .collectAsState(initial = true)

    val autoShowKeyboardOnDrawer by DrawerSettingsStore.getAutoShowKeyboardOnDrawer(ctx)
        .collectAsState(initial = false)

    val gridSize by DrawerSettingsStore.getGridSize(ctx)
        .collectAsState(initial = 1)

//    val searchBarBottom by DrawerSettingsStore.getSearchBarBottom(ctx)
//        .collectAsState(initial = true)
    val searchBarBottom = false



    val leftDrawerAction by DrawerSettingsStore.getLeftDrawerAction(ctx)
        .collectAsState(initial = DrawerActions.TOGGLE_KB)

    val rightDrawerAction by DrawerSettingsStore.getRightDrawerAction(ctx)
        .collectAsState(initial = DrawerActions.CLOSE)

    val leftDrawerWidth by DrawerSettingsStore.getLeftDrawerWidth(ctx)
        .collectAsState(initial = 0.1f)
    val rightDrawerWidth  by DrawerSettingsStore.getRightDrawerWidth(ctx)
        .collectAsState(initial = 0.1f)

    val forceAppWidgetsSelector by DebugSettingsStore.getForceAppWidgetsSelector(ctx)
        .collectAsState(initial = false)


    val showSetDefaultLauncherBanner by PrivateSettingsStore.getShowSetDefaultLauncherBanner(ctx)
        .collectAsState(initial = false)


    val lifecycleOwner = LocalLifecycleOwner.current
    var isDefaultLauncher by remember { mutableStateOf(ctx.isDefaultLauncher) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val autoBackupEnabled by BackupSettingsStore.getAutoBackupEnabled(ctx).collectAsState(initial = false)
    val autoBackupUriString by BackupSettingsStore.getAutoBackupUri(ctx).collectAsState(initial = null)
    val autoBackupUri = autoBackupUriString?.toUri()


    LaunchedEffect(Unit, lastSeenVersionCode, currentRoute) {
        showWhatsNewBottomSheet = lastSeenVersionCode < currentVersionCode && currentRoute != ROUTES.WELCOME
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // The activity resumes when the user returns from the Home settings screen
            if (event == Lifecycle.Event.ON_RESUME) {
                // IMPORTANT: Re-check the status and update the state
                isDefaultLauncher = ctx.isDefaultLauncher
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the composable leaves the screen, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    fun goMainScreen() {
        navController.navigate(ROUTES.MAIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun goSettingsRoot() =  navController.navigate(SETTINGS.ROOT)
    fun goAdvSettingsRoot() =  navController.navigate(SETTINGS.ADVANCED_ROOT)
    fun goDrawer() = navController.navigate(ROUTES.DRAWER)
    fun goWelcome() = navController.navigate(ROUTES.WELCOME)
    fun goAppearance() = navController.navigate(SETTINGS.APPEARANCE)
    fun goDebug() = navController.navigate(SETTINGS.DEBUG)



    fun launchWidgetsPicker(nestId: Int) {
        if (!forceAppWidgetsSelector) onLaunchSystemWidgetPicker(nestId)
        else showWidgetPicker = nestId
    }


    val showSetAsDefaultBanner = showSetDefaultLauncherBanner &&
            !isDefaultLauncher &&
            currentRoute != ROUTES.WELCOME


    var hasAutoBackupPermission by remember {
        mutableStateOf<Boolean?>(null)
    }

    LaunchedEffect(autoBackupUri) {
        hasAutoBackupPermission = if (autoBackupUri == null) {
            null
        } else {
            ctx.hasUriReadWritePermission(autoBackupUri)
        }
    }


    val showReselectAutoBackupFile =
        autoBackupEnabled &&
        hasAutoBackupPermission == false &&
        autoBackupUri != null &&
        currentRoute != ROUTES.WELCOME



    val autoBackupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            ctx.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            hasAutoBackupPermission = true

            scope.launch {
                BackupSettingsStore.setAutoBackupUri(ctx, uri)
                BackupSettingsStore.setAutoBackupEnabled(ctx, true)
            }
        }
    }

    val containerColor =
        if (currentRoute == ROUTES.DRAWER || currentRoute == ROUTES.MAIN || currentRoute == SETTINGS.WALLPAPER) {
            Color.Transparent
        } else {
            MaterialTheme.colorScheme.background
        }


    Scaffold(
        topBar = {
            Column {
                if (showSetAsDefaultBanner) {
                    SetDefaultLauncherBanner()
                }
                if (showReselectAutoBackupFile) {
                    ReselectAutoBackupBanner {
                        autoBackupLauncher.launch("dragonlauncher-auto-backup.json")
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = containerColor,
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ROUTES.MAIN,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Main App (LauncherScreen)
            composable(ROUTES.MAIN) {
                MainScreen(
                    appsViewModel = appsViewModel,
                    floatingAppsViewModel = floatingAppsViewModel,
                    widgetHostProvider = widgetHostProvider,
                    onAppDrawer = { goDrawer() },
                    onGoWelcome = { goWelcome() },
                    onLongPress3Sec = { goSettingsRoot() }
                )
            }

            composable(ROUTES.DRAWER) {
                AppDrawerScreen(
                    appsViewModel = appsViewModel,
                    showIcons = showAppIconsInDrawer,
                    showLabels = showAppLabelsInDrawer,
                    autoShowKeyboard = autoShowKeyboardOnDrawer,
                    gridSize = gridSize,
                    searchBarBottom = searchBarBottom,
                    leftAction = leftDrawerAction,
                    leftWidth = leftDrawerWidth,
                    rightAction = rightDrawerAction,
                    rightWidth = rightDrawerWidth
                ) { goMainScreen() }
            }


            // Settings + Welcome

            composable(ROUTES.WELCOME) {
                WelcomeScreen(
                    backupVm =  backupViewModel,
                    onEnterSettings = { goSettingsRoot() },
                    onEnterApp = { goMainScreen() }
                )
            }

            composable(SETTINGS.ROOT) {
                SettingsScreen(
                    appsViewModel = appsViewModel,
                    onAdvSettings = { goAdvSettingsRoot() },
                    onBack = { goMainScreen() }
                )
            }
            composable(SETTINGS.ADVANCED_ROOT) { AdvancedSettingsScreen(appsViewModel, navController ) { goSettingsRoot() } }

            composable(SETTINGS.APPEARANCE)    { AppearanceTab(appsViewModel, navController) { goAdvSettingsRoot() } }
            composable(SETTINGS.WALLPAPER)     { WallpaperTab { goAppearance() } }
            composable(SETTINGS.ICON_PACK)     { IconPackTab(appsViewModel) { goAppearance() } }
            composable(SETTINGS.STATUS_BAR)    { StatusBarTab(appsViewModel) { goAppearance() } }
            composable(SETTINGS.THEME)         { ThemesTab { goAppearance() } }
            composable(SETTINGS.FLOATING_APPS) {
                FloatingAppsTab(
                    appsViewModel = appsViewModel,
                    floatingAppsViewModel = floatingAppsViewModel,
                    widgetHostProvider = widgetHostProvider,
                    onBack = ::goAppearance,
                    onLaunchSystemWidgetPicker = ::launchWidgetsPicker,
                    onResetWidgetSize = onResetWidgetSize,
                    onRemoveWidget = onRemoveFloatingApp
                )
            }
            composable(SETTINGS.BEHAVIOR)      { BehaviorTab(appsViewModel) { goAdvSettingsRoot() } }
            composable(SETTINGS.DRAWER)        { DrawerTab(appsViewModel) { goAdvSettingsRoot() } }
            composable(SETTINGS.COLORS)        { ColorSelectorTab { goAppearance() } }
            composable(SETTINGS.DEBUG)         { DebugTab(navController, appsViewModel, onShowWelcome = { goWelcome() } ) { goAdvSettingsRoot() } }
            composable(SETTINGS.LOGS)          { LogsTab { goDebug() } }
            composable(SETTINGS.SETTINGS_JSON) { SettingsDebugTab { goDebug() } }
            composable(SETTINGS.LANGUAGE)      { LanguageTab { goAdvSettingsRoot() } }
            composable(SETTINGS.BACKUP)        { BackupTab(backupViewModel) { goAdvSettingsRoot() } }
            composable(SETTINGS.CHANGELOGS)    { ChangelogsScreen { goAdvSettingsRoot() } }

            composable(SETTINGS.WORKSPACE) {
                WorkspaceListScreen(
                    appsViewModel = appsViewModel,
                    onOpenWorkspace = { id ->
                        navController.navigate(
                            SETTINGS.WORKSPACE_DETAIL.replace("{id}", id)
                        )
                    },
                    onBack = { goAdvSettingsRoot() }
                )
            }

            composable(
                SETTINGS.WORKSPACE_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStack ->
                WorkspaceDetailScreen(
                    workspaceId = backStack.arguments!!.getString("id")!!,
                    appsViewModel = appsViewModel,
                    showIcons = showAppIconsInDrawer,
                    showLabels = showAppLabelsInDrawer,
                    gridSize = gridSize,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }

    if (showWhatsNewBottomSheet) {
        WhatsNewBottomSheet(
            updates = updates
        ) {
            showWhatsNewBottomSheet = false
            scope.launch { PrivateSettingsStore.setLastSeenVersionCode(ctx, currentVersionCode) }
        }
    }

    if (showWidgetPicker != null) {
        val nestToBind = showWidgetPicker!!
        WidgetPickerDialog(
            onBindCustomWidget = { id, info ->
                onBindCustomWidget(id, info, nestToBind)
            }
        ) { showWidgetPicker = null }
    }

    // ------------------------------------------------------------
    // RESULT DIALOG ( IMPORT / EXPORT )
    // ------------------------------------------------------------
    result?.let { res ->
        val isError = res.error
        val isExport = res.export
        val errorMessage = res.message

        UserValidation(
            title = when {
                isError && isExport -> stringResource(R.string.export_failed)
                isError && !isExport -> stringResource(R.string.import_failed)
                !isError && isExport -> stringResource(R.string.export_successful)
                else -> stringResource(R.string.import_successful)
            },
            message = when {
                isError -> errorMessage.ifBlank { stringResource(R.string.unknown_error) }
                isExport -> stringResource(R.string.export_successful)
                else -> stringResource(R.string.import_successful)
            },
            titleIcon = if (isError) Icons.Default.Warning else Icons.Default.Check,
            titleColor = if (isError) MaterialTheme.colorScheme.error else Color.Green,
            cancelText = null,
            copy = isError,
            onCancel = {},
            onAgree = { backupViewModel.setResult(null) }
        )
    }
}
