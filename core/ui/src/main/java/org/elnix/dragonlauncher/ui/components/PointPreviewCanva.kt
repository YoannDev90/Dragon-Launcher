package org.elnix.dragonlauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.logging.logW
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.ICONS_TAG
import org.elnix.dragonlauncher.ui.helpers.nests.actionsInCircle
import org.elnix.dragonlauncher.ui.theme.ExtraColors

@Composable
fun PointPreviewCanvas(
    editPoint: SwipePointSerializable,
    nests: List<CircleNest>,
    points: List<SwipePointSerializable>,
    defaultPoint: SwipePointSerializable,
    circleColor: Color,
    backgroundSurfaceColor: Color,
    extraColors: ExtraColors,
    pointIcons: Map<String, ImageBitmap>,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current

    ctx.logW(ICONS_TAG, "PointPreview: editPoint: $editPoint; pointIcons: $pointIcons")
    ctx.logW(ICONS_TAG, "${pointIcons[editPoint.id]}")


    Canvas(
        modifier = modifier
            .height(40.dp)
    ) {
        val centerY = size.height / 2f
        val leftX = size.width * 0.25f
        val rightX = size.width * 0.75f

        // Left action
        actionsInCircle(
            selected = false,
            point = editPoint,
            nests = nests,
            points = points,
            center = Offset(leftX, centerY),
            ctx = ctx,
            circleColor = circleColor,
            showCircle = true,
            surfaceColorDraw = backgroundSurfaceColor,
            extraColors = extraColors,
            pointIcons = pointIcons,
            defaultPoint = defaultPoint,
            deepNest = 1,
            preventBgErasing = true
        )

        // Right action
        actionsInCircle(
            selected = true,
            point = editPoint,
            nests = nests,
            points = points,
            center = Offset(rightX, centerY),
            ctx = ctx,
            circleColor = circleColor,
            showCircle = true,
            surfaceColorDraw = backgroundSurfaceColor,
            extraColors = extraColors,
            pointIcons = pointIcons,
            defaultPoint = defaultPoint,
            deepNest = 1,
            preventBgErasing = true
        )
    }
}
