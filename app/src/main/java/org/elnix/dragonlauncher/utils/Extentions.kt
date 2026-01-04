package org.elnix.dragonlauncher.utils

import android.app.SearchManager
import android.app.role.RoleManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.Settings
import android.widget.Toast
import androidx.core.net.toUri
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.utils.logs.logD
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Functions from https://github.com/mlm-games/CCLauncher
 * (https://github.com/mlm-games/CCLauncher/blob/compose/app/src/main/java/app/cclauncher/helper/Extensions.kt)
 */


/**
 * Show a toast message with flexible input types
 * @param message Can be a String, StringRes Int, or null
 * @param duration Toast duration (LENGTH_SHORT or LENGTH_LONG)
 */
fun Context.showToast(
    message: Any?,
    duration: Int = Toast.LENGTH_SHORT
) {
    when (message) {
        is String -> {
            if (message.isNotBlank()) {
                Toast.makeText(this, message, duration).show()
            }
        }

        is Int -> {
            try {
                Toast.makeText(this, message, duration).show()
            } catch (_: Exception) {
                // Invalid resource ID, ignore
            }
        }

        else -> {
            // Null or unsupported type, do nothing
        }
    }
}


fun Context.copyToClipboard(text: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(getString(R.string.app_name), text)
    clipboardManager.setPrimaryClip(clipData)
    showToast("")
}

fun Context.pasteClipboard(): String? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = clipboard.primaryClip ?: return null
    if (clip.itemCount == 0) return null
    return clip.getItemAt(0).coerceToText(this)?.toString()
}


fun Context.openUrl(url: String) {
    if (url.isEmpty()) return
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = url.toUri()
    startActivity(intent)
}


val Context.isDefaultLauncher: Boolean
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        } else {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }

            val resolveInfo = packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )

            resolveInfo?.activityInfo?.packageName == packageName
        }
    }


//fun hasAllFilesAccess(context: Context): Boolean {
//    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        // Android 11+
//        Environment.isExternalStorageManager()
//    } else {
//        // Android 10 and below (uses old READ/WRITE)
//        ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//}
//
//// Request function (requires an Activity or the activity result launcher equivalent)
//fun requestAllFilesAccess(activity: Activity) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        // Intent to redirect the user to the "All files access" setting
//        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//        val uri = Uri.fromParts("package", activity.packageName, null)
//        intent.data = uri
//        activity.startActivity(intent)
//    }
//    // For older APIs, the standard permission request dialog is used.
//}


fun Context.hasUriReadPermission(uri: Uri): Boolean {
    val perms = contentResolver.persistedUriPermissions
    return perms.any { it.uri == uri && it.isReadPermission }
}

fun Context.hasUriReadWritePermission(uri: Uri): Boolean {
    val perms = contentResolver.persistedUriPermissions
    return perms.any { perm ->
        perm.uri == uri &&
                perm.isReadPermission &&
                perm.isWritePermission
    }
}


fun Context.openSearch(query: String) {
    val intent = Intent(Intent.ACTION_WEB_SEARCH)
    intent.putExtra(SearchManager.QUERY, query)
    startActivity(intent)
}

//@SuppressLint("WrongConstant")
fun expandQuickActionsDrawer(context: Context) {
    try {
        //  (Android 12+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val statusBarManager = context.getSystemService(Context.STATUS_BAR_SERVICE) as StatusBarManager
//            statusBarManager.expandNotificationsPanel()
//            return
//        }

        // Fall back -> reflection for older versions
        val statusBarService = context.getSystemService("statusbar")
        val statusBarManager = Class.forName("android.app.StatusBarManager")
        val method = statusBarManager.getMethod("expandNotificationsPanel")
        method.invoke(statusBarService)
    } catch (_: Exception) {
        // If all else fails, try to use the notification intent
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            context.startActivity(intent)
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
    }
}

fun openAlarmApp2(ctx: Context): Boolean {
    val pm = ctx.packageManager

    // 1. Official alarm UI
    val alarmIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    if (alarmIntent.resolveActivity(pm) != null) {
        ctx.startActivity(alarmIntent)
        return true
    }

    // 2. Clock apps that declare alarm/clock actions
    val alarmLikeIntents = listOf(
        Intent(AlarmClock.ACTION_SET_ALARM),
        Intent("android.intent.action.SHOW_ALARMS"),
        Intent("android.intent.action.SHOW_ALARM")
    )

    val candidates = alarmLikeIntents
        .flatMap { base ->
            pm.queryIntentActivities(
                base,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        }
        .distinctBy { it.activityInfo.packageName to it.activityInfo.name }

    if (candidates.isNotEmpty()) {
        val best = candidates.first()
        ctx.startActivity(
            Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                component = ComponentName(
                    best.activityInfo.packageName,
                    best.activityInfo.name
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        return true
    }

    // 3. Launcher activities, filtered by known clock packages or name
    val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val launcherActivities = pm.queryIntentActivities(
        launcherIntent,
        PackageManager.MATCH_DEFAULT_ONLY
    )

    val knownClockPackages = listOf(
        "com.google.android.deskclock",
        "com.android.deskclock",
        "com.samsung.android.clockpackage",
        "com.htc.android.worldclock"
    )

    val fallback = launcherActivities.firstOrNull {
        val pkg = it.activityInfo.packageName
        pkg in knownClockPackages ||
                pkg
                    .contains("clock", ignoreCase = true) || it.loadLabel(pm).toString()
                    .contains("clock", ignoreCase = true)
    }

    if (fallback != null) {
        ctx.startActivity(
            Intent(Intent.ACTION_MAIN).apply {
                component = ComponentName(
                    fallback.activityInfo.packageName,
                    fallback.activityInfo.name
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        return true
    }

    return false
}

fun openAlarmApp(ctx: Context) {
    val pm = ctx.packageManager

    // Try official alarm actions in priority order
    listOf(
        AlarmClock.ACTION_SHOW_ALARMS,
        AlarmClock.ACTION_SET_ALARM,
        AlarmClock.ACTION_SET_TIMER
    ).forEach { action ->
        val intent = Intent(action).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(pm) != null) {
            ctx.startActivity(intent)
            return
        }
    }

    // Fallback, use the other function
    if (!openAlarmApp2(ctx)) return

    // No alarm-capable app found
    ctx.logD("TAG", "No alarm app found")
}


fun openCalendar(context: Context) {
    try {
        val calendarUri = CalendarContract.CONTENT_URI
            .buildUpon()
            .appendPath("time")
            .build()
        context.startActivity(Intent(Intent.ACTION_VIEW, calendarUri))
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            val intent = Intent(Intent.ACTION_MAIN).setClassName(
                context,
                "org.elnix.dragonlauncher.MainActivity"
            )
            intent.addCategory(Intent.CATEGORY_APP_CALENDAR)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


fun getVersionCode(ctx: Context): Int =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ctx.packageManager.getPackageInfo(ctx.packageName, 0).longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionCode
    }


fun Long.formatDateTime(): String {
    return SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        .format(Date(this))
}

//fun Long.timeAgo(): String {
//    val seconds = (System.currentTimeMillis() - this) / 1000
//    return when {
//        seconds < 60 -> "${seconds}s ago"
//        seconds < 3600 -> "${seconds / 60}m ago"
//        seconds < 86400 -> "${seconds / 3600}h ago"
//        seconds < 2592000 -> "${seconds / 86400}d ago"
//        else -> "${seconds / 2592000}mo ago"
//    }
//}
