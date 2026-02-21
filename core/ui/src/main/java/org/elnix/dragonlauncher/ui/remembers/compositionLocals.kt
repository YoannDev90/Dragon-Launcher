package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.ImageBitmap
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable

val LocalIcons = compositionLocalOf<Map<String, ImageBitmap>> { error("No icons provided") }
val LocalIconShape = compositionLocalOf<IconShape> { error("No iconShape Provided") }
val LocalNests = compositionLocalOf<List<CircleNest>> { error("No nests provided") }
val LocalPoints = compositionLocalOf<List<SwipePointSerializable>> { error("No points provided") }
val LocalDefaultPoint = compositionLocalOf<SwipePointSerializable> { error("No default point provided") }
