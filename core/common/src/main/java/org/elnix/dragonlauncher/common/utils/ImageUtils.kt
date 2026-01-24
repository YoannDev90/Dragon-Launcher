@file:Suppress("DEPRECATION")

package org.elnix.dragonlauncher.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Base64
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.withSave
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.serializables.CustomIconSerializable
import org.elnix.dragonlauncher.common.serializables.IconType
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.targetPackage
import java.io.ByteArrayOutputStream
import kotlin.math.ceil

object ImageUtils {

    fun loadBitmap(ctx: Context, uri: Uri): Bitmap {
        ctx.contentResolver.openInputStream(uri).use {
            return BitmapFactory.decodeStream(it!!)
        }
    }


    fun loadDrawableAsBitmap(
        drawable: Drawable,
        width: Int,
        height: Int,
        tint: Int? = null
    ): ImageBitmap {
        // Adaptive icon support
        val bmp = if (drawable is AdaptiveIconDrawable) {
            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } else {
            drawable.toBitmap(width, height)
        }
        val imageBitmap = bmp.asImageBitmap()
        return tint?.let{
            tintBitmap(imageBitmap, tint)
        } ?: imageBitmap
    }

    fun cropCenterSquare(src: Bitmap): Bitmap {
        val size = minOf(src.width, src.height)
        val left = (src.width - size) / 2
        val top = (src.height - size) / 2

        return Bitmap.createBitmap(src, left, top, size, size)
    }

    fun resize(src: Bitmap, size: Int): Bitmap =
        src.scale(size, size)


