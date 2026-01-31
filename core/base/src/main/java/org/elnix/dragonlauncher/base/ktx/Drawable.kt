package org.elnix.dragonlauncher.base.ktx

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable

fun Drawable.drawWithColorFilter(canvas: Canvas, colorFilter: ColorFilter?) {
    val cf = this.colorFilter
    this.colorFilter = colorFilter
    this.draw(canvas)
    this.colorFilter = cf
}
