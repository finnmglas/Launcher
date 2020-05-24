package com.finnmglas.launcher

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.finnmglas.launcher.extern.*
import com.finnmglas.launcher.settings.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.fragment_settings_apps.*
import kotlinx.android.synthetic.main.fragment_settings_theme.*


class SettingsActivity : AppCompatActivity() {

    /** Activity Lifecycle functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(
            when (getSavedTheme(this)) {
                "dark" -> R.style.darkTheme
                "finn" -> R.style.finnmglasTheme
                else -> R.style.finnmglasTheme
            }
        )

        setContentView(R.layout.activity_settings)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.activity_settings_view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.activity_settings_tabs)
        tabs.setupWithViewPager(viewPager)

        // As older APIs somehow do not recognize the xml defined onClick
        activity_settings_close.setOnClickListener() { finish() }
        activity_settings_device_settings.setOnClickListener {
            startActivityForResult(Intent(android.provider.Settings.ACTION_SETTINGS), 0)
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

    fun backHome(view: View) { finish() }

    /** Theme - related */


}
