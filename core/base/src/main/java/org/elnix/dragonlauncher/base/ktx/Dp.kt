package org.elnix.dragonlauncher.base.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPixels(): Float {
    return value * LocalDensity.current.density
}




/* ───────────── Cool and beautiful ───────────── */
/** Create a [Dp] using an [Float], using local density for consistent results across different density devices */
@Stable
inline val Float.toDp: Dp
    @Composable
    get() = with(LocalDensity.current) { this@toDp.toDp() }

/**
 * Create a [Dp] using an [Float], using local density for consistent results across different density devices
 * same thing as [toDp] but not composable
 * */
fun Float.toDp(density: Density): Dp = with(density) { this@toDp.toDp() }





/** Create a [Dp] using an [Int], using local density for consistent results across different density devices */
inline val Int.toDp: Dp
    @Composable
    get() = with(LocalDensity.current) { this@toDp.toDp() }

/**
 * Create a [Dp] using an [Int], using local density for consistent results across different density devices
 * same thing as [toDp] but not composable
 * */
fun Int.toDp(density: Density): Dp = with(density) { this@toDp.toDp() }
