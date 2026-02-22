package org.elnix.dragonlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.utils.Constants.Logging.BROADCAST_TAG
import org.elnix.dragonlauncher.common.utils.Constants.Logging.TAG

class PackageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        Log.i(BROADCAST_TAG, "Got intent: $intent")


        if (
            action == Intent.ACTION_PACKAGE_ADDED ||
            action == Intent.ACTION_PACKAGE_REMOVED ||
            action == Intent.ACTION_PACKAGE_REPLACED ||
            action == Intent.ACTION_PACKAGES_SUSPENDED ||
            action == Intent.ACTION_PACKAGES_UNSUSPENDED ||
            action == Intent.ACTION_PACKAGE_CHANGED
        ) {
            val packageName = intent.data?.schemeSpecificPart
            val scope = CoroutineScope(Dispatchers.Default)

            Log.i(BROADCAST_TAG, "Got intent: $intent, action! $action, pkg: $packageName")
            if (packageName != context.packageName) {
                try {
                    val app = context.applicationContext as MyApplication
                    scope.launch {
                        app.appsViewModel.reloadApps()
                    }
                } catch (e: Exception) {
                    logE(TAG, e.toString())
                }
            }
        }
    }
}
