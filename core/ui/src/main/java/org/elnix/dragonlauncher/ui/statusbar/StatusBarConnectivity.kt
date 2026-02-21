package org.elnix.dragonlauncher.ui.statusbar

import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun StatusBarConnectivity(
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    var connectivityState by remember { mutableStateOf(ConnectivityState()) }

    LaunchedEffect(Unit) {
        while (true) {
            connectivityState = readConnectivityState(ctx)
            delay(2_000)
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        if (connectivityState.isVpnEnabled) {
//            Icon(
//                Icons.Default.VpnLock,
//                "VPN",
//                textColor,
//                Modifier.size(14.dp)
//            )
//        }

        if (connectivityState.isAirplaneMode) {
            Icon(
                imageVector = Icons.Default.AirplanemodeActive,
                contentDescription = "Airplane",
                modifier = Modifier.size(14.dp)
            )
        }
        if (connectivityState.isWifiEnabled) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "WiFi on",
                modifier = Modifier.size(14.dp)
            )
        }

        if (connectivityState.isBluetoothEnabled) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = "Bluetooth",
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

data class ConnectivityState(
    val isAirplaneMode: Boolean = false,
    val isWifiEnabled: Boolean = false,
    val isVpnEnabled: Boolean = false,
    val isBluetoothEnabled: Boolean = false
)

private fun readConnectivityState(ctx: Context): ConnectivityState {
    val resolver = ctx.contentResolver

//    ctx.logD("StatusBar", "AIRPLANE: ${Settings.Global.getInt(resolver, Settings.Global.AIRPLANE_MODE_ON, 0)}")
//    ctx.logD("StatusBar", "WIFI_ON: ${Settings.Global.getInt(resolver, Settings.Global.WIFI_ON, 0)}")
//    ctx.logD("StatusBar", "VPN_ALWAYS_ON: ${Settings.Global.getInt(resolver, Settings.Global.VPN_ALWAYS_ON_GENERIC, 0)}")
//    ctx.logD("StatusBar", "BLUETOOTH_ON: ${Settings.Global.getInt(resolver, Settings.Global.BLUETOOTH_ON, 0)}")

    return ConnectivityState(
        isAirplaneMode = Settings.Global.getInt(resolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1,

        isWifiEnabled = when {
            Settings.Global.getInt(resolver, Settings.Global.WIFI_ON, 0) == 1 -> true
            Settings.Global.getInt(resolver, "wifi_on", 0) == 1 -> true
            else -> false
        },

//        isVpnEnabled = Settings.Global.getInt(
//            resolver,
//            Settings.Global.VPN_ALWAYS_ON_GENERIC,
//            0
//        ) == 1 ||
//                Settings.Global.getInt(
//                    resolver,
//                    "vpn_always_on_generic",
//                    0
//                ) == 1,

        isBluetoothEnabled = Settings.Global.getInt(resolver, Settings.Global.BLUETOOTH_ON, 0) == 1
    )
}
