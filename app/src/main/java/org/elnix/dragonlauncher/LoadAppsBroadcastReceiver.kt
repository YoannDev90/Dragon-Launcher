package org.elnix.dragonlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.utils.TAG
import org.elnix.dragonlauncher.utils.logs.logE

class PackageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_PACKAGE_ADDED || action == Intent.ACTION_PACKAGE_REMOVED) {
            val packageName = intent.data?.schemeSpecificPart
            if (packageName != context.packageName) {
                try {
                    val app = context.applicationContext as MyApplication
                    app.appsViewModel.viewModelScope.launch {
                        app.appsViewModel.reloadApps(context)
                    }
                } catch (e: Exception) {
                    logE(TAG, e.toString())
                }
            }
        }
    }
}
