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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.common.utils.vibrate
import org.elnix.dragonlauncher.enumsui.NestEditMode
import org.elnix.dragonlauncher.enumsui.nestEditModeIcon
import org.elnix.dragonlauncher.enumsui.nestEditModeLabel
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.ui.components.generic.ActionRow
import org.elnix.dragonlauncher.ui.defaultDragDistance
import org.elnix.dragonlauncher.ui.defaultHapticFeedback
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
    val backgroundColor = MaterialTheme.colorScheme.background


    val circles: SnapshotStateList<UiCircle> = remember { mutableStateListOf() }


    val dragDistancesState = remember(currentNest.id) {
        mutableStateMapOf<Int, Int>().apply {
            putAll(currentNest.dragDistances)
        }
    }

    LaunchedEffect(currentNest.dragDistances) {
        circles.clear()
        dragDistancesState.forEach { (circleNumber, radius) ->
            circles.add(
                UiCircle(
                    id = circleNumber,
                    radius = radius.toFloat()
                )
            )
        }
    }



    var currentEditMode by remember { mutableStateOf(NestEditMode.DRAG) }

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
                    nests = nests,
                    ctx = ctx,
                    extraColors = extraColors,
                    pointIcons = pointIcons,
                    nestId = nestId,
                    deepNest = 1,
                    preventBgErasing = true
                )
            }


            ActionRow(
                actions = NestEditMode.entries,
                selectedView = currentEditMode,
                backgroundColor = MaterialTheme.colorScheme.primary,
                actionName = { nestEditModeLabel(ctx, it) },
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
                if (currentEditMode == NestEditMode.DRAG) {
                    dragDistancesState.forEach { (index, distance) ->
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

//                            pendingNestUpdate = nests.map { nest ->
//                                if (nest.id == nestId) {
//                                    val newDistances = nest.dragDistances.toMutableMap().apply {
//                                        this[index] = newValue
//                                    }
//                                    nest.copy(dragDistances = newDistances)
//                                } else nest
//                            }
                        }
                    }
                } else {
                    currentNest.dragDistances.forEach { (index, _) ->
                        val milliseconds = currentNest.haptic[index] ?: defaultHapticFeedback(index)
                        SliderWithLabel(
                            label = if (index == -1) "${stringResource(R.string.cancel_zone)} ->"
                            else "${stringResource(R.string.circle)}: $index ->",
                            value = milliseconds,
                            valueRange = 0..300,
                            color = MaterialTheme.colorScheme.primary,
                            onReset = {
                                pendingNestUpdate = nests.map { nest ->
                                    if (nest.id == nestId) {

                                        // Remove the haptic for this index (circle) so it uses the default value
                                        val newHaptic = nest.haptic.toMutableMap().filter {
                                            it.key != index
                                        }

                                        nest.copy(haptic = newHaptic)
                                    } else nest
                                }
                            },
                            onDragStateChange = {
                                if (!it && milliseconds > 0) {
                                    vibrate(ctx, milliseconds.toLong())
                                }
                            }
                        ) { newValue ->
                            pendingNestUpdate = nests.map { nest ->
                                if (nest.id == nestId) {
                                    val newHaptic = nest.haptic.toMutableMap().apply {
                                        this[index] = newValue
                                    }
                                    nest.copy(haptic = newHaptic)
                                } else nest
                            }
                        }
                    }
                }
            }
        }
    )
}
