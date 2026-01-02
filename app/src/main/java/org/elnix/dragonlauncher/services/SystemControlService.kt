package org.elnix.dragonlauncher.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.data.stores.DebugSettingsStore
import org.elnix.dragonlauncher.utils.ACCESSIBILITY_TAG

@SuppressLint("AccessibilityPolicy")
class SystemControlService : AccessibilityService() {


    private var systemLauncher: String? = null
    private var autoRaiseEnabled = false

    // Service scope for Flows
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d(ACCESSIBILITY_TAG, "Detected accessibility event! $event")

        Log.d(ACCESSIBILITY_TAG, "$autoRaiseEnabled, $systemLauncher")


        if (!autoRaiseEnabled || systemLauncher == null) return

        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val pkg = event.packageName?.toString() ?: return
        if (pkg != systemLauncher) return

        val now = System.currentTimeMillis()
        if (now - lastLaunchTime < DEBOUNCE_DELAY_MS) return
        if (isSwitching.value) return  // Prevent recursive launch

        val className = event.className?.toString() ?: ""
        if (!className.contains("Launcher") && !className.contains("Home")) return

        Log.d(ACCESSIBILITY_TAG, "Confirmed system launcher: $pkg ($className)")
        launchDragon()
    }

    override fun onInterrupt() {
        Log.w(ACCESSIBILITY_TAG, "Accessibility service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        listenToSettingsChanges()

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED

            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
            notificationTimeout = 50
        }
        serviceInfo = info

        SystemControl.attachInstance(this)
        Log.d(ACCESSIBILITY_TAG, "Service ready - Gestures & window monitoring enabled")
    }

    fun openNotificationShade() {
        performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    }

//    fun openQuickSettings() {
//        performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
//    }

    fun openRecentApps() {
        performGlobalAction(GLOBAL_ACTION_RECENTS)
    }


    private fun launchDragon() {
        isSwitching.value = true
        lastLaunchTime = System.currentTimeMillis()

        SystemControl.launchDragon(this)
        Log.d(ACCESSIBILITY_TAG, "Dragon Launcher launched")

        // Post to handler for debounce; after launching Dragon, for faster visual effect
        Handler(Looper.getMainLooper()).postDelayed({
            isSwitching.value = false
        }, 100L)
    }

    private fun listenToSettingsChanges() {
        serviceScope.launch {
            DebugSettingsStore.getSystemLauncherPackageName(this@SystemControlService)
                .collect { pkg ->
                    systemLauncher = pkg.ifBlank { null }
                    Log.d(ACCESSIBILITY_TAG, "Launcher setting updated: $pkg")
                }
        }

        serviceScope.launch {
            DebugSettingsStore.getAutoRaiseDragonOnSystemLauncher(this@SystemControlService)
                .collect { enabled ->
                    autoRaiseEnabled = enabled
                    Log.d(ACCESSIBILITY_TAG, "Auto-raise toggled: $enabled")
                }
        }
    }

    companion object {
        var INSTANCE: SystemControlService? = null
        private const val DEBOUNCE_DELAY_MS = 100L
        private var lastLaunchTime = 0L
        private val isSwitching = mutableStateOf(false)
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

}
