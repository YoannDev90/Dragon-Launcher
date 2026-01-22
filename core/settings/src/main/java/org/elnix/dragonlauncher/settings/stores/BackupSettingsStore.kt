package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.MapSettingsStore
import org.elnix.dragonlauncher.settings.SettingType
import org.elnix.dragonlauncher.settings.TypedSettingObject

object BackupSettingsStore : MapSettingsStore() {

    override val name: String = "Backup"
    override val dataStoreName = DataStoreName.BACKUP


    // Because it caused crash at runtime due to early .entries initialization
    private val defaultBackupStores: Set<String>
        get() = DataStoreName.entries
            .map { it.value }
            .toSet()



    val AUTO_BACKUP_ENABLED = TypedSettingObject(
        key = "autoBackupEnabled",
        dataStoreName = dataStoreName,
        default = false,
        type = SettingType.Boolean
    )

    val AUTO_BACKUP_URI = TypedSettingObject(
        key = "autoBackupUri",
        dataStoreName = dataStoreName,
        default = "",
        type = SettingType.StringSet
    )

    val BACKUP_STORES = TypedSettingObject(
        key = "backupStores",
        dataStoreName = dataStoreName,
        default = defaultBackupStores,
        type = SettingType.StringSet
    )


    // TODO PUT THIS IN PRIVATE SETTINGS STORE
    val LAST_BACKUP_TIME = TypedSettingObject(
        key = "lastBackupTime",
        dataStoreName = dataStoreName,
        default = System.currentTimeMillis(),
        type = SettingType.Long
    )


    override val ALL = listOf(
        AUTO_BACKUP_ENABLED,
        AUTO_BACKUP_URI,
        BACKUP_STORES,
//        LAST_BACKUP_TIME
    )

//    fun getAutoBackupEnabled(ctx: Context) = ctx
//        .backupDatastore.data
//        .map { it[AUTO_BACKUP_ENABLED] ?: defaults.autoBackupEnabled }
//
//    suspend fun setAutoBackupEnabled(ctx: Context, enabled: Boolean) {
//        ctx.backupDatastore.edit { it[AUTO_BACKUP_ENABLED] = enabled }
//    }
//
//    fun getAutoBackupUri(ctx: Context) = ctx
//        .backupDatastore.data
//        .map { it[AUTO_BACKUP_URI]?.ifBlank { null } }
//
//    suspend fun setAutoBackupUri(ctx: Context, uri: Uri?) {
//        ctx.backupDatastore.edit {
//            it[AUTO_BACKUP_URI] = uri?.toString() ?: ""
//        }
//    }
//
//    fun getBackupStores(ctx: Context) = ctx
//        .backupDatastore.data
//        .map { it[BACKUP_STORES] ?: defaultBackupStores }
//
//    suspend fun setBackupStores(ctx: Context, stores: List<DataStoreName>) {
//        ctx.backupDatastore.edit { prefs ->
//            prefs[BACKUP_STORES] = stores.map { it.value }.toSet()
//        }
//    }
//
//    fun getLastBackupTime(ctx: Context) = ctx
//        .backupDatastore.data
//        .map { it[Keys.LAST_BACKUP_TIME] ?: defaults.lastBackupTime }
//
//    suspend fun setLastBackupTime(ctx: Context) {
//        ctx.backupDatastore.edit { it[Keys.LAST_BACKUP_TIME] = System.currentTimeMillis() }
//    }
//
//    override suspend fun resetAll(ctx: Context) {
//        ctx.backupDatastore.edit { prefs ->
//            Keys.ALL.forEach { prefs.remove(it) }
//        }
//    }
//
//    override suspend fun getAll(ctx: Context): Map<String, Any> {
////        val prefs = ctx.backupDatastore.data.first()
//        return buildMap {
//
//            ALL.forEach {
//                it.get(ctx)
//            }
//            putIfNonDefault(
//                AUTO_BACKUP_ENABLED,
//                prefs[AUTO_BACKUP_ENABLED],
//                defaults.autoBackupEnabled
//            )
//
//            putIfNonDefault(
//                AUTO_BACKUP_URI,
//                prefs[AUTO_BACKUP_URI],
//                null
//            )
//
//            putIfNonDefault(
//                BACKUP_STORES,
//                prefs[BACKUP_STORES],
//                defaultBackupStores
//            )
//        }
//    }

}
