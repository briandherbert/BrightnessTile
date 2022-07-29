package com.example.brianherbert.brightnesstile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView

class PermissionActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    private val TAG = "PermissionActivity"

    var brightness = 255
    var REQ_CODE = 1
    var VERBOSE = true

    lateinit var SHARED_PREFS : SharedPreferences;

    lateinit var lblDimness : TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setupPermissions()

        var bar = findViewById<SeekBar>(R.id.seekbar)
        bar.setOnSeekBarChangeListener(this)

        var curBrightness = android.provider.Settings.System.getInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS)
        var seekVal = curBrightness.toDouble() / 255.0 * 100
        bar.setProgress(seekVal.toInt())

        BrightService.log("starting, curr brightness is " + curBrightness.toString())

        findViewById<View>(R.id.btn_ok).setOnClickListener(View.OnClickListener { saveBrightness() })

        lblDimness = findViewById(R.id.lbl_dimness)

        SHARED_PREFS = getSharedPreferences(packageName, Context.MODE_PRIVATE);
    }

    fun saveBrightness() {
        BrightService.log("Save brightness " + brightness)
        BrightService.setDimness(SHARED_PREFS, brightness)
    }

    private fun setupPermissions() {
        val canWrite = Settings.System.canWrite(this)
        if (!canWrite) {
            intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.setData(Uri.parse("package:" + packageName))
            startActivityForResult(intent, REQ_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("Got result back, request code " + REQ_CODE + " result " + resultCode.toString())
        // Check which request we're responding to
        if (requestCode == REQ_CODE) {
            // This is beat, but we need to make sure the OS is properly reporting max brightness, unlike Pixel 6a
            BrightService.log("Setting max brightness")
            BrightService.setCurrentBrightness(contentResolver, BrightService.MAX_BRIGHTNESS)
            var maxBright = BrightService.getCurrentBrightness(contentResolver)
            BrightService.log("max brightness is " + maxBright.toString())
            Thread.sleep(1_000)
            maxBright = BrightService.getCurrentBrightness(contentResolver)
            BrightService.log("max brightness is now " + BrightService.getCurrentBrightness(contentResolver))
            BrightService.setMaxBrightness(SHARED_PREFS, maxBright)
        }
    }

    fun log(s: String) {
        if (VERBOSE) {
            BrightService.log(s)
        }
    }

    override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            brightness = (progress.toDouble() * 2.55).toInt()
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
            lblDimness.text = "Set dimness to " + brightness
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }
}