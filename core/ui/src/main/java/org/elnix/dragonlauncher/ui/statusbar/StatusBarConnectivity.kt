package org.elnix.dragonlauncher.ui.statusbar

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import org.elnix.dragonlauncher.common.serializables.StatusBarSerializable

@Composable
fun StatusBarConnectivity(
    element: StatusBarSerializable.Connectivity,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    var connectivityState by remember { mutableStateOf(ConnectivityState()) }

    // Periodic updates every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            connectivityState = readConnectivityState(ctx)
            delay(5_000)
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
        } else {
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

            if (connectivityState.mobileDataStatus.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.SignalCellularAlt,
                    contentDescription = connectivityState.mobileDataStatus,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

data class ConnectivityState(
    val isAirplaneMode: Boolean = false,
    val isWifiEnabled: Boolean = false,
    val isVpnEnabled: Boolean = false,
    val isBluetoothEnabled: Boolean = false,
    val mobileDataStatus: String = ""
)

private fun readConnectivityState(ctx: Context): ConnectivityState {
    val resolver = ctx.contentResolver
    val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val mobileDataStatus = getMobileDataStatus(ctx, connectivityManager, resolver)

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

        isBluetoothEnabled = Settings.Global.getInt(resolver, Settings.Global.BLUETOOTH_ON, 0) == 1,
        mobileDataStatus = mobileDataStatus
    )
}

private fun getMobileDataStatus(
    ctx: Context,
    connectivityManager: ConnectivityManager,
    resolver: android.content.ContentResolver
): String {
    /*  ─────────────  Mobile data status  ─────────────  */
    // 1. Check if mobile data is enabled (check multiple SIMs)
    val mobileDataEnabled = try {
        Settings.Global.getInt(resolver, "mobile_data", 0) == 1 ||
                Settings.Global.getInt(resolver, "mobile_data1", 0) == 1 ||
                Settings.Global.getInt(resolver, "mobile_data2", 0) == 1
    } catch (e: Exception) {
        true // Default to enabled if unable to access
    }

    if (!mobileDataEnabled) return "Data OFF"

    // 2. Get active cellular network + signal
    val activeNetwork = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

    if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
        // For now, just return network type without signal strength
        // Signal strength access might require additional permissions
        val telephonyManager = ctx.getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
        val networkType = try {
            telephonyManager.dataNetworkType
        } catch (e: Exception) {
            android.telephony.TelephonyManager.NETWORK_TYPE_UNKNOWN
        }

        val typeStr = when (networkType) {
            android.telephony.TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            20 -> "5G" // TelephonyManager.NETWORK_TYPE_NR = 20
            android.telephony.TelephonyManager.NETWORK_TYPE_HSDPA, android.telephony.TelephonyManager.NETWORK_TYPE_HSUPA -> "3G"
            else -> "2G"
        }

        return typeStr
    }

    return "Data ON"
}
