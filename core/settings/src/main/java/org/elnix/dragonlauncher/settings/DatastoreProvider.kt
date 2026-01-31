package org.elnix.dragonlauncher.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.elnix.dragonlauncher.settings.bases.BaseSettingsStore
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
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore
import org.elnix.dragonlauncher.settings.stores.WorkspaceSettingsStore

enum class DataStoreName(
    val value: String,
    val backupKey: String,
//    val store: BaseSettingsStore<*>,
    val userBackup: Boolean = true
) {
    UI("uiDatastore", "ui"),
    COLOR_MODE("colorModeDatastore", "color_mode"),
    COLOR("colorDatastore", "color"),
    PRIVATE_SETTINGS("privateSettingsStore", "private", false),
    SWIPE("swipePointsDatastore", "new_actions"),
    LANGUAGE("languageDatastore", "language"),
    DRAWER("drawerDatastore", "drawer"),
    DEBUG("debugDatastore", "debug"),
    WORKSPACES("workspacesDataStore", "workspaces"),
    APPS("appsDatastore","apps", false),
    BEHAVIOR("behaviorDatastore", "behavior"),
    BACKUP("backupDatastore", "backup"),
    STATUS_BAR("statusDatastore", "status_bar"),
    FLOATING_APPS("floatingAppsDatastore", "floating_apps"),
    WELLBEING("wellbeingDatastore", "wellbeing")
}


object SettingsStoreRegistry {
    val byName: Map<DataStoreName, BaseSettingsStore<*>> = mapOf(
        DataStoreName.UI to UiSettingsStore,
        DataStoreName.COLOR_MODE to ColorModesSettingsStore,
        DataStoreName.COLOR to ColorSettingsStore,
        DataStoreName.PRIVATE_SETTINGS to PrivateSettingsStore,
        DataStoreName.SWIPE to SwipeSettingsStore,
        DataStoreName.LANGUAGE to LanguageSettingsStore,
        DataStoreName.DRAWER to DrawerSettingsStore,
        DataStoreName.DEBUG to DebugSettingsStore,
        DataStoreName.WORKSPACES to WorkspaceSettingsStore,
        DataStoreName.APPS to AppsSettingsStore,
        DataStoreName.BEHAVIOR to BehaviorSettingsStore,
        DataStoreName.BACKUP to BackupSettingsStore,
        DataStoreName.STATUS_BAR to StatusBarSettingsStore,
        DataStoreName.FLOATING_APPS to FloatingAppsSettingsStore,
        DataStoreName.WELLBEING to WellbeingSettingsStore
    )
}

val allStores = SettingsStoreRegistry.byName


val backupableStores =
    SettingsStoreRegistry.byName
        .filterKeys { it.userBackup }


/**
 * All the stores, minus the one that hols big data (the app cache)
 */
val defaultDebugStores =
    SettingsStoreRegistry.byName
        .filterValues { it != AppsSettingsStore }


/**
 * Datastore, now handled by a conditional function to avoid errors, all private
 */
private val Context.uiDatastore by preferencesDataStore(name = DataStoreName.UI.value)
private val Context.colorModeDatastore by preferencesDataStore(name = DataStoreName.COLOR_MODE.value)
private val Context.colorDatastore by preferencesDataStore(name = DataStoreName.COLOR.value)
private val Context.privateSettingsStore by preferencesDataStore(name = DataStoreName.PRIVATE_SETTINGS.value)
private val Context.swipeDataStore by preferencesDataStore(name = DataStoreName.SWIPE.value)
private val Context.languageDatastore by preferencesDataStore(name = DataStoreName.LANGUAGE.value)
private val Context.drawerDataStore by preferencesDataStore(name = DataStoreName.DRAWER.value)
private val Context.debugDatastore by preferencesDataStore(name = DataStoreName.DEBUG.value)
private val Context.workspaceDataStore by preferencesDataStore(name = DataStoreName.WORKSPACES.value)
private val Context.appsDatastore by preferencesDataStore(name = DataStoreName.APPS.value)
private val Context.behaviorDataStore by preferencesDataStore(name = DataStoreName.BEHAVIOR.value)
private val Context.backupDatastore by preferencesDataStore(name = DataStoreName.BACKUP.value)
private val Context.statusBarDatastore by preferencesDataStore(name = DataStoreName.STATUS_BAR.value)
private val Context.floatingAppsDatastore by preferencesDataStore(name = DataStoreName.FLOATING_APPS.value)
private val Context.wellbeingDatastore by preferencesDataStore(name = DataStoreName.WELLBEING.value)



fun Context.resolveDataStore(name: DataStoreName): DataStore<Preferences> {
    val appCtx = applicationContext
    return when (name) {
        DataStoreName.UI -> appCtx.uiDatastore
        DataStoreName.COLOR_MODE -> appCtx.colorModeDatastore
        DataStoreName.COLOR -> appCtx.colorDatastore
        DataStoreName.PRIVATE_SETTINGS -> appCtx.privateSettingsStore
        DataStoreName.SWIPE -> appCtx.swipeDataStore
        DataStoreName.LANGUAGE -> appCtx.languageDatastore
        DataStoreName.DRAWER -> appCtx.drawerDataStore
        DataStoreName.DEBUG -> appCtx.debugDatastore
        DataStoreName.WORKSPACES -> appCtx.workspaceDataStore
        DataStoreName.APPS -> appCtx.appsDatastore
        DataStoreName.BEHAVIOR -> appCtx.behaviorDataStore
        DataStoreName.BACKUP -> appCtx.backupDatastore
        DataStoreName.STATUS_BAR -> appCtx.statusBarDatastore
        DataStoreName.FLOATING_APPS -> appCtx.floatingAppsDatastore
        DataStoreName.WELLBEING -> appCtx.wellbeingDatastore
    }
}
