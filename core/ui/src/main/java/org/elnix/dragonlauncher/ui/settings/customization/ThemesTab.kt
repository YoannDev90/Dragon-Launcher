package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.ThemeObject
import org.elnix.dragonlauncher.ui.helpers.ThemesList
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.common.utils.loadThemes

@Composable
fun ThemesTab(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current

    var themes by remember { mutableStateOf<List<ThemeObject>?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        themes = loadThemes(ctx)
        loading = false
    }

    SettingsLazyHeader(
        title = stringResource(R.string.theme_selector),
        onBack = onBack,
        helpText = stringResource(R.string.theme_selector_help),
        onReset = null,
        content = {
            ThemesList(loading, themes)
        }
    )
}
