package org.elnix.dragonlauncher
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import org.elnix.dragonlauncher.ui.theme.DragonLauncherTheme

object DragonLauncherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            DragonLauncherTheme {
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
