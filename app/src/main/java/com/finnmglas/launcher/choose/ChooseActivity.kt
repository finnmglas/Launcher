package com.finnmglas.launcher.choose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.finnmglas.launcher.R
import com.finnmglas.launcher.extern.*
import com.finnmglas.launcher.settings.intendedSettingsPause
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_choose.*

var intendedChoosePause = false // know when to close

// TODO: Better solution for this (used in choose-fragments)
var action = "view"
var forApp = ""


class ChooseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setTheme(
            when (getSavedTheme(this)) {
                "dark" -> R.style.darkTheme
                "finn" -> R.style.finnmglasTheme
                else -> R.style.finnmglasTheme
            }
        )
        setContentView(R.layout.activity_choose)

        if (getSavedTheme(this) == "custom") {
            activity_choose_container.setBackgroundColor(dominantColor)
            activity_choose_app_bar.setBackgroundColor(dominantColor)
            activity_choose_close.setTextColor(vibrantColor)
        }

        // As older APIs somehow do not recognize the xml defined onClick
        activity_choose_close.setOnClickListener() { finish() }

        // get info about which action this activity is open for
        val bundle = intent.extras
        if (bundle != null) {
            action = bundle.getString("action")!! // why choose an app
            if (action != "view")
                forApp = bundle.getString("forApp")!! // which app we choose
        }

        // Hide tabs for the "view" action
        if (action == "view") {
            activity_choose_tabs.visibility = View.GONE
        }

        when (action) {
            "view" -> activity_choose_heading.text = getString(R.string.choose_title_view)
            "pick" -> activity_choose_heading.text = getString(R.string.choose_title)
        }

        val sectionsPagerAdapter = ChooseSectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.activity_choose_view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.activity_choose_tabs)
        tabs.setupWithViewPager(viewPager)
    }

    override fun onPause() {
        super.onPause()
        intendedSettingsPause = false
        if(!intendedChoosePause) finish()
    }

    override fun onResume() {
        super.onResume()
        intendedChoosePause = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UNINSTALL) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, getString(R.string.choose_removed_toast), Toast.LENGTH_LONG).show()
                finish()
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                Toast.makeText(this, getString(R.string.choose_not_removed_toast), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    /** onClick functions */

    fun backHome(view: View) { finish() }

}
