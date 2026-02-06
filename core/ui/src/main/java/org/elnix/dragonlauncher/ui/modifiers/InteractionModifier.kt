package org.elnix.dragonlauncher.ui.modifiers

import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import org.elnix.dragonlauncher.ui.UiConstants


@Composable
fun Modifier.shapedClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val transition = updateTransition(
        targetState = isPressed,
        label = "press_transition"
    )

    val shapeRound by transition.animateInt(
        label = "shape_round"
    ) { pressed ->
        if (pressed) UiConstants.DRAGON_SHAPE_CORNER
        else UiConstants.DRAGON_SHAPE_CORNER_MORE_ROUNDED
    }

    val shape = RoundedCornerShape(shapeRound)


    return this
        .clip(shape)
        .clickable(
        interactionSource = interactionSource,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick
    )
}
