package org.elnix.dragonlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.elnix.dragonlauncher.ui.AdvancedSettingsScreen
import org.elnix.dragonlauncher.ui.SettingsScreen

class AdvancedSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdvancedSettingsScreen {
                finish()
            }
        }
    }
}
