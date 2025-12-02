package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.drawerDataStore

object DrawerSettingsStore {

    // -------------------------------------------------------------------------
    // Backup data class
    // -------------------------------------------------------------------------
    private data class DrawerSettingsBackup(
        val autoOpenSingleMatch: Boolean = true,
        val showAppIconsInDrawer: Boolean = true,
        val searchBarBottom: Boolean = true
    )

    private val defaults = DrawerSettingsBackup()

    // -------------------------------------------------------------------------
    // Keys
    // -------------------------------------------------------------------------
    private val AUTO_OPEN_SINGLE_MATCH =
        booleanPreferencesKey(defaults.autoOpenSingleMatch.toString())

    private val SHOW_APP_ICONS_IN_DRAWER =
        booleanPreferencesKey(defaults.showAppIconsInDrawer.toString())

    private val SEARCH_BAR_BOTTOM =
        booleanPreferencesKey(defaults.searchBarBottom.toString())

    // -------------------------------------------------------------------------
    // Accessors + Mutators
    // -------------------------------------------------------------------------
    fun getAutoLaunchSingleMatch(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map { prefs ->
            prefs[AUTO_OPEN_SINGLE_MATCH] ?: defaults.autoOpenSingleMatch
        }

    suspend fun setAutoLaunchSingleMatch(ctx: Context, enabled: Boolean) {
        ctx.drawerDataStore.edit { it[AUTO_OPEN_SINGLE_MATCH] = enabled }
    }

    fun getShowAppIconsInDrawer(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map { prefs ->
            prefs[SHOW_APP_ICONS_IN_DRAWER] ?: defaults.showAppIconsInDrawer
        }

    suspend fun setShowAppIconsInDrawer(ctx: Context, enabled: Boolean) {
        ctx.drawerDataStore.edit { it[SHOW_APP_ICONS_IN_DRAWER] = enabled }
    }

    fun getSearchBarBottom(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map { prefs ->
            prefs[SEARCH_BAR_BOTTOM] ?: defaults.searchBarBottom
        }

    suspend fun setSearchBarBottom(ctx: Context, enabled: Boolean) {
        ctx.drawerDataStore.edit { it[SEARCH_BAR_BOTTOM] = enabled }
    }

    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------
    suspend fun resetAll(ctx: Context) {
        ctx.drawerDataStore.edit { prefs ->
            prefs.remove(AUTO_OPEN_SINGLE_MATCH)
            prefs.remove(SHOW_APP_ICONS_IN_DRAWER)
            prefs.remove(SEARCH_BAR_BOTTOM)
        }
    }

    // -------------------------------------------------------------------------
    // Backup export
    // -------------------------------------------------------------------------
    suspend fun getAll(ctx: Context): Map<String, Boolean> {
        val prefs = ctx.drawerDataStore.data.first()

        return buildMap {

            fun putIfNonDefault(key: String, value: Boolean?, defaultVal: Boolean) {
                if (value != null && value != defaultVal) {
                    put(key, value)
                }
            }

            putIfNonDefault(
                defaults.autoOpenSingleMatch.toString(),
                prefs[AUTO_OPEN_SINGLE_MATCH],
                defaults.autoOpenSingleMatch
            )

            putIfNonDefault(
                defaults.showAppIconsInDrawer.toString(),
                prefs[SHOW_APP_ICONS_IN_DRAWER],
                defaults.showAppIconsInDrawer
            )

            putIfNonDefault(
                defaults.searchBarBottom.toString(),
                prefs[SEARCH_BAR_BOTTOM],
                defaults.searchBarBottom
            )
        }
    }

    // -------------------------------------------------------------------------
    // Backup import
    // -------------------------------------------------------------------------
    suspend fun setAll(ctx: Context, backup: Map<String, Boolean>) {
        ctx.drawerDataStore.edit { prefs ->

            backup[defaults.autoOpenSingleMatch.toString()]?.let {
                prefs[AUTO_OPEN_SINGLE_MATCH] = it
            }

            backup[defaults.showAppIconsInDrawer.toString()]?.let {
                prefs[SHOW_APP_ICONS_IN_DRAWER] = it
            }

            backup[defaults.searchBarBottom.toString()]?.let {
                prefs[SEARCH_BAR_BOTTOM] = it
            }
        }
    }
}
