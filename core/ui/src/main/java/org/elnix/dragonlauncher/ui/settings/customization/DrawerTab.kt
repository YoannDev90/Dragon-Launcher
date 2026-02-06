@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.utils.SHAPES_TAG
import org.elnix.dragonlauncher.enumsui.DrawerActions
import org.elnix.dragonlauncher.enumsui.drawerActionIcon
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.settings.DrawerActionSelector
import org.elnix.dragonlauncher.ui.components.settings.SettingsSlider
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.dialogs.ShapePickerDialog
import org.elnix.dragonlauncher.ui.helpers.GridSizeSlider
import org.elnix.dragonlauncher.ui.helpers.ShapeRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader


@Composable
fun DrawerTab(
    appsViewModel: AppsViewModel,
    onBack: () -> Unit
) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val apps by appsViewModel.userApps.collectAsState(initial = emptyList())
    val icons by appsViewModel.icons.collectAsState()

    val leftDrawerAction by DrawerSettingsStore.leftDrawerAction.asState()

    val rightDrawerAction by DrawerSettingsStore.rightDrawerAction.asState()

    val leftDrawerWidth by DrawerSettingsStore.leftDrawerWidth.asState()
    val rightDrawerWidth by DrawerSettingsStore.rightDrawerWidth.asState()

    val iconsShape by DrawerSettingsStore.iconsShape.asState()

    var drawerCategorySettingsExpanded by remember { mutableStateOf(false) }

    var totalWidthPx by remember { mutableFloatStateOf(0f) }

    var leftWeight by remember { mutableFloatStateOf(leftDrawerWidth) }
    var rightWeight by remember { mutableFloatStateOf(rightDrawerWidth) }

    LaunchedEffect(leftDrawerWidth, rightDrawerWidth) {
        leftWeight = leftDrawerWidth
        rightWeight = rightDrawerWidth
    }

    val leftActionNotNone = leftDrawerAction != DrawerActions.NONE
    val rightActionNotNone = rightDrawerAction != DrawerActions.NONE

    val leftActionNotDisabled = leftDrawerAction != DrawerActions.DISABLED
    val rightActionNotDisabled = rightDrawerAction != DrawerActions.DISABLED

    var showShapePickerDialog by remember { mutableStateOf(false) }

    SettingsLazyHeader(
        title = stringResource(R.string.app_drawer),
        onBack = onBack,
        helpText = stringResource(R.string.drawer_tab_text),
        onReset = {
            scope.launch {
                DrawerSettingsStore.resetAll(ctx)
            }
        }
    ) {

        item { TextDivider(stringResource(R.string.behavior)) }

        item {
            SettingsSwitchRow(
                setting = DrawerSettingsStore.autoOpenSingleMatch,
                title = stringResource(R.string.auto_launch_single_match),
                description = stringResource(R.string.auto_launch_single_match_desc),
            )
        }

        item {
            SettingsSwitchRow(
                setting = DrawerSettingsStore.autoShowKeyboardOnDrawer,
                title = stringResource(R.string.auto_show_keyboard),
                description = stringResource(R.string.auto_show_keyboard_desc),
            )
        }

        item { TextDivider(stringResource(R.string.appearance)) }


        item {
            SettingsSwitchRow(
                setting = DrawerSettingsStore.showAppIconsInDrawer,
                title = stringResource(R.string.show_app_icons_in_drawer),
                description = stringResource(R.string.show_app_icons_in_drawer_desc)
            )
        }

        item {
            SettingsSwitchRow(
                setting = DrawerSettingsStore.showAppLabelInDrawer,
                title = stringResource(R.string.show_app_labels_in_drawer),
                description = stringResource(R.string.show_app_labels_in_drawer_desc)
            )
        }

        item {
            SettingsSwitchRow(
                setting = DrawerSettingsStore.useCategory,
                title = stringResource(R.string.use_categories),
                description = stringResource(R.string.use_categories_desc)
            )
        }

        item {

            val rotationDegrees = remember {
                Animatable(0f)
            }

            LaunchedEffect(drawerCategorySettingsExpanded) {
                rotationDegrees.animateTo(if (drawerCategorySettingsExpanded) 90f else 0f)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DragonShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        drawerCategorySettingsExpanded = !drawerCategorySettingsExpanded
                    }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.category_settings)
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = stringResource(R.string.expanded_chevron_indicator),
                    modifier = Modifier
                        .rotate(rotationDegrees.value)
                )
            }
            Spacer(Modifier.height(10.dp))

            AnimatedVisibility(
                visible = drawerCategorySettingsExpanded
            ) {
                Column(
                    modifier = Modifier
                        .clip(DragonShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(10.dp)
                ) {
                    SettingsSlider(
                        setting = DrawerSettingsStore.categoryGridWidth,
                        title = stringResource(R.string.category_grid_width),
                        valueRange = 1..3
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .clip(DragonShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp)
            ) {
                SettingsSlider(
                    setting = DrawerSettingsStore.maxIconSize,
                    description = stringResource(R.string.max_icon_size_desc),
                    title = stringResource(R.string.max_icon_size),
                    valueRange = 0..200
                )

                SettingsSlider(
                    setting = DrawerSettingsStore.iconsSpacingHorizontal,
                    title = stringResource(R.string.icons_spacing_horizontal),
                    description = stringResource(R.string.icons_spacing_horizontal_desc),
                    valueRange = 0..50
                )

                SettingsSlider(
                    setting = DrawerSettingsStore.iconsSpacingVertical,
                    title = stringResource(R.string.icons_spacing_vertical),
                    description = stringResource(R.string.icons_spacing_vertical_desc),
                    valueRange = 0..50
                )
            }
        }

        item {
            GridSizeSlider(
                apps = apps,
                icons = icons
            )
        }


        //Shapes picker
        item {
            ShapeRow(
                selected = iconsShape,
                onReset = { scope.launch { DrawerSettingsStore.iconsShape.reset(ctx) } }
            ) { showShapePickerDialog = true }
        }


        item { TextDivider(stringResource(R.string.drawer_actions)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.drawer_actions_width),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.width(15.dp))

                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(R.string.reset),
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .clickable {
                            scope.launch {
                                DrawerSettingsStore.leftDrawerWidth.reset(ctx)
                                DrawerSettingsStore.rightDrawerWidth.reset(ctx)
                            }
                        }
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .onGloballyPositioned {
                        totalWidthPx = it.size.width.toFloat()
                    },
                horizontalArrangement = Arrangement.Center
            ) {

                if (leftActionNotDisabled) {
                    // LEFT PANEL -----------------------------------------------------------
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(leftWeight.coerceIn(0.001f, 1f))
                            .background(MaterialTheme.colorScheme.primary.copy(if (leftActionNotNone) 1f else 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (leftActionNotNone) {
                            Icon(
                                imageVector = drawerActionIcon(leftDrawerAction),
                                contentDescription = stringResource(R.string.left_drawer_action),
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    DragHandle(
                        onDrag = { dx ->
                            if (totalWidthPx > 0f) {
                                leftWeight = (leftWeight + dx / totalWidthPx).coerceIn(0.001f, 1f)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                DrawerSettingsStore.leftDrawerWidth.set(ctx, leftWeight)
                            }
                        }
                    )
                }

                Spacer(Modifier.weight(1f))

                if (rightActionNotDisabled) {

                    // DRAG HANDLE RIGHT ----------------------------------------------------
                    DragHandle(
                        onDrag = { dx ->
                            if (totalWidthPx > 0f) {
                                rightWeight = (rightWeight - dx / totalWidthPx).coerceIn(0.001f, 1f)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                DrawerSettingsStore.rightDrawerWidth.set(ctx, rightWeight)
                            }
                        }
                    )

                    // RIGHT PANEL ----------------------------------------------------------
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(rightWeight.coerceIn(0.001f, 1f))
                            .background(MaterialTheme.colorScheme.primary.copy(if (rightActionNotNone) 1f else 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rightActionNotNone) {
                            Icon(
                                imageVector = drawerActionIcon(rightDrawerAction),
                                contentDescription = stringResource(R.string.right_drawer_action),
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        item {
            DrawerActionSelector(
                settingObject = DrawerSettingsStore.leftDrawerAction,
                label = stringResource(R.string.left_drawer_action),
                allowNone = true
            )
        }

        item {
            DrawerActionSelector(
                settingObject = DrawerSettingsStore.rightDrawerAction,
                label = stringResource(R.string.right_drawer_action),
                allowNone = true
            )
        }

        item { HorizontalDivider() }

        item {
            DrawerActionSelector(
                settingObject = DrawerSettingsStore.scrollUpDrawerAction,
                label = stringResource(R.string.scroll_up_action),
            )
        }

        item {
            DrawerActionSelector(
                settingObject = DrawerSettingsStore.scrollDownDrawerAction,
                label = stringResource(R.string.scroll_down_actiob),
            )
        }

        item {
            DrawerActionSelector(
                settingObject = DrawerSettingsStore.tapEmptySpaceAction,
                label = stringResource(R.string.tap_empty_space_action),
            )
        }

        item {
            DrawerActionSelector(
                settingObject = DrawerSettingsStore.backDrawerAction,
                label = stringResource(R.string.back_action),
            )
        }

        item {
            DrawerActionSelector(
                settingObject = DrawerSettingsStore.drawerEnterAction,
                label = stringResource(R.string.drawer_enter_key_action),
            )
        }

        item {
            DrawerActionSelector(
                settingObject = DrawerSettingsStore.drawerHomeAction,
                label = stringResource(R.string.drawer_home_action),
            )
        }
    }

    if (showShapePickerDialog) {
        ShapePickerDialog(
            selected = iconsShape,
            onDismiss = { showShapePickerDialog = false }
        ) {
            ctx.logD(SHAPES_TAG, "Picked: $it")
            scope.launch {
                DrawerSettingsStore.iconsShape.set(ctx, it)
                showShapePickerDialog = false
            }
        }
    }
}

@Composable
private fun DragHandle(
    onDrag: (dx: Float) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(6.dp)
            .background(MaterialTheme.colorScheme.outline)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, drag ->
                        change.consume()
                        onDrag(drag.x)
                    },
                    onDragEnd = onDragEnd
                )
            }
    )
}
