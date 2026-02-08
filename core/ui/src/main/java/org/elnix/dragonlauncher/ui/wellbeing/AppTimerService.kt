package org.elnix.dragonlauncher.ui.wellbeing

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.formatDuration

/**
 * Foreground service that:
 * 1. Tracks how long the user has been on a paused app.
 * 2. Optionally sends periodic reminder notifications (every X minutes).
 * 3. Optionally triggers an overlay popup via [OverlayReminderActivity].
 * 4. Optionally returns the user to Dragon Launcher when the time limit is reached.
 */
class AppTimerService : Service() {

    companion object {
        const val CHANNEL_TIMER = "dragon_timer_channel"
        const val CHANNEL_REMINDER = "dragon_reminder_channel"
        const val NOTIF_ID_TIMER = 9001
        const val NOTIF_ID_REMINDER = 9002

        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_NAME = "extra_app_name"

        // Reminder mode
        const val EXTRA_REMINDER_ENABLED = "extra_reminder_enabled"
        const val EXTRA_REMINDER_INTERVAL_MINUTES = "extra_reminder_interval_min"
        const val EXTRA_REMINDER_MODE = "extra_reminder_mode" // "notification" | "overlay"

        // Return-to-launcher mode
        const val EXTRA_TIME_LIMIT_ENABLED = "extra_time_limit_enabled"
        const val EXTRA_TIME_LIMIT_MINUTES = "extra_time_limit_min"

        const val ACTION_STOP = "org.elnix.dragonlauncher.STOP_TIMER"

        fun start(
            ctx: Context,
            packageName: String,
            appName: String,
            reminderEnabled: Boolean = false,
            reminderIntervalMinutes: Int = 5,
            reminderMode: String = "overlay",
            timeLimitEnabled: Boolean = false,
            timeLimitMinutes: Int = 0
        ) {
            val intent = Intent(ctx, AppTimerService::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_APP_NAME, appName)
                putExtra(EXTRA_REMINDER_ENABLED, reminderEnabled)
                putExtra(EXTRA_REMINDER_INTERVAL_MINUTES, reminderIntervalMinutes)
                putExtra(EXTRA_REMINDER_MODE, reminderMode)
                putExtra(EXTRA_TIME_LIMIT_ENABLED, timeLimitEnabled)
                putExtra(EXTRA_TIME_LIMIT_MINUTES, timeLimitMinutes)
            }
            ctx.startForegroundService(intent)
        }

        fun stop(ctx: Context) {
            ctx.stopService(Intent(ctx, AppTimerService::class.java))
        }

