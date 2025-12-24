package org.elnix.dragonlauncher.data.stores

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.BackupTypeException
import org.elnix.dragonlauncher.data.BaseSettingsStore
import org.elnix.dragonlauncher.data.drawerDataStore
import org.elnix.dragonlauncher.data.helpers.DrawerActions
import org.elnix.dragonlauncher.data.helpers.DrawerEnterActions
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.AUTO_OPEN_SINGLE_MATCH
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.AUTO_SHOW_KEYBOARD_ON_DRAWER
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.DRAWER_ENTER_ACTIONS
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.GRID_SIZE
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.LEFT_DRAWER_ACTION
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.LEFT_DRAWER_WIDTH
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.RIGHT_DRAWER_ACTION
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.RIGHT_DRAWER_WIDTH
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.SEARCH_BAR_BOTTOM
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.SHOW_APP_ICONS_IN_DRAWER
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore.Keys.SHOW_APP_LABEL_IN_DRAWER
import org.elnix.dragonlauncher.data.uiDatastore

object DrawerSettingsStore : BaseSettingsStore() {
    override val name: String = "Drawer"

    // -------------------------------------------------------------------------
    // Backup data class
    // -------------------------------------------------------------------------
    private data class DrawerSettingsBackup(
        val autoOpenSingleMatch: Boolean = true,
        val showAppIconsInDrawer: Boolean = true,
        val showAppLabelInDrawer: Boolean = true,
        val searchBarBottom: Boolean = true,
        val autoShowKeyboardOnDrawer: Boolean = true,
        val clickEmptySpaceToRaiseKeyboard: Boolean = false,
        val gridSize: Int = 6,
        val initialPage: Int = 0,
        val leftDrawerAction: DrawerActions = DrawerActions.TOGGLE_KB,
        val rightDrawerAction: DrawerActions = DrawerActions.CLOSE,
        val leftDrawerWidth: Float = 0.1f,
        val rightDrawerWidth: Float = 0.1f,
        val drawerEnterAction: DrawerEnterActions = DrawerEnterActions.CLEAR
    )

    private val defaults = DrawerSettingsBackup()

    // -------------------------------------------------------------------------
    // Keys
    // -------------------------------------------------------------------------
    private object Keys {
        val AUTO_OPEN_SINGLE_MATCH = booleanPreferencesKey("autoOpenSingleMatch")
        val SHOW_APP_ICONS_IN_DRAWER = booleanPreferencesKey("showAppIconsInDrawer")
        val SHOW_APP_LABEL_IN_DRAWER = booleanPreferencesKey("showAppLabelInDrawer")
        val SEARCH_BAR_BOTTOM = booleanPreferencesKey("searchBarBottom")
        val AUTO_SHOW_KEYBOARD_ON_DRAWER = booleanPreferencesKey("autoShowKeyboardOnDrawer")
        val CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD =
            booleanPreferencesKey("clickEmptySpaceToRaiseKeyboard")
        val GRID_SIZE = intPreferencesKey("gridSize")
        val LEFT_DRAWER_ACTION = stringPreferencesKey("leftDrawerAction")
        val RIGHT_DRAWER_ACTION = stringPreferencesKey("rightDrawerAction")
        val LEFT_DRAWER_WIDTH = floatPreferencesKey("leftDrawerWidth")
        val RIGHT_DRAWER_WIDTH = floatPreferencesKey("rightDrawerWidth")
        val DRAWER_ENTER_ACTIONS = stringPreferencesKey("drawerEnterAction")

        val ALL = listOf(
            AUTO_OPEN_SINGLE_MATCH,
            SHOW_APP_ICONS_IN_DRAWER,
            SHOW_APP_LABEL_IN_DRAWER,
            SEARCH_BAR_BOTTOM,
            AUTO_SHOW_KEYBOARD_ON_DRAWER,
            CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD,
            GRID_SIZE,
            LEFT_DRAWER_ACTION,
            RIGHT_DRAWER_ACTION,
            LEFT_DRAWER_WIDTH,
            RIGHT_DRAWER_WIDTH,
            DRAWER_ENTER_ACTIONS
        )
    }

    // -------------------------------------------------------------------------
    // Accessors + Mutators
    // -------------------------------------------------------------------------
    fun getAutoLaunchSingleMatch(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map { it[AUTO_OPEN_SINGLE_MATCH] ?: defaults.autoOpenSingleMatch }

    suspend fun setAutoLaunchSingleMatch(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[AUTO_OPEN_SINGLE_MATCH] = v }
    }

