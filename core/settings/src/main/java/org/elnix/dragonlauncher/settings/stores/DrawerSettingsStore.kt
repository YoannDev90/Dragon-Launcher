package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.enumsui.DrawerActions
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object DrawerSettingsStore : MapSettingsStore() {
    override val name: String = "Drawer"
    override val dataStoreName = DataStoreName.DRAWER

    // -------------------------------------------------------------------------
    // Settings as typed SettingObjects
    // -------------------------------------------------------------------------
    val autoOpenSingleMatch = SettingObject(
        key = "autoOpenSingleMatch",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showAppIconsInDrawer = SettingObject(
        key = "showAppIconsInDrawer",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val showAppLabelInDrawer = SettingObject(
        key = "showAppLabelInDrawer",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val searchBarBottom = SettingObject(
        key = "searchBarBottom",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val autoShowKeyboardOnDrawer = SettingObject(
        key = "autoShowKeyboardOnDrawer",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val clickEmptySpaceToRaiseKeyboard = SettingObject(
        key = "clickEmptySpaceToRaiseKeyboard",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val gridSize = SettingObject(
        key = "gridSize",
        dataStoreName = dataStoreName,
        default = 6,
        type = SettingType.Int
    )

    val initialPage = SettingObject(
        key = "initialPage",
        dataStoreName = dataStoreName,
        default = 0,
        type = SettingType.Int
    )

    val leftDrawerAction = SettingObject(
        key = "leftDrawerAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.TOGGLE_KB,
        type = SettingType.Enum(DrawerActions::class.java)
    )

    val rightDrawerAction = SettingObject(
        key = "rightDrawerAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.CLOSE,
        type = SettingType.Enum(DrawerActions::class.java)
    )

    val leftDrawerWidth = SettingObject(
        key = "leftDrawerWidth",
        dataStoreName = dataStoreName,
        default = 0.1f,
        type = SettingType.Float
    )

    val rightDrawerWidth = SettingObject(
        key = "rightDrawerWidth",
        dataStoreName = dataStoreName,
        default = 0.1f,
        type = SettingType.Float
    )

    val drawerEnterAction = SettingObject(
        key = "drawerEnterAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.CLEAR,
        type = SettingType.Enum(DrawerActions::class.java)
    )

    val drawerHomeAction = SettingObject(
        key = "drawerHomeAction",
        dataStoreName = dataStoreName,
        default = DrawerActions.CLOSE,
        type = SettingType.Enum(DrawerActions::class.java)
    )

    val scrollDownToCloseDrawerOnTop = SettingObject(
        key = "scrollDownToCloseDrawerOnTop",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val scrollUpToCloseKeyboard = SettingObject(
        key = "scrollUpToCloseKeyboard",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    // -------------------------------------------------------------------------
    // ALL registry for iteration / reset / backup
    // -------------------------------------------------------------------------
    override val ALL: List<SettingObject<*>>
        get() = listOf(
            autoOpenSingleMatch,
            showAppIconsInDrawer,
            showAppLabelInDrawer,
            searchBarBottom,
            autoShowKeyboardOnDrawer,
            clickEmptySpaceToRaiseKeyboard,
            gridSize,
            initialPage,
            leftDrawerAction,
            rightDrawerAction,
            leftDrawerWidth,
            rightDrawerWidth,
            drawerEnterAction,
            drawerHomeAction,
            scrollDownToCloseDrawerOnTop,
            scrollUpToCloseKeyboard
        )
}
