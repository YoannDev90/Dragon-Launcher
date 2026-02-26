@file:Suppress("AssignedValueIsNeverRead", "DEPRECATION")

package org.elnix.dragonlauncher.ui.settings.customization

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.WallpaperHelper
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.enumsui.WallpaperEditMode
import org.elnix.dragonlauncher.enumsui.WallpaperTarget
import org.elnix.dragonlauncher.enumsui.wallpaperEditModeIcon
import org.elnix.dragonlauncher.enumsui.wallpaperEditModeLabel
import org.elnix.dragonlauncher.settings.stores.StatusBarSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.components.dragon.DragonButton
import org.elnix.dragonlauncher.ui.components.dragon.DragonColumnGroup
import org.elnix.dragonlauncher.ui.components.generic.ActionRow
import org.elnix.dragonlauncher.ui.components.generic.ActionSelector
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.WallpaperDim
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.statusbar.StatusBar

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

    var selectedView by remember { mutableStateOf(WallpaperEditMode.MAIN) }

    val wallpaperDimMainScreen by UiSettingsStore.wallpaperDimMainScreen.asState()
    val wallpaperDimDrawerScreen by UiSettingsStore.wallpaperDimDrawerScreen.asState()

    val dimAmount = when (selectedView) {
        WallpaperEditMode.MAIN -> wallpaperDimMainScreen
        WallpaperEditMode.DRAWER -> wallpaperDimDrawerScreen
    }

//    WallpaperBlur(blurRadius)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        WallpaperDim(dimAmount)
    }



    fun applyWallpaper(target: WallpaperTarget) {
        val bitmap = wallpaperHelper.createPlainWallpaperBitmap(ctx, plainColor)
        scope.launch {
            wallpaperHelper.setWallpaper(bitmap, target.flags)

            ctx.showToast("Wallpaper applied")
            showTargetDialog = false
        }
    }


    /**
     * Status bar things, copy paste from the getters, do no change that, it's just for displaying
     * the status bar if enabled to preview more easily
     */
    val systemInsets = WindowInsets.systemBars.asPaddingValues()

    val isRealFullscreen = systemInsets.calculateTopPadding() == 0.dp

    val showStatusBar by StatusBarSettingsStore.showStatusBar.flow(ctx)
        .collectAsState(initial = false)

    /** ───────────────────────────────────────────────────────────────── */


    Column {
        AnimatedVisibility(showStatusBar && isRealFullscreen) {
            StatusBar(null)
        }

        SettingsLazyHeader(
            title = stringResource(R.string.wallpaper),
            onBack = onBack,
            helpText = stringResource(R.string.wallpaper_help),
            onReset = null
        ) {

            item {
                DragonButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                        ctx.startActivity(
                            Intent.createChooser(
                                intent,
                                ctx.getString(R.string.select_image)
                            )
                        )
                    },
                    colors = AppObjectsColors.buttonColors()
                ) {

                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = stringResource(R.string.set_wallpaper)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.set_wallpaper),
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                DragonColumnGroup {
                    ColorPickerRow(
                        label = stringResource(R.string.plain_wallpaper_color),
                        currentColor = plainColor
                    ) {
                        plainColor = it ?: Color.Black
                    }


                    DragonButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            originalBitmap =
                                wallpaperHelper.createPlainWallpaperBitmap(ctx, plainColor)
                            showTargetDialog = true
                        },
                        colors = AppObjectsColors.buttonColors()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = stringResource(R.string.set_plain_wallpaper)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            stringResource(R.string.set_plain_wallpaper),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                DragonColumnGroup {
                    ActionRow(
                        actions = WallpaperEditMode.entries,
                        selectedView = selectedView,
                        actionName = { wallpaperEditModeLabel(ctx, it) },
                        actionIcon = { wallpaperEditModeIcon(it) },
                    ) { selectedView = it }



                    SliderWithLabel(
                        modifier = Modifier.padding(10.dp),
                        label = stringResource(R.string.wallpaper_dim_amount),
                        value = if (selectedView == WallpaperEditMode.MAIN) wallpaperDimMainScreen
                        else wallpaperDimDrawerScreen,
                        valueRange = 0f..1f,
                        color = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(
                            0.5f
                        ),
                        onReset = {
                            scope.launch {
                                if (selectedView == WallpaperEditMode.MAIN) {
                                    UiSettingsStore.wallpaperDimMainScreen.reset(ctx)
                                } else {
                                    UiSettingsStore.wallpaperDimDrawerScreen.reset(ctx)

                                }
                            }
                        },
                    ) {
                        scope.launch {
                            if (selectedView == WallpaperEditMode.MAIN) {
                                UiSettingsStore.wallpaperDimMainScreen.set(ctx, it)
                            } else {
                                UiSettingsStore.wallpaperDimDrawerScreen.set(ctx, it)
                            }
                        }
                    }
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
