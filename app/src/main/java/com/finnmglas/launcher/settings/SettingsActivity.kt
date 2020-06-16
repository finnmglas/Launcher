package com.finnmglas.launcher.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.finnmglas.launcher.R
import com.finnmglas.launcher.extern.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_settings.*

var intendedSettingsPause = false // know when to close

class SettingsActivity : AppCompatActivity() {

    /** Activity Lifecycle functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(
            when (getSavedTheme(this)) {
                "dark" -> R.style.darkTheme
                "finn" -> R.style.finnmglasTheme
                else -> R.style.customTheme
            }
        )

        setContentView(R.layout.activity_settings)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sectionsPagerAdapter = SettingsSectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.activity_settings_view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.activity_settings_tabs)
        tabs.setupWithViewPager(viewPager)

        // As older APIs somehow do not recognize the xml defined onClick
        activity_settings_close.setOnClickListener() { finish() }
        // open device settings (see https://stackoverflow.com/a/62092663/12787264)
        activity_settings_device_settings.setOnClickListener {
            intendedSettingsPause = true
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    override fun onStart() {
        super.onStart()

        if (getSavedTheme(this) == "custom") {
            activity_settings_container.setBackgroundColor(dominantColor)
            activity_settings_app_bar.setBackgroundColor(dominantColor)

            activity_settings_device_settings.setTextColor(vibrantColor)
            activity_settings_close.setTextColor(vibrantColor)
            activity_settings_tabs.setSelectedTabIndicatorColor(vibrantColor)
        }
    }

    override fun onResume() {
        super.onResume()
        intendedSettingsPause = false
    }

    override fun onPause() {
        super.onPause()
        if (!intendedSettingsPause) finish()
    }

    fun backHome(view: View) { finish() }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHOOSE_APP -> {
                val value = data?.getStringExtra("value")
                val forApp = data?.getStringExtra("forApp") ?: return

                // Save the new App to Preferences
                val sharedPref = this.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE)

                val editor : SharedPreferences.Editor = sharedPref.edit()
                editor.putString("action_$forApp", value.toString())
                editor.apply()

                loadSettings(sharedPref)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


}
