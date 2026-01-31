package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape


@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = DragonShape,
    containerColor: Color= MaterialTheme.colorScheme.surface,
    imePadding: Boolean = true,
    scroll: Boolean = true,
    alignment: Alignment = Alignment.BottomCenter
) {

    val maxDialogHeight = LocalConfiguration.current.screenHeightDp.dp * 0.9f


    FullScreenOverlay(
        onDismissRequest = onDismissRequest,
        imePadding = imePadding,
        alignment = alignment
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = maxDialogHeight)
                .clip(shape)
                .background(containerColor)
                .padding(top = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                icon?.invoke()
                title?.invoke()
            }

            Box(
                Modifier
                    .padding(horizontal = 15.dp)
                    .weight(1f, fill = false)
                    .then(
                        if (scroll) {
                            Modifier.verticalScroll(rememberScrollState())
                        }
                        else Modifier
                    )
            ) {
                text?.invoke()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                dismissButton?.invoke()
                confirmButton()
            }
        }
    }
}
