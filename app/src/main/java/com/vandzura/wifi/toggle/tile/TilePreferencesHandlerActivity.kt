package com.vandzura.wifi.toggle.tile

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.service.quicksettings.TileService
import androidx.appcompat.app.AppCompatActivity

/**
 * Handle long-press tile action
 */
class TilePreferencesHandlerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent != null) {
            if (intent.action == TileService.ACTION_QS_TILE_PREFERENCES) {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
        }
        finish()
    }

}