    fun base64ToImageBitmap(base64: String?): ImageBitmap? {
        return try {
            base64?.let {
                val bytes = Base64.decode(it, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bitmap?.asImageBitmap()
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String? {
        return try {
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
        } catch (e: Exception) {
            logE(IMAGE_TAG, e.toString())
            null
        }
    }

    fun uriToBase64(ctx: Context, uri: Uri): String? {
        return try {
            val bmp = loadBitmap(ctx, uri)
                .let(ImageUtils::cropCenterSquare)
                .let { resize(it, 256) }

            bitmapToBase64(bmp)
        } catch (e: Exception) {
            logE(IMAGE_TAG, e.toString())
            null
        }
    }

    fun imageBitmapToBase64(imageBitmap: ImageBitmap): String? {
        return try {
            val androidBitmap = imageBitmap.asAndroidBitmap()
            bitmapToBase64(androidBitmap)
        } catch (e: Exception) {
            logE(IMAGE_TAG, e.toString())
            null
        }
    }


    fun blurBitmap(ctx: Context, bitmap: Bitmap, radius: Float): Bitmap {
        if (radius <= 0f) return bitmap

        val scaleFactor = (25f - radius) / 25f.coerceAtLeast(0.1f)
        val scaledWidth = (bitmap.width * scaleFactor).toInt().coerceAtLeast(100)
        val scaledHeight = (bitmap.height * scaleFactor).toInt().coerceAtLeast(100)

        val scaledBitmap = bitmap.scale(scaledWidth, scaledHeight, false)
        val output = createBitmap(scaledWidth, scaledHeight)

        val rs = RenderScript.create(ctx)
        val input = Allocation.createFromBitmap(rs, scaledBitmap)
        val outputAlloc = Allocation.createFromBitmap(rs, output)

        val blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        blur.setRadius(radius.coerceIn(1f, 25f))
        blur.setInput(input)
        blur.forEach(outputAlloc)
        outputAlloc.copyTo(output)

        rs.destroy()
        input.destroy()
        outputAlloc.destroy()
        scaledBitmap.recycle()

        return output
    }


    fun textToBitmap(
        text: String,
        sizePx: Int,
        color: Int = 0xFFFFFFFF.toInt()
    ): ImageBitmap {
        val paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
            textSize = sizePx.toFloat()
            this.color = color
            isSubpixelText = true
            isLinearText = true
        }

        val maxWidth = ceil(paint.measureText(text)).toInt().coerceAtLeast(1)

        val layout = StaticLayout.Builder
            .obtain(text, 0, text.length, paint, maxWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .build()

        val bitmap = createBitmap(
            layout.width.coerceAtLeast(1),
            layout.height.coerceAtLeast(1)
        )

        val canvas = Canvas(bitmap)
        layout.draw(canvas)

        return bitmap.asImageBitmap()
    }

    fun tintBitmap(original: ImageBitmap, color: Int): ImageBitmap {
        val bitmap = createBitmap(original.width, original.height)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            colorFilter = PorterDuffColorFilter(
                color,
                PorterDuff.Mode.SRC_IN
            )
        }
        canvas.drawBitmap(original.asAndroidBitmap(), 0f, 0f, paint)
        return bitmap.asImageBitmap()
    }

    fun createDefaultBitmap(
        width: Int,
        height: Int
    ): ImageBitmap {
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.Gray.toArgb())
        return bitmap.asImageBitmap()
    }

    fun loadDrawableResAsBitmap(
        ctx: Context,
        resId: Int,
        width: Int,
        height: Int
    ): ImageBitmap {
        val drawable = ContextCompat.getDrawable(ctx, resId)
            ?: return createDefaultBitmap(width, height)

        return loadDrawableAsBitmap(drawable, width, height)
    }

    fun createUntintedBitmap(
        action: SwipeActionSerializable,
        ctx: Context,
        icons: Map<String, ImageBitmap>,
        width: Int,
        height: Int
    ): ImageBitmap {
        val pm = ctx.packageManager
        val pmCompat = PackageManagerCompat(pm, ctx)

        return when (action) {
            is SwipeActionSerializable.LaunchApp,
            is SwipeActionSerializable.LaunchShortcut -> {
                val pkg = action.targetPackage()!!

                if (action is SwipeActionSerializable.LaunchShortcut) {
                    val shortcutIcon = loadShortcutIcon(ctx, pkg, action.shortcutId)
                    if (shortcutIcon != null) return shortcutIcon
                }

                pmCompat.getAppIcon(pkg, 0)
                icons[pkg]

            }

            is SwipeActionSerializable.OpenUrl ->
                loadDrawableResAsBitmap(ctx, R.drawable.ic_action_web, width, height)

            SwipeActionSerializable.NotificationShade ->
                loadDrawableResAsBitmap(ctx, R.drawable.ic_action_notification, width, height)

            SwipeActionSerializable.ControlPanel ->
                loadDrawableResAsBitmap(ctx, R.drawable.ic_action_grid, width, height)

            SwipeActionSerializable.OpenAppDrawer ->
                loadDrawableResAsBitmap(ctx, R.drawable.ic_action_drawer, width, height)

            SwipeActionSerializable.OpenDragonLauncherSettings ->
                loadDrawableResAsBitmap(ctx, R.drawable.dragon_launcher_foreground, width, height)

            SwipeActionSerializable.Lock -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_lock, width, height)
            is SwipeActionSerializable.OpenFile -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_open_file, width, height)
            SwipeActionSerializable.ReloadApps -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_reload, width, height)
            SwipeActionSerializable.OpenRecentApps -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_recent, width, height)
            is SwipeActionSerializable.OpenCircleNest -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_target, width, height)
            SwipeActionSerializable.GoParentNest -> loadDrawableResAsBitmap(ctx, R.drawable.ic_icon_go_parent_nest, width, height)
            is SwipeActionSerializable.OpenWidget -> loadDrawableResAsBitmap(ctx, R.drawable.ic_action_widgets, width, height)
            SwipeActionSerializable.None -> null
        } ?: loadDrawableResAsBitmap(ctx, R.drawable.ic_app_default, width, height)
    }

    fun resolveCustomIconBitmap(
        base: ImageBitmap,
        icon: CustomIconSerializable,
        sizePx: Int
    ): ImageBitmap {
        // Step 1: choose source bitmap (override or base)
        val sourceBitmap: ImageBitmap = when (icon.type) {
            IconType.BITMAP -> {
                icon.source
                    ?.let { base64ToImageBitmap(it) }
                    ?: base
            }

            IconType.ICON_PACK -> base
            IconType.TEXT -> {
                icon.source?.let {
                    textToBitmap(
                        text = it,
                        sizePx = sizePx
                    )
                } ?: base
            }

            IconType.PLAIN_COLOR -> icon.source?.let {
                try {
                    val sourceColor = it.toInt()
                    val bmp = createDefaultBitmap(sizePx, sizePx)
                    tintBitmap(bmp, sourceColor)
                } catch (_: Exception) {
                    base
                }
            } ?: base

            IconType.SHAPE,
            null -> base
        }

        // Step 2: prepare output bitmap
        val outBitmap = createBitmap(sizePx, sizePx)
        val canvas = Canvas(outBitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Step 3: opacity
        paint.alpha = ((icon.opacity ?: 1f).coerceIn(0f, 1f) * 255).toInt()

        // Step 4: color tint
        icon.tint?.let {
            paint.colorFilter = PorterDuffColorFilter(
                it,
                PorterDuff.Mode.SRC_IN
            )
        }

        // Step 5: blend mode (best-effort)
        icon.blendMode?.let {
            paint.xfermode = when (it.uppercase()) {
                "MULTIPLY" -> PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
                "SCREEN" -> PorterDuffXfermode(PorterDuff.Mode.SCREEN)
                "OVERLAY" -> PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
                else -> null
            }
        }

        // Step 6: shadow
        if (icon.shadowRadius != null) {
            paint.setShadowLayer(
                icon.shadowRadius,
                icon.shadowOffsetX ?: 0f,
                icon.shadowOffsetY ?: 0f,
                (icon.shadowColor ?: 0x55000000).toInt()
            )
        }

        // Step 7: transform (scale + rotation)
        canvas.withSave {

            val scaleX = icon.scaleX ?: 1f
            val scaleY = icon.scaleY ?: 1f
            val rotation = icon.rotationDeg ?: 0f

            canvas.translate(sizePx / 2f, sizePx / 2f)
            canvas.rotate(rotation)
            canvas.scale(scaleX, scaleY)
            canvas.translate(-sizePx / 2f, -sizePx / 2f)

            // Step 8: draw bitmap
            canvas.drawBitmap(
                sourceBitmap.asAndroidBitmap(),
                null,
                Rect(0, 0, sizePx, sizePx),
                paint
            )

        }

        // Step 9: stroke (rectangular, corner clipping is UI-level)
        if (icon.strokeWidth != null && icon.strokeColor != null) {
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = icon.strokeWidth
                color = icon.strokeColor.toInt()
            }
            canvas.drawRect(
                0f,
                0f,
                sizePx.toFloat(),
                sizePx.toFloat(),
                strokePaint
            )
        }

        return outBitmap.asImageBitmap()
    }
}
