package org.elnix.dragonlauncher.ui.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.utils.actions.actionIcon

@Composable
fun AppItem(
    app: AppModel,
    showIcons: Boolean,
    icons: Map<String, ImageBitmap>,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcons) {
            val icon = icons[app.packageName]

            if (icon != null) {
                Image(
                    bitmap = icon,
                    contentDescription = app.name,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_app_default),
                    contentDescription = app.name,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
        }

        Text(app.name, color = Color.White)
    }
}
