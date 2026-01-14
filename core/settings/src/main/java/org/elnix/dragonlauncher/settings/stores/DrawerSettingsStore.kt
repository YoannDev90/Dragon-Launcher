package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.enumsui.DrawerActions
import org.elnix.dragonlauncher.settings.BackupTypeException
import org.elnix.dragonlauncher.settings.BaseSettingsStore
import org.elnix.dragonlauncher.settings.drawerDataStore
import org.elnix.dragonlauncher.settings.getBooleanStrict
import org.elnix.dragonlauncher.settings.getFloatStrict
import org.elnix.dragonlauncher.settings.getIntStrict
import org.elnix.dragonlauncher.settings.putIfNonDefault
import org.elnix.dragonlauncher.settings.uiDatastore

object DrawerSettingsStore : BaseSettingsStore<Map<String, Any?>>() {
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
        val drawerEnterAction: DrawerActions = DrawerActions.CLEAR,
        val scrollDownToCloseDrawerOnTop: Boolean = true
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
        val SCROLL_DOWN_TO_CLOSE_DRAWER_ON_TOP = booleanPreferencesKey("scrollDownToCloseDrawerOnTop")

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
            DRAWER_ENTER_ACTIONS,
            SCROLL_DOWN_TO_CLOSE_DRAWER_ON_TOP
        )
    }

    // -------------------------------------------------------------------------
    // Accessors + Mutators
    // -------------------------------------------------------------------------
    fun getAutoLaunchSingleMatch(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map { it[Keys.AUTO_OPEN_SINGLE_MATCH] ?: defaults.autoOpenSingleMatch }

    suspend fun setAutoLaunchSingleMatch(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[Keys.AUTO_OPEN_SINGLE_MATCH] = v }
    }

    fun getShowAppIconsInDrawer(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map {
            it[Keys.SHOW_APP_ICONS_IN_DRAWER] ?: defaults.showAppIconsInDrawer
        }

    suspend fun setShowAppIconsInDrawer(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[Keys.SHOW_APP_ICONS_IN_DRAWER] = v }
    }

    fun getShowAppLabelsInDrawer(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map {
            it[Keys.SHOW_APP_LABEL_IN_DRAWER] ?: defaults.showAppLabelInDrawer
        }

    suspend fun setShowAppLabelsInDrawer(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[Keys.SHOW_APP_LABEL_IN_DRAWER] = v }
    }

    fun getSearchBarBottom(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map { it[Keys.SEARCH_BAR_BOTTOM] ?: defaults.searchBarBottom }

    suspend fun setSearchBarBottom(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[Keys.SEARCH_BAR_BOTTOM] = v }
    }

    fun getAutoShowKeyboardOnDrawer(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map {
            it[Keys.AUTO_SHOW_KEYBOARD_ON_DRAWER] ?: defaults.autoShowKeyboardOnDrawer
        }

    suspend fun setAutoShowKeyboardOnDrawer(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[Keys.AUTO_SHOW_KEYBOARD_ON_DRAWER] = v }
    }

    fun getClickEmptySpaceToRaiseKeyboard(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map {
            it[Keys.CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD] ?: defaults.clickEmptySpaceToRaiseKeyboard
        }

    suspend fun setClickEmptySpaceToRaiseKeyboard(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[Keys.CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD] = v }
    }

    fun getGridSize(ctx: Context): Flow<Int> =
        ctx.drawerDataStore.data.map { it[Keys.GRID_SIZE] ?: defaults.gridSize }

    suspend fun setGridSize(ctx: Context, size: Int) {
        ctx.drawerDataStore.edit { it[Keys.GRID_SIZE] = size }
    }

    fun getLeftDrawerAction(ctx: Context): Flow<DrawerActions> =
        ctx.drawerDataStore.data.map {
            DrawerActions.valueOf(
                it[Keys.LEFT_DRAWER_ACTION] ?: defaults.leftDrawerAction.name
            )
        }

    suspend fun setLeftDrawerAction(ctx: Context, action: DrawerActions) {
        ctx.drawerDataStore.edit { it[Keys.LEFT_DRAWER_ACTION] = action.name }
    }

    fun getRightDrawerAction(ctx: Context): Flow<DrawerActions> =
        ctx.drawerDataStore.data.map {
            DrawerActions.valueOf(
                it[Keys.RIGHT_DRAWER_ACTION] ?: defaults.rightDrawerAction.name
            )
        }

    suspend fun setRightDrawerAction(ctx: Context, action: DrawerActions) {
        ctx.drawerDataStore.edit { it[Keys.RIGHT_DRAWER_ACTION] = action.name }
    }


    fun getLeftDrawerWidth(ctx: Context): Flow<Float> =
        ctx.drawerDataStore.data.map { it[Keys.LEFT_DRAWER_WIDTH] ?: defaults.leftDrawerWidth }

    suspend fun setLeftDrawerWidth(ctx: Context, width: Float) {
        ctx.drawerDataStore.edit { it[Keys.LEFT_DRAWER_WIDTH] = width }
    }

    fun getRightDrawerWidth(ctx: Context): Flow<Float> =
        ctx.drawerDataStore.data.map { it[Keys.RIGHT_DRAWER_WIDTH] ?: defaults.rightDrawerWidth }

    suspend fun setRightDrawerWidth(ctx: Context, width: Float) {
        ctx.drawerDataStore.edit { it[Keys.RIGHT_DRAWER_WIDTH] = width }
    }

    fun getDrawerEnterAction(ctx: Context): Flow<DrawerActions> =
        ctx.drawerDataStore.data.map {
            DrawerActions.valueOf(
                it[Keys.DRAWER_ENTER_ACTIONS] ?: defaults.drawerEnterAction.name
            )
        }

    suspend fun setDrawerEnterAction(ctx: Context, v: DrawerActions) {
        ctx.drawerDataStore.edit { it[Keys.DRAWER_ENTER_ACTIONS] = v.name }
    }

    fun getScrollDownToCloseDrawerOnTop(ctx: Context): Flow<Boolean> =
        ctx.drawerDataStore.data.map {
            it[Keys.SCROLL_DOWN_TO_CLOSE_DRAWER_ON_TOP] ?: defaults.scrollDownToCloseDrawerOnTop
        }

    suspend fun setScrollDownToCloseDrawerOnTop(ctx: Context, v: Boolean) {
        ctx.drawerDataStore.edit { it[Keys.SCROLL_DOWN_TO_CLOSE_DRAWER_ON_TOP] = v }
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
    override suspend fun getAll(ctx: Context): Map<String, Any> {
        val prefs = ctx.drawerDataStore.data.first()

        return buildMap {


            putIfNonDefault(
                Keys.AUTO_OPEN_SINGLE_MATCH,
                prefs[Keys.AUTO_OPEN_SINGLE_MATCH],
                defaults.autoOpenSingleMatch
            )
            putIfNonDefault(
                Keys.SHOW_APP_ICONS_IN_DRAWER,
                prefs[Keys.SHOW_APP_ICONS_IN_DRAWER],
                defaults.showAppIconsInDrawer
            )
            putIfNonDefault(
                Keys.SHOW_APP_LABEL_IN_DRAWER,
                prefs[Keys.SHOW_APP_LABEL_IN_DRAWER],
                defaults.showAppLabelInDrawer
            )
            putIfNonDefault(
                Keys.SEARCH_BAR_BOTTOM,
                prefs[Keys.SEARCH_BAR_BOTTOM],
                defaults.searchBarBottom
            )
            putIfNonDefault(
                Keys.AUTO_SHOW_KEYBOARD_ON_DRAWER,
                prefs[Keys.AUTO_SHOW_KEYBOARD_ON_DRAWER],
                defaults.autoShowKeyboardOnDrawer
            )
            putIfNonDefault(
                Keys.CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD,
                prefs[Keys.CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD],
                defaults.clickEmptySpaceToRaiseKeyboard
            )
            putIfNonDefault(
                Keys.GRID_SIZE,
                prefs[Keys.GRID_SIZE],
                defaults.gridSize
            )
            putIfNonDefault(
                Keys.LEFT_DRAWER_ACTION,
                prefs[Keys.LEFT_DRAWER_ACTION],
                defaults.leftDrawerAction.name
            )
            putIfNonDefault(
                Keys.RIGHT_DRAWER_ACTION,
                prefs[Keys.RIGHT_DRAWER_ACTION],
                defaults.rightDrawerAction.name
            )
            putIfNonDefault(
                Keys.LEFT_DRAWER_WIDTH,
                prefs[Keys.LEFT_DRAWER_WIDTH],
                defaults.leftDrawerWidth
            )
            putIfNonDefault(
                Keys.RIGHT_DRAWER_WIDTH,
                prefs[Keys.RIGHT_DRAWER_WIDTH],
                defaults.rightDrawerWidth
            )

            putIfNonDefault(
                Keys.DRAWER_ENTER_ACTIONS,
                prefs[Keys.DRAWER_ENTER_ACTIONS],
                defaults.drawerEnterAction
            )
            putIfNonDefault(
                Keys.SCROLL_DOWN_TO_CLOSE_DRAWER_ON_TOP,
                prefs[Keys.SCROLL_DOWN_TO_CLOSE_DRAWER_ON_TOP],
                defaults.scrollDownToCloseDrawerOnTop
            )
        }
    }

    // -------------------------------------------------------------------------
    // Backup import
    // -------------------------------------------------------------------------
    override suspend fun setAll(ctx: Context, value: Map<String, Any?>) {


        fun getDrawerActionStrict(key: String): DrawerActions {
            val v = value[key] ?: return when (key) {
                Keys.LEFT_DRAWER_ACTION.name -> defaults.leftDrawerAction
                Keys.RIGHT_DRAWER_ACTION.name -> defaults.rightDrawerAction
                else -> throw BackupTypeException(key, "DrawerAction", null, null)
            }

            return when (v) {
                is String -> DrawerActions.valueOf(v)
                else -> throw BackupTypeException(key, "DrawerAction", v::class.simpleName, v)
            }
        }

        fun getDrawerEnterActionStrict(key: String): DrawerActions {
            val v = value[key] ?: return when (key) {
                Keys.DRAWER_ENTER_ACTIONS.name -> defaults.drawerEnterAction
                else -> throw BackupTypeException(key, "DrawerEnterAction", null, null)
            }

            return when (v) {
                is String -> DrawerActions.valueOf(v)
                else -> throw BackupTypeException(key, "DrawerEnterAction", v::class.simpleName, v)
            }
        }

        val backup = DrawerSettingsBackup(
            autoOpenSingleMatch = getBooleanStrict(
                value, Keys.AUTO_OPEN_SINGLE_MATCH, defaults.autoOpenSingleMatch
            ),
            showAppIconsInDrawer = getBooleanStrict(
                value, Keys.SHOW_APP_ICONS_IN_DRAWER, defaults.showAppIconsInDrawer
            ),
            showAppLabelInDrawer = getBooleanStrict(
                value, Keys.SHOW_APP_LABEL_IN_DRAWER, defaults.showAppLabelInDrawer
            ),
            searchBarBottom = getBooleanStrict(
                value, Keys.SEARCH_BAR_BOTTOM, defaults.searchBarBottom
            ),
            autoShowKeyboardOnDrawer = getBooleanStrict(
                value, Keys.AUTO_SHOW_KEYBOARD_ON_DRAWER, defaults.autoShowKeyboardOnDrawer
            ),
            clickEmptySpaceToRaiseKeyboard = getBooleanStrict(
                value,
                Keys.CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD,
                defaults.clickEmptySpaceToRaiseKeyboard
            ),
            gridSize = getIntStrict(
                value, Keys.GRID_SIZE, defaults.gridSize
            ),
            leftDrawerAction = getDrawerActionStrict(Keys.LEFT_DRAWER_ACTION.name),
            rightDrawerAction = getDrawerActionStrict(Keys.RIGHT_DRAWER_ACTION.name),
            leftDrawerWidth = getFloatStrict(
                value, Keys.LEFT_DRAWER_WIDTH, defaults.leftDrawerWidth
            ),
            rightDrawerWidth = getFloatStrict(
                value, Keys.RIGHT_DRAWER_WIDTH, defaults.rightDrawerWidth
            ),
            drawerEnterAction = getDrawerEnterActionStrict(Keys.DRAWER_ENTER_ACTIONS.name),
            scrollDownToCloseDrawerOnTop = getBooleanStrict(
                value,
                Keys.SCROLL_DOWN_TO_CLOSE_DRAWER_ON_TOP,
                defaults.scrollDownToCloseDrawerOnTop
            )
        )

        ctx.drawerDataStore.edit { prefs ->
            prefs[Keys.AUTO_OPEN_SINGLE_MATCH] = backup.autoOpenSingleMatch
            prefs[Keys.SHOW_APP_ICONS_IN_DRAWER] = backup.showAppIconsInDrawer
            prefs[Keys.SHOW_APP_LABEL_IN_DRAWER] = backup.showAppLabelInDrawer
            prefs[Keys.SEARCH_BAR_BOTTOM] = backup.searchBarBottom
            prefs[Keys.AUTO_SHOW_KEYBOARD_ON_DRAWER] = backup.autoShowKeyboardOnDrawer
            prefs[Keys.CLICK_EMPTY_SPACE_TO_RAISE_KEYBOARD] = backup.clickEmptySpaceToRaiseKeyboard
            prefs[Keys.GRID_SIZE] = backup.gridSize
            prefs[Keys.LEFT_DRAWER_ACTION] = backup.leftDrawerAction.name
            prefs[Keys.RIGHT_DRAWER_ACTION] = backup.rightDrawerAction.name
            prefs[Keys.LEFT_DRAWER_WIDTH] = backup.leftDrawerWidth
            prefs[Keys.RIGHT_DRAWER_WIDTH] = backup.rightDrawerWidth
            prefs[Keys.DRAWER_ENTER_ACTIONS] = backup.drawerEnterAction.name
            prefs[Keys.SCROLL_DOWN_TO_CLOSE_DRAWER_ON_TOP] = backup.scrollDownToCloseDrawerOnTop
        }
    }
}
