package org.elnix.dragonlauncher
import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import org.elnix.dragonlauncher.base.theme.AmoledDragonColorScheme
import org.elnix.dragonlauncher.common.utils.definedOrNull
import org.elnix.dragonlauncher.settings.stores.ColorModesSettingsStore
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.ui.theme.DragonLauncherTheme
import org.elnix.dragonlauncher.ui.theme.getDefaultColorScheme

object DragonLauncherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {


            val dynamicColor by ColorModesSettingsStore.dynamicColor.flow(context)
                .collectAsState(ColorModesSettingsStore.dynamicColor.default)

            val defaultTheme by ColorModesSettingsStore.defaultTheme.flow(context)
                .collectAsState(ColorModesSettingsStore.defaultTheme.default)


            val background by ColorSettingsStore.backgroundColor.flow(context).collectAsState(initial = null)

            val defaultColorScheme: ColorScheme = getDefaultColorScheme(defaultTheme, dynamicColor) ?: AmoledDragonColorScheme

            val customColorScheme = darkColorScheme(
                background = background.definedOrNull() ?: defaultColorScheme.background
            )


            DragonLauncherTheme(
                defaultTheme = defaultTheme,
                dynamicColor = dynamicColor,
                customColorScheme = customColorScheme
            ) {
                DragonWidgetPreview()
            }
        }
    }
}

@Composable
fun DragonWidgetPreview() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(actionStartActivity<MainActivity>())
    ) {}
}
