package org.elnix.dragonlauncher.ui.modifiers

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import org.elnix.dragonlauncher.common.utils.UiConstants


@Composable
fun Modifier.shapedClickable(
    enabled: Boolean = true,
    isSelected: Boolean = false,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
): Modifier {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shapeRound by animateDpAsState(
        targetValue = if (isPressed || isSelected)
            UiConstants.DRAGON_SHAPE_CORNER_DP
        else
            UiConstants.CIRCLE_SHAPE_CORNER_DP,
        label = "shape_anim"
    )


    val shape = RoundedCornerShape(shapeRound)


    return this
        .clip(shape)
        .combinedClickable(
            interactionSource = interactionSource,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = onClick,
            onLongClick = onLongClick
        )
}

@Composable
fun rememberPressedShape(
    selected: Boolean = false
): Pair<Shape, MutableInteractionSource> {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shapeRound by animateDpAsState(
        targetValue =
            if (isPressed || selected)
                UiConstants.DRAGON_SHAPE_CORNER_DP
            else
                UiConstants.CIRCLE_SHAPE_CORNER_DP,
        label = "shape_anim"
    )

    val shape = remember(shapeRound) {
        RoundedCornerShape(shapeRound)
    }

    return shape to interactionSource
}
