@file:Suppress("AssignedValueIsNeverRead", "DEPRECATION")

package org.elnix.dragonlauncher.ui.settings.customization

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.WallpaperHelper
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.enumsui.WallpaperTarget
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.helpers.ActionSelector
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader

@SuppressLint("LocalContextResourcesRead")
@Composable
fun WallpaperTab(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val wallpaperHelper = remember { WallpaperHelper(ctx) }

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showTargetDialog by remember { mutableStateOf(false) }

    val bgColor = MaterialTheme.colorScheme.background
    var plainColor by remember { mutableStateOf(bgColor) }



    fun applyWallpaper(target: WallpaperTarget) {
        val bitmap = wallpaperHelper.createPlainWallpaperBitmap(ctx, plainColor)
        scope.launch {
            wallpaperHelper.setWallpaper(bitmap, target.flags)

            ctx.showToast("Wallpaper applied")
            showTargetDialog = false
        }
    }

    SettingsLazyHeader(
        title = stringResource(R.string.wallpaper),
        onBack = onBack,
        helpText = stringResource(R.string.wallpaper_help),
        onReset = null
    ) {
        item {
            ColorPickerRow(
                label = stringResource(R.string.plain_wallpaper_color),
                defaultColor = MaterialTheme.colorScheme.background,
                currentColor = plainColor
            ) {
                plainColor = it
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = {
                        val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                        ctx.startActivity(
                            Intent.createChooser(intent, ctx.getString(R.string.select_image))
                        )
                    },
                    colors = AppObjectsColors.buttonColors()
                ) {
                    Text(
                        text = stringResource(R.string.set_wallpaper),
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = {
                        originalBitmap = wallpaperHelper.createPlainWallpaperBitmap(ctx, plainColor)
                        showTargetDialog = true
                    },
                    colors = AppObjectsColors.buttonColors()
                ) {
                    Text(stringResource(R.string.set_plain_wallpaper), textAlign = TextAlign.Center)
                }
            }
        }
    }

    ActionSelector(
        visible = showTargetDialog && originalBitmap != null,
        label = stringResource(R.string.apply_wallpaper_to),
        options = WallpaperTarget.entries,
        selected = null,
        onSelected = ::applyWallpaper,
        onDismiss = { showTargetDialog = false }
    )
}
