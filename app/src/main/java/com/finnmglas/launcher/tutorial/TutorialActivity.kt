package com.finnmglas.launcher.tutorial

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.finnmglas.launcher.*
import kotlinx.android.synthetic.main.tutorial.*

/**
 * The [TutorialActivity] is displayed automatically on new installations.
 * It can also be opened from Settings.
 *
 * It tells the user about the concept behind launcher
 * and helps with the setup process (on new installations)
 */
class TutorialActivity: AppCompatActivity(), UIObject {

    private var menuNumber = 0
    private var defaultApps = mutableListOf<String>()
    private var isFirstTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise layout
        setContentView(R.layout.tutorial)
        loadMenu(this)

        // Check if it is the first time starting the app
        isFirstTime = !launcherPreferences.getBoolean("startedBefore", false)
        if (isFirstTime)
            defaultApps = resetSettings(this) // UP, DOWN, RIGHT, LEFT, VOLUME_UP, VOLUME_DOWN
        else tutorial_appbar.visibility = View.VISIBLE
    }

    override fun onStart() {
        super<AppCompatActivity>.onStart()
        super<UIObject>.onStart()
    }

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

    private fun loadMenu(context: Context) { // Context needed for packageManager
        val intro = resources.getStringArray(R.array.intro)

        if (menuNumber < intro.size){
            val entry = intro[menuNumber].split("|").toTypedArray() //heading|infoText|hintText|size

            tutorial_page_heading.text = entry[0]
            if (entry[4] == "1" && isFirstTime)
                tutorial_page_text.text = String.format(entry[1],
                defaultApps[0], defaultApps[1], defaultApps[2], defaultApps[3], defaultApps[4], defaultApps[5])
            else if (entry[4] == "1" && !isFirstTime)
                tutorial_page_text.text = String.format(entry[1],
                "-", "-", "-", "-", "-", "-")
            else tutorial_page_text.text = entry[1]
            tutorial_page_hint.text = entry[2]
            tutorial_page_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, entry[3].toFloat())

        } else { // End intro
            if (isFirstTime){
                launcherPreferences.edit()
                    .putBoolean("startedBefore", true) // never auto run this again
                    .putLong("firstStartup", System.currentTimeMillis() / 1000L) // record first startup timestamp
                    .apply()
            }
            finish()
        }
    }

    override fun setTheme() {
        if (getSavedTheme(this) == "custom") {
            tutorial_appbar.setBackgroundColor(dominantColor)
            tutorial_container.setBackgroundColor(dominantColor)
            tutorial_close.setTextColor(vibrantColor)
        }

        tutorial_page_hint.blink() // animate
    }

    override fun setOnClicks() {
        tutorial_close.setOnClickListener() { finish() }
    }
}
