package org.elnix.dragonlauncher.utils.actions

import android.content.Context
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.SwipeActionSerializable

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
fun actionIcon(
    action: SwipeActionSerializable,
    icons: Map<String, ImageBitmap>? = null
): Painter = when (action) {

    is SwipeActionSerializable.LaunchApp ->
        appIcon(
            packageName = action.packageName,
            icons = icons
        )

    is SwipeActionSerializable.OpenUrl ->
        painterResource(R.drawable.ic_action_web)

    SwipeActionSerializable.NotificationShade ->
        painterResource(R.drawable.ic_action_notification)

    SwipeActionSerializable.ControlPanel ->
        painterResource(R.drawable.ic_action_grid)

    SwipeActionSerializable.OpenAppDrawer ->
        painterResource(R.drawable.ic_action_drawer)

    SwipeActionSerializable.OpenDragonLauncherSettings ->
        painterResource(R.drawable.dragon_launcher_foreground)

    SwipeActionSerializable.Lock -> painterResource(R.drawable.ic_action_lock)
    is SwipeActionSerializable.OpenFile -> painterResource(R.drawable.ic_action_open_file)
    SwipeActionSerializable.ReloadApps -> painterResource(R.drawable.ic_action_reload)
    SwipeActionSerializable.OpenRecentApps -> painterResource(R.drawable.ic_action_recent)
}



fun actionIconBitmap(
    action: SwipeActionSerializable,
    context: Context,
    tintColor: Color
): ImageBitmap {
    val bitmap = createUntintedBitmap(action, context)
    return if (action is SwipeActionSerializable.LaunchApp || action is SwipeActionSerializable.OpenDragonLauncherSettings) {
        bitmap
    } else {
        tintBitmap(bitmap, tintColor)
    }
}

private fun createUntintedBitmap(action: SwipeActionSerializable, context: Context): ImageBitmap {
    return when (action) {
        is SwipeActionSerializable.LaunchApp -> {
            try {
                loadDrawableAsBitmap(
                    context.packageManager.getApplicationIcon(action.packageName),
                    48,
                    48
                )

            } catch (_: Exception) { // If the app was uninstalled, it could not reload it
                loadDrawableResAsBitmap(context, R.drawable.ic_app_default)
            }
        }

        is SwipeActionSerializable.OpenUrl ->
            loadDrawableResAsBitmap(context, R.drawable.ic_action_web)

        SwipeActionSerializable.NotificationShade ->
            loadDrawableResAsBitmap(context, R.drawable.ic_action_notification)

        SwipeActionSerializable.ControlPanel ->
            loadDrawableResAsBitmap(context, R.drawable.ic_action_grid)

        SwipeActionSerializable.OpenAppDrawer ->
            loadDrawableResAsBitmap(context, R.drawable.ic_action_drawer)

        SwipeActionSerializable.OpenDragonLauncherSettings ->
            loadDrawableResAsBitmap(context, R.drawable.dragon_launcher_foreground)

        SwipeActionSerializable.Lock -> loadDrawableResAsBitmap(context, R.drawable.ic_action_lock)
        is SwipeActionSerializable.OpenFile -> loadDrawableResAsBitmap(context, R.drawable.ic_action_open_file)
        SwipeActionSerializable.ReloadApps -> loadDrawableResAsBitmap(context, R.drawable.ic_action_reload)
        SwipeActionSerializable.OpenRecentApps -> loadDrawableResAsBitmap(context, R.drawable.ic_action_recent)
    }
}


private fun tintBitmap(original: ImageBitmap, color: Color): ImageBitmap {
    val bitmap = createBitmap(original.width, original.height)
    val canvas = Canvas(bitmap)
    val paint = android.graphics.Paint().apply {
        colorFilter = android.graphics.PorterDuffColorFilter(
            color.toArgb(),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }
    canvas.drawBitmap(original.asAndroidBitmap(), 0f, 0f, paint)
    return bitmap.asImageBitmap()
}


// Fallback: Create a simple colored square if all else fails
private fun createDefaultBitmap(): ImageBitmap {
    val bitmap = createBitmap(48, 48)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.Gray.toArgb())
    return bitmap.asImageBitmap()
}


private fun loadDrawableResAsBitmap(context: Context, resId: Int): ImageBitmap {
    val drawable = ContextCompat.getDrawable(context, resId)
        ?: return createDefaultBitmap()

    return loadDrawableAsBitmap(drawable, 48, 48)
}

@Suppress("SameParameterValue")
fun loadDrawableAsBitmap(
    drawable: android.graphics.drawable.Drawable,
    width: Int,
    height: Int
): ImageBitmap {
    // Adaptive icon support
    val bmp = if (drawable is android.graphics.drawable.AdaptiveIconDrawable) {
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    } else {
        drawable.toBitmap(width, height)
    }
    return bmp.asImageBitmap()
}
