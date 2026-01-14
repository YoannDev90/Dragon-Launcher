package org.elnix.dragonlauncher.enumsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface WorkspaceAction {
    val icon: ImageVector
    val label: String

    object Rename : WorkspaceAction {
        override val icon = Icons.Default.Edit
        override val label = "Rename"
    }

    object Delete : WorkspaceAction {
        override val icon = Icons.Default.Delete
        override val label = "Delete"
    }
}
