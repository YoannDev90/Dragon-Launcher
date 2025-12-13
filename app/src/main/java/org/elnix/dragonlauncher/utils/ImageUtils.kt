package org.elnix.dragonlauncher.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap

object ImageUtils {

    fun loadBitmap(ctx: Context, uri: Uri): Bitmap {
        ctx.contentResolver.openInputStream(uri).use {
            return BitmapFactory.decodeStream(it!!)
        }
    }

    fun cropCenterSquare(src: Bitmap): Bitmap {
        val size = minOf(src.width, src.height)
        val left = (src.width - size) / 2
        val top = (src.height - size) / 2

        return Bitmap.createBitmap(src, left, top, size, size)
    }

    fun resize(src: Bitmap, size: Int): Bitmap =
        Bitmap.createScaledBitmap(src, size, size, true)


    fun base64ToImageBitmap(base64: String?): androidx.compose.ui.graphics.ImageBitmap? {
        return try {
            base64?.let {
                val bytes = Base64.decode(it, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bitmap?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }
}
