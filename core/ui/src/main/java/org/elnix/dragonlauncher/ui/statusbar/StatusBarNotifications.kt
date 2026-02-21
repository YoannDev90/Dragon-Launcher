package org.elnix.dragonlauncher.ui.statusbar

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.elnix.dragonlauncher.services.DragonNotificationListenerService

@Composable
fun StatusBarNotifications(
    textColor: Color,
    maxIcons: Int
) {
    val ctx = LocalContext.current
    val packageNames by DragonNotificationListenerService.notifications.collectAsState()
    var hasPermission by remember { mutableStateOf(DragonNotificationListenerService.isPermissionGranted(ctx)) }

    LaunchedEffect(Unit) {
        while (isActive) {
            hasPermission = DragonNotificationListenerService.isPermissionGranted(ctx)
            delay(5_000L)
        }
    }

    if (!hasPermission) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = textColor.copy(alpha = 0.4f),
            modifier = Modifier
                .size(14.dp)
                .clickable { DragonNotificationListenerService.openNotificationSettings(ctx) }
        )
        return
    }

    if (packageNames.isEmpty()) return

    // TODO replace with later LocalIcons search, in the upcoming refactor RN it doesn't display any icon, just the fallback
    val icons = remember(packageNames, maxIcons) {
        packageNames.take(maxIcons).map { pkg ->
            pkg to try {
                val drawable = ctx.packageManager.getApplicationIcon(pkg)
                (drawable as? BitmapDrawable)?.bitmap?.asImageBitmap()
            } catch (_: Exception) {
                null
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icons.forEach { (pkg, bitmap) ->
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = pkg,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = pkg,
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
