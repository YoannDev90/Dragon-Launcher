package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
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
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.ui.helpers.Bubble

@Composable
fun NestManagementDialog(
    nests: List<CircleNest>,
    onDismissRequest: () -> Unit,
    onChange: ((id: String, newId: String) -> Unit)? = null,
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
                modifier = Modifier.height(700.dp),

            ) {
                items(nests) { nest ->
                    NestManagementItem(
                        nest = nest,
                        onChange = onChange,
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
    onChange: ((id: String, newId: String) -> Unit)? = null,
    onSelect: (() -> Unit)? = null
) {
    val ctx = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
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
            backgroundColor = MaterialTheme.colorScheme.primary,
            borderColor = MaterialTheme.colorScheme.secondary,
            leadingIcon = {
                IconButton(
                    onClick = { ctx.copyToClipboard(nest.id.toString()) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = stringResource(R.string.copy_point),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            trailingIcon = onChange?.let {
                {
                    IconButton(
                        onClick = {
                            onChange.invoke(nest.id.toString(), nest.id.toString())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.change),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        ) {
            Text(
                text = nest.id.toString(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