        /**
         * Helper used by debug UI to send a one-off reminder notification for testing.
         */
        fun sendTestReminderNotification(ctx: Context, appName: String = "Dragon Launcher", minutes: Int = 5) {
            val nm = ctx.getSystemService(NotificationManager::class.java) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_REMINDER,
                        "Usage Reminders",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Periodic reminders when you stay on an app too long"
                    }
                )
            }
            val timeText = "${minutes} min"
            val notif = NotificationCompat.Builder(ctx, CHANNEL_REMINDER)
                .setSmallIcon(R.drawable.ic_action_notification)
                .setContentTitle(ctx.getString(R.string.reminder_notification_title, appName))
                .setContentText(ctx.getString(R.string.reminder_notification_text, appName, timeText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            nm.notify(NOTIF_ID_REMINDER, notif)
        }
    }

    private var trackedPackage: String = ""
    private var appName: String = ""
    private var reminderEnabled = false
    private var reminderIntervalMs = 5 * 60 * 1000L
    private var reminderMode = "overlay"
    private var timeLimitEnabled = false
    private var timeLimitMs = 0L
    private var startTimeMs = 0L
    private var timerThread: Thread? = null
    private var fiveMinWarningShown = false

    private fun createTimerThread() = object : Thread("AppTimerThread") {
        override fun run() {
            try {
                var elapsed = 0L
                var nextReminderAt = if (reminderEnabled) reminderIntervalMs else Long.MAX_VALUE

                while (!isInterrupted) {
                    sleep(1000)
                    elapsed = System.currentTimeMillis() - startTimeMs

                    // Update foreground notification every 30s
                    if (timeLimitEnabled && elapsed % 30_000 < 1000) {
                        val remaining = (timeLimitMs - elapsed).coerceAtLeast(0)
                        updateTimerNotification(remaining)
                    }

                    // 5-minute warning overlay (only once, only if total limit > 5 min)
                    if (timeLimitEnabled && !fiveMinWarningShown) {
                        val remainingMs = timeLimitMs - elapsed
                        if (remainingMs in 1..300_000 && timeLimitMs > 300_000) {
                            fiveMinWarningShown = true
                            val remainingMinutes = (remainingMs / 60_000).coerceAtLeast(1)
                            val remainingText = remainingMinutes.formatDuration()
                            val sessionMinutes = (elapsed / 60_000).coerceAtLeast(1)
                            val sessionText = sessionMinutes.formatDuration()
                            
                            OverlayReminderActivity.show(
                                this@AppTimerService, appName, sessionText, "", remainingText, true, "time_warning"
                            )
                        }
                    }

                    // Periodic reminder
                    if (reminderEnabled && elapsed >= nextReminderAt) {
                        sendReminder(elapsed)
                        nextReminderAt += reminderIntervalMs
                    }

                    // Time limit reached
                    if (timeLimitEnabled && elapsed >= timeLimitMs) {
                        returnToLauncher()
                        break
                    }
                }
            } catch (_: InterruptedException) {
                // Service stopped
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Stop previous timer if running
        timerThread?.interrupt()
        timerThread = null
        fiveMinWarningShown = false

        trackedPackage = intent?.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        appName = intent?.getStringExtra(EXTRA_APP_NAME) ?: trackedPackage
        reminderEnabled = intent?.getBooleanExtra(EXTRA_REMINDER_ENABLED, false) ?: false
        val intervalMin = intent?.getIntExtra(EXTRA_REMINDER_INTERVAL_MINUTES, 5) ?: 5
        reminderIntervalMs = intervalMin * 60 * 1000L
        reminderMode = intent?.getStringExtra(EXTRA_REMINDER_MODE) ?: "overlay"
        timeLimitEnabled = intent?.getBooleanExtra(EXTRA_TIME_LIMIT_ENABLED, false) ?: false
        val limitMin = intent?.getIntExtra(EXTRA_TIME_LIMIT_MINUTES, 0) ?: 0
        timeLimitMs = limitMin * 60 * 1000L
        startTimeMs = System.currentTimeMillis()

        val notification = buildTimerNotification(
            if (timeLimitEnabled) timeLimitMs else 0L
        )

        ServiceCompat.startForeground(
            this,
            NOTIF_ID_TIMER,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            else 0
        )

        timerThread = createTimerThread().also { it.start() }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timerThread?.interrupt()
        timerThread = null
        val nm = getSystemService(NotificationManager::class.java)
        nm?.cancel(NOTIF_ID_TIMER)
        nm?.cancel(NOTIF_ID_REMINDER)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ─────────── Notification channels ───────────

    private fun createNotificationChannels() {
        val nm = getSystemService(NotificationManager::class.java) ?: return

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_TIMER,
                "App Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows remaining time for app usage limit"
                setShowBadge(false)
            }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_REMINDER,
                "Usage Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Periodic reminders when you stay on an app too long"
            }
        )
    }

    // ─────────── Notifications ───────────

    private fun buildTimerNotification(remainingMs: Long): Notification {
        val stopIntent = Intent(this, AppTimerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPI = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = if (timeLimitEnabled && remainingMs > 0) {
            val remainingMinutes = (remainingMs / 60_000).coerceAtLeast(1)
            getString(R.string.timer_notification_text, remainingMinutes.formatDuration(), appName)
        } else {
            val elapsedMs = System.currentTimeMillis() - startTimeMs
            val elapsedMinutes = (elapsedMs / 60_000).coerceAtLeast(1)
            getString(R.string.reminder_notification_text, appName, elapsedMinutes.formatDuration())
        }

        return NotificationCompat.Builder(this, CHANNEL_TIMER)
            .setSmallIcon(R.drawable.ic_action_notification)
            .setContentTitle(getString(R.string.timer_notification_title))
            .setContentText(text)
            .setOngoing(true)
            .setSilent(true)
            .addAction(R.drawable.ic_action_notification, getString(R.string.time_limit_cancel), stopPI)
            .build()
    }

    private fun updateTimerNotification(remainingMs: Long) {
        val nm = getSystemService(NotificationManager::class.java) ?: return
        nm.notify(NOTIF_ID_TIMER, buildTimerNotification(remainingMs))
    }

    // ─────────── Reminders ───────────

    private fun sendReminder(elapsedMs: Long) {
        val elapsedMinutes = (elapsedMs / 60_000).coerceAtLeast(1)
        val timeText = elapsedMinutes.formatDuration()

        when (reminderMode) {
            "notification" -> {
                val nm = getSystemService(NotificationManager::class.java) ?: return
                val notif = NotificationCompat.Builder(this, CHANNEL_REMINDER)
                    .setSmallIcon(R.drawable.ic_action_notification)
                    .setContentTitle(getString(R.string.reminder_notification_title, appName))
                    .setContentText(getString(R.string.reminder_notification_text, appName, timeText))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()
                nm.notify(NOTIF_ID_REMINDER, notif)
            }

            "overlay" -> {
                val remainingText = if (timeLimitEnabled) {
                    val remaining = (timeLimitMs - elapsedMs).coerceAtLeast(0)
                    val remainingMinutes = (remaining / 60_000).coerceAtLeast(1)
                    remainingMinutes.formatDuration()
                } else ""
                
                OverlayReminderActivity.show(
                    this, appName, timeText, "", remainingText, timeLimitEnabled, "reminder"
                )
            }
        }
    }

    // ─────────── Return to launcher ───────────

    private fun returnToLauncher() {
        val intent = Intent(this, TimeLimitExceededActivity::class.java).apply {
            putExtra(TimeLimitExceededActivity.EXTRA_APP_NAME, appName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        stopSelf()
    }
}
