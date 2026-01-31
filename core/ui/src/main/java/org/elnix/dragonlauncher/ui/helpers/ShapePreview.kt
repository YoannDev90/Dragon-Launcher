package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.IconShape
import org.elnix.dragonlauncher.ui.components.resolveShape

@Composable
fun ShapePreview(
    iconShape: IconShape,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {

    val shape = resolveShape(iconShape)

    val bgColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .padding(5.dp)
            .size(60.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick != null) { onClick?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .clip(shape)
                .background(bgColor.copy(0.5f))
                .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
        )
    }
}
