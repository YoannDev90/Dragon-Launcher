package org.elnix.dragonlauncher.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject



sealed interface SettingType {
    data object Boolean : SettingType
    data object Int : SettingType
    data object Float : SettingType
    data object Long : SettingType
//    data object Double : SettingType
    data object String : SettingType
    data object StringSet : SettingType
}


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


class JsonSettingObject(
    private val delegate: SettingObject<String>,
    private val default: JSONObject = JSONObject()
) {

    suspend fun get(ctx: Context): JSONObject {
        val raw = delegate.get(ctx)
        return if (raw.isBlank()) default else JSONObject(raw)
    }

    suspend fun set(ctx: Context, value: JSONObject) {
        delegate.set(ctx, value.toString())
    }

    suspend fun reset(ctx: Context) {
        delegate.reset(ctx)
    }
}
