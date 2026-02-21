package org.elnix.dragonlauncher.enumsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.TextRotationAngleup
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.ui.graphics.vector.ImageVector

enum class NestEditMode(
    val icon: ImageVector
) {
    DRAG(Icons.Default.DragIndicator),
    HAPTIC(Icons.Default.Vibration),
    MIN_ANGLE(Icons.Default.TextRotationAngleup)
}
