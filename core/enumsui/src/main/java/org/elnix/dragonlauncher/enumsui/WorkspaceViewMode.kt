package org.elnix.dragonlauncher.enumsui

import android.content.Context
import org.elnix.dragonlauncher.common.R

enum class WorkspaceViewMode {
    DEFAULTS,
    ADDED,
    REMOVED
}

fun workspaceViewMode(ctx: Context, mode: WorkspaceViewMode): String {
    return when(mode) {
        WorkspaceViewMode.DEFAULTS -> ctx.getString(R.string.workspace_defaults)
        WorkspaceViewMode.ADDED -> ctx.getString(R.string.workspace_added)
        WorkspaceViewMode.REMOVED -> ctx.getString(R.string.workspace_removed)
    }
}
