package org.elnix.dragonlauncher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.ui.actions.actionColor
import org.elnix.dragonlauncher.ui.actions.actionLabel
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors

@Composable
fun AppPreviewTitle(
    offsetY: Dp,
    alpha: Float,
    pointIcons: Map<String, ImageBitmap>,
    point: SwipePointSerializable,
    topPadding: Dp = 60.dp,
    labelSize: Int,
    iconSize: Int,
    showLabel: Boolean,
    showIcon: Boolean
) {

    val extraColors = LocalExtraColors.current

    val label = actionLabel(point.action, point.customName)

    val action = point.action
    if (showIcon || showLabel) {
        Box(
            Modifier
                .fillMaxWidth()
                .offset(y = offsetY)
                .padding(top = topPadding)
                .alpha(alpha),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                if (showIcon) {
                    val colorAction =
                        actionColor(action, extraColors, point.customActionColor?.let { Color(it) })
                    pointIcons[point.id]?.let {
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            colorFilter = if (
                                action !is SwipeActionSerializable.LaunchApp &&
                                action !is SwipeActionSerializable.LaunchShortcut &&
                                action !is SwipeActionSerializable.OpenDragonLauncherSettings
                            ) ColorFilter.tint(colorAction)
                            else null,
                            modifier = Modifier.size(iconSize.dp)
                        )
                    }
                }

                if (showLabel) {
                    Text(
                        text = label,
                        color = actionColor(action, extraColors, point.customActionColor?.let { Color(it) }),
                        fontSize = labelSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
