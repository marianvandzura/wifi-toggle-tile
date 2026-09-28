package com.vandzura.wifi.toggle.tile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.net.wifi.WifiManager
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

class WifiToggleTileService : TileService() {

    private var wifiStateReceiver: WifiStateReceiver? = null

    override fun onTileAdded() {
        super.onTileAdded()
        debugLog("onTileAdded")

        invalidateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        debugLog("onStartListening")

        invalidateTile()
        //listen to wifi changes and rssi signal changes
        wifiStateReceiver = WifiStateReceiver().also {
            val filter = IntentFilter().apply {
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                addAction(WifiManager.RSSI_CHANGED_ACTION)
            }
            registerReceiver(it, filter)
        }
    }

    override fun onStopListening() {
        debugLog("onStopListening")
        //stop listening to wifi changes
        wifiStateReceiver?.let { unregisterReceiver(it) }
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()

        if (qsTile.state == Tile.STATE_INACTIVE) {
            debugLog("onClick - turn on")
            // Turn on
            NetworkUtils.turnWifiOn(this)
        } else {
            debugLog("onClick - turn off")
            // Turn off
            NetworkUtils.turnWifiOff(this)
        }

        invalidateTile()
    }

    private fun invalidateTile(
        wifiState: Int = NetworkUtils.wifiState(this),
        wifiSSID: String? = NetworkUtils.wifiSSID(this)
    ) {
        qsTile.run {
            if (WifiManager.WIFI_STATE_ENABLING == wifiState ||
                WifiManager.WIFI_STATE_ENABLED == wifiState
            ) {
                state = Tile.STATE_ACTIVE
                contentDescription = resources.getString(R.string.content_description_turn_wifi_off)
            } else {
                state = Tile.STATE_INACTIVE
                contentDescription = resources.getString(R.string.content_description_turn_wifi_on)
            }
        }

        val iconRes: Int
        val subtitleRes: Int
        var subtitleMetadata: String? = null

        when (wifiState) {
            WifiManager.WIFI_STATE_ENABLING -> {
                debugLog("invalidateTile - enabling")
                iconRes = R.drawable.ic_wifi_on
                subtitleRes = R.string.wifi_state_turning_on
                subtitleMetadata = wifiSSID
            }

            WifiManager.WIFI_STATE_ENABLED -> {
                debugLog("invalidateTile - enabled")
                iconRes = when (NetworkUtils.wifiSignalLevel(this, 3)) {
                    2 -> R.drawable.wifi_signal_3
                    1 -> R.drawable.wifi_signal_2
                    else -> R.drawable.wifi_signal_1
                }
                subtitleRes = R.string.wifi_state_on
                subtitleMetadata = wifiSSID
            }

            WifiManager.WIFI_STATE_DISABLING -> {
                debugLog("invalidateTile - disabling")
                iconRes = R.drawable.ic_wifi_off
                subtitleRes = R.string.wifi_state_turning_off
            }

            WifiManager.WIFI_STATE_DISABLED -> {
                debugLog("invalidateTile - disabled")
                iconRes = R.drawable.ic_wifi_off
                subtitleRes = R.string.wifi_state_off
            }

            else -> {
                debugLog("invalidateTile - unknown")
                iconRes = R.drawable.ic_wifi_off
                subtitleRes = -1
            }
        }

        val icon = Icon.createWithResource(this, iconRes)
        val title = resources.getString(R.string.wifi)
        val subtitle = if (subtitleRes != -1) {
            var subtitleText = resources.getString(subtitleRes)
            if (!subtitleMetadata.isNullOrBlank()) {
                "$subtitleText • $subtitleMetadata"
            } else {
                subtitleText
            }
        } else {
            ""
        }

        qsTile.icon = icon
        if (Build.VERSION.SDK_INT >= 29) {
            qsTile.label = title
            qsTile.subtitle = subtitle
        } else {
            qsTile.label = "$title $subtitle"
        }

        qsTile.updateTile()
    }

    inner class WifiStateReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                if (intent.action == WifiManager.RSSI_CHANGED_ACTION) {
                    debugLog("WifiStateReceiver - RSSI changed")
                    invalidateTile()
                } else {
                    val wifiState =
                        intent.getIntExtra(
                            WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_UNKNOWN
                        )
                    debugLog("WifiStateReceiver - wifi state changed")
                    invalidateTile(wifiState)
                }
            }
        }
    }

    private fun debugLog(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("WifiToggleTileService", message)
        }
    }

}
