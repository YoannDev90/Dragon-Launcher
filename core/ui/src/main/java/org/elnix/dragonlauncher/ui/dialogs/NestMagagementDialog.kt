package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.ui.helpers.Bubble

@Composable
fun NestManagementDialog(
    nests: List<CircleNest>,
    onDismissRequest: () -> Unit,
    onPaste: ((id: Int) -> Unit)? = null,
    onDelete: ((id: Int) -> Unit ),
    onSelect: ((CircleNest) -> Unit)? = null
) {

    CustomAlertDialog(
        modifier = Modifier.padding(15.dp),
        onDismissRequest = onDismissRequest,
        alignment = Alignment.Center,
        scroll = false,
        confirmButton = {},
        title = {
            Text(
                text = stringResource(R.string.manage_nests),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.heightIn(max = 700.dp)
            ) {
                items(nests) { nest ->
                    NestManagementItem(
                        nest = nest,
                        onPaste = onPaste,
                        onDelete = onDelete,
                        onSelect = { onSelect?.invoke(nest) }
                    )
                }
            }
        }
    )
}



@Composable
fun NestManagementItem(
    nest: CircleNest,
    onPaste: ((id: Int) -> Unit)? = null,
    onDelete: ((id: Int) -> Unit ),
    onSelect: (() -> Unit)? = null
) {
    val ctx = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.5f))
            .clickable { onSelect?.invoke() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(15.dp)
        ) {
            Text(
                text = nest.name ?: nest.id.toString(),
                color = MaterialTheme.colorScheme.onPrimary
            )

            if (nest.name != null) {
                Text(
                    text = "ID: ${nest.id}",
                    color = MaterialTheme.colorScheme.onPrimary.copy(0.9f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Bubble(
            onClick = onSelect,
            backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.5f),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = stringResource(R.string.copy_point),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { ctx.copyToClipboard(nest.id.toString()) }
                )
            },
            trailingIcon = onPaste?.let {
                {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = stringResource(R.string.change),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { onPaste.invoke(nest.id) }
                    )
                }
            }
        ) {
            Text(
                text = nest.id.toString(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = { onDelete(nest.id) }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.delete_circle_nest),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
