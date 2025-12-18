package org.elnix.dragonlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PackageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_PACKAGE_ADDED || action == Intent.ACTION_PACKAGE_REMOVED) {
            val packageName = intent.data?.schemeSpecificPart
            if (packageName != context.packageName) {
                val vm = (context.applicationContext as MyApplication).appViewModel
                vm.viewModelScope.launch {
                    vm.reloadApps(context)
                }
            }
        }
    }
}
