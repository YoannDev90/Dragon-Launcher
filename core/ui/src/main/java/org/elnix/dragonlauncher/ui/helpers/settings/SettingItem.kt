package org.elnix.dragonlauncher.ui.helpers.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.components.dragon.DragonSurfaceRow
import org.elnix.dragonlauncher.ui.helpers.IconC

@Composable
fun SettingsItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    comingSoon: Boolean = false,
    icon: Any? = null,
    leadIcon: Any? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {

    DragonSurfaceRow (
        enabled = enabled,
        backgroundColor = backgroundColor,
        onLongClick = onLongClick,
        onClick = onClick
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.weight(1f)
        ) {

            if (icon != null) {
                IconC(
                    icon = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.adjustBrightness(if (enabled) 1f else 0.5f)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.adjustBrightness(if (enabled) 1f else 0.5f)
                )

                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.adjustBrightness(if (enabled) 0.8f else 0.4f),
                        modifier = Modifier.sizeIn(maxHeight = 30.dp)
                    )
                }
            }
            if (leadIcon != null) {
                IconC(
                    icon = leadIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.adjustBrightness(if (enabled) 1f else 0.5f),
                    modifier = Modifier.sizeIn(maxHeight = 25.dp)
                )
            }
        }

        if (comingSoon) {
            Text(
                text = stringResource(R.string.coming_soon),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.adjustBrightness(0.5f)
            )
        }
    }
}



@Composable
fun SettingItemWithExternalOpen(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    comingSoon: Boolean = false,
    icon: Any? = null,
    leadIcon: Any? = null,
    extIcon: Any = Icons.AutoMirrored.Filled.OpenInNew,
    onLongClick: (() -> Unit)? = null,
    onExtClick: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        SettingsItem(
            title = title,
            modifier = modifier.weight(1f),
            description = description,
            enabled = enabled,
            comingSoon = comingSoon,
            icon = icon,
            leadIcon = leadIcon,
            onLongClick = onLongClick,
            onClick = onClick
        )

        DragonIconButton(
            onClick = onExtClick,
            colors = AppObjectsColors.iconButtonColors(),
            modifier = Modifier.size(52.dp)
        ) {
            IconC(
                icon = extIcon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.sizeIn(maxHeight = 30.dp)
            )
        }
    }
}
