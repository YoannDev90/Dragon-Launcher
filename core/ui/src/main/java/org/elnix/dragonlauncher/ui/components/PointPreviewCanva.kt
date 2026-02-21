package org.elnix.dragonlauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.base.theme.ExtraColors
import org.elnix.dragonlauncher.common.logging.logW
import org.elnix.dragonlauncher.common.points.SwipeDrawParams
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.Constants.Logging.ICONS_TAG
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.helpers.nests.actionsInCircle

@Composable
fun PointPreviewCanvas(
    editPoint: SwipePointSerializable,
    nests: List<CircleNest>,
    points: List<SwipePointSerializable>,
    defaultPoint: SwipePointSerializable,
    backgroundSurfaceColor: Color,
    extraColors: ExtraColors,
    pointIcons: Map<String, ImageBitmap>,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val density = LocalDensity.current

    val iconsShape by DrawerSettingsStore.iconsShape.asState()
    val maxNestsDepth by UiSettingsStore.maxNestsDepth.asState()

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
            drawParams = SwipeDrawParams(
                nests = nests,
                points = points,
                center = Offset(leftX, centerY),
                ctx = ctx,
                defaultPoint = defaultPoint,
                icons = pointIcons,
                surfaceColorDraw = Color.Unspecified,
                extraColors = extraColors,
                showCircle = true,
                density = density,
                depth = 1,
                maxDepth = maxNestsDepth,
                iconShape = iconsShape
            )
        )

        // Right action
        actionsInCircle(
            selected = true,
            point = editPoint,
            drawParams = SwipeDrawParams(
                nests = nests,
                points = points,
                center = Offset(rightX, centerY),
                ctx = ctx,
                defaultPoint = defaultPoint,
                icons = pointIcons,
                surfaceColorDraw = Color.Unspecified,
                extraColors = extraColors,
                showCircle = true,
                density = density,
                depth = 1,
                maxDepth = maxNestsDepth,
                iconShape = iconsShape
            )
        )
    }
}
