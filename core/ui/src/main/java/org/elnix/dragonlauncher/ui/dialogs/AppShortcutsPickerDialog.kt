package org.elnix.dragonlauncher.ui.dialogs

import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.utils.ImageUtils.loadDrawableAsBitmap
import org.elnix.dragonlauncher.ui.actions.appIcon

@Composable
fun AppShortcutPickerDialog(
    app: AppModel,
    icons: Map<String, ImageBitmap>,
    shortcuts: List<ShortcutInfo>,
    onDismiss: () -> Unit,
    onShortcutSelected: (packageName: String, shortcutId: String) -> Unit,
    onOpenApp: () -> Unit
) {
    val ctx = LocalContext.current

    val appName = app.name

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select action for $appName") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (shortcuts.isEmpty()) {
                    Text("No extra actions available.")
                } else {
                    shortcuts.forEach { shortcut ->
                        val drawable = remember(shortcut.id) {
                            val launcherApps =
                                ctx.getSystemService(LauncherApps::class.java)
                            launcherApps?.getShortcutIconDrawable(shortcut, 0)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onShortcutSelected(shortcut.`package`, shortcut.id)
                                }
                                .padding(8.dp)
                        ) {
                            if (drawable != null) {
                                Image(
                                    painter = remember(drawable) {
                                        val bmp = loadDrawableAsBitmap(drawable, 48, 48)
                                        BitmapPainter(bmp)
                                    },
                                    contentDescription = shortcut.shortLabel?.toString(),
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                text = shortcut.shortLabel?.toString() ?: "Unnamed",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenApp() }
                        .padding(8.dp)
                ) {

                    Image(
                        painter = appIcon(app.packageName, icons),
                        contentDescription = "App icon",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.width(8.dp))


                    Text(
                        text = "Just open $appName",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {},
        containerColor = MaterialTheme.colorScheme.surface
    )
}
