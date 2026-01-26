package org.elnix.dragonlauncher.enumsui

import android.content.Context
import org.elnix.dragonlauncher.common.R

enum class SelectedUnselectedViewMode {
    UNSELECTED,
    SELECTED
}

fun selectedUnselectedViewName(ctx: Context, mode: SelectedUnselectedViewMode): String {
    return when(mode) {
        SelectedUnselectedViewMode
            .SELECTED -> ctx.getString(R.string.selected_text)
        SelectedUnselectedViewMode
            .UNSELECTED -> ctx.getString(R.string.unselected)
    }
}
