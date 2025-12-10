package org.elnix.dragonlauncher.data.stores


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.data.privateSettingsStore
import org.elnix.dragonlauncher.data.uiDatastore

object PrivateSettingsStore {

    // ---------------------------------------------------------
    // Backup structure
    // ---------------------------------------------------------
    private data class PrivateSettingsBackup(
        val hasSeenWelcome: Boolean = false,
        val hasInitialized: Boolean = false,
        val showSetDefaultLauncherBanner: Boolean = true,
        val useAccessibilityInsteadOfContextToExpandActionPanel: Boolean = false,
        val showMethodAsking: Boolean = true,
        val lastSeenVersionCode: Int = 0
    )

    private val defaults = PrivateSettingsBackup()

    // ---------------------------------------------------------
    // Keys object (authoritative)
    // ---------------------------------------------------------
    private object Keys {
        const val HAS_SEEN_WELCOME = "hasSeenWelcome"
        const val HAS_INITIALIZED = "hasInitialized"
        const val SHOW_SET_DEFAULT_LAUNCHER_BANNER = "showSetDefaultLauncherBanner"
        const val USE_ACCESSIBILITY_INSTEAD_OF_CONTEXT = "useAccessibilityInsteadOfContextToExpandActionPanel"
        const val SHOW_METHOD_ASKING = "showMethodAsking"
        const val LAST_SEEN_VERSION_CODE = "lastSeenVersionCode"
    }


    // ---------------------------------------------------------
    // DataStore preference keys
    // ---------------------------------------------------------
    private val HAS_SEEN_WELCOME =
        booleanPreferencesKey(Keys.HAS_SEEN_WELCOME)

    private val HAS_INITIALIZED =
        booleanPreferencesKey(Keys.HAS_INITIALIZED)

    private val SHOW_SET_DEFAULT_LAUNCHER_BANNER =
        booleanPreferencesKey(Keys.SHOW_SET_DEFAULT_LAUNCHER_BANNER)

    private val USE_ACCESSIBILITY_INSTEAD_OF_CONTEXT =
        booleanPreferencesKey(Keys.USE_ACCESSIBILITY_INSTEAD_OF_CONTEXT)

    private val SHOW_METHOD_ASKING =
        booleanPreferencesKey(Keys.SHOW_METHOD_ASKING)

    private val  LAST_SEEN_VERSION_CODE = intPreferencesKey(Keys.LAST_SEEN_VERSION_CODE)

    // ---------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------
    fun getHasSeenWelcome(ctx: Context): Flow<Boolean> =
        ctx.privateSettingsStore.data.map { prefs ->
            prefs[HAS_SEEN_WELCOME] ?: defaults.hasSeenWelcome
        }

    suspend fun setHasSeenWelcome(ctx: Context, v: Boolean) {
        ctx.privateSettingsStore.edit { it[HAS_SEEN_WELCOME] = v }
    }

    fun getHasInitialized(ctx: Context): Flow<Boolean> =
        ctx.privateSettingsStore.data.map { prefs ->
            prefs[HAS_INITIALIZED] ?: defaults.hasInitialized
        }

    suspend fun setHasInitialized(ctx: Context, v: Boolean) {
        ctx.privateSettingsStore.edit { it[HAS_INITIALIZED] = v }
    }

    fun getShowSetDefaultLauncherBanner(ctx: Context): Flow<Boolean> =
        ctx.privateSettingsStore.data.map { prefs ->
            prefs[SHOW_SET_DEFAULT_LAUNCHER_BANNER] ?: defaults.showSetDefaultLauncherBanner
        }

    suspend fun setShowSetDefaultLauncherBanner(ctx: Context, v: Boolean) {
        ctx.privateSettingsStore.edit { it[SHOW_SET_DEFAULT_LAUNCHER_BANNER] = v }
    }

    fun getUseAccessibilityInsteadOfContextToExpandActionPanel(ctx: Context): Flow<Boolean> =
        ctx.privateSettingsStore.data.map { prefs ->
            prefs[USE_ACCESSIBILITY_INSTEAD_OF_CONTEXT] ?: defaults.useAccessibilityInsteadOfContextToExpandActionPanel
        }

    suspend fun setUseAccessibilityInsteadOfContextToExpandActionPanel(ctx: Context, v: Boolean) {
        ctx.privateSettingsStore.edit { it[USE_ACCESSIBILITY_INSTEAD_OF_CONTEXT] = v }
    }

    fun getShowMethodAsking(ctx: Context): Flow<Boolean> =
        ctx.privateSettingsStore.data.map { prefs ->
            prefs[SHOW_METHOD_ASKING] ?: defaults.showMethodAsking
        }

    suspend fun setShowMethodAsking(ctx: Context, v: Boolean) {
        ctx.privateSettingsStore.edit { it[SHOW_METHOD_ASKING] = v }
    }

    fun getLastSeenVersionCode(ctx: Context): Flow<Int> =
        ctx.uiDatastore.data.map { it[LAST_SEEN_VERSION_CODE] ?: defaults.lastSeenVersionCode }

    suspend fun setLastSeenVersionCode(ctx: Context, value: Int) {
        ctx.uiDatastore.edit { it[LAST_SEEN_VERSION_CODE] = value }
    }

    // ---------------------------------------------------------
    // Reset
    // ---------------------------------------------------------
    suspend fun resetAll(ctx: Context) {
        ctx.privateSettingsStore.edit { prefs ->
            prefs.remove(HAS_SEEN_WELCOME)
            prefs.remove(HAS_INITIALIZED)
            prefs.remove(SHOW_SET_DEFAULT_LAUNCHER_BANNER)
            prefs.remove(USE_ACCESSIBILITY_INSTEAD_OF_CONTEXT)
            prefs.remove(SHOW_METHOD_ASKING)
            prefs.remove(LAST_SEEN_VERSION_CODE)
        }
    }
}
