package com.finnmglas.launcher

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.finnmglas.launcher.extern.*
import kotlinx.android.synthetic.main.activity_firststartup.*


class FirstStartupActivity : AppCompatActivity(){

    /** Variables for this activity */

    private var menuNumber = 0
    private var defaultApps = mutableListOf<String>()
    private var isFirstTime = false

    /** Activity Lifecycle functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Flags
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setTheme(
            when (getSavedTheme(this)) {
                "dark" -> R.style.darkTheme
                "finn" -> R.style.finnmglasTheme
                else -> R.style.finnmglasTheme
            }
        )
        setContentView(R.layout.activity_firststartup)

        if (getSavedTheme(this) == "custom") {
            activity_firststartup_app_bar.setBackgroundColor(dominantColor)
            activity_firststartup_container.setBackgroundColor(dominantColor)
            activity_firststartup_close.setTextColor(vibrantColor)
        }

        activity_firststartup_hint_text.blink() // animate
        loadMenu(this)

        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        isFirstTime = !sharedPref.getBoolean("startedBefore", false)

        if (isFirstTime)
            defaultApps = resetSettings(sharedPref, this) // UP, DOWN, RIGHT, LEFT, VOLUME_UP, VOLUME_DOWN
        else
            activity_firststartup_app_bar.visibility = View.VISIBLE

        // As older APIs somehow do not recognize the xml defined onClick
        activity_firststartup_close.setOnClickListener() { finish() }
    }

    /** Touch- and Key-related functions to navigate */

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            menuNumber++
            loadMenu(this)
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_BACK){
            menuNumber--

            // prevent negative indices
            if (menuNumber < 0) menuNumber = 0

            loadMenu(this)
        }
        return true
    }

    fun clickAnywhere(view: View){
        menuNumber++
        loadMenu(this)
    }

    fun backToSettings(view: View){
        finish()
    }

    /** Touch- and Key-related functions to navigate */

    private fun loadMenu(context :Context) { // Context needed for packageManager

        val intro = resources.getStringArray(R.array.intro)

        if (menuNumber < intro.size){
            val entry = intro[menuNumber].split("|").toTypedArray() //heading|infoText|hintText|size

            activity_firststartup_section_heading.text = entry[0]
            if (entry[4] == "1" && isFirstTime)
                activity_firststartup_descriptive_text.text = String.format(entry[1],
                defaultApps[0], defaultApps[1], defaultApps[2], defaultApps[3], defaultApps[4], defaultApps[5])
            else if (entry[4] == "1" && !isFirstTime)
                activity_firststartup_descriptive_text.text = String.format(entry[1],
                "-", "-", "-", "-", "-", "-")
            else activity_firststartup_descriptive_text.text = entry[1]
            activity_firststartup_hint_text.text = entry[2]
            activity_firststartup_descriptive_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, entry[3].toFloat())

        } else { // End intro
            if (isFirstTime){
                val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putBoolean("startedBefore", true) // never auto run this again
                editor.putLong("firstStartup", System.currentTimeMillis() / 1000L) // record first startup timestamp
                editor.apply()
            }
            finish()
        }
    }
}
