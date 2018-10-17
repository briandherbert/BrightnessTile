package com.example.brianherbert.brightnesstile

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

class BrightService : TileService() {
    val TAG = "BrightService"

    override fun onTileAdded() {
        super.onTileAdded()
        Log.v(TAG, "on tile added");

        updateState(false)
    }

    override fun onStartListening() {
        super.onStartListening()
        Log.v(TAG, "start listening");

    }

    override fun onClick() {
        super.onClick()
        Log.v(TAG, "onclick");

        var lvl = android.provider.Settings.System.getInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS)
        val isBright = lvl > 200;
        updateState(isBright)
    }

    fun updateState(isBright : Boolean) {
        Log.v(TAG, "update state ");

        var brightness = 255
        if (isBright) {
            brightness = getSharedPreferences(packageName, Context.MODE_PRIVATE).getInt(PermissionActivity.PREF_BRIGHTNESS, brightness)
        }

        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        qsTile.icon = Icon.createWithResource(this, if (isBright) R.drawable.ic_brightness_low_24dp else R.drawable.ic_brightness_full_24dp)
        qsTile.updateTile()
    }
}