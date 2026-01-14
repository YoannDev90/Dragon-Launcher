package org.elnix.dragonlauncher.common.logging

import android.util.Log

fun Any.logD(tag: String = this::class.java.simpleName, message: String) {
    Log.d(tag, message)
    DragonLogManager.log(Log.DEBUG, tag, message)
}

fun Any.logI(tag: String = this::class.java.simpleName, message: String) {
    Log.i(tag, message)
    DragonLogManager.log(Log.INFO, tag, message)
}

fun Any.logW(tag: String = this::class.java.simpleName, message: String) {
    Log.w(tag, message)
    DragonLogManager.log(Log.WARN, tag, message)
}

fun Any.logE(tag: String = this::class.java.simpleName, message: String, throwable: Throwable? = null) {
    Log.e(tag, message, throwable)
    DragonLogManager.log(Log.ERROR, tag, message, throwable)
}
