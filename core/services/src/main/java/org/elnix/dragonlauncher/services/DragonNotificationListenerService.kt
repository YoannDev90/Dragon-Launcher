package org.elnix.dragonlauncher.services

import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Listens to active notifications and exposes them as a [StateFlow] of package names.
 * The user must grant Notification Access in
 * Settings > Apps > Special App Access > Notification Access.
 */
class DragonNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        refreshNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        refreshNotifications()
    }

    override fun onListenerConnected() {
        refreshNotifications()
    }

    override fun onListenerDisconnected() {
        _notifications.value = emptyList()
    }

    private fun refreshNotifications() {
        val packages = try {
            activeNotifications
                ?.filter { !it.isOngoing }
                ?.map { it.packageName }
                ?.distinct()
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        _notifications.value = packages
    }

    companion object {
        private val _notifications = MutableStateFlow<List<String>>(emptyList())

        /** Distinct package names of apps with active (non-ongoing) notifications. */
        val notifications: StateFlow<List<String>> = _notifications

        /**
         * Returns true if the notification listener permission has been granted for this app.
         */
        fun isPermissionGranted(context: Context): Boolean {
            val flat = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ENABLED_NOTIFICATION_LISTENERS
            ) ?: return false
            val cn = ComponentName(context, DragonNotificationListenerService::class.java)
            return flat.split(":").any { ComponentName.unflattenFromString(it) == cn }
        }
    }
}
