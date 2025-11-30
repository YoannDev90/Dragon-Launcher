package org.elnix.dragonlauncher

import android.app.Application
import org.elnix.dragonlauncher.utils.AppDrawerViewModel

class LauncherApplication : Application() {
    val appViewModel by lazy {
        AppDrawerViewModel(this)
    }
}
