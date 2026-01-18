package org.elnix.dragonlauncher.ui.components

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.ui.helpers.nests.actionsInCircle
import org.elnix.dragonlauncher.ui.theme.ExtraColors

@Composable
fun PointPreviewCanvas(
    editPoint: SwipePointSerializable,
    nests: List<CircleNest>,
    points: List<SwipePointSerializable>,
    defaultPoint: SwipePointSerializable,
    ctx: Context,
    circleColor: Color,
    backgroundSurfaceColor: Color,
    extraColors: ExtraColors,
    pointIcons: Map<String, ImageBitmap>,
    modifier: Modifier = Modifier
) {
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
            defaultPoint = defaultPoint,
            center = Offset(leftX, centerY),
            ctx = ctx,
            circleColor = circleColor,
            surfaceColorDraw = backgroundSurfaceColor,
            extraColors = extraColors,
            pointIcons = pointIcons,
            preventBgErasing = true,
            deepNest = 1
        )

        // Right action
        actionsInCircle(
            selected = true,
            point = editPoint,
            points = points,
            defaultPoint = defaultPoint,
            nests = nests,
            center = Offset(rightX, centerY),
            ctx = ctx,
            circleColor = circleColor,
            surfaceColorDraw = backgroundSurfaceColor,
            extraColors = extraColors,
            pointIcons = pointIcons,
            preventBgErasing = true,
            deepNest = 1
        )
    }
}
