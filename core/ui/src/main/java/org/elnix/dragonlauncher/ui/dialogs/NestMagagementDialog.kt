package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.theme.AmoledDefault
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.helpers.CircleIconButton
import org.elnix.dragonlauncher.ui.helpers.nests.actionsInCircle
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors

@Composable
fun NestManagementDialog(
    appsViewModel: AppsViewModel,
    title: String? = null,
    onDismissRequest: () -> Unit,
    onNewNest: (() -> Unit)? = null,
    onNameChange: ((id: Int, name: String) -> Unit)?,
//    onPaste: ((id: Int) -> Unit),
    onDelete: ((id: Int) -> Unit)?,
    onSelect: ((CircleNest) -> Unit)? = null
) {
    val ctx = LocalContext.current

    val points by SwipeSettingsStore.getPointsFlow(ctx).collectAsState(emptyList())
    val nests by SwipeSettingsStore.getNestsFlow(ctx).collectAsState(emptyList())
    val circleColor by ColorSettingsStore.getCircleColor(ctx)
        .collectAsState(initial = AmoledDefault.CircleColor)

    val pointIcons by appsViewModel.pointIcons.collectAsState()


    CustomAlertDialog(
        modifier = Modifier.padding(15.dp),
        onDismissRequest = onDismissRequest,
        alignment = Alignment.Center,
        scroll = false,
        confirmButton = {},
        title = {
            Text(
                text = title ?: stringResource(R.string.manage_nests),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.heightIn(max = 700.dp)
            ) {
                if (onNewNest != null) {
                    item {
                        TextButton(
                            onClick = { onNewNest() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.new_nest),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                items(nests) { nest ->
                    NestManagementItem(
                        nest = nest,
                        nests = nests,
                        points = points,
                        circleColor = circleColor,
                        pointIcons = pointIcons,
                        onNameChange = onNameChange,
//                        onPaste = onPaste,
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
    nests: List<CircleNest>,
    points: List<SwipePointSerializable>,
    circleColor: Color,
    pointIcons: Map<String, ImageBitmap>,
    onNameChange: ((id: Int, name: String) -> Unit)?,
//    onPaste: ((id: Int) -> Unit),
    onDelete: ((id: Int) -> Unit)?,
    onSelect: (() -> Unit)? = null
) {
    val ctx = LocalContext.current
    val extraColors = LocalExtraColors.current

    var tempCustomName by remember { mutableStateOf(nest.name ?: "") }

    val surfaceColorDraw = MaterialTheme.colorScheme.surface.adjustBrightness(0.5f)

    val editPoint = SwipePointSerializable(
        circleNumber = 0,
        angleDeg = 0.0,
        SwipeActionSerializable.OpenCircleNest(nest.id),
        id = ""
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceColorDraw)
            .clickable { onSelect?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Canvas(
            modifier = Modifier
                .size(100.dp)
        ) {
            val center = size.center

            actionsInCircle(
                selected = false,
                point = editPoint,
                nests = nests,
                points = points,
                center = center,
                ctx = ctx,
                circleColor = circleColor,
                surfaceColorDraw = surfaceColorDraw,
                extraColors = extraColors,
                pointIcons = pointIcons,
                preventBgErasing = true,
                deepNest = 1
            )

        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        ) {

            Text(
                text = "ID: ${nest.id}",
                color = MaterialTheme.colorScheme.onPrimary.copy(0.9f)
            )

            if (onNameChange != null) {
                TextField(
                    value = tempCustomName,
                    onValueChange = {
                        tempCustomName = it
                        onNameChange(nest.id, it)
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.custom_name),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = AppObjectsColors.outlinedTextFieldColors(
                        backgroundColor = surfaceColorDraw,
                        onBackgroundColor = MaterialTheme.colorScheme.onSurface,
                        removeBorder = true
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }

        CircleIconButton(
            icon = Icons.Default.ContentCopy,
            contentDescription = stringResource(R.string.copy_point),
        ) {
            ctx.copyToClipboard(nest.id.toString())
        }

//        Bubble(
//            onClick = onSelect,
//            backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.5f),
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Default.ContentCopy,
//                    contentDescription = stringResource(R.string.copy_point),
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier
//                        .clickable { ctx.copyToClipboard(nest.id.toString()) }
//                )
//            },
//            trailingIcon = {
//                Icon(
//                    imageVector = Icons.Default.ContentPaste,
//                    contentDescription = stringResource(R.string.change),
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier
//                        .clickable { onPaste(nest.id) }
//                )
//            }
//
//        ) {}

        if (onDelete != null) {
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
}
