package org.elnix.dragonlauncher.utils.actions

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import org.elnix.dragonlauncher.data.SwipeActionSerializable
import org.elnix.dragonlauncher.services.SystemControl
import org.elnix.dragonlauncher.utils.showToast
import java.io.File


/**
 * Exception for app launch failures
 */
class AppLaunchException(message: String, cause: Throwable? = null) : Exception(message, cause)

private const val FILE_PROVIDER_AUTHORITY = "org.elnix.dragonlauncher.fileprovider"

fun launchSwipeAction(
    ctx: Context,
    action: SwipeActionSerializable?,
    onAppSettings: (() -> Unit)? = null,
    onAppDrawer: (() -> Unit)? = null
) {
    if (action == null) return

    when (action) {

        is SwipeActionSerializable.LaunchApp -> {
            val i = ctx.packageManager.getLaunchIntentForPackage(action.packageName)
            if (i != null) {
                try {
                    ctx.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } catch (e: SecurityException) {
                    throw AppLaunchException("Security error launching ${action.packageName}", e)
                } catch (e: NullPointerException) {
                    throw AppLaunchException("App component not found for ${action.packageName}", e)
                } catch (e: Exception) {
                    throw AppLaunchException("Failed to launch ${action.packageName}", e)
                }
            }
        }

        is SwipeActionSerializable.OpenUrl -> {
            val i = Intent(Intent.ACTION_VIEW, action.url.toUri())
            ctx.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        SwipeActionSerializable.NotificationShade -> {
            if (!SystemControl.isServiceEnabled(ctx)) {
                ctx.showToast("Please enable accessibility settings to use that feature")
                SystemControl.openServiceSettings(ctx)
                return
            }
            SystemControl.expandNotifications(ctx)
        }

        SwipeActionSerializable.ControlPanel -> {
            SystemControl.expandQuickSettings(ctx)
        }

        SwipeActionSerializable.OpenAppDrawer -> {
            onAppDrawer?.invoke()
        }

        SwipeActionSerializable.OpenDragonLauncherSettings -> onAppSettings?.invoke()
<<<<<<< Updated upstream
        SwipeActionSerializable.Lock -> null// TODO("Lock the phone (maybe using admin rights")
=======

        SwipeActionSerializable.Lock -> {
            if (!SystemControl.isServiceEnabled(ctx)) {
                ctx.showToast("Please enable accessibility settings to use that feature")
                SystemControl.openServiceSettings(ctx)
                return
            }
            SystemControl.lockScreen(ctx)
        }

>>>>>>> Stashed changes
        is SwipeActionSerializable.OpenFile -> {
            try {
                val file = File(action.filePath)
                if (!file.exists()) {
                    ctx.showToast("File not found: ${action.filePath} (Check All Files Access)")
                    return
                }

                // --- Crucial change: Use FileProvider instead of Uri.fromFile ---
                // FileProvider creates a content:// Uri with temporary permissions for the target app.
                val uri = FileProvider.getUriForFile(
                    ctx,
                    FILE_PROVIDER_AUTHORITY,
                    file
                )

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, action.mimeType ?: "*/*")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    // Grant temporary permission to the receiving application
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                // -----------------------------------------------------------------

                if (intent.resolveActivity(ctx.packageManager) != null) {
                    ctx.startActivity(intent)
                } else {
                    ctx.showToast("No app available to open this file")
                }
            } catch (e: Exception) {
                ctx.showToast("Unable to open file: ${e.message}")
                Log.e("File", "Error opening file: $e")
            }
        }
    }
}
