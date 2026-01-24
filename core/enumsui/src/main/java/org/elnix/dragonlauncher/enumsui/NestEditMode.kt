package org.elnix.dragonlauncher.enumsui

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Vibration


enum class NestEditMode { DRAG, HAPTIC }

fun nestEditModeIcon(action: NestEditMode) = when (action) {
    NestEditMode.DRAG -> Icons.Default.DragIndicator
    NestEditMode.HAPTIC -> Icons.Default.Vibration
}

fun nestEditModeLabel(ctx: Context,action: NestEditMode) = when (action) {
    NestEditMode.DRAG -> ctx.getString(org.elnix.dragonlauncher.common.R.string.drag_distances)
    NestEditMode.HAPTIC -> ctx.getString(org.elnix.dragonlauncher.common.R.string.haptic_feedback)

}
