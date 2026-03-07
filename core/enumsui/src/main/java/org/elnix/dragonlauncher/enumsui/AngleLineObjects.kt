package org.elnix.dragonlauncher.enumsui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.common.R

enum class AngleLineObjects {
    Line,
    Angle,
    Start,
    End
}

@Composable
fun AngleLineObjects.label(): String = when (this) {
    AngleLineObjects.Line -> stringResource(R.string.line_object)
    AngleLineObjects.Angle -> stringResource(R.string.angle_object)
    AngleLineObjects.Start -> stringResource(R.string.start_object)
    AngleLineObjects.End -> stringResource(R.string.end_object)
}
