package org.elnix.dragonlauncher.ui.components.dragon

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.elnix.dragonlauncher.ui.UiConstants
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors


@Composable
fun DragonIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = AppObjectsColors.iconButtonColors(),
    content: @Composable () -> Unit,
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val transition = updateTransition(
        targetState = isPressed,
        label = "button_press_transition"
    )

    val shapeRound by transition.animateDp(
        label = "shape_round"
    ) { pressed ->
        if (pressed) UiConstants.DRAGON_SHAPE_CORNER_DP
        else UiConstants.CIRCLE_SHAPE_CORNER_DP
    }

    val shape = RoundedCornerShape(shapeRound)


    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        shape = shape,
        content = content
    )
}
