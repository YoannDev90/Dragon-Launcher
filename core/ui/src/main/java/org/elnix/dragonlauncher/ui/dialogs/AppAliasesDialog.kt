@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.AppModel
import org.elnix.dragonlauncher.common.serializables.WorkspaceState
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.ui.helpers.Bubble

@Composable
fun AppAliasesDialog(
    appsViewModel: AppsViewModel,
    app: AppModel,
    onDismiss: () -> Unit
) {
    var showAliasEditScreen by remember { mutableStateOf<String?>(null) }

    val aliases by appsViewModel.enabledState
        .collectAsState(WorkspaceState())
        .value
        .appAliases

    AlertDialog(
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = app.name,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Icon(
                        imageVector = Icons.Default.AlternateEmail,
                        contentDescription = "Details",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = stringResource(R.string.app_aliases),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        },
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FlowRow(
                    modifier = Modifier
                        .heightIn(700.dp)
                ) {
                    aliases.forEach { alias ->

                        Bubble(
                            onClick = { showAliasEditScreen = alias },
                            onDelete = {
                                appsViewModel.removeAliasFromWorkspace(alias, app.packageName)
                            }
                        ) {
                            Text(
                                text = alias,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clip(RoundedCornerShape(12.dp))
//                                .background(alias.backgroundColor)
//                                .clickable { alias.onClick() }
//                                .padding(16.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                imageVector = alias.icon,
//                                contentDescription = alias.label,
//                                modifier = Modifier.size(24.dp),
//                                tint = alias.iconTint
//                            )
//                            Spacer(modifier = Modifier.width(12.dp))
//
//                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                stringResource(R.string.ok)
            }
        },
        dismissButton = {},
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )

    if (showAliasEditScreen != null) {

        val aliasToEdit = showAliasEditScreen

        EditAliasDialog(
            initialAlias = showAliasEditScreen,
            onDismiss = { showAliasEditScreen = null }
        ) {
            appsViewModel.removeAliasFromWorkspace(aliasToEdit ?: "", app.packageName)
            appsViewModel.addAliasToApp(it, app.packageName)
            showAliasEditScreen = null
        }
    }

}
