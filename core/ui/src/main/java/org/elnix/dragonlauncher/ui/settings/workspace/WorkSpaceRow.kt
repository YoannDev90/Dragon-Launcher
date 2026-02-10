package org.elnix.dragonlauncher.ui.settings.workspace

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder
import org.elnix.dragonlauncher.common.serializables.Workspace
import org.elnix.dragonlauncher.common.serializables.WorkspaceType
import org.elnix.dragonlauncher.common.utils.PrivateSpaceUtils
import org.elnix.dragonlauncher.enumsui.WorkspaceAction
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton

@Composable
fun WorkspaceRow(
    workspace: Workspace,
    reorderState: ReorderableLazyListState,
    isDragging: Boolean = false,
    onClick: () -> Unit,
    onCheck: (Boolean) -> Unit,
    onAction: (WorkspaceAction) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val enabled = workspace.enabled
    val elevation = if (isDragging) 8.dp else 0.dp
    val scale = if (isDragging) 1.05f else 1f
    
    // Private Space state tracking (Android 15+)
    var isPrivateSpaceLocked by remember { mutableStateOf<Boolean?>(null) }
    val isPrivateWorkspace = workspace.type == WorkspaceType.PRIVATE
    
    // Check Private Space lock status periodically
    LaunchedEffect(isPrivateWorkspace) {
        if (isPrivateWorkspace && Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            withContext(Dispatchers.IO) {
                isPrivateSpaceLocked = PrivateSpaceUtils.isPrivateSpaceLocked(ctx)
            }
        }
    }

    Card(
        colors = AppObjectsColors.cardColors(),
        shape = DragonShape,
        elevation = CardDefaults.cardElevation(elevation),
        modifier = Modifier
            .scale(scale)
            .clickable { onClick() }
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = enabled,
                onCheckedChange = { onCheck(it) },
                colors = AppObjectsColors.checkboxColors(),
                enabled = !isPrivateWorkspace // Private Space cannot be manually disabled
            )

            Text(
                text = workspace.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            
            // Private Space lock/unlock indicator
            if (isPrivateWorkspace && Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                when (isPrivateSpaceLocked) {
                    true -> {
                        DragonIconButton(
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    val success = PrivateSpaceUtils.requestUnlockPrivateSpace(ctx)
                                    if (success) {
                                        // Wait a bit and refresh status
                                        kotlinx.coroutines.delay(500)
                                        isPrivateSpaceLocked = PrivateSpaceUtils.isPrivateSpaceLocked(ctx)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Unlock Private Space",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    false -> {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Private Space Unlocked",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    null -> {
                        // Loading or unavailable
                    }
                }
            }

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Don't allow renaming or deleting system workspaces
                if (workspace.type == WorkspaceType.CUSTOM) {
                    listOf(
                        WorkspaceAction.Rename,
                        WorkspaceAction.Delete
                    ).forEach { action ->
                        DragonIconButton(onClick = { onAction(action) }) {
                            Icon(action.icon, action.label)
                        }
                    }
                } else if (!isPrivateWorkspace) {
                    // Allow renaming built-in workspaces (except Private)
                    DragonIconButton(onClick = { onAction(WorkspaceAction.Rename) }) {
                        Icon(WorkspaceAction.Rename.icon, WorkspaceAction.Rename.label)
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.DragIndicator,
                contentDescription = "Drag",
                tint = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.detectReorder(reorderState)
            )
        }
    }
}
