package org.elnix.dragonlauncher.settings.bases

import android.content.Context

interface AnySettingObject {
    val key: String
    suspend fun getAny(ctx: Context): Any?
    suspend fun setAny(ctx: Context, value: Any?)
}
