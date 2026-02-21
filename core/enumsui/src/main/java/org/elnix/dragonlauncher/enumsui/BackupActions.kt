package org.elnix.dragonlauncher.enumsui

import android.content.Context
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.enumsui.BackupActions.CHANGE
import org.elnix.dragonlauncher.enumsui.BackupActions.REMOVE
import org.elnix.dragonlauncher.enumsui.BackupActions.TRIGGER

enum class BackupActions {
    CHANGE, REMOVE, TRIGGER
}

fun BackupActions.label(ctx: Context): String = when (this) {
    CHANGE -> ctx.getString(R.string.change)
    REMOVE -> ctx.getString(R.string.remove)
    TRIGGER -> ctx.getString(R.string.trigger_backup)
}
