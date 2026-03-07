package org.elnix.dragonlauncher.common.logging

import android.util.Log
import timber.log.Timber

object DragonLogManager {
    private var isLoggingEnabled = false
    private var fileTree: FileLoggingTree? = null

    fun init(ctx: android.content.Context) {
        // Arbre pour Logcat (uniquement en debug)
        if (android.util.Log.isLoggable("DragonLauncher", Log.DEBUG)) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Initialisation de l'arbre pour les fichiers
        fileTree = FileLoggingTree(ctx)
        updateLoggingState()
    }

    fun enableLogging(enable: Boolean) {
        if (isLoggingEnabled == enable) return
        isLoggingEnabled = enable
        updateLoggingState()
    }

    private fun updateLoggingState() {
        val tree = fileTree ?: return
        if (isLoggingEnabled) {
            Timber.plant(tree)
        } else {
            Timber.uproot(tree)
        }
    }

    fun getAllLogFiles(context: android.content.Context): List<java.io.File> {
        return fileTree?.getAllLogFiles() ?: emptyList()
    }

    fun clearLogs(ctx: android.content.Context) {
        fileTree?.clearAllLogs()
    }
}
