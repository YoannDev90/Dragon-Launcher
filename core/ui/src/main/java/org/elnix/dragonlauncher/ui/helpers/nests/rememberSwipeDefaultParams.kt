package org.elnix.dragonlauncher.ui.helpers.nests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import org.elnix.dragonlauncher.common.points.SwipeDrawParams
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore.backgroundColor
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore.iconsShape
import org.elnix.dragonlauncher.settings.stores.SwipeMapSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore.maxNestsDepth
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.theme.LocalNests
import org.elnix.dragonlauncher.ui.theme.LocalPoints


@Composable
fun rememberSwipeDefaultParams(
    nests: List<CircleNest>,
    center: Offset,
): SwipeDrawParams {
    val ctx = LocalContext.current
    val points = LocalPoints.current
    val nests = LocalNests.current


    val defaultPoint by SwipeSettingsStore.getDefaultPointFlow(ctx).collectAsState(
        defaultSwipePointsValues
    )

    val subNestDefaultRadius by SwipeMapSettingsStore.subNestDefaultRadius.asState()

    return SwipeDrawParams(
        nests = nests,
        points = points,
        center = center,
        ctx = ctx,
        defaultPoint = defaultPoint,
        icons = pointIcons,
        surfaceColorDraw = backgroundColor,
        extraColors = extraColors,
        showCircle = true,
        density = density,
        depth = 1,
        maxDepth = maxNestsDepth,
        iconShape = iconsShape,
        subNestDefaultRadius = subNestDefaultRadius,
    )
}
}
