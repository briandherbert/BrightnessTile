package com.example.brianherbert.brightnesstile

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

class BrightService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        log("start listening, brightness is " + getCurrentBrightness(contentResolver))
        updateIcon(isBright(contentResolver, getSharedPrefs()))
    }

    override fun onClick() {
        super.onClick()
        toggleState()
    }

    companion object {
        fun getCurrentBrightness(contentResolver: ContentResolver): Int {
            return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        }

        fun setCurrentBrightness(contentResolver: ContentResolver, brightness: Int) {
            log("Setting brightness to " + brightness)
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        }

        fun isBright(contentResolver: ContentResolver, sharedPreferences: SharedPreferences?): Boolean {
            var maxBrightness = MAX_BRIGHTNESS

            sharedPreferences?.let { maxBrightness = getMaxBrightness(sharedPreferences) }
            var currBrightness = getCurrentBrightness(contentResolver)

            var isBright = currBrightness > maxBrightness - 10;
            log("is bright? ${isBright} curr " + currBrightness + " max " + maxBrightness)

            return isBright
        }

        fun getDimness(sharedPreferences: SharedPreferences): Int {
            return sharedPreferences.getInt(PREF_DIMNESS, MAX_BRIGHTNESS)
        }

        fun setDimness(sharedPreferences: SharedPreferences, dimness: Int) {
            sharedPreferences.edit().putInt(PREF_DIMNESS, dimness).commit()
        }

        fun getMaxBrightness(sharedPreferences: SharedPreferences): Int {
            return sharedPreferences.getInt(PREF_MAX_BRIGHTNESS, MAX_BRIGHTNESS)
        }

        fun setMaxBrightness(sharedPreferences: SharedPreferences, maxBright: Int) {
            log( "Setting max brightness to " + maxBright)
            sharedPreferences.edit().putInt(PREF_MAX_BRIGHTNESS, maxBright).commit()
        }

        fun log(s:String) {
            if (VERBOSE) {
                Log.v(TAG, s)
            }
        }

        var MAX_BRIGHTNESS = 255;
        val PREF_DIMNESS = "brightness"
        val PREF_MAX_BRIGHTNESS = "maxbright"

        val TAG = "BrightOps"
        val VERBOSE = false
    }

    fun updateState(isBright : Boolean) {
        log("update state ");

        var brightness = 255
        if (isBright) {
            var sharedPrefs = getSharedPreferences(packageName, Context.MODE_PRIVATE)
            brightness = getDimness(sharedPrefs)
        }

        setCurrentBrightness(contentResolver, brightness)
        qsTile.icon = Icon.createWithResource(this, if (isBright) R.drawable.ic_brightness_low_24dp else R.drawable.ic_brightness_full_24dp)
        qsTile.updateTile()
    }

    fun getSharedPrefs() : SharedPreferences {
        return getSharedPreferences(packageName, Context.MODE_PRIVATE)
    }

    fun toggleState() {
        log("toggle state");

        var brightness = MAX_BRIGHTNESS
        var sharedPrefs = getSharedPrefs()
        var isBright = isBright(contentResolver, sharedPrefs)

        if (isBright) {
            brightness = getDimness(sharedPrefs)
            log("Dimness is ${brightness}")
        }

        setCurrentBrightness(contentResolver, brightness)
        updateIcon(!isBright)
    }

    fun updateIcon(isBright: Boolean) {
        qsTile.icon = Icon.createWithResource(this, if (isBright) R.drawable.ic_brightness_full_24dp else R.drawable.ic_brightness_low_24dp)
        qsTile.updateTile()
    }
}