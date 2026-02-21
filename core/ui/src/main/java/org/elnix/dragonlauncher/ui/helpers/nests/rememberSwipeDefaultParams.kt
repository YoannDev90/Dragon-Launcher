package org.elnix.dragonlauncher.ui.helpers.nests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.common.points.SwipeDrawParams
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.settings.stores.SwipeMapSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.remembers.LocalIconShape
import org.elnix.dragonlauncher.ui.remembers.LocalIcons
import org.elnix.dragonlauncher.ui.remembers.LocalNests
import org.elnix.dragonlauncher.ui.remembers.LocalPoints


@Composable
fun rememberSwipeDefaultParams(
    center: Offset,
    backgroundColor: Color? = null,
): SwipeDrawParams {
    val ctx = LocalContext.current
    val points = LocalPoints.current
    val nests = LocalNests.current
    val icons= LocalIcons.current
    val iconShape = LocalIconShape.current
    val extraColors = LocalExtraColors.current
    val density = LocalDensity.current

    val surfaceColorDraw = backgroundColor ?: Color.Unspecified

    val defaultPoint by SwipeSettingsStore.getDefaultPointFlow(ctx).collectAsState(
        defaultSwipePointsValues
    )
    val showCircle by UiSettingsStore.showCirclePreview.asState()
    val maxNestsDepth by UiSettingsStore.maxNestsDepth.asState()

    val subNestDefaultRadius by SwipeMapSettingsStore.subNestDefaultRadius.asState()

    return SwipeDrawParams(
        nests = nests,
        points = points,
        center = center,
        ctx = ctx,
        defaultPoint = defaultPoint,
        icons = icons,
        surfaceColorDraw = surfaceColorDraw,
        extraColors = extraColors,
        showCircle = showCircle,
        density = density,
        depth = 1,
        maxDepth = maxNestsDepth,
        iconShape = iconShape,
        subNestDefaultRadius = subNestDefaultRadius,
    )
}
