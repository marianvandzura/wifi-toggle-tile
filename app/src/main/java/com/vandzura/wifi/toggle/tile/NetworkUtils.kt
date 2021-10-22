package com.vandzura.wifi.toggle.tile

import android.content.Context
import android.net.wifi.WifiManager

object NetworkUtils {

    private fun wifiManager(context: Context) = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun wifiState(context: Context) = wifiManager(context).wifiState

    fun turnWifiOn(context: Context) { wifiManager(context).isWifiEnabled = true }

    fun turnWifiOff(context: Context) { wifiManager(context).isWifiEnabled = false }

}