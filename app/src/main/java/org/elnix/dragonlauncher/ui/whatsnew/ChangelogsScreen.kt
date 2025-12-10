package org.elnix.dragonlauncher.ui.whatsnew

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.elnix.dragonlauncher.R
import org.elnix.dragonlauncher.ui.helpers.TextDivider
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.utils.getVersionCode
import org.elnix.dragonlauncher.utils.loadChangelogs

@Composable
fun ChangelogsScreen(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current

    val versionCode = getVersionCode(ctx)

    val updates by produceState(initialValue = emptyList()) {
        value = loadChangelogs(ctx, versionCode)
    }


    SettingsLazyHeader(
        title = stringResource(R.string.changelogs),
        onBack = onBack,
        helpText = stringResource(R.string.changelogs_help),
        resetText = null,
        onReset = null
    ) {
        item { TextDivider("Changelogs") }

        items(updates) { update ->
            UpdateCard(update)
        }
    }
}