    fun getShowAppIconsInDrawer(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map {
            it[SHOW_APP_ICONS_IN_DRAWER] ?: defaults.showAppIconsInDrawer
        }

    suspend fun setShowAppIconsInDrawer(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[SHOW_APP_ICONS_IN_DRAWER] = v }
    }

    fun getShowAppLabelsInDrawer(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map {
            it[SHOW_APP_LABEL_IN_DRAWER] ?: defaults.showAppLabelInDrawer
        }

    suspend fun setShowAppLabelsInDrawer(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[SHOW_APP_LABEL_IN_DRAWER] = v }
    }

    fun getSearchBarBottom(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map { it[SEARCH_BAR_BOTTOM] ?: defaults.searchBarBottom }

    suspend fun setSearchBarBottom(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[SEARCH_BAR_BOTTOM] = v }
    }

    fun getAutoShowKeyboardOnDrawer(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map {
            it[AUTO_SHOW_KEYBOARD_ON_DRAWER] ?: defaults.autoShowKeyboardOnDrawer
        }

    suspend fun setAutoShowKeyboardOnDrawer(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[AUTO_SHOW_KEYBOARD_ON_DRAWER] = v }
    }

    fun getClickEmptySpaceToRaiseKeyboard(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map {
            it[CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD] ?: defaults.clickEmptySpaceToRaiseKeyboard
        }

    suspend fun setClickEmptySpaceToRaiseKeyboard(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD] = v }
    }

    fun getGridSize(ctx: Context): Flow<Int> =
        ctx.drawerDataStore.data.map { it[GRID_SIZE] ?: defaults.gridSize }

    suspend fun setGridSize(ctx: Context, size: Int) {
        ctx.drawerDataStore.edit { it[GRID_SIZE] = size }
    }

    fun getLeftDrawerAction(ctx: Context): Flow<DrawerActions> =
        ctx.drawerDataStore.data.map {
            DrawerActions.valueOf(
                it[LEFT_DRAWER_ACTION] ?: defaults.leftDrawerAction.name
            )
        }

    suspend fun setLeftDrawerAction(ctx: Context, action: DrawerActions) {
        ctx.drawerDataStore.edit { it[LEFT_DRAWER_ACTION] = action.name }
    }

    fun getRightDrawerAction(ctx: Context): Flow<DrawerActions> =
        ctx.drawerDataStore.data.map {
            DrawerActions.valueOf(
                it[RIGHT_DRAWER_ACTION] ?: defaults.rightDrawerAction.name
            )
        }

    suspend fun setRightDrawerAction(ctx: Context, action: DrawerActions) {
        ctx.drawerDataStore.edit { it[RIGHT_DRAWER_ACTION] = action.name }
    }


    fun getLeftDrawerWidth(ctx: Context): Flow<Float> =
        ctx.drawerDataStore.data.map { it[LEFT_DRAWER_WIDTH] ?: defaults.leftDrawerWidth }

    suspend fun setLeftDrawerWidth(ctx: Context, width: Float) {
        ctx.drawerDataStore.edit { it[LEFT_DRAWER_WIDTH] = width }
    }

    fun getRightDrawerWidth(ctx: Context): Flow<Float> =
        ctx.drawerDataStore.data.map { it[RIGHT_DRAWER_WIDTH] ?: defaults.rightDrawerWidth }

    suspend fun setRightDrawerWidth(ctx: Context, width: Float) {
        ctx.drawerDataStore.edit { it[RIGHT_DRAWER_WIDTH] = width }
    }

    fun getDrawerEnterAction(ctx: Context): Flow<DrawerEnterActions> =
        ctx.drawerDataStore.data.map {
            DrawerEnterActions.valueOf(
                it[DRAWER_ENTER_ACTIONS] ?: defaults.drawerEnterAction.name
            )
        }

    suspend fun setDrawerEnterAction(ctx: Context, v: DrawerEnterActions) {
        ctx.drawerDataStore.edit { it[DRAWER_ENTER_ACTIONS] = v.name }
    }


    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------
    override suspend fun resetAll(ctx: Context) {
        ctx.uiDatastore.edit { prefs ->
            Keys.ALL.forEach { prefs.remove(it) }
        }
    }

    // -------------------------------------------------------------------------
    // Backup export
    // -------------------------------------------------------------------------
    suspend fun getAll(ctx: Context): Map<String, String> {
        val prefs = ctx.drawerDataStore.data.first()

        return buildMap {

            fun putIfNonDefault(key: Preferences.Key<*>, value: Any?, def: Any) {
                if (value != null && value != def) {
                    put(key.name, value.toString())
                }
            }

            putIfNonDefault(
                AUTO_OPEN_SINGLE_MATCH,
                prefs[AUTO_OPEN_SINGLE_MATCH],
                defaults.autoOpenSingleMatch
            )
            putIfNonDefault(
                SHOW_APP_ICONS_IN_DRAWER,
                prefs[SHOW_APP_ICONS_IN_DRAWER],
                defaults.showAppIconsInDrawer
            )
            putIfNonDefault(
                SHOW_APP_LABEL_IN_DRAWER,
                prefs[SHOW_APP_LABEL_IN_DRAWER],
                defaults.showAppLabelInDrawer
            )
            putIfNonDefault(
                SEARCH_BAR_BOTTOM,
                prefs[SEARCH_BAR_BOTTOM],
                defaults.searchBarBottom
            )
            putIfNonDefault(
                AUTO_SHOW_KEYBOARD_ON_DRAWER,
                prefs[AUTO_SHOW_KEYBOARD_ON_DRAWER],
                defaults.autoShowKeyboardOnDrawer
            )
            putIfNonDefault(
                CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD,
                prefs[CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD],
                defaults.clickEmptySpaceToRaiseKeyboard
            )
            putIfNonDefault(GRID_SIZE, prefs[GRID_SIZE], defaults.gridSize)
            putIfNonDefault(
                LEFT_DRAWER_ACTION,
                prefs[LEFT_DRAWER_ACTION],
                defaults.leftDrawerAction.name
            )
            putIfNonDefault(
                RIGHT_DRAWER_ACTION,
                prefs[RIGHT_DRAWER_ACTION],
                defaults.rightDrawerAction.name
            )
            putIfNonDefault(
                LEFT_DRAWER_WIDTH,
                prefs[LEFT_DRAWER_WIDTH],
                defaults.leftDrawerWidth
            )
            putIfNonDefault(
                RIGHT_DRAWER_WIDTH,
                prefs[RIGHT_DRAWER_WIDTH],
                defaults.rightDrawerWidth
            )

            putIfNonDefault(
                DRAWER_ENTER_ACTIONS,
                prefs[DRAWER_ENTER_ACTIONS],
                defaults.drawerEnterAction
            )
        }
    }

    // -------------------------------------------------------------------------
    // Backup import
    // -------------------------------------------------------------------------
    suspend fun setAll(ctx: Context, raw: Map<String, Any?>) {

        fun getBooleanStrict(key: String): Boolean {
            val v = raw[key] ?: return when (key) {
                AUTO_OPEN_SINGLE_MATCH.name -> defaults.autoOpenSingleMatch
                SHOW_APP_ICONS_IN_DRAWER.name -> defaults.showAppIconsInDrawer
                SHOW_APP_LABEL_IN_DRAWER.name -> defaults.showAppLabelInDrawer
                SEARCH_BAR_BOTTOM.name -> defaults.searchBarBottom
                AUTO_SHOW_KEYBOARD_ON_DRAWER.name -> defaults.autoShowKeyboardOnDrawer
                CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD.name -> defaults.clickEmptySpaceToRaiseKeyboard
                else -> throw BackupTypeException(key, "Boolean", null, null)
            }

            return when (v) {
                is Boolean -> v
                is Number -> v.toInt() != 0
                is String -> when (v.trim().lowercase()) {
                    "true", "1", "yes", "y", "on" -> true
                    "false", "0", "no", "n", "off" -> false
                    else -> throw BackupTypeException(key, "Boolean", "String", v)
                }

                else -> throw BackupTypeException(key, "Boolean", v::class.simpleName, v)
            }
        }

        fun getIntStrict(key: String): Int {
            val v = raw[key] ?: return when (key) {
                GRID_SIZE.name -> defaults.gridSize
                else -> throw BackupTypeException(key, "Int", null, null)
            }

            return when (v) {
                is Int -> v
                is Number -> v.toInt()
                is String -> v.toIntOrNull() ?: throw BackupTypeException(key, "Int", "String", v)
                else -> throw BackupTypeException(key, "Int", v::class.simpleName, v)
            }
        }

        fun getFloatStrict(key: String): Float {
            val v = raw[key] ?: return when (key) {
                LEFT_DRAWER_WIDTH.name -> defaults.leftDrawerWidth
                RIGHT_DRAWER_WIDTH.name -> defaults.rightDrawerWidth
                else -> throw BackupTypeException(key, "Float", null, null)
            }

            return when (v) {
                is Float -> v
                is Number -> v.toFloat()
                is String -> v.toFloatOrNull() ?: throw BackupTypeException(
                    key,
                    "Float",
                    "String",
                    v
                )

                else -> throw BackupTypeException(key, "Float", v::class.simpleName, v)
            }
        }

        fun getDrawerActionStrict(key: String): DrawerActions {
            val v = raw[key] ?: return when (key) {
                LEFT_DRAWER_ACTION.name -> defaults.leftDrawerAction
                RIGHT_DRAWER_ACTION.name -> defaults.rightDrawerAction
                else -> throw BackupTypeException(key, "DrawerAction", null, null)
            }

            return when (v) {
                is String -> DrawerActions.valueOf(v)
                else -> throw BackupTypeException(key, "DrawerAction", v::class.simpleName, v)
            }
        }

        fun getDrawerEnterActionStrict(key: String): DrawerEnterActions {
            val v = raw[key] ?: return when (key) {
                DRAWER_ENTER_ACTIONS.name -> defaults.drawerEnterAction
                else -> throw BackupTypeException(key, "DrawerEnterAction", null, null)
            }

            return when (v) {
                is String -> DrawerEnterActions.valueOf(v)
                else -> throw BackupTypeException(key, "DrawerEnterAction", v::class.simpleName, v)
            }
        }

        val backup = DrawerSettingsBackup(
            autoOpenSingleMatch = getBooleanStrict(AUTO_OPEN_SINGLE_MATCH.name),
            showAppIconsInDrawer = getBooleanStrict(SHOW_APP_ICONS_IN_DRAWER.name),
            showAppLabelInDrawer = getBooleanStrict(SHOW_APP_LABEL_IN_DRAWER.name),
            searchBarBottom = getBooleanStrict(SEARCH_BAR_BOTTOM.name),
            autoShowKeyboardOnDrawer = getBooleanStrict(AUTO_SHOW_KEYBOARD_ON_DRAWER.name),
            clickEmptySpaceToRaiseKeyboard = getBooleanStrict(CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD.name),
            gridSize = getIntStrict(GRID_SIZE.name),
            leftDrawerAction = getDrawerActionStrict(LEFT_DRAWER_ACTION.name),
            rightDrawerAction = getDrawerActionStrict(RIGHT_DRAWER_ACTION.name),
            leftDrawerWidth = getFloatStrict(LEFT_DRAWER_WIDTH.name),
            rightDrawerWidth = getFloatStrict(RIGHT_DRAWER_WIDTH.name),
            drawerEnterAction = getDrawerEnterActionStrict(DRAWER_ENTER_ACTIONS.name)
        )

        ctx.drawerDataStore.edit { prefs ->
            prefs[AUTO_OPEN_SINGLE_MATCH] = backup.autoOpenSingleMatch
            prefs[SHOW_APP_ICONS_IN_DRAWER] = backup.showAppIconsInDrawer
            prefs[SHOW_APP_LABEL_IN_DRAWER] = backup.showAppLabelInDrawer
            prefs[SEARCH_BAR_BOTTOM] = backup.searchBarBottom
            prefs[AUTO_SHOW_KEYBOARD_ON_DRAWER] = backup.autoShowKeyboardOnDrawer
            prefs[CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD] = backup.clickEmptySpaceToRaiseKeyboard
            prefs[GRID_SIZE] = backup.gridSize
            prefs[LEFT_DRAWER_ACTION] = backup.leftDrawerAction.name
            prefs[RIGHT_DRAWER_ACTION] = backup.rightDrawerAction.name
            prefs[LEFT_DRAWER_WIDTH] = backup.leftDrawerWidth
            prefs[RIGHT_DRAWER_WIDTH] = backup.rightDrawerWidth
            prefs[DRAWER_ENTER_ACTIONS] = backup.drawerEnterAction.name
        }
    }
}
