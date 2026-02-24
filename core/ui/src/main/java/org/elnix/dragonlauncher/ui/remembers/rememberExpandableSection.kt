package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class ExpandableSectionState(
    val isExpanded: () -> Boolean,
    val title: String,
    val toggle: () -> Unit,
)

@Composable
fun rememberExpandableSection(title: String): ExpandableSectionState {
    var isExpanded by remember { mutableStateOf(false) }

    return remember(title) {
        ExpandableSectionState(
            isExpanded = { isExpanded },
            title = title,
            toggle = { isExpanded = !isExpanded }
        )
    }
}
