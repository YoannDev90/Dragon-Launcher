package org.elnix.dragonlauncher.enumsui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.common.R

enum class SelectedUnselectedViewMode {
    UNSELECTED,
    SELECTED
}

@Composable
fun selectedUnselectedViewName(mode: SelectedUnselectedViewMode): String {
    return when(mode) {
        SelectedUnselectedViewMode
            .SELECTED -> stringResource(R.string.selected)
        SelectedUnselectedViewMode
            .UNSELECTED -> stringResource(R.string.unselected)
    }
}
