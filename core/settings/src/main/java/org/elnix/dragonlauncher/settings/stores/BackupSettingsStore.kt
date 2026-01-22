package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.SettingObject
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object BackupSettingsStore : MapSettingsStore() {

    override val name: String = "Backup"
    override val dataStoreName = DataStoreName.BACKUP

    override val ALL: List<SettingObject<*>>
        get() = listOf(
            autoBackupEnabled,
            autoBackupUri,
            backupStores
        )


    // Because it caused crash at runtime due to early .entries initialization
    private val defaultBackupStores: Set<String>
        get() = DataStoreName.entries
            .map { it.value }
            .toSet()



    val autoBackupEnabled = SettingObject(
        key = "autoBackupEnabled",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val autoBackupUri = SettingObject(
        key = "autoBackupUri",
        dataStoreName = dataStoreName,
        default = "",
        type = SettingType.StringSet
    )

    val backupStores = SettingObject(
        key = "backupStores",
        dataStoreName = dataStoreName,
        default = defaultBackupStores,
        type = SettingType.StringSet
    )


    // TODO PUT THIS IN PRIVATE SETTINGS STORE
    val lastBackupTime = SettingObject(
        key = "lastBackupTime",
        dataStoreName = dataStoreName,
        default = System.currentTimeMillis(),
        type = SettingType.Long
    )
}
