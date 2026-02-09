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
            showUsageStats,
            reminderEnabled,
            reminderIntervalMinutes,
            reminderMode,
            popupShowSessionTime,
            popupShowTodayTime,
            popupShowRemainingTime,
            returnToLauncherEnabled
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
        default = 10,
        allowedRange = 3..60
    )

    /*  ─────────────  Periodic Reminder  ─────────────  */

    /**
     * Whether the periodic reminder feature is enabled.
     * When active, the user gets reminded every X minutes that they are still on a paused app.
     */
    val reminderEnabled = Settings.boolean(
        key = "REMINDER_ENABLED",
        dataStoreName = dataStoreName,
        default = false
    )

    /**
     * How often to remind (in minutes). Default 5.
     */
    val reminderIntervalMinutes = Settings.int(
        key = "REMINDER_INTERVAL_MINUTES",
        dataStoreName = dataStoreName,
        default = 5,
        allowedRange = 1..30
    )

    /**
     * Reminder delivery mode: "notification" or "overlay"
     */
    val reminderMode = Settings.string(
        key = "REMINDER_MODE",
        dataStoreName = dataStoreName,
        default = "overlay"
    )

    /**
     * Show session time in popup overlay (time since app opened)
     */
    val popupShowSessionTime = Settings.boolean(
        key = "POPUP_SHOW_SESSION_TIME",
        dataStoreName = dataStoreName,
        default = true
    )

    /**
     * Show today's total time in popup overlay
     */
    val popupShowTodayTime = Settings.boolean(
        key = "POPUP_SHOW_TODAY_TIME",
        dataStoreName = dataStoreName,
        default = true
    )

    /**
     * Show remaining time before limit in popup overlay (when return to launcher enabled)
     */
    val popupShowRemainingTime = Settings.boolean(
        key = "POPUP_SHOW_REMAINING_TIME",
        dataStoreName = dataStoreName,
        default = true
    )

    /*  ─────────────  Return to Launcher  ─────────────  */

    /**
     * Whether the auto-return-to-launcher feature is enabled.
     * User must set a time limit before opening a paused app; after the limit
     * they are brought back to Dragon Launcher.
     */
    val returnToLauncherEnabled = Settings.boolean(
        key = "RETURN_TO_LAUNCHER_ENABLED",
        dataStoreName = dataStoreName,
        default = false
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
                e.printStackTrace()
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
