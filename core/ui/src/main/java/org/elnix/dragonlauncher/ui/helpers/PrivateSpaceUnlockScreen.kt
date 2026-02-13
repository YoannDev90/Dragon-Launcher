package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import org.elnix.dragonlauncher.models.AppsViewModel

@Composable
fun PrivateSpaceUnlockScreen(
    appsViewModel: AppsViewModel,
    onStart: (CoroutineScope) -> Unit
) {
    val scope = rememberCoroutineScope()

    val isLoadingPrivateSpace by appsViewModel.isLoadingPrivateSpace.collectAsState()

    LaunchedEffect(Unit) {
        onStart(scope)
    }

    PrivateSpaceLoadingOverlay(isLoadingPrivateSpace)
}
