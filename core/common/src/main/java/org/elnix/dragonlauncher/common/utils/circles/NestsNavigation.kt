package org.elnix.dragonlauncher.common.utils.circles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import org.elnix.dragonlauncher.common.serializables.CircleNest

data class NestNavigationState(
    val currentNest: CircleNest,
    val goBack: () -> Unit,
    val goToNest: (Int) -> Unit,
    val clearStack: () -> Unit
)


/**
 * Holds and manages CircleNest navigation state.
 */
@Composable
fun rememberNestNavigation(
    nests: List<CircleNest>,
): NestNavigationState {
    val nestsStack = remember { mutableStateListOf<Int>() }
    val nestId = nestsStack.lastOrNull() ?: 0

    val currentNest = remember(nestId, nests) {
        nests.find { it.id ==  nestId}
            ?: CircleNest(nestId)
    }

    return NestNavigationState(
        currentNest = currentNest,
        goBack = {
            // Remove last element of the navigation and use it as the new nest
            if (nestsStack.isNotEmpty()) {
                nestsStack.removeAt(nestsStack.lastIndex)
            }
        },
        goToNest = { newNestId ->
            if (newNestId != nestId) {
                nestsStack.add(newNestId)
            }
        },
        clearStack = { nestsStack.clear() }
    )
}
