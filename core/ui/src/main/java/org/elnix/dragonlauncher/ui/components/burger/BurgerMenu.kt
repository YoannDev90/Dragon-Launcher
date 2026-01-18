package org.elnix.dragonlauncher.ui.components.burger

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun BurgerMenu(
    onDismiss: () -> Unit,
    alignment: Alignment,
    content: @Composable () -> Unit
) {
    Box(Modifier.fillMaxSize()) {

        Box(
            Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectTapGestures { onDismiss() }
                }
        )

        Box(
            modifier = Modifier
                .align(alignment)
        ) {
            content()
        }
    }
}
