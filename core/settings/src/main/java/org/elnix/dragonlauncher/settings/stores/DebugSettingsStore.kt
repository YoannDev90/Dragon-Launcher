package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object DebugSettingsStore : MapSettingsStore() {
    override val name: String = "Debug"
    override val dataStoreName = DataStoreName.DEBUG

    val debugEnabled = SettingObject(
        key = "debugEnabled",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val debugInfos = SettingObject(
        key = "debugInfos",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val settingsDebugInfo = SettingObject(
        key = "settingsDebugInfo",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val widgetsDebugInfo = SettingObject(
        key = "widgetsDebugInfo",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val workspacesDebugInfo = SettingObject(
        key = "workspacesDebugInfo",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val forceAppLanguageSelector = SettingObject(
        key = "forceAppLanguageSelector",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val forceAppWidgetsSelector = SettingObject(
        key = "forceAppWidgetsSelector",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val autoRaiseDragonOnSystemLauncher = SettingObject(
        key = "autoRaiseDragonOnSystemLauncher",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val systemLauncherPackageName = SettingObject(
        key = "systemLauncherPackageName",
        dataStoreName = dataStoreName,
        default = "",
        type = SettingType.String
    )

    val useAccessibilityInsteadOfContextToExpandActionPanel = SettingObject(
        key = "useAccessibilityInsteadOfContextToExpandActionPanel",
        dataStoreName = dataStoreName,
        default = true,
        type = SettingType.Boolean
    )

    val enableLogging = SettingObject(
        key = "enableLogging",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )


    override val ALL: List<SettingObject<*>>
        get() = listOf(
            debugEnabled,
            debugInfos,
            settingsDebugInfo,
            widgetsDebugInfo,
            workspacesDebugInfo,
            forceAppLanguageSelector,
            forceAppWidgetsSelector,
            autoRaiseDragonOnSystemLauncher,
            systemLauncherPackageName,
            useAccessibilityInsteadOfContextToExpandActionPanel,
            enableLogging
        )
    }
