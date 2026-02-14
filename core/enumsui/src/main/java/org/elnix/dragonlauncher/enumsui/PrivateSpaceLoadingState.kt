package org.elnix.dragonlauncher.enumsui

//enum class PrivateSpaceLoadingState {
//    Authenticating,
//    Loading,
//    Available,
//    Locked
//}



data class PrivateSpaceLoadingState (
    val isLocked: Boolean,
    val isLoading: Boolean,
    val isAuthenticating: Boolean
)
