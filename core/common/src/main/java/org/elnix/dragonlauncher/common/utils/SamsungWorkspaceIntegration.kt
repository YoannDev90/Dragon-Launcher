package org.elnix.dragonlauncher.common.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

object SamsungWorkspaceIntegration {

    private const val TAG = "SamsungIntegration"
    private const val SECURE_FOLDER_PACKAGE = "com.samsung.knox.securefolder"
    private const val SECURE_FOLDER_ACTION = "com.samsung.knox.securefolder.LAUNCH_SECURE_FOLDER"

    fun isSamsungDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER
        val brand = Build.BRAND
        val isSamsung = manufacturer.equals("Samsung", ignoreCase = true) ||
            brand.equals("samsung", ignoreCase = true)

        Log.d(TAG, "Device manufacturer: $manufacturer")
        Log.d(TAG, "Device brand: $brand")
        Log.d(TAG, "Is Samsung: $isSamsung")

        return isSamsung
    }

    fun isSecureFolderAvailable(context: Context): Boolean {
        Log.d(TAG, "Checking if Secure Folder is available...")

        val hasSecureFolder = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    SECURE_FOLDER_PACKAGE,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(SECURE_FOLDER_PACKAGE, 0)
            }
            true
        } catch (_: Exception) {
            false
        }

        Log.i(TAG, "Secure Folder available: $hasSecureFolder")
        return hasSecureFolder
    }

    fun resolveUseSecureFolder(
        context: Context,
        preferenceEnabled: Boolean
    ): Boolean {
        val isSamsung = isSamsungDevice()
        if (!isSamsung) return false

        val hasSecureFolder = isSecureFolderAvailable(context)
        if (preferenceEnabled && !hasSecureFolder) {
            Log.w(TAG, "Secure Folder unavailable, falling back to Private Space")
        }

        return preferenceEnabled && hasSecureFolder
    }

    fun openSecureFolder(
        context: Context,
        onFallback: () -> Unit
    ) {
        try {
            val intent = Intent(SECURE_FOLDER_ACTION).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                setPackage(SECURE_FOLDER_PACKAGE)
            }
            context.startActivity(intent)
            Log.i(TAG, "Opened Secure Folder")
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to launch Secure Folder", exception)
            Log.w(TAG, "Secure Folder unavailable, falling back to Private Space")
            onFallback()
        }
    }
}
