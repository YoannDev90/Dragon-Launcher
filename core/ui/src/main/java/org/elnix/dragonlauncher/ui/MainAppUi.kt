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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.FloatingAppObject
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.utils.ROUTES
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.WidgetHostProvider
import org.elnix.dragonlauncher.common.utils.getVersionCode
import org.elnix.dragonlauncher.common.utils.hasUriReadWritePermission
import org.elnix.dragonlauncher.common.utils.isDefaultLauncher
import org.elnix.dragonlauncher.common.utils.loadChangelogs
import org.elnix.dragonlauncher.common.utils.transparentScreens
import org.elnix.dragonlauncher.enumsui.DrawerActions
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
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
import org.elnix.dragonlauncher.ui.helpers.noAnimComposable
import org.elnix.dragonlauncher.ui.settings.SettingsNavHost
import org.elnix.dragonlauncher.ui.welcome.WelcomeScreen
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
    val lastSeenVersionCode by PrivateSettingsStore.lastSeenVersionCode.flow(ctx)
        .collectAsState(initial = Int.MAX_VALUE)

    val currentVersionCode = getVersionCode(ctx)
    var showWhatsNewBottomSheet by remember { mutableStateOf(false) }

    var showWidgetPicker by remember { mutableStateOf<Int?>(null) }

    val updates by produceState(initialValue = emptyList()) {
        value = loadChangelogs(ctx, versionCode)
    }

    val showAppIconsInDrawer by DrawerSettingsStore.showAppIconsInDrawer.flow(ctx)
        .collectAsState(initial = true)

    val showAppLabelsInDrawer by DrawerSettingsStore.showAppLabelInDrawer.flow(ctx)
        .collectAsState(initial = true)

    val autoShowKeyboardOnDrawer by DrawerSettingsStore.autoShowKeyboardOnDrawer.flow(ctx)
        .collectAsState(initial = false)

    val gridSize by DrawerSettingsStore.gridSize.flow(ctx)
        .collectAsState(initial = 1)

//    val searchBarBottom by DrawerSettingsStore.getSearchBarBottom(ctx)
//        .collectAsState(initial = true)
    val searchBarBottom = false



    val leftDrawerAction by DrawerSettingsStore.leftDrawerAction.flow(ctx)
        .collectAsState(initial = DrawerActions.TOGGLE_KB)

    val rightDrawerAction by DrawerSettingsStore.rightDrawerAction.flow(ctx)
        .collectAsState(initial = DrawerActions.CLOSE)

    val leftDrawerWidth by DrawerSettingsStore.leftDrawerWidth.flow(ctx)
        .collectAsState(initial = 0.1f)
    val rightDrawerWidth  by DrawerSettingsStore.rightDrawerWidth.flow(ctx)
        .collectAsState(initial = 0.1f)

    val forceAppWidgetsSelector by DebugSettingsStore.forceAppWidgetsSelector.flow(ctx)
        .collectAsState(initial = false)


    val showSetDefaultLauncherBanner by PrivateSettingsStore.showSetDefaultLauncherBanner.flow(ctx)
        .collectAsState(initial = false)


    val lifecycleOwner = LocalLifecycleOwner.current
    var isDefaultLauncher by remember { mutableStateOf(ctx.isDefaultLauncher) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val autoBackupEnabled by BackupSettingsStore.autoBackupEnabled.flow(ctx).collectAsState(initial = false)
    val autoBackupUriString by BackupSettingsStore.autoBackupUri.flow(ctx).collectAsState(initial = null)
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

        // When the noAnimComposable leaves the screen, remove the observer
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
    fun goDrawer() = navController.navigate(ROUTES.DRAWER)
    fun goWelcome() = navController.navigate(ROUTES.WELCOME)



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
            ctx.logE("Wallpaper", "IS TRANSPARENT: $currentRoute")
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
                    onAppDrawer = { goDrawer() },
                    onGoWelcome = { goWelcome() },
//                    ontest = { navController.navigate("test") },
                    onLongPress3Sec = { goSettingsRoot() }
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
//
//            noAnimComposable("test") {
//                SettingsLazyHeader(
//                    title = "test",
//                    onBack = ::goMainScreen,
//                    helpText = "",
//                    onReset = null
//                ) {
//                    item {
//                        Text("Test")
//                    }
//                }
//            }

            // Welcome screen
            noAnimComposable(ROUTES.WELCOME) {
                WelcomeScreen(
                    backupVm =  backupViewModel,
                    onEnterSettings = ::goSettingsRoot,
                    onEnterApp = ::goMainScreen
                )
            }


            // Settings Nav Host, holds all the settings
            noAnimComposable(SETTINGS.ROOT) {
                SettingsNavHost(
                    appsViewModel = appsViewModel,
                    backupViewModel = backupViewModel,
                    appLifecycleViewModel = appLifecycleViewModel,
                    floatingAppsViewModel = floatingAppsViewModel,
                    goMainScreen = ::goMainScreen,
                    goWelcome = ::goWelcome,
                    widgetHostProvider = widgetHostProvider,
                    launchWidgetsPicker = ::launchWidgetsPicker,
                    onResetWidgetSize = onResetWidgetSize,
                    onRemoveFloatingApp = onRemoveFloatingApp
                )
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

    // ────────────────────────────────────────────────────
    // RESULT DIALOG ( IMPORT / EXPORT )
    // ────────────────────────────────────────────────────
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
