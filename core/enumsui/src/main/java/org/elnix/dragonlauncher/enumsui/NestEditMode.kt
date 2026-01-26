package org.elnix.dragonlauncher.enumsui

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.TextRotationAngleup
import androidx.compose.material.icons.filled.Vibration
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.enumsui.NestEditMode.DRAG
import org.elnix.dragonlauncher.enumsui.NestEditMode.HAPTIC
import org.elnix.dragonlauncher.enumsui.NestEditMode.MIN_ANGLE


enum class NestEditMode { DRAG, HAPTIC, MIN_ANGLE }

fun nestEditModeIcon(action: NestEditMode) = when (action) {
    DRAG -> Icons.Default.DragIndicator
    HAPTIC -> Icons.Default.Vibration
    MIN_ANGLE -> Icons.Default.TextRotationAngleup
}

fun nestEditModeLabel(ctx: Context,action: NestEditMode) = when (action) {
    DRAG -> ctx.getString(R.string.drag_distances)
    HAPTIC -> ctx.getString(R.string.haptic_feedback)
    MIN_ANGLE ->ctx.getString(R.string.min_angle_to_activate)
}
