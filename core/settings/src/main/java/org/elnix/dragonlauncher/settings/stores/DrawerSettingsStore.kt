package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.enumsui.DrawerActions
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object DrawerSettingsStore : MapSettingsStore() {
    override val name: String = "Drawer"
    override val dataStoreName = DataStoreName.DRAWER

    val autoOpenSingleMatch = Settings.boolean(
        key = "autoOpenSingleMatch",
        dataStoreName = dataStoreName,
        default = true
    )

    val showAppIconsInDrawer = Settings.boolean(
        key = "showAppIconsInDrawer",
        dataStoreName = dataStoreName,
        default = true
    )

    val showAppLabelInDrawer = Settings.boolean(
        key = "showAppLabelInDrawer",
        dataStoreName = dataStoreName,
        default = true
    )

    val searchBarBottom = Settings.boolean(
        key = "searchBarBottom",
        dataStoreName = dataStoreName,
        default = true
    )

    val autoShowKeyboardOnDrawer = Settings.boolean(
        key = "autoShowKeyboardOnDrawer",
        dataStoreName = dataStoreName,
        default = true
    )

    val tapEmptySpaceAction = Settings.enum(
        key = "tabEmptySpaceToRaiseKeyboard",
        dataStoreName = dataStoreName,
        default = DrawerActions.CLOSE,
        enumClass = DrawerActions::class.java    )

    val gridSize = Settings.int(
        key = "gridSize",
        dataStoreName = dataStoreName,
        default = 6,
        allowedRange = 1..15
    )

    val initialPage = Settings.int(
        key = "initialPage",
        dataStoreName = dataStoreName,
        default = 0,
        allowedRange = 0..Int.MAX_VALUE
    )

    val leftDrawerAction = Settings.enum(
        key = "leftDrawerAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.DISABLED,
        enumClass = DrawerActions::class.java
    )

    val rightDrawerAction = Settings.enum(
        key = "rightDrawerAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.DISABLED,
        enumClass = DrawerActions::class.java
    )

    val leftDrawerWidth = Settings.float(
        key = "leftDrawerWidth",
        dataStoreName = dataStoreName,
        default = 0f,
        allowedRange = 0f..1f
    )

    val rightDrawerWidth = Settings.float(
        key = "rightDrawerWidth",
        dataStoreName = dataStoreName,
        default = 0f,
        allowedRange = 0f..1f
    )

    val drawerEnterAction = Settings.enum(
        key = "drawerEnterAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.CLEAR,
        enumClass = DrawerActions::class.java
    )

    val drawerHomeAction = Settings.enum(
        key = "drawerHomeAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.CLOSE,
        enumClass = DrawerActions::class.java
    )

    val scrollDownDrawerAction = Settings.enum(
        key = "scrollDownDrawerAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.CLOSE,
        enumClass = DrawerActions::class.java
    )

    val scrollUpDrawerAction = Settings.enum(
        key = "scrollUpDrawerAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.CLOSE_KB,
        enumClass = DrawerActions::class.java
    )


    val backDrawerAction = Settings.enum(
        key = "backDrawerAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.CLOSE,
        enumClass = DrawerActions::class.java
    )

    val iconsShape = Settings.shape(
        key = "iconsShape",
        dataStoreName = dataStoreName,
        default = IconShape.PlatformDefault
    )


    val iconsSpacingHorizontal = Settings.int(
        key = "iconsSpacingHorizontal",
        dataStoreName = dataStoreName,
        default = 8,
        allowedRange = 0..50
    )


    val iconsSpacingVertical = Settings.int(
        key = "iconsSpacingVertical",
        dataStoreName = dataStoreName,
        default = 8,
        allowedRange = 0..50
    )
    val maxIconSize = Settings.int(
        key = "maxIconSize",
        dataStoreName = dataStoreName,
        default = 96,
        allowedRange = 0..200
    )

    val useCategory = Settings.boolean(
        key = "useCategory",
        dataStoreName = dataStoreName,
        default = false
    )

    val categoryGridWidth = Settings.int(
        key = "categoryGridWidth",
        dataStoreName = dataStoreName,
        default = 3,
        allowedRange = 1..4
    )


    val categoryGridCells = Settings.int(
        key = "categoryGridCells",
        dataStoreName = dataStoreName,
        default = 3,
        allowedRange = 2..5
    )

    val showCategoryName = Settings.boolean(
        key = "showCategoryName",
        dataStoreName = dataStoreName,
        default = true
    )

    /* ───────────── Recently Used Apps ───────────── */

    val showRecentlyUsedApps = Settings.boolean(
        key = "showRecentlyUsedApps",
        dataStoreName = dataStoreName,
        default = false
    )

    val recentlyUsedAppsCount = Settings.int(
        key = "recentlyUsedAppsCount",
        dataStoreName = dataStoreName,
        default = 5,
        allowedRange = 1..20
    )

    /** JSON-encoded ordered list of recently-used package names (most recent first). */
    val recentlyUsedPackages = Settings.string(
        key = "recentlyUsedPackages",
        dataStoreName = dataStoreName,
        default = ""
    )


    override val ALL: List<BaseSettingObject<*,*>>
        get() = listOf(
            autoOpenSingleMatch,
            showAppIconsInDrawer,
            showAppLabelInDrawer,
            searchBarBottom,
            autoShowKeyboardOnDrawer,
            tapEmptySpaceAction,
            gridSize,
            initialPage,
            leftDrawerAction,
            rightDrawerAction,
            leftDrawerWidth,
            rightDrawerWidth,
            drawerEnterAction,
            drawerHomeAction,
            scrollDownDrawerAction,
            scrollUpDrawerAction,
            iconsShape,
            iconsSpacingVertical,
            iconsSpacingHorizontal,
            maxIconSize,
            useCategory,
            showRecentlyUsedApps,
            recentlyUsedAppsCount,
            recentlyUsedPackages,
            categoryGridWidth,
            categoryGridCells,
            showCategoryName
        )
}
