package org.elnix.dragonlauncher.ui.actions

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.utils.ImageUtils.createUntintedBitmap
import org.elnix.dragonlauncher.common.utils.ImageUtils.loadDrawableResAsBitmap
import org.elnix.dragonlauncher.ui.components.PlatformShape
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors

@Composable
fun appIcon(
    packageName: String,
    icons: Map<String, ImageBitmap>? = null
): Painter {
    val cached = icons?.get(packageName)
    return if (cached != null) {
        BitmapPainter(cached)
    } else {
        painterResource(R.drawable.ic_app_default)
    }
}


@Composable
fun ActionIcon(
    action: SwipeActionSerializable,
    icons: Map<String, ImageBitmap>,
    modifier: Modifier = Modifier,
    size: Int = 64,
    showLaunchAppVectorGrid: Boolean = false
) {
    val ctx = LocalContext.current
    val extraColors = LocalExtraColors.current


    val bitmap: ImageBitmap? = when {
        action is SwipeActionSerializable.LaunchApp && showLaunchAppVectorGrid ->
            loadDrawableResAsBitmap(ctx, R.drawable.ic_app_grid, size, size)
        else -> {
            createUntintedBitmap(
                icons = icons,
                action = action,
                ctx = ctx,
                width = size,
                height = size
            )
        }
    }

    if (bitmap == null) return



    Image(
        bitmap = bitmap,
        contentDescription = null,
        colorFilter = if (
            ((action !is SwipeActionSerializable.LaunchApp) || showLaunchAppVectorGrid) &&
            (action !is SwipeActionSerializable.LaunchShortcut || action.packageName.isEmpty()) &&
            action !is SwipeActionSerializable.OpenDragonLauncherSettings
        ) ColorFilter.tint(actionColor(action, extraColors))
        else null,
        modifier = modifier
            .clip(PlatformShape)
    )
}
