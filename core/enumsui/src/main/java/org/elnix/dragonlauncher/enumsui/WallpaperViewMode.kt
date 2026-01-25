package org.elnix.dragonlauncher.enumsui

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home


enum class WallpaperEditMode {
    MAIN,
    DRAWER
}

fun wallpaperEditModeIcon(mode: WallpaperEditMode) = when (mode) {
    WallpaperEditMode.MAIN ->
        Icons.Default.Home

    WallpaperEditMode.DRAWER ->
        Icons.Default.Apps
}

fun wallpaperEditModeLabel(ctx: Context, mode: WallpaperEditMode) = when (mode) {
    WallpaperEditMode.MAIN ->
        ctx.getString(org.elnix.dragonlauncher.common.R.string.wallpaper_edit_main)

    WallpaperEditMode.DRAWER ->
        ctx.getString(org.elnix.dragonlauncher.common.R.string.wallpaper_edit_drawer)
}
