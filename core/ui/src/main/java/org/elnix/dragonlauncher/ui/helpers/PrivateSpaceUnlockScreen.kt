package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import org.elnix.dragonlauncher.enumsui.PrivateSpaceLoadingState
import org.elnix.dragonlauncher.models.AppsViewModel

@Composable
fun PrivateSpaceUnlockScreen(
    appsViewModel: AppsViewModel,
    onStart: (CoroutineScope) -> Unit
) {
    val scope = rememberCoroutineScope()

    val privateSpaceState by appsViewModel.privateSpaceState.collectAsState()

    LaunchedEffect(Unit) {
        onStart(scope)
    }

    if (
        privateSpaceState == PrivateSpaceLoadingState.Loading ||
        privateSpaceState == PrivateSpaceLoadingState.Authenticating
    ) {
        PrivateSpaceLoadingOverlay(privateSpaceState)
    }
}
