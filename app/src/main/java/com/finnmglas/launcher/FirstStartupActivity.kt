package com.finnmglas.launcher

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_firststartup.*

// Taken from https://stackoverflow.com/questions/47293269
fun View.blink(
    times: Int = Animation.INFINITE,
    duration: Long = 1000L,
    offset: Long = 20L,
    minAlpha: Float = 0.2f,
    maxAlpha: Float = 1.0f,
    repeatMode: Int = Animation.REVERSE
) {
    startAnimation(AlphaAnimation(minAlpha, maxAlpha).also {
        it.duration = duration
        it.startOffset = offset
        it.repeatMode = repeatMode
        it.repeatCount = times
    })
}

class FirstStartupActivity : AppCompatActivity(){

    var menuNumber = 0
    var defaultApps = mutableListOf<String>()

    /* Overrides */

    @SuppressLint("SetTextI18n") // I do not care
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Flags
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_firststartup)

        continue_text.blink() // animate
        loadMenu(this)
    }

    fun clickAnywhere(view: View){
        menuNumber++
        loadMenu(this)
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

    @SuppressLint("SetTextI18n") // I don't care! (Yet)
    fun loadMenu(context :Context) { // Context needed for packageManager

        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // Intro
        if (menuNumber == 0){
            heading.text = ""
            description.text = "Take a few seconds to learn how to use this Launcher!\n\n"
            continue_text.text = "-- Tap anywhere to continue --"

            defaultApps = resetSettings(sharedPref, context) // UP, DOWN, RIGHT, LEFT, VOLUME_UP, VOLUME_DOWN
        }
        // Concept
        else if (menuNumber == 1){
            heading.text = "Concept"
            description.text = "It is designed to be minimal, efficient and free of distraction."
        }
        else if (menuNumber == 2){
            heading.text = "Concept"
            description.text = "It is free of payments, ads and tracking services."
            continue_text.text = "-- Tap anywhere to continue --"
        }
        // Usage
        else if (menuNumber == 3){
            heading.text = "Usage"
            description.text = "Your home screen contains the local date and time. No distraction."
            continue_text.text = "-- Use volume keys to navigate --"
        }
        else if (menuNumber == 4){
            heading.text = "Usage"
            description.text = "You can open your apps with a single swipe or button press."
        }
        // Setup
        else if (menuNumber == 5){
            heading.text = "Setup"
            description.setTextSize(TypedValue.COMPLEX_UNIT_SP,36F)
            description.text = "We have set up some default actions for you..."
        }
        else if (menuNumber == 6){
            description.setTextSize(TypedValue.COMPLEX_UNIT_SP,18F)
            description.text = "Swipe Up: Open a Browser (" + defaultApps[0] + ")\n\n" +
                    "Swipe Down: Open internal Search App (" + defaultApps[1] + ")\n\n" +
                    "Swipe Right: Open Mail (" + defaultApps[2] + ")\n\n" +
                    "Swipe Left: Open Calendar (" + defaultApps[3] + ")\n\n" +
                    "Volume Up: Open a messenger (" + defaultApps[4] + ")\n\n" +
                    "Volume Down: Open Utilities (" + defaultApps[5] + ")"
        }
        else if (menuNumber == 7){
            heading.text = "Setup"
            description.setTextSize(TypedValue.COMPLEX_UNIT_SP,36F)
            description.text = "You can choose your own apps:\n\nOpen settings by tapping and holding the home screen."
            continue_text.text = "-- Use volume keys to navigate --"
        }
        else if (menuNumber == 8){
            heading.text = ""
            description.text = "You are ready to get started!\n\n I hope this provides great value to you!\n\n- Finn M Glas\n\n"
            continue_text.text = "-- Launcher by Finn M Glas --"
        }
        // End Intro
        else {
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean("startedBefore", true) // never run this again
            editor.putLong("firstStartup", System.currentTimeMillis() / 1000L) // record first startup timestamp
            editor.apply()

            finish()
        }

    }
}
