package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object BackupSettingsStore : MapSettingsStore() {

    override val name: String = "Backup"
    override val dataStoreName = DataStoreName.BACKUP

    override val ALL: List<BaseSettingObject <*, *> >
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



    val autoBackupEnabled = Settings.boolean(
        key = "autoBackupEnabled",
        dataStoreName = dataStoreName,
        default = false
    )

    val autoBackupUri = Settings.string(
        key = "autoBackupUri",
        dataStoreName = dataStoreName,
        default = ""
    )

    val backupStores = Settings.stringSet(
        key = "backupStores",
        dataStoreName = dataStoreName,
        default = defaultBackupStores
    )


    // TODO PUT THIS IN PRIVATE SETTINGS STORE
    val lastBackupTime = Settings.long(
        key = "lastBackupTime",
        dataStoreName = dataStoreName,
        default = System.currentTimeMillis()
    )
}
