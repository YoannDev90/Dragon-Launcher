package org.elnix.dragonlauncher

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.settings.stores.LanguageSettingsStore
import org.elnix.dragonlauncher.models.AppsViewModel

class MyApplication : Application() {

    lateinit var appsViewModel: AppsViewModel

    val appScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    override fun onCreate() {
        super.onCreate()

        appsViewModel = AppsViewModel(
            application = this,
            coroutineScope = appScope
        )

        CoroutineScope(Dispatchers.Default).launch {
            val tag = LanguageSettingsStore.getLanguageTag(this@MyApplication)
            if (!tag.isNullOrEmpty()) {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(tag)
                )
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appScope.cancel()
    }
}
