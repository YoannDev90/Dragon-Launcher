package org.elnix.dragonlauncher.settings.stores

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore
import org.elnix.dragonlauncher.settings.resolveDataStore
import org.json.JSONArray

/**
 * Settings store for the Digital Wellbeing feature.
 * Manages social media pause, guilt mode, and paused apps configuration.
 */
object WellbeingSettingsStore : MapSettingsStore() {

    override val name: String = "Wellbeing"

    override val dataStoreName = DataStoreName.WELLBEING

    override val ALL: List<BaseSettingObject<*, *>>
        get() = listOf(
            socialMediaPauseEnabled,
            guiltModeEnabled,
            pauseDurationSeconds,
            showUsageStats
        )

    /*  ─────────────  Main toggles  ─────────────  */

    /**
     * Whether the social media pause feature is enabled
     */
    val socialMediaPauseEnabled = Settings.boolean(
        key = "SOCIAL_MEDIA_PAUSE_ENABLED",
        dataStoreName = dataStoreName,
        default = false
    )

    /**
     * Whether to show guilt-inducing usage statistics
     */
    val guiltModeEnabled = Settings.boolean(
        key = "GUILT_MODE_ENABLED",
        dataStoreName = dataStoreName,
        default = false
    )

    /**
     * Whether to show detailed usage stats (time spent yesterday, etc.)
     */
    val showUsageStats = Settings.boolean(
        key = "SHOW_USAGE_STATS",
        dataStoreName = dataStoreName,
        default = true
    )

    /*  ─────────────  Configuration  ─────────────  */

    /**
     * Duration of the pause countdown in seconds (default 10s)
     */
    val pauseDurationSeconds = Settings.int(
        key = "PAUSE_DURATION_SECONDS",
        dataStoreName = dataStoreName,
        default = 10
    )

    /*  ─────────────  Paused Apps Management  ─────────────  */

    private val pausedAppsKey = stringPreferencesKey("PAUSED_APPS_LIST")

    /**
     * Flow of the list of paused app package names
     */
    fun getPausedAppsFlow(ctx: Context): Flow<Set<String>> {
        return ctx.resolveDataStore(dataStoreName).data.map { prefs ->
            val json = prefs[pausedAppsKey] ?: "[]"
            try {
                val jsonArray = JSONArray(json)
                (0 until jsonArray.length()).map { jsonArray.getString(it) }.toSet()
            } catch (e: Exception) {
                emptySet()
            }
        }
    }

    /**
     * Set the list of paused apps
     */
    suspend fun setPausedApps(ctx: Context, apps: Set<String>) {
        ctx.resolveDataStore(dataStoreName).updateData { prefs ->
            prefs.toMutablePreferences().apply {
                val jsonArray = JSONArray(apps.toList())
                this[pausedAppsKey] = jsonArray.toString()
            }
        }
    }

    /**
     * Add a single app to the paused list
     */
    suspend fun addPausedApp(ctx: Context, packageName: String, currentApps: Set<String>) {
        setPausedApps(ctx, currentApps + packageName)
    }

    /**
     * Remove a single app from the paused list
     */
    suspend fun removePausedApp(ctx: Context, packageName: String, currentApps: Set<String>) {
        setPausedApps(ctx, currentApps - packageName)
    }

    /**
     * Check if an app is in the paused list
     */
    fun isAppPaused(packageName: String, pausedApps: Set<String>): Boolean {
        return packageName in pausedApps
    }

    /*  ─────────────  Social Media Detection  ─────────────  */

    /**
     * Known social media package names for auto-detection
     */
    val knownSocialMediaApps = setOf(
        // Meta
        "com.instagram.android",
        "com.facebook.katana",
        "com.facebook.orca", // Messenger
        "com.whatsapp",
        "com.facebook.lite",
        
        // ByteDance
        "com.zhiliaoapp.musically", // TikTok
        "com.ss.android.ugc.trill", // TikTok (alternate)
        
        // Snap
        "com.snapchat.android",
        
        // Twitter/X
        "com.twitter.android",
        "com.twitter.android.lite",
        
        // Reddit
        "com.reddit.frontpage",
        
        // Pinterest
        "com.pinterest",
        
        // LinkedIn
        "com.linkedin.android",
        
        // Telegram
        "org.telegram.messenger",
        "org.telegram.messenger.web",
        
        // Discord
        "com.discord",
        
        // BeReal
        "com.bereal.ft",
        
        // Threads
        "com.instagram.barcelona",
        
        // YouTube (can be considered social)
        "com.google.android.youtube",
        
        // Twitch
        "tv.twitch.android.app",
        
        // Tumblr
        "com.tumblr",
        
        // WeChat
        "com.tencent.mm"
    )
}
