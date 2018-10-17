package com.example.brianherbert.brightnesstile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.SeekBar

class PermissionActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    private val TAG = "PermissionActivity"
    private val SYSTEM_WRITE_REQ_CODE = 102

    companion object {
        final val PREF_BRIGHTNESS = "brightness"
    }

    var brightness = 255

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setupPermissions()
        Log.v(TAG, "starting")

        var bar = findViewById<SeekBar>(R.id.seekbar)
        bar.setOnSeekBarChangeListener(this)

        var curBrightness = android.provider.Settings.System.getInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS)
        var seekVal = curBrightness.toDouble() / 255.0 * 100
        bar.setProgress(seekVal.toInt())

        findViewById<View>(R.id.btn_ok).setOnClickListener(View.OnClickListener { view -> saveBrightness() })
    }

    fun saveBrightness() {
        Log.v(TAG, "Save brightness " + brightness)
        getSharedPreferences(packageName, Context.MODE_PRIVATE).edit().putInt(PREF_BRIGHTNESS, brightness).commit()
    }

    private fun setupPermissions() {
        val canWrite = Settings.System.canWrite(this)
        if (!canWrite) {
            intent = Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.setData(Uri.parse("package:" + packageName))
            startActivityForResult(intent, SYSTEM_WRITE_REQ_CODE)
        }
    }

    override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            brightness = (progress.toDouble() * 2.55).toInt()
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }
}