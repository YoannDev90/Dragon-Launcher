package org.elnix.dragonlauncher.ui.settings

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.FloatingAppObject
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.WidgetHostProvider
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.common.utils.transparentScreens
import org.elnix.dragonlauncher.enumsui.LockMethod
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.models.BackupViewModel
import org.elnix.dragonlauncher.models.FloatingAppsViewModel
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.ui.AdvancedSettingsScreen
import org.elnix.dragonlauncher.ui.SettingsScreen
import org.elnix.dragonlauncher.ui.dialogs.PinUnlockDialog
import org.elnix.dragonlauncher.ui.helpers.SecurityHelper
import org.elnix.dragonlauncher.ui.helpers.noAnimComposable
import org.elnix.dragonlauncher.ui.settings.backup.BackupTab
import org.elnix.dragonlauncher.ui.settings.customization.AppearanceTab
import org.elnix.dragonlauncher.ui.settings.customization.BehaviorTab
import org.elnix.dragonlauncher.ui.settings.customization.colors.ColorSelectorTab
import org.elnix.dragonlauncher.ui.settings.customization.DrawerTab
import org.elnix.dragonlauncher.ui.settings.customization.FloatingAppsTab
import org.elnix.dragonlauncher.ui.settings.customization.IconPackTab
import org.elnix.dragonlauncher.ui.settings.customization.NestEditingScreen
import org.elnix.dragonlauncher.ui.settings.customization.StatusBarTab
import org.elnix.dragonlauncher.ui.settings.customization.ThemesTab
import org.elnix.dragonlauncher.ui.settings.customization.WallpaperTab
import org.elnix.dragonlauncher.ui.settings.debug.DebugTab
import org.elnix.dragonlauncher.ui.settings.debug.LogsTab
import org.elnix.dragonlauncher.ui.settings.debug.SettingsDebugTab
import org.elnix.dragonlauncher.ui.settings.language.LanguageTab
import org.elnix.dragonlauncher.ui.settings.wellbeing.WellbeingTab
import org.elnix.dragonlauncher.ui.settings.workspace.WorkspaceDetailScreen
import org.elnix.dragonlauncher.ui.settings.workspace.WorkspaceListScreen
import org.elnix.dragonlauncher.ui.whatsnew.ChangelogsScreen

