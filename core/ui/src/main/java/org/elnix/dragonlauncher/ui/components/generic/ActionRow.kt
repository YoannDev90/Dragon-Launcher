package org.elnix.dragonlauncher.ui.components.generic

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.ui.UiConstants

@Composable
fun <T> ActionRow(
    actions: List<T>,
    selectedView: T,
    enabled: Boolean = true,
    backgroundColorUnselected: Color? = null,
    actionName: @Composable ((T) -> String)? = null,
    actionIcon: @Composable ((T) -> ImageVector)? = null,
    onClick: (T) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        actions.forEach { mode ->
            val isSelected = mode == selectedView

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            val transition = updateTransition(
                targetState = isPressed,
                label = "button_press_transition"
            )

            val backgroundColor = (
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else backgroundColorUnselected ?: MaterialTheme.colorScheme.surface
                    ).copy(if (enabled) 1f else 0.5f)

            val textColor = (
                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                    ).copy(if (enabled) 1f else 0.5f)


            val shapeRound by transition.animateDp(
                label = "shape_round"
            ) { pressed ->
                if (pressed) UiConstants.DRAGON_SHAPE_CORNER_DP
                else UiConstants.CIRCLE_SHAPE_CORNER_DP
            }

            val shape = RoundedCornerShape(shapeRound)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(shape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled
                    ) { onClick(mode) }
                    .background(backgroundColor)
                    .padding(5.dp)
            ) {
                actionIcon?.let {
                    Icon(
                        imageVector = it(mode),
                        contentDescription = null,
                        tint = textColor
                    )
                    Spacer(Modifier.width(5.dp))
                }

                actionName?.let {
                    Text(
                        text = it(mode),
                        modifier = Modifier
                            .padding(12.dp),
                        color = textColor,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
