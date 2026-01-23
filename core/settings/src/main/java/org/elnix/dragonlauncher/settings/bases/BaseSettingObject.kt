package org.elnix.dragonlauncher.settings.bases

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.resolveDataStore


class BaseSettingObject <T, R> (
    override val key: String,
    val dataStoreName: DataStoreName,
    val default: T,
    private val preferenceKey: Preferences.Key<R>,
    val encode: (T) -> R,
    val decode: (Any?) -> T
) : AnySettingObject {


    override suspend fun getAny(ctx: Context) = get(ctx)
    override suspend fun setAny(ctx: Context, value: Any?) {
        @Suppress("UNCHECKED_CAST")
        set(ctx, decode(value as R))
    }

//    /**
//     * The preference key that stores the actual value in the datastore
//     * protected: not editable
//     */
//    protected abstract val preferenceKey:  Preferences.Key<R>
//
//    /**
//     *  Decode raw imported value into [T]
//     */
//    abstract fun decode(raw: R): T

//    /**
//     * Encode value into [R]
//     *
//     * @param value
//     * @return
//     */
//    abstract fun encode(value: Any?): R


    /* ───────────── GETTERS ───────────── */
    /**
     * Get the value one shot for logic, no flow
     *
     * @param ctx
     * @return value of settings type [T]
     */
    suspend fun get(ctx: Context): T {
        val raw = ctx.resolveDataStore(dataStoreName)
            .data
            .first()[preferenceKey] ?: encode(default)
        return decode(raw)
    }

    /**
     * Flow, outputs a flow of the value, for compose
     *
     * @param ctx
     * @return [Flow] of the settings type [T]
     */
    fun flow(ctx: Context): Flow<T> =
        ctx.resolveDataStore(dataStoreName)
            .data
            .map { decode(it[preferenceKey] ?: encode(default)) }



    /* ───────────── SETTERS ───────────── */


    /**
     * Set; saves the value in the datastore for persistence
     *
     * @param ctx
     * @param value either the good type or a null, to reset
     */
    suspend fun set(ctx: Context, value: T?) {
        ctx.resolveDataStore(dataStoreName).edit {
            if (value != null) {
                it[preferenceKey] = encode(value)
            } else {
                it.remove(preferenceKey)
            }
        }
    }


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
