package org.elnix.dragonlauncher.ui.utils.actions

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_CLOSE_SYSTEM_DIALOGS
import android.provider.Settings
import org.elnix.dragonlauncher.data.SwipeActionSerializable
import androidx.core.net.toUri

fun launchSwipeAction(ctx: Context, action: SwipeActionSerializable?) {
    if (action == null) return
    when (action) {

        is SwipeActionSerializable.LaunchApp -> {
            val i = ctx.packageManager.getLaunchIntentForPackage(action.packageName)
            if (i != null) ctx.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        is SwipeActionSerializable.OpenUrl -> {
            val i = Intent(Intent.ACTION_VIEW, action.url.toUri())
            ctx.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        SwipeActionSerializable.NotificationShade -> {
            ctx.sendBroadcast(Intent(ACTION_CLOSE_SYSTEM_DIALOGS))
            TODO()
        }

        SwipeActionSerializable.ControlPanel -> {
            val i = Intent(Settings.ACTION_SETTINGS)
            ctx.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        SwipeActionSerializable.OpenAppDrawer -> {
            TODO()
        }
    }
}
