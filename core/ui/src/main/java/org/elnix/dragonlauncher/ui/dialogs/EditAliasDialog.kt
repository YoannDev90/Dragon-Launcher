@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors


@Composable
fun EditAliasDialog(
    initialAlias: String?,
    onDismiss: () -> Unit,
    onValidate: (String) -> Unit
) {
    var editAlias by remember { mutableStateOf(initialAlias ?: "") }

    AlertDialog(
        title = {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Alias",
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = stringResource(R.string.edit_alias),
                    color = MaterialTheme.colorScheme.onSurface
                )


            }

        },
        onDismissRequest = onDismiss,
        text = {
            TextField(
                value = editAlias,
                onValueChange = { editAlias = it },
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = stringResource(R.string.reset),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable {
                            editAlias = initialAlias ?: ""
                        }
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.alias),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = AppObjectsColors.outlinedTextFieldColors(
                    backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f),
                    removeBorder = true
                ),
                modifier = Modifier
                    .clip(CircleShape),
                maxLines = 1
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onValidate(editAlias) }
            ) {
                Text(
                    text = stringResource(R.string.ok),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        dismissButton = {},
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}
