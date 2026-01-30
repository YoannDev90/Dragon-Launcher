package org.elnix.dragonlauncher.settings.stores

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
        default = 6
    )

    val initialPage = Settings.int(
        key = "initialPage",
        dataStoreName = dataStoreName,
        default = 0
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
        default = 0f
    )

    val rightDrawerWidth = Settings.float(
        key = "rightDrawerWidth",
        dataStoreName = dataStoreName,
        default = 0f
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
            scrollUpDrawerAction
        )
}
