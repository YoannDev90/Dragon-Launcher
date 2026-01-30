package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.enumsui.DrawerActions
import org.elnix.dragonlauncher.enumsui.drawerActionIcon
import org.elnix.dragonlauncher.models.AppsViewModel
import org.elnix.dragonlauncher.settings.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.settings.DrawerActionSelector
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.helpers.GridSizeSlider
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
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

    val showAppIconsInDrawer by DrawerSettingsStore.showAppIconsInDrawer.flow(ctx)
        .collectAsState(initial = true)

    val showAppLabelsInDrawer by DrawerSettingsStore.showAppLabelInDrawer.flow(ctx)
        .collectAsState(initial = true)

//    val searchBarBottom by DrawerSettingsStore.getSearchBarBottom(ctx)
//        .collectAsState(initial = true)


    val leftDrawerAction by DrawerSettingsStore.leftDrawerAction.flow(ctx)
        .collectAsState(initial = DrawerActions.TOGGLE_KB)

    val rightDrawerAction by DrawerSettingsStore.rightDrawerAction.flow(ctx)
        .collectAsState(initial = DrawerActions.CLOSE)

    val leftDrawerWidth by DrawerSettingsStore.leftDrawerWidth.flow(ctx)
        .collectAsState(initial = 0.1f)
    val rightDrawerWidth by DrawerSettingsStore.rightDrawerWidth.flow(ctx)
        .collectAsState(initial = 0.1f)


    var totalWidthPx by remember { mutableFloatStateOf(0f) }

    var localLeft by remember { mutableFloatStateOf(leftDrawerWidth) }
    var localRight by remember { mutableFloatStateOf(rightDrawerWidth) }

    val leftActionNotNone = leftDrawerAction != DrawerActions.NONE
    val rightActionNotNone = rightDrawerAction != DrawerActions.NONE

    val leftActionNotDisabled = leftDrawerAction != DrawerActions.DISABLED
    val rightActionNotDisabled = rightDrawerAction != DrawerActions.DISABLED

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


