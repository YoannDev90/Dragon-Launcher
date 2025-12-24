package org.elnix.dragonlauncher.data.helpers

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.data.helpers.DrawerActions.CLOSE
import org.elnix.dragonlauncher.data.helpers.DrawerActions.DISABLED
import org.elnix.dragonlauncher.data.helpers.DrawerActions.NONE
import org.elnix.dragonlauncher.data.helpers.DrawerActions.TOGGLE_KB

enum class DrawerActions { CLOSE, TOGGLE_KB, NONE, DISABLED }

enum class DrawerEnterActions { CLOSE_DRAWER, CLEAR, SEARCH_WEB, OPEN_FIRST_APP, NOTHING }
@Composable
fun drawerActionIcon(action: DrawerActions) = when (action) {
    CLOSE -> Icons.Default.Close
    TOGGLE_KB -> Icons.Default.Keyboard
    NONE -> Icons.Default.RadioButtonUnchecked
    DISABLED -> Icons.Default.RadioButtonUnchecked
}

fun drawerActionsLabel(ctx: Context,action: DrawerActions) = when (action) {
    CLOSE -> ctx.getString(R.string.close_drawer)
    TOGGLE_KB -> ctx.getString(R.string.toggle_kb)
    NONE -> ctx.getString(R.string.none)
    DISABLED -> ""
}

fun drawerEnterActionLabel(context: Context, action: DrawerEnterActions): String =
    when (action) {
        DrawerEnterActions.CLOSE_DRAWER ->
            context.getString(R.string.drawer_action_close_drawer)

        DrawerEnterActions.CLEAR ->
            context.getString(R.string.drawer_action_clear)

        DrawerEnterActions.SEARCH_WEB ->
            context.getString(R.string.drawer_action_search_web)

        DrawerEnterActions.OPEN_FIRST_APP ->
            context.getString(R.string.drawer_action_open_first_app)

        DrawerEnterActions.NOTHING ->
            context.getString(R.string.drawer_action_nothing)
    }
