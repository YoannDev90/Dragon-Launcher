package org.elnix.dragonlauncher.ui

import android.R.attr.versionCode
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.FloatingAppObject
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.common.utils.Constants.Navigation.transparentScreens
import org.elnix.dragonlauncher.common.utils.ROUTES
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.WidgetHostProvider
import org.elnix.dragonlauncher.common.utils.getVersionCode
import org.elnix.dragonlauncher.common.utils.hasUriReadWritePermission
import org.elnix.dragonlauncher.common.utils.isDefaultLauncher
import org.elnix.dragonlauncher.common.utils.loadChangelogs
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.enumsui.LockMethod
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.models.BackupViewModel
import org.elnix.dragonlauncher.models.FloatingAppsViewModel
import org.elnix.dragonlauncher.settings.stores.BackupSettingsStore
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.components.settings.asStateNull
import org.elnix.dragonlauncher.ui.dialogs.PinUnlockDialog
import org.elnix.dragonlauncher.ui.dialogs.UserValidation
import org.elnix.dragonlauncher.ui.dialogs.WidgetPickerDialog
import org.elnix.dragonlauncher.ui.drawer.AppDrawerScreen
import org.elnix.dragonlauncher.ui.helpers.ReselectAutoBackupBanner
import org.elnix.dragonlauncher.ui.helpers.SecurityHelper
import org.elnix.dragonlauncher.ui.helpers.SetDefaultLauncherBanner
import org.elnix.dragonlauncher.ui.helpers.findFragmentActivity
import org.elnix.dragonlauncher.ui.helpers.noAnimComposable
import org.elnix.dragonlauncher.ui.settings.backup.BackupTab
import org.elnix.dragonlauncher.ui.settings.customization.AppearanceTab
import org.elnix.dragonlauncher.ui.settings.customization.BehaviorTab
import org.elnix.dragonlauncher.ui.settings.customization.DrawerTab
import org.elnix.dragonlauncher.ui.settings.customization.FloatingAppsTab
import org.elnix.dragonlauncher.ui.settings.customization.IconPackTab
import org.elnix.dragonlauncher.ui.settings.customization.NestEditingScreen
import org.elnix.dragonlauncher.ui.settings.customization.StatusBarTab
import org.elnix.dragonlauncher.ui.settings.customization.ThemesTab
import org.elnix.dragonlauncher.ui.settings.customization.WallpaperTab
import org.elnix.dragonlauncher.ui.settings.customization.colors.ColorSelectorTab
import org.elnix.dragonlauncher.ui.settings.debug.DebugTab
import org.elnix.dragonlauncher.ui.settings.debug.LogsTab
import org.elnix.dragonlauncher.ui.settings.debug.SettingsDebugTab
import org.elnix.dragonlauncher.ui.settings.language.LanguageTab
import org.elnix.dragonlauncher.ui.settings.wellbeing.WellbeingTab
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
    appLifecycleViewModel: AppLifecycleViewModel,
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
    val lastSeenVersionCode by PrivateSettingsStore.lastSeenVersionCode.asState()

    val currentVersionCode = getVersionCode(ctx)
    var showWhatsNewBottomSheet by remember { mutableStateOf(false) }

    var showWidgetPicker by remember { mutableStateOf<Int?>(null) }

    val updates by produceState(initialValue = emptyList()) {
        value = loadChangelogs(ctx, versionCode)
    }

    val showAppIconsInDrawer by DrawerSettingsStore.showAppIconsInDrawer.asState()
    val showAppLabelsInDrawer by DrawerSettingsStore.showAppLabelInDrawer.asState()
    val autoShowKeyboardOnDrawer by DrawerSettingsStore.autoShowKeyboardOnDrawer.asState()
    val gridSize by DrawerSettingsStore.gridSize.asState()
    val searchBarBottom by DrawerSettingsStore.searchBarBottom.asState()


    val leftDrawerAction by DrawerSettingsStore.leftDrawerAction.asState()
    val rightDrawerAction by DrawerSettingsStore.rightDrawerAction.asState()

    val leftDrawerWidth by DrawerSettingsStore.leftDrawerWidth.asState()
    val rightDrawerWidth by DrawerSettingsStore.rightDrawerWidth.asState()

    val forceAppWidgetsSelector by DebugSettingsStore.forceAppWidgetsSelector.asState()

    val showSetDefaultLauncherBanner by PrivateSettingsStore.showSetDefaultLauncherBanner.asState()


    val lifecycleOwner = LocalLifecycleOwner.current
    var isDefaultLauncher by remember { mutableStateOf(ctx.isDefaultLauncher) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val autoBackupEnabled by BackupSettingsStore.autoBackupEnabled.asState()
    val autoBackupUriString by BackupSettingsStore.autoBackupUri.asStateNull()
    val autoBackupUri = autoBackupUriString?.toUri()

    val nests by SwipeSettingsStore.getNestsFlow(ctx).collectAsState(initial = emptyList())
    val points by SwipeSettingsStore.getPointsFlow(ctx).collectAsState(emptyList())

    val pointIcons by appsViewModel.pointIcons.collectAsState()
    val defaultPoint by appsViewModel.defaultPoint.collectAsState(defaultSwipePointsValues)


    var startDestination by remember { mutableStateOf(SETTINGS.ROOT) }


    LaunchedEffect(Unit, lastSeenVersionCode, currentRoute) {
        showWhatsNewBottomSheet =
            lastSeenVersionCode < currentVersionCode && currentRoute != ROUTES.WELCOME
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

        // When the noAnimComposable leaves the screen, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }



    /* navigation functions, all settings are nested under the lock state */

    fun goMainScreen() {
        navController.navigate(ROUTES.MAIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun goDrawer() = navController.navigate(ROUTES.DRAWER)
    fun goWelcome() = navController.navigate(ROUTES.WELCOME)



    // ── Lock gate state ──
    val lockMethod by PrivateSettingsStore.lockMethod.asState()
    val pinHash by PrivateSettingsStore.lockPinHash.asState()

    /** Once unlocked during this session, stay unlocked */
    var isUnlocked by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        appLifecycleViewModel.homeEvents.collect {
            isUnlocked = false
            goMainScreen()
        }
    }

    @SuppressLint("LocalContextGetResourceValueCall")
    fun goSettings(route: String) {
        if (isUnlocked || lockMethod == LockMethod.NONE) {
            navController.navigate(route)
            return
        }
        @Suppress("KotlinConstantConditions")
        when (lockMethod) {
            LockMethod.PIN -> {
                showPinDialog = true
            }

            LockMethod.DEVICE_UNLOCK -> {
                val activity = ctx.findFragmentActivity()
                if (activity != null && SecurityHelper.isDeviceUnlockAvailable(ctx)) {
                    SecurityHelper.showDeviceUnlockPrompt(
                        activity = activity,
                        onSuccess = {
                            isUnlocked = true
                            navController.navigate(route)
                        },
                        onError = { msg ->
                            ctx.showToast(ctx.getString(R.string.authentication_error, msg))
                        },
                        onFailed = {
                            ctx.showToast(ctx.getString(R.string.authentication_failed))
                        }
                    )
                } else {
                    ctx.showToast(ctx.getString(R.string.device_credentials_not_available))
                }
            }

            LockMethod.NONE -> navController.navigate(route)
        }
    }

    fun goSettingsRoot() = goSettings(SETTINGS.ROOT)
    fun goAdvSettingsRoot() = goSettings(SETTINGS.ADVANCED_ROOT)


    var pendingNestToEdit by remember { mutableStateOf<Int?>(null) }

    fun navigateToAdvSettings() = goSettings(SETTINGS.ADVANCED_ROOT)
    fun goAppearance() = goSettings(SETTINGS.APPEARANCE)
    fun goDebug() = goSettings(SETTINGS.DEBUG)
    fun goNestEdit(nest: Int) {
        pendingNestToEdit = nest
        goSettings(SETTINGS.NESTS_EDIT)
    }





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
                BackupSettingsStore.autoBackupUri.set(ctx, uri.toString())
                BackupSettingsStore.autoBackupEnabled.set(ctx, true)
            }
        }
    }

    val containerColor =
        if (currentRoute in transparentScreens) {
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
        contentWindowInsets = WindowInsets(),
        containerColor = containerColor,
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ROUTES.MAIN,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Main App (LauncherScreen)
            noAnimComposable(ROUTES.MAIN) {
                MainScreen(
                    appsViewModel = appsViewModel,
                    floatingAppsViewModel = floatingAppsViewModel,
                    appLifecycleViewModel = appLifecycleViewModel,
                    widgetHostProvider = widgetHostProvider,
                    onAppDrawer = { workspaceId ->
                        if (workspaceId != null) {
                            appsViewModel.selectWorkspace(workspaceId)
                        }
                        goDrawer()
                    },
                    onGoWelcome = ::goWelcome,
                    onSettings = ::goSettings
                )
            }

            noAnimComposable(ROUTES.DRAWER) {
                AppDrawerScreen(
                    appsViewModel = appsViewModel,
                    appLifecycleViewModel = appLifecycleViewModel,
                    showIcons = showAppIconsInDrawer,
                    showLabels = showAppLabelsInDrawer,
                    autoShowKeyboard = autoShowKeyboardOnDrawer,
                    gridSize = gridSize,
                    searchBarBottom = searchBarBottom,
                    leftAction = leftDrawerAction,
                    leftWeight = leftDrawerWidth,
                    rightAction = rightDrawerAction,
                    rightWeight = rightDrawerWidth
                ) { goMainScreen() }
            }

            // Welcome screen
            noAnimComposable(ROUTES.WELCOME) {
                WelcomeScreen(
                    backupVm = backupViewModel,
                    onEnterSettings = ::goSettingsRoot,
                    onEnterApp = ::goMainScreen
                )
            }


            /* ───────────── Settings navigation ───────────── */
            navigation(
                startDestination = startDestination,
                route = "settings_graph"
            ) {
                noAnimComposable(SETTINGS.ROOT) {
                    SettingsScreen(
                        appsViewModel = appsViewModel,
                        pointIcons = pointIcons,
                        defaultPoint = defaultPoint,
                        nests = nests,
                        onAdvSettings = ::goAdvSettingsRoot,
                        onNestEdit = ::goNestEdit,
                        onBack = ::goMainScreen
                    )
                }
                noAnimComposable(SETTINGS.ADVANCED_ROOT) {
                    AdvancedSettingsScreen(
                        appsViewModel,
                        navController
                    ) { goSettingsRoot() }
                }

                noAnimComposable(SETTINGS.APPEARANCE) {
                    AppearanceTab(
                        appsViewModel,
                        navController,
                        ::goAdvSettingsRoot
                    )
                }
                noAnimComposable(SETTINGS.WALLPAPER) { WallpaperTab(::goAppearance) }
                noAnimComposable(SETTINGS.ICON_PACK) { IconPackTab(appsViewModel, ::goAppearance) }
                noAnimComposable(SETTINGS.STATUS_BAR) {
                    StatusBarTab(
                        appsViewModel,
                        ::goAppearance
                    )
                }
                noAnimComposable(SETTINGS.THEME) { ThemesTab(::goAppearance) }

                noAnimComposable(SETTINGS.BEHAVIOR) {
                    BehaviorTab(
                        appsViewModel,
                        ::goAdvSettingsRoot
                    )
                }
                noAnimComposable(SETTINGS.DRAWER) { DrawerTab(appsViewModel, ::goAdvSettingsRoot) }
                noAnimComposable(SETTINGS.COLORS) { ColorSelectorTab(::goAppearance) }
                noAnimComposable(SETTINGS.DEBUG) {
                    DebugTab(
                        navController,
                        appsViewModel,
                        onShowWelcome = ::goWelcome,
                        ::goAdvSettingsRoot
                    )
                }
                noAnimComposable(SETTINGS.LOGS) { LogsTab(::goDebug) }
                noAnimComposable(SETTINGS.SETTINGS_JSON) { SettingsDebugTab(::goDebug) }
                noAnimComposable(SETTINGS.LANGUAGE) { LanguageTab(::goAdvSettingsRoot) }
                noAnimComposable(SETTINGS.BACKUP) {
                    BackupTab(
                        backupViewModel,
                        ::goAdvSettingsRoot
                    )
                }
                noAnimComposable(SETTINGS.CHANGELOGS) { ChangelogsScreen(::goAdvSettingsRoot) }

                noAnimComposable(SETTINGS.WELLBEING) {
                    WellbeingTab(
                        appsViewModel = appsViewModel,
                        onBack = ::goAdvSettingsRoot
                    )
                }

                noAnimComposable(SETTINGS.NESTS_EDIT) {
                    NestEditingScreen(
                        nestId = pendingNestToEdit,
                        nests = nests,
                        points = points,
                        pointIcons = pointIcons,
                        defaultPoint = defaultPoint,
                        onBack = ::goSettingsRoot
                    )
                }

                noAnimComposable(SETTINGS.FLOATING_APPS) {
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

                noAnimComposable(SETTINGS.WORKSPACE) {
                    WorkspaceListScreen(
                        appsViewModel = appsViewModel,
                        onOpenWorkspace = { id ->
                            navController.navigate(
                                SETTINGS.WORKSPACE_DETAIL.replace("{id}", id)
                            )
                        },
                        onBack = ::goAdvSettingsRoot
                    )
                }

                composable(
                    route = SETTINGS.WORKSPACE_DETAIL,
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None }
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
    }

    if (showWhatsNewBottomSheet) {
        WhatsNewBottomSheet(
            updates = updates
        ) {
            showWhatsNewBottomSheet = false
            scope.launch { PrivateSettingsStore.lastSeenVersionCode.set(ctx, currentVersionCode) }
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

    /* ───────────── RESULT DIALOG ( IMPORT / EXPORT ) ───────────── */
    result?.let { res ->
        val isError = res.error
        val isExport = res.export
        val errorMessage = res.message

        // Reload the whole viewModel data after restore
        scope.launch {
            appsViewModel.loadAll()
        }

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
                else -> null
            },
            titleIcon = if (isError) Icons.Default.Warning else Icons.Default.Check,
            titleColor = if (isError) MaterialTheme.colorScheme.error else Color.Green,
            copy = isError,
            onValidate = { backupViewModel.setResult(null) }
        )
    }

    /* ────────── PIN unlock dialog ────────── */
    if (showPinDialog) {
        PinUnlockDialog(
            onDismiss = { showPinDialog = false; pinError = null },
            onPinEntered = { enteredPin ->
                if (SecurityHelper.verifyPin(enteredPin, pinHash)) {
                    isUnlocked = true
                    showPinDialog = false
                    pinError = null
                    navigateToAdvSettings()
                } else {
                    pinError = ctx.getString(R.string.wrong_pin)
                }
            },
            errorMessage = pinError
        )
    }
}
