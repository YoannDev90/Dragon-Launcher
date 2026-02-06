package org.elnix.dragonlauncher.common.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Process
import android.os.UserManager
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.ContextCompat
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.mapAppToSection
import org.elnix.dragonlauncher.common.utils.ImageUtils.loadDrawableAsBitmap

class PackageManagerCompat(private val pm: PackageManager, private val ctx: Context) {

    fun getInstalledPackages(flags: Int = 0): List<PackageInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledPackages(flags)
        } else {
            pm.getInstalledPackages(flags)
        }
    }


    fun getAllApps(): List<AppModel> {
        val userManager = ctx.getSystemService(Context.USER_SERVICE) as UserManager
        val launcherApps = ctx.getSystemService(LauncherApps::class.java)
        val pm = ctx.packageManager

        val result = mutableListOf<AppModel>()

        userManager.userProfiles.forEach { userHandle ->
            val isWorkProfile = userHandle != Process.myUserHandle()
            val userId = userHandle.hashCode()

            /* ────────── 1. Launchable apps (LauncherApps) ────────── */
            val activities = launcherApps
                ?.getActivityList(null, userHandle)
                ?: emptyList()

            activities.forEach { activity ->
                val appInfo = activity.applicationInfo

                val category = mapAppToSection(appInfo)
                val pkg = appInfo.packageName

                if (!isAppEnabled(pkg)) return@forEach

                result += AppModel(
                    name = activity.label?.toString() ?: pkg,
                    packageName = pkg,
                    userId = userId,
                    isEnabled = true,
                    isSystem = isSystemApp(appInfo),
                    isWorkProfile = isWorkProfile,
                    isLaunchable = true,
                    category = category
                )
            }


             /* ────────── 2. Non-launchable system apps (PackageManager) ────────── */
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .forEach { appInfo ->
                    val pkg = appInfo.packageName
                    val category = mapAppToSection(appInfo)


                    // Skip apps already added via LauncherApps
                    if (result.any { it.packageName == pkg && it.userId == userId }) return@forEach

                    // Only add enabled system apps
                    if (!isSystemApp(appInfo)) return@forEach
                    if (!appInfo.enabled) return@forEach

                    val label = pm.getApplicationLabel(appInfo).toString()

                    result += AppModel(
                        name = label,
                        packageName = pkg,
                        userId = userId,
                        isEnabled = true,
                        isSystem = true,
                        isWorkProfile = isWorkProfile,
                        isLaunchable = false,
                        category = category
                    )
                }
        }

        return result
            .distinctBy { "${it.packageName}_${it.userId}" }
    }



    private fun isAppEnabled(pkgName: String): Boolean {
        return try {
            pm.getApplicationEnabledSetting(pkgName) !=
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        } catch (_: Exception) {
            true
        }
    }


    private fun isSystemApp(appInfo: ApplicationInfo): Boolean {
        val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val isUpdatedSystem = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        return isSystem && !isUpdatedSystem &&
                (appInfo.packageName.startsWith("com.android.") || appInfo.packageName.startsWith("android"))
    }

    fun getAppIcon(
        packageName: String,
        userId: Int
    ): Drawable {
        val launcherApps = ctx.getSystemService(LauncherApps::class.java)
        val userManager = ctx.getSystemService(UserManager::class.java)

        val userHandle = userManager.userProfiles
            .firstOrNull { it.hashCode() == userId }
            ?: Process.myUserHandle()

        return try {
            // ─── WORK PROFILE OR ANY NON-CURRENT USER ───
            if (userHandle != Process.myUserHandle() && launcherApps != null) {

                // Try launcher activity icon first (correct & badged)
                val activities = launcherApps.getActivityList(packageName, userHandle)
                if (!activities.isNullOrEmpty()) {
                    return activities[0].getBadgedIcon(0)
                }

                // Fallback: application icon via LauncherApps
                val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    launcherApps.getApplicationInfo(
                        packageName,
                        0,
                        userHandle
                    )
                } else {
                    pm.getApplicationInfo(packageName, 0)
                }

                return appInfo.loadIcon(pm)
            }

            // ─── PERSONAL PROFILE ───
            val appInfo = pm.getApplicationInfo(packageName, 0)
            appInfo.loadIcon(pm)

        } catch (e: Exception) {
            logE(TAG, "Failed to load icon for $packageName (userId=$userId)", e)
            ContextCompat.getDrawable(ctx, R.drawable.ic_app_default)!!
        }
    }


    fun getResourcesForApplication(pkgName: String): Resources {
        return pm.getResourcesForApplication(pkgName)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun queryAppShortcuts(packageName: String): List<ShortcutInfo> {
        logE(TAG, "Starting queryAppShortcuts for package: $packageName")

        try {
            logE(TAG, "Getting LauncherApps service...")
            val launcherApps = ctx.getSystemService(LauncherApps::class.java)
            if (launcherApps == null) {
                logE(TAG, "LauncherApps service is null - returning empty list")
                return emptyList()
            }
            logE(TAG, "LauncherApps service obtained successfully")

            logE(TAG, "Creating ShortcutQuery with flags...")
            val query = LauncherApps.ShortcutQuery()
                .setPackage(packageName)
                .setQueryFlags(
                    LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or
                            LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or
                            LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
                )
            logE(TAG, "ShortcutQuery created with package: $packageName and flags: $query")

            logE(TAG, "Getting current userHandle...")
            val userHandle = Process.myUserHandle()
            logE(TAG, "UserHandle obtained: $userHandle")

            logE(TAG, "Calling getShortcuts with query and userHandle...")
            val shortcuts = launcherApps.getShortcuts(query, userHandle)
            logE(TAG, "getShortcuts returned: ${shortcuts?.size ?: 0} shortcuts")

            if (shortcuts != null) {
                logE(TAG, "Shortcuts details: ${shortcuts.joinToString { it.id }}")
                return shortcuts
            } else {
                logE(TAG, "getShortcuts returned null - returning empty list")
                return emptyList()
            }

        } catch (e: SecurityException) {
            logE(TAG, "SecurityException: ${e.message}", e)
            ctx.showToast("Need to be default launcher to query shortcuts")
            return emptyList()
        } catch (e: IllegalStateException) {
            logE(TAG, "IllegalStateException: ${e.message}", e)
            return emptyList()
        } catch (e: NullPointerException) {
            logE(TAG, "NullPointerException: ${e.message}", e)
            return emptyList()
        } catch (e: Exception) {
            logE(TAG, "Unexpected exception: ${e.message}", e)
            return emptyList()
        }
    }
}


fun launchShortcut(ctx: Context, pkg: String, id: String) {
    val launcherApps = ctx.getSystemService(LauncherApps::class.java) ?: return
    try {
        launcherApps.startShortcut(pkg, id, null, null, Process.myUserHandle())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun loadShortcutIcon(
    ctx: Context,
    packageName: String,
    shortcutId: String
): ImageBitmap? {
    try {
        val launcherApps = ctx.getSystemService(LauncherApps::class.java) ?: return null
        val user = Process.myUserHandle()

        val query = LauncherApps.ShortcutQuery()
            .setPackage(packageName)
            .setQueryFlags(
                LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or
                        LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or
                        LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
            )

        val shortcuts = launcherApps.getShortcuts(query, user) ?: return null
        val shortcut = shortcuts.firstOrNull { it.id == shortcutId } ?: return null

        val drawable = launcherApps.getShortcutIconDrawable(shortcut, 0) ?: return null

        // Convert Drawable → ImageBitmap
        return loadDrawableAsBitmap(drawable, 48, 48)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}
