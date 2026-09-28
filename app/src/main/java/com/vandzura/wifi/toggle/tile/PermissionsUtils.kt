package com.vandzura.wifi.toggle.tile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionsUtils {

    fun checkPermissionToAccessWifiSSID(activity: MainActivity) {
        if (!hasPermissionToAccessWifiSSID(activity)) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }
    }

    fun hasPermissionToAccessWifiSSID(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= 26) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
