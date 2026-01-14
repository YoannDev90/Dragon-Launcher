package org.elnix.dragonlauncher.settings

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import org.elnix.dragonlauncher.settings.stores.AppsSettingsStore
import org.elnix.dragonlauncher.settings.stores.BackupSettingsStore
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.ColorModesSettingsStore
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.FloatingAppsSettingsStore
import org.elnix.dragonlauncher.settings.stores.LanguageSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.settings.stores.WorkspaceSettingsStore

enum class DataStoreName(
    val value: String,
    val backupKey: String,
    val store: BaseSettingsStore<*>,
    val userBackup: Boolean = true
) {
    UI("uiDatastore", "ui", UiSettingsStore),
    COLOR_MODE("colorModeDatastore", "color_mode", ColorModesSettingsStore),
    COLOR("colorDatastore", "color", ColorSettingsStore),
    PRIVATE_SETTINGS("privateSettingsStore", "private", PrivateSettingsStore, false),
    SWIPE("swipePointsDatastore", "new_actions", SwipeSettingsStore),
    LANGUAGE("languageDatastore", "language", LanguageSettingsStore),
    DRAWER("drawerDatastore", "drawer", DrawerSettingsStore),
    DEBUG("debugDatastore", "debug", DebugSettingsStore),
    WORKSPACES("workspacesDataStore", "workspaces", WorkspaceSettingsStore),
    APPS("appsDatastore","apps", AppsSettingsStore, false),
    BEHAVIOR("behaviorDatastore", "behavior", BehaviorSettingsStore),
    BACKUP("backupDatastore", "backup", BackupSettingsStore),
    STATUS_BAR("statusDatastore", "status_bar", StatusBarSettingsStore),
    FLOATING_APPS("floatingAppsDatastore", "floating_apps", FloatingAppsSettingsStore)
}

val allStores = DataStoreName.entries
val backupableStores = allStores.filter { it.userBackup }


/**
 * All the stores, minus the 2 that hols big data (the wallpapers in b64 and the app cache)
 */
val defaultDebugStores = allStores.filter { it.store != AppsSettingsStore }

val Context.uiDatastore by preferencesDataStore(name = DataStoreName.UI.value)
val Context.colorModeDatastore by preferencesDataStore(name = DataStoreName.COLOR_MODE.value)
val Context.colorDatastore by preferencesDataStore(name = DataStoreName.COLOR.value)
val Context.privateSettingsStore by preferencesDataStore(name = DataStoreName.PRIVATE_SETTINGS.value)
val Context.swipeDataStore by preferencesDataStore(name = DataStoreName.SWIPE.value)
val Context.languageDatastore by preferencesDataStore(name = DataStoreName.LANGUAGE.value)
val Context.drawerDataStore by preferencesDataStore(name = DataStoreName.DRAWER.value)
val Context.debugDatastore by preferencesDataStore(name = DataStoreName.DEBUG.value)
val Context.workspaceDataStore by preferencesDataStore(name = DataStoreName.WORKSPACES.value)
val Context.appDrawerDataStore by preferencesDataStore(name = DataStoreName.APPS.value)
val Context.behaviorDataStore by preferencesDataStore(name = DataStoreName.BEHAVIOR.value)
val Context.backupDatastore by preferencesDataStore(name = DataStoreName.BACKUP.value)
val Context.statusBarDatastore by preferencesDataStore(name = DataStoreName.STATUS_BAR.value)
val Context.floatingAppsDatastore by preferencesDataStore(name = DataStoreName.FLOATING_APPS.value)
