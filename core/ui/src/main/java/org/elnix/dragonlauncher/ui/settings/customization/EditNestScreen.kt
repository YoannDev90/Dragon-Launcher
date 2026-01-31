@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.common.utils.vibrate
import org.elnix.dragonlauncher.enumsui.NestEditMode
import org.elnix.dragonlauncher.enumsui.NestEditMode.DEEPNEST
import org.elnix.dragonlauncher.enumsui.NestEditMode.DRAG
import org.elnix.dragonlauncher.enumsui.NestEditMode.HAPTIC
import org.elnix.dragonlauncher.enumsui.NestEditMode.MIN_ANGLE
import org.elnix.dragonlauncher.enumsui.nestEditModeIcon
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.ui.components.generic.ActionRow
import org.elnix.dragonlauncher.ui.defaultDragDistance
import org.elnix.dragonlauncher.ui.defaultHapticFeedback
import org.elnix.dragonlauncher.ui.defaultMinAngleActivation
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.helpers.nests.circlesSettingsOverlay
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.theme.LocalExtraColors

@Composable
fun NestEditingScreen(
    nestId: Int?,
    nests: List<CircleNest>,
    points: List<SwipePointSerializable>,
    pointIcons: Map<String, ImageBitmap>,
    defaultPoint: SwipePointSerializable,
    onBack: () -> Unit
) {
    if (nestId == null) return
    val currentNest = nests.find { it.id == nestId } ?: return

    val ctx = LocalContext.current
    val extraColors = LocalExtraColors.current
    val density = LocalDensity.current
    val backgroundColor = MaterialTheme.colorScheme.background
    val angleColor = MaterialTheme.colorScheme.tertiary

    val iconsShape by DrawerSettingsStore.iconsShape.flow(ctx)
        .collectAsState(DrawerSettingsStore.iconsShape.default)

    val dragDistancesState = remember(currentNest.id) {
        mutableStateMapOf<Int, Int>().apply {
            putAll(currentNest.dragDistances)
        }
    }

    val hapticState = remember(currentNest.id) {
        mutableStateMapOf<Int, Int>().apply {
            putAll(currentNest.haptic)
        }
    }

    val minAngleState = remember(currentNest.id) {
        mutableStateMapOf<Int, Int>().apply {
            putAll(currentNest.minAngleActivation)
        }
    }


    // used to draw the circles in the preview
//    val circles: SnapshotStateList<UiCircle> = remember { mutableStateListOf() }
//
//    LaunchedEffect(currentNest.dragDistances, dragDistancesState) {
//        circles.clear()
//        dragDistancesState.forEach { (circleNumber, radius) ->
//                circles.add(
//                    UiCircle(
//                        id = circleNumber,
//                        radius = radius.toFloat()
//                    )
//                )
//            }
//    }


    val circles = dragDistancesState.map { (id, radius) ->
        UiCircle(
            id = id,
            radius = radius.toFloat()
        )
    }

    var currentEditMode by remember { mutableStateOf(DRAG) }

    var pendingNestUpdate by remember { mutableStateOf<List<CircleNest>?>(null) }

    /**
     * Saving system, the nests are immutable, they are saved using a pending value, that
     * asynchronously saves the nests in the datastore
     */
    LaunchedEffect(pendingNestUpdate) {
        pendingNestUpdate?.let { nests ->
            SwipeSettingsStore.saveNests(ctx, nests)
            pendingNestUpdate = null
        }
    }


    fun commitDragDistances(state: Map<Int, Int>) {
        pendingNestUpdate = nests.map { nest ->
            if (nest.id == nestId) {
                nest.copy(dragDistances = state.toMap())
            } else nest
        }
    }

    fun commitHaptic(state: Map<Int, Int>) {
        pendingNestUpdate = nests.map { nest ->
            if (nest.id == nestId) {
                nest.copy(haptic = state.toMap())
            } else nest
        }
    }

    fun commitAngle(state: Map<Int, Int>) {
        pendingNestUpdate = nests.map { nest ->
            if (nest.id == nestId) {
                nest.copy(minAngleActivation = state.toMap())
            } else nest
        }
    }




    SettingsLazyHeader(
        title = stringResource(R.string.dragging_distance_selection),
        onBack = onBack,
        helpText = "Help",
        onReset = {
            pendingNestUpdate = nests.filter { it.id != nestId }
        },

        content = {

            Canvas(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                circlesSettingsOverlay(
                    circles = circles,
                    circleColor = extraColors.circle,
                    showCircle = true,
                    center = center,
                    points = points,
                    defaultPoint = defaultPoint,
                    selectedPoint = null,
                    backgroundColor = backgroundColor,
                    nests = nests.map {
                        if (it.id == nestId) it.copy(
                            dragDistances = dragDistancesState
                        ) else it
                    },
                    ctx = ctx,
                    extraColors = extraColors,
                    pointIcons = pointIcons,
                    nestId = nestId,
                    deepNest = 1,
                    shape = iconsShape,
                    density = density,
                    preventBgErasing = true
                )


                // Show the min angle to activate
                circles.forEach { circle ->
                    val arcRadius = circle.radius + 10

                    val rect = Rect(
                        center.x - arcRadius,
                        center.y - arcRadius,
                        center.x + arcRadius,
                        center.y + arcRadius
                    )

                    drawArc(
                        color = angleColor,
                        startAngle = -90f,
                        sweepAngle = minAngleState[circle.id]?.toFloat() ?: 0f,
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = Size(rect.width, rect.height),
                        style = Stroke(width = 3f)
                    )
                }
            }


            ActionRow(
                actions = NestEditMode.entries,
                selectedView = currentEditMode,
                backgroundColor = MaterialTheme.colorScheme.primary,
                actionIcon = { nestEditModeIcon(it) }
            ) {
                currentEditMode = it
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                when (currentEditMode) {
                    DRAG -> {
                        dragDistancesState.toSortedMap().forEach { (index, distance) ->
                            SliderWithLabel(
                                label = if (index == -1) "${stringResource(R.string.cancel_zone)} ->"
                                else "${stringResource(R.string.circle)}: $index ->",
                                value = distance,
                                valueRange = 0..1000,
                                showValue = true,
                                color = MaterialTheme.colorScheme.primary,
                                onReset = {
                                    dragDistancesState[index] = defaultDragDistance(index)
                                    commitDragDistances(dragDistancesState)
                                },
                                onDragStateChange = { isDragging ->
                                    if (!isDragging) {
                                        commitDragDistances(dragDistancesState)
                                    }
                                }
                            ) { newValue ->
                                dragDistancesState[index] = newValue
                            }
                        }
                    }

                    HAPTIC -> {
                        // Keep drag distance state here cause haptic may be empty dues to how it is handled
                        dragDistancesState.toSortedMap().filter { it.key != -1 }
                            .forEach { (index, _) ->
                                val milliseconds =
                                    hapticState[index] ?: defaultHapticFeedback(index)
                                SliderWithLabel(
                                    label = "${stringResource(R.string.circle)}: $index ->",
                                    value = milliseconds,
                                    valueRange = 0..300,
                                    color = MaterialTheme.colorScheme.primary,
                                    onReset = {
                                        hapticState[index] = defaultHapticFeedback(index)
                                        commitHaptic(hapticState)
                                    },
                                    onDragStateChange = { isDragging ->
                                        if (!isDragging) {
                                            commitHaptic(hapticState)
                                        }

                                        if (!isDragging && milliseconds > 0) {
                                            vibrate(ctx, milliseconds.toLong())
                                        }
                                    }
                                ) { newValue ->
                                    hapticState[index] = newValue
                                }
                            }
                    }

                    MIN_ANGLE -> {
                        dragDistancesState.toSortedMap().filter { it.key != -1 }
                            .forEach { (index, distance) ->
                                val angle = minAngleState[index] ?: defaultMinAngleActivation(distance)
                                SliderWithLabel(
                                    label = "${stringResource(R.string.circle)}: $index ->",
                                    value = angle,
                                    valueRange = 0..360,
                                    color = MaterialTheme.colorScheme.primary,
                                    onReset = {
                                        minAngleState[index] = defaultMinAngleActivation(distance)
                                        commitAngle(minAngleState)
                                    },
                                    onDragStateChange = { isDragging ->
                                        if (!isDragging) {
                                            commitAngle(minAngleState)
                                        }
                                    }
                                ) { newValue ->
                                    minAngleState[index] = newValue
                                }
                            }
                    }

                    DEEPNEST -> {

                        var tempDeepnest by remember { mutableStateOf(currentNest.deepnest) }
                        SliderWithLabel(
                            label = stringResource(R.string.deepnest),
                            description = stringResource(R.string.deepnest_desc),
                            backgroundColor = MaterialTheme.colorScheme.surface.adjustBrightness(0.7f),
                            value = tempDeepnest,
                            valueRange = 0..5
                        ) {
                            tempDeepnest = it
                            pendingNestUpdate = nests.map { nest ->
                                if (nest.id == nestId) {
                                    nest.copy(deepnest = it)
                                } else nest
                            }
                        }


                        Text(
                            text = stringResource(R.string.warning_large_values_can_lag_your_phone),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    )
}
