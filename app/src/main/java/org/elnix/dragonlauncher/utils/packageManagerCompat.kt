package org.elnix.dragonlauncher.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.ContextCompat
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.ui.drawer.AppModel

class PackageManagerCompat(private val pm: PackageManager) {

    fun getInstalledPackages(flags: Int = 0): List<PackageInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledPackages(flags)
        } else {
            pm.getInstalledPackages(flags)
        }
    }


    fun getAllApps(): List<AppModel> {
        // 1. Get all installed package infos (includes non-launchable, system, etc.)
        val allPackages = getInstalledPackages(PackageManager.GET_META_DATA)

        // 2. Build a quick lookup set of launchable package names
        val launchIntent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val launchablePackages = pm.queryIntentActivities(launchIntent, 0)
            .mapNotNull { it.activityInfo?.applicationInfo?.packageName }
            .toSet()

        // 3. Map all packages into AppModel including a "launchable" field
        return allPackages.mapNotNull { pkgInfo ->
            val appInfo = pkgInfo.applicationInfo ?: return@mapNotNull null
            val pkgName = appInfo.packageName

            if (!isAppEnabled(pkgName)) return@mapNotNull null

            val label = try {
                appInfo.loadLabel(pm).toString()
            } catch (_: Exception) {
                pkgName
            }

            val isLaunchable = launchablePackages.contains(pkgName)

            AppModel(
                name = label,
                packageName = pkgName,
                isEnabled = true,
                isSystem = isSystemApp(appInfo),
                isWorkProfile = false, // TODO later, RN nobody uses work profile
                isLaunchable = isLaunchable
            )
        }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase() }
    }



    private fun isAppEnabled(pkgName: String): Boolean {
        return pm.getApplicationEnabledSetting(pkgName) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    }

    private fun isSystemApp(appInfo: ApplicationInfo): Boolean {
        val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val isUpdatedSystem = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        return isSystem && !isUpdatedSystem &&
                (appInfo.packageName.startsWith("com.android.") || appInfo.packageName.startsWith("android"))
    }

    fun getAppIcon(pkgName: String, ctx: Context): Drawable {
        return try {
            val appInfo = pm.getApplicationInfo(pkgName, 0)
            appInfo.loadUnbadgedIcon(pm)
        } catch (_: Exception) {
            ContextCompat.getDrawable(ctx, R.drawable.ic_app_default)!!
        }
    }

    fun getResourcesForApplication(pkgName: String): android.content.res.Resources {
        return pm.getResourcesForApplication(pkgName)
    }
}
