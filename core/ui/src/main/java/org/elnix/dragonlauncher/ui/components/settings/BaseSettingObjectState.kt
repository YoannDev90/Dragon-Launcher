package org.elnix.dragonlauncher.ui.components.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.map
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject

@Composable
fun <T, R> BaseSettingObject<T, R>.asState(default: T? = null): State<T> {
    val ctx = LocalContext.current
    return flow(ctx).collectAsState(initial = default ?: this.default)
}


@Composable
fun <T, R> BaseSettingObject<T, R>.asStateNull(): State<T?> {
    val ctx = LocalContext.current
    return flow(ctx).map { it }.collectAsState(initial = null)
}
