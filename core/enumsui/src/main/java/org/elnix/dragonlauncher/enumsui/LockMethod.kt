package org.elnix.dragonlauncher.enumsui

/**
 * Available methods for locking the settings screen.
 */
enum class LockMethod {
    /** No lock — settings are freely accessible */
    NONE,

    /** Require a user-defined PIN code */
    PIN,

    /** Use native Android device unlock (biometric + device credentials fallback) */
    DEVICE_UNLOCK
}
