package org.elnix.dragonlauncher.ui.components.dragon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.ui.modifiers.settingsGroup

@Composable
fun DragonColumnGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.settingsGroup(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}
