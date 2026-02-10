package org.elnix.dragonlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserHandle
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logI

/**
 * BroadcastReceiver to listen for Private Space lock/unlock events (Android 15+).
 * 
 * Listens to:
 * - ACTION_PROFILE_AVAILABLE: Private Space is unlocked and accessible
 * - ACTION_PROFILE_UNAVAILABLE: Private Space is locked
 */
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
class PrivateSpaceReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        val action = intent.action ?: return
        val userHandle = intent.getParcelableExtra<UserHandle>(Intent.EXTRA_USER)
        
        logI("PrivateSpaceReceiver", "========================================")
        logI("PrivateSpaceReceiver", "Received action: $action for user: $userHandle")
        logI("PrivateSpaceReceiver", "========================================")
        
        when (action) {
            Intent.ACTION_PROFILE_AVAILABLE -> {
                // Private Space is now unlocked
                logI("PrivateSpaceReceiver", "Private Space unlocked - reloading apps")
                handlePrivateSpaceUnlocked(context, userHandle)
            }
            Intent.ACTION_PROFILE_UNAVAILABLE -> {
                // Private Space is now locked
                logI("PrivateSpaceReceiver", "Private Space locked - reloading apps")
                handlePrivateSpaceLocked(context, userHandle)
            }
            else -> {
                logI("PrivateSpaceReceiver", "Unknown action: $action")
            }
        }
    }
    
    private fun handlePrivateSpaceUnlocked(context: Context, userHandle: UserHandle?) {
        logI("PrivateSpaceReceiver", "handlePrivateSpaceUnlocked called")
        // Reload apps to include Private Space apps
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val app = context.applicationContext as? MyApplication
                if (app == null) {
                    logI("PrivateSpaceReceiver", "ERROR: Could not get MyApplication instance")
                    return@launch
                }
                
                logI("PrivateSpaceReceiver", "Calling appsViewModel.reloadApps()...")
                app.appsViewModel.reloadApps()
                logI("PrivateSpaceReceiver", "Apps reloaded successfully after Private Space unlock")
            } catch (e: Exception) {
                logI("PrivateSpaceReceiver", "ERROR reloading apps: ${e.message}\n${e.stackTraceToString()}")
            }
        }
    }
    
    private fun handlePrivateSpaceLocked(context: Context, userHandle: UserHandle?) {
        logI("PrivateSpaceReceiver", "handlePrivateSpaceLocked called")
        // Reload apps to hide Private Space apps
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val app = context.applicationContext as? MyApplication
                if (app == null) {
                    logI("PrivateSpaceReceiver", "ERROR: Could not get MyApplication instance")
                    return@launch
                }
                
                logI("PrivateSpaceReceiver", "Calling appsViewModel.reloadApps()...")
                app.appsViewModel.reloadApps()
                logI("PrivateSpaceReceiver", "Apps reloaded successfully after Private Space lock")
            } catch (e: Exception) {
                logI("PrivateSpaceReceiver", "ERROR reloading apps: ${e.message}\n${e.stackTraceToString()}")
            }
        }
    }
}
