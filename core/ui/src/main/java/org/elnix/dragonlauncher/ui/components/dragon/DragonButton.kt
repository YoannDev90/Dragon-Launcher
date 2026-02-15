package org.elnix.dragonlauncher.ui.components.dragon

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.ui.UiConstants
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.dialogs.UserValidation


@Suppress("AssignedValueIsNeverRead")
@Composable
fun DragonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    needValidation: Boolean = false,
    confirmText: String = stringResource(R.string.are_you_sure),
    colors: ButtonColors = AppObjectsColors.buttonColors(),
    content: @Composable RowScope.() -> Unit,
) {
    var showConfirmPopup by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()


    val shapeRound by animateDpAsState(
        targetValue = if (isPressed)
            UiConstants.DRAGON_SHAPE_CORNER_DP
        else
            UiConstants.CIRCLE_SHAPE_CORNER_DP,
        label = "shape_anim"
    )

    val shape = RoundedCornerShape(shapeRound)

    Button(
        modifier = modifier,
        onClick = {
            if (needValidation) showConfirmPopup = true
            else onClick()
        },
        shape = shape,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = content
    )


    if (showConfirmPopup) {
        UserValidation(
            message = confirmText,
            onDismiss = { showConfirmPopup = false }
        ) {
            onClick()
            showConfirmPopup = false
        }
    }
}
