package org.elnix.dragonlauncher.enumsui

/**
 * Available methods for locking the settings screen.
 */
enum class LockMethod {
    /** No lock — settings are freely accessible */
    NONE,

    /** Require a user-defined PIN code */
    PIN,

    /** Require biometric authentication (fingerprint) */
    BIOMETRIC,

    /** Use Android device credentials (pattern / PIN / password) */
    DEVICE_CREDENTIALS
}
