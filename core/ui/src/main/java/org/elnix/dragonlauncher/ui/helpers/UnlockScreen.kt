package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.elnix.dragonlauncher.common.utils.PrivateSpaceUtils

@Composable
fun UnlockScreen(
    onUnlockFinished: (Boolean) -> Unit
) {
    val ctx = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {

        if (!PrivateSpaceUtils.isPrivateSpaceSupported()) {
            onUnlockFinished(false)
            return@LaunchedEffect
        }

        val locked = withContext(Dispatchers.IO) {
            PrivateSpaceUtils.isPrivateSpaceLocked(ctx)
        }

        if (locked != true) {
            onUnlockFinished(true)
            return@LaunchedEffect
        }

        withContext(Dispatchers.IO) {
            PrivateSpaceUtils.requestUnlockPrivateSpace(ctx)
        }

        // Poll for unlock
        repeat(20) { // ~4 seconds
            delay(200)

            val stillLocked = withContext(Dispatchers.IO) {
                PrivateSpaceUtils.isPrivateSpaceLocked(ctx)
            }

            if (stillLocked == false) {
                onUnlockFinished(true)
                return@LaunchedEffect
            }
        }

        onUnlockFinished(false)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}
