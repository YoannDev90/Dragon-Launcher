package org.elnix.dragonlauncher.ui.helpers

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable


/**
 * No anim composable
 *
 * Blocks enter/exit transitions of screen in a navHost
 *
 * @param route
 * @param arguments
 * @param content
 * @receiver
 */
fun NavGraphBuilder.noAnimComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
        content = content
    )
}


//fun NavGraphBuilder.settingComposable(
//    route: String,
//    arguments: List<NamedNavArgument> = emptyList(),
//    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
//) {
//    composable(
//        route = route,
//        arguments = arguments,
//        enterTransition = { forwardTransition().targetContentEnter },
//        exitTransition = { forwardTransition().initialContentExit },
//        popEnterTransition = { backTransition().targetContentEnter },
//        popExitTransition = { backTransition().initialContentExit },
//        content = content
//    )
//}


fun raiseUpAnimation() =
    fadeIn(
        animationSpec = tween(50),
        initialAlpha = 0.5f
    ) + slideInVertically(
        initialOffsetY = { it / 2 },
        animationSpec = tween(100, easing = FastOutSlowInEasing)
    )

fun collapseDownAnimation() =
    fadeOut(
        animationSpec = tween(50),
        targetAlpha = 0f
    ) + slideOutVertically(
        targetOffsetY = { it / 2 },
        animationSpec = tween(50, easing = LinearOutSlowInEasing)
    )


private fun forwardTransition() =
    slideInHorizontally { it / 2 } +
            scaleIn(initialScale = 0.9f) +
            fadeIn() togetherWith
            slideOutHorizontally { -it / 4 }

private fun backTransition() =
    slideInHorizontally { -it / 4 } togetherWith
            slideOutHorizontally { it / 2 } +
            scaleOut(targetScale = 0.9f) +
            fadeOut()
