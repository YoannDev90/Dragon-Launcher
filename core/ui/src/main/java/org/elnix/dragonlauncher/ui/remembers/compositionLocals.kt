package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.ImageBitmap
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable

val LocalIcons = compositionLocalOf<Map<String, ImageBitmap>> { emptyMap() }
val LocalIconShape = compositionLocalOf<IconShape> { error("No iconShape Provided") }
val LocalNests = compositionLocalOf { emptyList<CircleNest>() }
val LocalPoints = compositionLocalOf { emptyList<SwipePointSerializable>() }