//        item {
//            SwitchRow(
//                searchBarBottom,
//                "Search bar ${if (searchBarBottom) "Bottom" else "Top"}",
//                enabled = false
//            ) { scope.launch { DrawerSettingsStore.setSearchBarBottom(ctx, it) } }
//        }

        item { TextDivider(stringResource(R.string.appearance)) }


        item {
            SwitchRow(
                showAppIconsInDrawer,
                "Show App Icons in Drawer",
            ) { scope.launch { DrawerSettingsStore.showAppIconsInDrawer.set(ctx, it) } }
        }

        item {
            SwitchRow(
                showAppLabelsInDrawer,
                "Show App Labels in Drawer",
            ) { scope.launch { DrawerSettingsStore.showAppLabelInDrawer.set(ctx, it) } }
        }

        item {
            GridSizeSlider(
                apps = apps,
                icons = icons,
                showIcons = showAppIconsInDrawer,
                showLabels = showAppLabelsInDrawer
            )
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
                            localLeft = 0.1f
                            localRight = 0.1f
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
                            .fillMaxWidth(localLeft)
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

                    // DRAG HANDLE LEFT -----------------------------------------------------
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(6.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(if (rightActionNotNone) 1f else 0.5f))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, drag ->
                                        change.consume()
                                        if (totalWidthPx > 0f) {
                                            val deltaPercent = drag.x / totalWidthPx
                                            localLeft = (localLeft + deltaPercent).coerceIn(0f, 1f)
                                        }
                                    },
                                    onDragEnd = {
                                        scope.launch {
                                            DrawerSettingsStore.leftDrawerWidth.set(ctx, localLeft)
                                        }
                                    }
                                )
                            }
                    )
                }

                Spacer(Modifier.weight(1f))

                if (rightActionNotDisabled) {

                    // DRAG HANDLE RIGHT ----------------------------------------------------
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(6.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(if (rightActionNotNone) 1f else 0.5f))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, drag ->
                                        change.consume()
                                        if (totalWidthPx > 0f) {
                                            val deltaPercent = -drag.x / totalWidthPx // reversed
                                            localRight =
                                                (localRight + deltaPercent).coerceIn(0f, 1f)
                                        }
                                    },
                                    onDragEnd = {
                                        scope.launch {
                                            DrawerSettingsStore.rightDrawerWidth.set(
                                                ctx,
                                                localRight
                                            )
                                        }
                                    }
                                )
                            }
                    )

                    // RIGHT PANEL ----------------------------------------------------------
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(localRight)
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
                settingObject = DrawerSettingsStore.rightDrawerAction,
                label = stringResource(R.string.right_drawer_action),
            )
        }

        item {
            DrawerActionSelector(
                settingObject = DrawerSettingsStore.leftDrawerAction,
                label = stringResource(R.string.left_drawer_action),
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

//        item {
//            ActionSelectorRow(
//                options = DrawerActions.entries.filter { it != DrawerActions.DISABLED },
//                selected = scrollDownDrawerAction,
//                label = stringResource(R.string.scroll_up_action),
//                optionLabel = { drawerActionsLabel(ctx, it) },
//                onToggle = {
//                    scope.launch {
//                        if (leftActionNotDisabled) {
//                            DrawerSettingsStore.scrollDownDrawerAction.set(
//                                ctx,
//                                DrawerActions.DISABLED
//                            )
//                        } else {
//                            DrawerSettingsStore.scrollDownDrawerAction.set(
//                                ctx,
//                                DrawerActions.TOGGLE_KB
//                            )
//                        }
//                    }
//                },
//                toggled = leftDrawerAction != DrawerActions.DISABLED
//            ) {
//                scope.launch { DrawerSettingsStore.scrollDownDrawerAction.set(ctx, it) }
//            }
//        }
//
//        item {
//            ActionSelectorRow(
//                options = DrawerActions.entries.filter { it != DrawerActions.DISABLED },
//                selected = leftDrawerAction,
//                label = stringResource(R.string.left_drawer_action),
//                optionLabel = { drawerActionsLabel(ctx, it) },
//                onToggle = {
//                    scope.launch {
//                        if (leftActionNotDisabled) {
//                            DrawerSettingsStore.leftDrawerAction.set(ctx, DrawerActions.DISABLED)
//                        } else {
//                            DrawerSettingsStore.leftDrawerAction.set(ctx, DrawerActions.TOGGLE_KB)
//                        }
//                    }
//                },
//                toggled = leftDrawerAction != DrawerActions.DISABLED
//            ) {
//                scope.launch { DrawerSettingsStore.leftDrawerAction.set(ctx, it) }
//            }
//        }
//
//        item {
//            ActionSelectorRow(
//                options = DrawerActions.entries.filter { it != DrawerActions.DISABLED },
//                selected = rightDrawerAction,
//                label = stringResource(R.string.right_drawer_action),
//                optionLabel = { drawerActionsLabel(ctx, it) },
//                onToggle = {
//                    scope.launch {
//                        if (rightActionNotDisabled) {
//                            DrawerSettingsStore.rightDrawerAction.set(ctx, DrawerActions.DISABLED)
//                        } else {
//                            DrawerSettingsStore.rightDrawerAction.set(ctx, DrawerActions.CLOSE)
//                        }
//                    }
//                },
//                toggled = rightDrawerAction != DrawerActions.DISABLED
//            ) {
//                scope.launch { DrawerSettingsStore.rightDrawerAction.set(ctx, it) }
//            }
//        }
//
//        item {
//            ActionSelectorRow(
//                options = DrawerActions.entries.filter { it != DrawerActions.NONE && it != DrawerActions.DISABLED },
//                selected = drawerEnterAction,
//                label = stringResource(R.string.drawer_enter_key_action),
//                optionLabel = { drawerActionsLabel(ctx, it) },
//                onToggle = {
//                    scope.launch {
//                        if (drawerEnterAction == DrawerActions.NONE) {
//                            DrawerSettingsStore.drawerEnterAction.set(
//                                ctx,
//                                DrawerActions.CLEAR
//                            )
//                        } else {
//                            DrawerSettingsStore.drawerEnterAction.set(
//                                ctx,
//                                DrawerActions.NONE
//                            )
//                        }
//                    }
//                },
//                toggled = drawerEnterAction != DrawerActions.NONE
//            ) {
//                scope.launch { DrawerSettingsStore.drawerEnterAction.set(ctx, it) }
//            }
//        }
//
//
//        item {
//            ActionSelectorRow(
//                options = DrawerActions.entries.filter { it != DrawerActions.NONE && it != DrawerActions.DISABLED },
//                selected = drawerHomeAction,
//                label = stringResource(R.string.drawer_home_action),
//                optionLabel = { drawerActionsLabel(ctx, it) },
//                onToggle = {
//                    scope.launch {
//                        if (drawerHomeAction == DrawerActions.NONE) {
//                            DrawerSettingsStore.drawerHomeAction.set(
//                                ctx,
//                                DrawerActions.CLEAR
//                            )
//                        } else {
//                            DrawerSettingsStore.drawerHomeAction.set(
//                                ctx,
//                                DrawerActions.NONE
//                            )
//                        }
//                    }
//                },
//                toggled = drawerHomeAction != DrawerActions.NONE
//            ) {
//                scope.launch { DrawerSettingsStore.drawerHomeAction.set(ctx, it) }
//            }
//        }
    }
}
