package org.elnix.dragonlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserHandle
import androidx.annotation.RequiresApi
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.logging.logI
import org.elnix.dragonlauncher.common.utils.PrivateSpaceUtils

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

        if (action != Intent.ACTION_PROFILE_AVAILABLE && action != Intent.ACTION_PROFILE_UNAVAILABLE) {
            return
        }

        val pendingResult = goAsync()
        val app = context.applicationContext as? MyApplication
        if (app == null) {
            pendingResult.finish()
            return
        }

        app.appScope.launch {
            try {
                val eventUser = intent.extractEventUserHandle()
                val privateUser = PrivateSpaceUtils.getPrivateSpaceUserHandle(context)

                if (privateUser == null || eventUser == null || eventUser != privateUser) {
                    logD(
                        "PrivateSpaceReceiver",
                        "Ignoring profile action=$action for non-private user=$eventUser"
                    )
                    return@launch
                }

                logI("PrivateSpaceReceiver", "Private Space action=$action, reloading apps")
                app.appsViewModel.reloadApps()
            } catch (e: Exception) {
                logE("PrivateSpaceReceiver", "Failed to process Private Space broadcast: ${e.message}", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun Intent.extractEventUserHandle(): UserHandle? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(Intent.EXTRA_USER, UserHandle::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(Intent.EXTRA_USER)
        }
    }
}
