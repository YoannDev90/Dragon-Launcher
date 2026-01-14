package org.elnix.dragonlauncher
import android.content.Context
import androidx.compose.material3.MaterialTheme
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
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.ui.DragonLauncherTheme

object DragonLauncherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val background by ColorSettingsStore.getBackground(context).collectAsState(initial = null)

            DragonLauncherTheme(
                customBackground = background,
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
