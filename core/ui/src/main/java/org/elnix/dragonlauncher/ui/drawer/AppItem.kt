package org.elnix.dragonlauncher.ui.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.actions.appIcon
import org.elnix.dragonlauncher.ui.components.resolveShape

@Composable
fun AppItemHorizontal(
    app: AppModel,
    shape: Shape,
    showIcons: Boolean,
    showLabels: Boolean,
    txtColor: Color,
    icons: Map<String, ImageBitmap>,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .height(height.dp)
            .clip(DragonShape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcons) {
            Image(
                painter = appIcon(app, icons),
                contentDescription = app.name,
                modifier = Modifier
                    .size(32.dp)
                    .clip(shape),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.width(12.dp))
        }

        if (showLabels) {
            Text(
                text = app.name,
                color = txtColor
            )
        }
    }
}

@Composable
fun AppItemGrid(
    app: AppModel,
    icons: Map<String, ImageBitmap>,
    showIcons: Boolean,
    maxIconSize: Int,
    iconShape: IconShape,
    showLabels: Boolean,
    txtColor: Color,
    onLongClick: ((AppModel) -> Unit)?,
    onClick: (AppModel) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(DragonShape)
            .combinedClickable(
                onLongClick = if (onLongClick != null) {
                    { onLongClick(app) }
                } else null
            ) { onClick(app) }
            .padding(5.dp)
    ) {
        if (showIcons) {
            Image(
                painter = appIcon(app, icons),
                contentDescription = app.name,
                modifier = Modifier
                    .sizeIn(maxWidth = maxIconSize.dp)
                    .aspectRatio(1f)
                    .clip(resolveShape(iconShape)),
                contentScale = ContentScale.Fit
            )
        }

        if (showLabels) {
            Spacer(Modifier.height(6.dp))

            Text(
                text = app.name,
                color = txtColor,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
