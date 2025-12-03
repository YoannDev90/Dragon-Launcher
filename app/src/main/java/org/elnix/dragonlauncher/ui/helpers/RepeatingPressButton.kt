package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

@Composable
fun RepeatingPressButton(
    enabled: Boolean = true,
    intervalMs: Long = 70L,
    startDelayMs: Long = 300L,
    onPress: () -> Unit,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(isPressed, enabled) {
        if (enabled && isPressed) {
            // Immediate single press action
            onPress()

            // Wait before repeat begins
            delay(startDelayMs)

            // If still pressed after delay, begin repeating
            while (isPressed) {
                onPress()
                delay(intervalMs)
            }
        }
    }

    Box(
        modifier = Modifier.pointerInput(enabled) {
            if (!enabled) return@pointerInput

            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
    ) {
        content()
    }
}
