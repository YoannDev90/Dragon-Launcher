package org.elnix.dragonlauncher

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.ui.helpers.PrivateSpaceUnlockScreen
import org.elnix.dragonlauncher.ui.theme.DragonLauncherTheme

class PrivateSpaceUnlockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val ctx = LocalContext.current
            val appsViewModel = remember(ctx) {
                (ctx.applicationContext as MyApplication).appsViewModel
            }

            DragonLauncherTheme {

                PrivateSpaceUnlockScreen(
                    appsViewModel = appsViewModel,
                    onStart = { scope ->
                        scope.launch {
                            appsViewModel.unlockAndReload()
                            finish()
                        }
                    }
                )
            }
        }
    }
}
