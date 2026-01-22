package org.elnix.dragonlauncher.settings.bases

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.resolveDataStore


abstract class BaseSettingObject <T> (
    val key: String,
    val dataStoreName: DataStoreName,
    val default: T
) {

    /**
     * The preference key that stores the actual value in the datastore
     * protected: not editable
     */
    protected abstract val preferenceKey:  Preferences.Key<T>



    /* ───────────── GETTERS ───────────── */
    /**
     * Get the value one shot for logic, no flow
     *
     * @param ctx
     * @return value of settings type [T]
     */
    abstract suspend fun get(ctx: Context): T

    /**
     * Flow, outputs a flow of the value, for compose
     *
     * @param ctx
     * @return [Flow] of the settings type [T]
     */
    abstract fun flow(ctx: Context): Flow<T>


    /**
     *  Decode raw imported value into T
     *  */
    abstract fun decode(raw: Any?): T



    /* ───────────── SETTERS ───────────── */

    /**
     * Set; saves the value in the datastore for persistence
     *
     * @param ctx
     * @param value either the good type or a null, to reset
     */
    abstract suspend fun set(ctx: Context, value: T?)


    /**
     * Reset; removes the value of the [preferenceKey] from the datastore
     *
     * @param ctx
     */
    suspend fun reset(ctx: Context) {
        ctx.resolveDataStore(dataStoreName).edit {
            it.remove(preferenceKey)
        }
    }
}