@Composable
fun SettingsNavHost(
    appsViewModel: AppsViewModel,
    backupViewModel: BackupViewModel,
    floatingAppsViewModel: FloatingAppsViewModel,
    appLifecycleViewModel: AppLifecycleViewModel,

    // Callbacks for main navHost
    goMainScreen: () -> Unit,
    goWelcome: () -> Unit,


    // Widget things
    widgetHostProvider: WidgetHostProvider,
    launchWidgetsPicker: (nestId: Int) -> Unit,
    onResetWidgetSize: (id: Int, widgetId: Int) -> Unit,
    onRemoveFloatingApp: (FloatingAppObject) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()


    // ── Lock gate state ──
    val lockMethod by BehaviorSettingsStore.lockMethod.flow(ctx)
        .collectAsState(initial = LockMethod.NONE)
    val pinHash by PrivateSettingsStore.lockPinHash.flow(ctx)
        .collectAsState(initial = "")

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



    val showAppIconsInDrawer by DrawerSettingsStore.showAppIconsInDrawer.flow(ctx)
        .collectAsState(initial = true)

    val showAppLabelsInDrawer by DrawerSettingsStore.showAppLabelInDrawer.flow(ctx)
        .collectAsState(initial = true)

    val gridSize by DrawerSettingsStore.gridSize.flow(ctx)
        .collectAsState(initial = 1)

    val nests by SwipeSettingsStore.getNestsFlow(ctx).collectAsState(initial = emptyList())
    val points by SwipeSettingsStore.getPointsFlow(ctx).collectAsState(emptyList())

    val pointIcons by appsViewModel.pointIcons.collectAsState()
    val defaultPoint by appsViewModel.defaultPoint.collectAsState(defaultSwipePointsValues)



    val navController = rememberNavController()

    var pendingNestToEdit by remember { mutableStateOf<Int?>(null) }

    fun navigateToAdvSettings() = navController.navigate(SETTINGS.ADVANCED_ROOT)

    fun goAdvSettingsRoot() {
        if (isUnlocked || lockMethod == LockMethod.NONE) {
            navigateToAdvSettings()
            return
        }
        when (lockMethod) {
            LockMethod.PIN -> {
                showPinDialog = true
            }
            LockMethod.DEVICE_UNLOCK -> {
                val activity = ctx as? FragmentActivity
                if (activity != null && SecurityHelper.isDeviceUnlockAvailable(ctx)) {
                    SecurityHelper.showDeviceUnlockPrompt(
                        activity = activity,
                        onSuccess = {
                            isUnlocked = true
                            navigateToAdvSettings()
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
            LockMethod.NONE -> navigateToAdvSettings()
        }
    }
    fun goAppearance() = navController.navigate(SETTINGS.APPEARANCE)
    fun goDebug() = navController.navigate(SETTINGS.DEBUG)
    fun goWellbeing() = navController.navigate(SETTINGS.WELLBEING)
    fun goNestEdit(nest: Int) {
        pendingNestToEdit = nest
        navController.navigate(SETTINGS.NESTS_EDIT)
    }


    fun goSettingsRoot() =  navController.navigate(SETTINGS.ROOT)



    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val containerColor =
        if (currentRoute in transparentScreens) {
            ctx.logE("Wallpaper", "Is in $currentRoute")
            Color.Transparent
        } else {
            MaterialTheme.colorScheme.background
        }

    // ── PIN unlock dialog ──
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

    Scaffold(
        contentWindowInsets = WindowInsets(),
        containerColor = containerColor,
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = SETTINGS.ROOT,
            modifier = Modifier.padding(paddingValues)
        ) {
            noAnimComposable(SETTINGS.ROOT) {
                SettingsScreen(
                    appsViewModel = appsViewModel,
                    pointIcons = pointIcons,
                    defaultPoint = defaultPoint,
                    nests = nests,
                    onAdvSettings = ::goAdvSettingsRoot,
                    onNestEdit = ::goNestEdit,
                    onBack = goMainScreen
                )
            }
            noAnimComposable(SETTINGS.ADVANCED_ROOT) {
                AdvancedSettingsScreen(
                    appsViewModel,
                    navController
                ) { goSettingsRoot() }
            }

            noAnimComposable(SETTINGS.APPEARANCE)    { AppearanceTab(appsViewModel, navController, ::goAdvSettingsRoot) }
            noAnimComposable(SETTINGS.WALLPAPER)     { WallpaperTab(::goAppearance) }
            noAnimComposable(SETTINGS.ICON_PACK)     { IconPackTab(appsViewModel, ::goAppearance) }
            noAnimComposable(SETTINGS.STATUS_BAR)    { StatusBarTab(appsViewModel, ::goAppearance) }
            noAnimComposable(SETTINGS.THEME)         { ThemesTab(::goAppearance) }

            noAnimComposable(SETTINGS.BEHAVIOR)      { BehaviorTab(appsViewModel, ::goAdvSettingsRoot) }
            noAnimComposable(SETTINGS.DRAWER)        { DrawerTab(appsViewModel, ::goAdvSettingsRoot) }
            noAnimComposable(SETTINGS.COLORS)        { ColorSelectorTab(::goAppearance) }
            noAnimComposable(SETTINGS.DEBUG)         { DebugTab(navController, appsViewModel, onShowWelcome = goWelcome ,::goAdvSettingsRoot) }
            noAnimComposable(SETTINGS.LOGS)          { LogsTab(::goDebug) }
            noAnimComposable(SETTINGS.SETTINGS_JSON) { SettingsDebugTab(::goDebug) }
            noAnimComposable(SETTINGS.LANGUAGE)      { LanguageTab (::goAdvSettingsRoot) }
            noAnimComposable(SETTINGS.BACKUP)        { BackupTab(backupViewModel, ::goAdvSettingsRoot) }
            noAnimComposable(SETTINGS.CHANGELOGS)    { ChangelogsScreen (::goAdvSettingsRoot) }

            noAnimComposable(SETTINGS.WELLBEING)     { WellbeingTab(
                appsViewModel = appsViewModel,
                onBack = ::goAdvSettingsRoot
            ) }

            noAnimComposable(SETTINGS.NESTS_EDIT)    { NestEditingScreen(
                nestId = pendingNestToEdit,
                nests = nests,
                points = points,
                pointIcons = pointIcons,
                defaultPoint = defaultPoint,
                onBack = ::goSettingsRoot
            ) }

            noAnimComposable(SETTINGS.FLOATING_APPS) {
                FloatingAppsTab(
                    appsViewModel = appsViewModel,
                    floatingAppsViewModel = floatingAppsViewModel,
                    widgetHostProvider = widgetHostProvider,
                    onBack = ::goAppearance,
                    onLaunchSystemWidgetPicker = launchWidgetsPicker,
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
