package org.elnix.dragonlauncher.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.elnix.dragonlauncher.common.FloatingAppObject
import org.elnix.dragonlauncher.common.utils.SETTINGS
import org.elnix.dragonlauncher.common.utils.WidgetHostProvider
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.models.BackupViewModel
import org.elnix.dragonlauncher.models.FloatingAppsViewModel
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.helpers.noAnimComposable
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


    LaunchedEffect(Unit) {
        appLifecycleViewModel.homeEvents.collect {
            goMainScreen()
        }
    }




    val showAppIconsInDrawer by DrawerSettingsStore.getShowAppIconsInDrawer(ctx)
        .collectAsState(initial = true)

    val showAppLabelsInDrawer by DrawerSettingsStore.getShowAppLabelsInDrawer(ctx)
        .collectAsState(initial = true)

    val gridSize by DrawerSettingsStore.getGridSize(ctx)
        .collectAsState(initial = 1)


    val navController = rememberNavController()


    fun goAdvSettingsRoot() =  navController.navigate(SETTINGS.ADVANCED_ROOT)
    fun goAppearance() = navController.navigate(SETTINGS.APPEARANCE)
    fun goDebug() = navController.navigate(SETTINGS.DEBUG)

    fun goSettingsRoot() =  navController.navigate(SETTINGS.ROOT)

    NavHost(
        navController = navController,
        startDestination = SETTINGS.ROOT
    ) {


        noAnimComposable(SETTINGS.ROOT) {
            SettingsScreen(
                appsViewModel = appsViewModel,
                onAdvSettings = { goAdvSettingsRoot() },
                onBack = { goMainScreen() }
            )
        }
        noAnimComposable(SETTINGS.ADVANCED_ROOT) { AdvancedSettingsScreen(appsViewModel, navController ) { goSettingsRoot() } }

        noAnimComposable(SETTINGS.APPEARANCE)    { AppearanceTab(appsViewModel, navController) { goAdvSettingsRoot() } }
        noAnimComposable(SETTINGS.WALLPAPER)     { WallpaperTab { goAppearance() } }
        noAnimComposable(SETTINGS.ICON_PACK)     { IconPackTab(appsViewModel) { goAppearance() } }
        noAnimComposable(SETTINGS.STATUS_BAR)    { StatusBarTab(appsViewModel) { goAppearance() } }
        noAnimComposable(SETTINGS.THEME)         { ThemesTab { goAppearance() } }

        noAnimComposable(SETTINGS.BEHAVIOR)      { BehaviorTab(appsViewModel) { goAdvSettingsRoot() } }
        noAnimComposable(SETTINGS.DRAWER)        { DrawerTab(appsViewModel) { goAdvSettingsRoot() } }
        noAnimComposable(SETTINGS.COLORS)        { ColorSelectorTab { goAppearance() } }
        noAnimComposable(SETTINGS.DEBUG)         { DebugTab(navController, appsViewModel, onShowWelcome = { goWelcome() } ) { goAdvSettingsRoot() } }
        noAnimComposable(SETTINGS.LOGS)          { LogsTab { goDebug() } }
        noAnimComposable(SETTINGS.SETTINGS_JSON) { SettingsDebugTab { goDebug() } }
        noAnimComposable(SETTINGS.LANGUAGE)      { LanguageTab { goAdvSettingsRoot() } }
        noAnimComposable(SETTINGS.BACKUP)        { BackupTab(backupViewModel) { goAdvSettingsRoot() } }
        noAnimComposable(SETTINGS.CHANGELOGS)    { ChangelogsScreen { goAdvSettingsRoot() } }

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
                onBack = { goAdvSettingsRoot() }
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
