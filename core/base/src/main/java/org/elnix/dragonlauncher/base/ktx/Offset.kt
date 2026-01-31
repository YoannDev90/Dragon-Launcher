package org.elnix.dragonlauncher.base.ktx

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

fun Offset.toIntOffset(): IntOffset {
    return IntOffset(x.roundToInt(), y.roundToInt())
}
