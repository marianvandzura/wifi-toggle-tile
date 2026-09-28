package com.vandzura.wifi.toggle.tile

import android.content.Context
import android.net.wifi.WifiManager

object NetworkUtils {

    private fun wifiManager(context: Context) =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun wifiState(context: Context) = wifiManager(context).wifiState

    fun wifiSSID(context: Context): String? {
        var result: String? = null
        if (PermissionsUtils.hasPermissionToAccessWifiSSID(context)) {
            wifiManager(context).connectionInfo?.ssid?.let { ssid ->
                if (ssid.isNotBlank()) {
                    result = ssid.replace("\"", "")
                }
            }
        }
        return result
    }

    fun turnWifiOn(context: Context) {
        wifiManager(context).isWifiEnabled = true
    }

    fun turnWifiOff(context: Context) {
        wifiManager(context).isWifiEnabled = false
    }

    fun wifiSignalLevel(context: Context, numLevels: Int = 3): Int {
        if (PermissionsUtils.hasPermissionToAccessWifiSSID(context)) {
            val rssi = wifiManager(context).connectionInfo?.rssi ?: 0
            if (rssi != 0 && rssi != -127) {
                val minRssi = -100
                val maxRssi = -55
                val clampedRssi = rssi.coerceIn(minRssi, maxRssi)
                val fraction = (clampedRssi - minRssi).toFloat() / (maxRssi - minRssi)
                val level = (fraction * (numLevels - 1)).toInt()
                return level.coerceIn(0, numLevels - 1)
            }
        }
        return 0
    }

}