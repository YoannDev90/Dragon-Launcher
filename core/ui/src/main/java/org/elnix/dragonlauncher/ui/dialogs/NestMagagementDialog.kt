package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.elnix.dragonlauncher.ui.helpers.Bubble
import org.elnix.dragonlauncher.ui.helpers.nests.actionsInCircle
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors

@Composable
fun NestManagementDialog(
    appsViewModel: AppsViewModel,
    onDismissRequest: () -> Unit,
    onPaste: ((id: Int) -> Unit)? = null,
    onDelete: ((id: Int) -> Unit ),
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
                        nests = nests,
                        points = points,
                        circleColor = circleColor,
                        pointIcons = pointIcons,
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
    nests: List<CircleNest>,
    points: List<SwipePointSerializable>,
    circleColor: Color,
    pointIcons: Map<String, ImageBitmap>,
    onPaste: ((id: Int) -> Unit)? = null,
    onDelete: ((id: Int) -> Unit ),
    onSelect: (() -> Unit)? = null
) {
    val ctx = LocalContext.current
    val extraColors = LocalExtraColors.current

    val surfaceColorDraw = MaterialTheme.colorScheme.surface

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
            .background(MaterialTheme.colorScheme.surface.adjustBrightness(0.5f))
            .clickable { onSelect?.invoke() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Canvas(
            modifier = Modifier
                .size(100.dp)
        ) {
            val center = size.center
            val actionSpacing = 240f

            actionsInCircle(
                selected = false,
                point = editPoint,
                nests = nests,
                points = points,
                center = center.copy(x = center.x - actionSpacing),
                ctx = ctx,
                circleColor = circleColor,
                surfaceColorDraw = surfaceColorDraw,
                extraColors = extraColors,
                pointIcons = pointIcons,
                preventBgErasing = true,
                deepNest = 1
            )

        }
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .padding(15.dp)
//        ) {
//            Text(
//                text = nest.name ?: nest.id.toString(),
//                color = MaterialTheme.colorScheme.onPrimary
//            )
//
//            if (nest.name != null) {
//                Text(
//                    text = "ID: ${nest.id}",
//                    color = MaterialTheme.colorScheme.onPrimary.copy(0.9f),
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//            }
//        }

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
