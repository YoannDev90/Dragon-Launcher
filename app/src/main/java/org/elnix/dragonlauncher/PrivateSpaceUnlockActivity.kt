package org.elnix.dragonlauncher

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import org.elnix.dragonlauncher.ui.helpers.UnlockScreen
import org.elnix.dragonlauncher.ui.theme.DragonLauncherTheme

class PrivateSpaceUnlockActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_UNLOCK_SUCCESS = "extra_unlock_success"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DragonLauncherTheme {
                UnlockScreen(
                    onUnlockFinished = { success ->
                        val resultIntent = Intent().apply {
                            putExtra(EXTRA_UNLOCK_SUCCESS, success)
                        }

                        setResult(
                            if (success) RESULT_OK
                            else RESULT_CANCELED,
                            resultIntent
                        )

                        finish()
                    }
                )
            }
        }
    }
}
