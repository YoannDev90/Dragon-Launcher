package org.elnix.dragonlauncher.common.logging

import timber.log.Timber

/**
 * Extension for optimized logging with Timber.
 * Uses inline and lambda for performance when logging complex strings.
 */
inline fun Any.logD(tag: String = this::class.java.simpleName, message: () -> String) {
    Timber.tag(tag).d(message())
}

inline fun Any.logI(tag: String = this::class.java.simpleName, message: () -> String) {
    Timber.tag(tag).i(message())
}

inline fun Any.logW(tag: String = this::class.java.simpleName, message: () -> String) {
    Timber.tag(tag).w(message())
}

inline fun Any.logE(tag: String = this::class.java.simpleName, throwable: Throwable? = null, message: () -> String) {
    Timber.tag(tag).e(throwable, message())
}

// Simple versions for static strings
fun Any.logD(tag: String = this::class.java.simpleName, message: String) {
    Timber.tag(tag).d(message)
}

fun Any.logI(tag: String = this::class.java.simpleName, message: String) {
    Timber.tag(tag).i(message)
}

fun Any.logW(tag: String = this::class.java.simpleName, message: String) {
    Timber.tag(tag).w(message)
}

fun Any.logE(tag: String = this::class.java.simpleName, message: String, throwable: Throwable? = null) {
    Timber.tag(tag).e(throwable, message)
}
