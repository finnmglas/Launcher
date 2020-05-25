package com.finnmglas.launcher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.finnmglas.launcher.extern.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs


class MainActivity : AppCompatActivity(),
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private var currentTheme = "" // keep track of theme changes

    /** Variables for this activity */
    private lateinit var mDetector: GestureDetectorCompat

    // get device dimensions
    private val displayMetrics = DisplayMetrics()

    // timers
    private var clockTimer = Timer()
    private var tooltipTimer = Timer()
    private var loadAppsTimer = Timer()

    private var settingsIconShown = false

    /** Activity Lifecycle functions */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Preferences
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // Flags
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        currentTheme = getSavedTheme(this)

        if (currentTheme == "custom") {
            try {
                background = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(sharedPref.getString("background_uri", "")))
            } catch (e: Exception) {  }

            if (background == null)
                currentTheme = saveTheme(this, "finn")
        }

        setTheme(
            when (currentTheme) {
                "dark" -> R.style.darkTheme
                "finn" -> R.style.finnmglasTheme
                else -> R.style.customTheme
            }
        )
        setContentView(R.layout.activity_main)

        // Start by showing the settings icon
        showSettingsIcon()

        // As older APIs somehow do not recognize the xml defined onClick
        activity_main_settings_icon.setOnClickListener() { openSettings() }

        // First Startup
        if (!sharedPref.getBoolean("startedBefore", false)){
            startActivity(Intent(this, FirstStartupActivity::class.java))
            tooltipTimer.cancel()
        }
    }

    override fun onStart(){
        super.onStart()

        // Preferences
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        loadSettings(sharedPref)

        if (currentTheme == "custom") {
            activity_main_settings_icon.setTextColor(vibrantColor)
        }

        mDetector = GestureDetectorCompat(this, this)
        mDetector.setOnDoubleTapListener(this)

        windowManager.defaultDisplay.getMetrics(displayMetrics)
    }

    override fun onResume() {
        super.onResume()

        // TODO: do this immediately after changing preferences
        if (currentTheme != getSavedTheme(this)) recreate()
        if (activity_main_background_image != null && getSavedTheme(this) == "custom")
            activity_main_background_image.setImageBitmap(background)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        clockTimer = fixedRateTimer("clockTimer", true, 0L, 100) {
            this@MainActivity.runOnUiThread {
                val t = timeFormat.format(Date())
                if (activity_main_time_view.text != t)
                    activity_main_time_view.text = t

                val d = dateFormat.format(Date())
                if (activity_main_date_view.text != d)
                    activity_main_date_view.text = d
            }
        }

        val pm = packageManager

        loadAppsTimer = fixedRateTimer("loadAppsTimer", true, 0L, 30000) {
            AsyncTask.execute { updateAppList(pm) }
        }
    }

    override fun onPause() {
        super.onPause()
        clockTimer.cancel()
        loadAppsTimer.cancel()
    }

    private fun openSettings(){
        startActivity(Intent(this, SettingsActivity::class.java))
        overridePendingTransition(R.anim.bottom_up, android.R.anim.fade_out)
    }

    /** Touch- and Key-related functions to start activities */

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { if (settingsIconShown) hideSettingsIcon() }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) launchApp(volumeUpApp, this)
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) launchApp(volumeDownApp, this)
        return true
    }

    fun dateViewOnTouch(v: View) { launchApp(calendarApp, this) }
    fun timeViewOnTouch(v: View) { launchApp(clockApp, this) }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean {

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val diffX = e1.x - e2.x
        val diffY = e1.y - e2.y

        val strictness = 4 // how distinguished the swipe has to be to be accepted

        // Only open if the swipe was not from the phones top edge
        if (diffY < -height / 8 && abs(diffY) > strictness * abs(diffX) && e1.y > 100) launchApp(downApp, this)
        else if (diffY > height / 8 && abs(diffY) > strictness * abs(diffX)) launchApp(upApp, this)
        else if (diffX > width / 4 && abs(diffX) > strictness * abs(diffY)) launchApp(leftApp, this)
        else if (diffX < -width / 4 && abs(diffX) > strictness * abs(diffY)) launchApp(rightApp, this)

        return true
    }

    override fun onLongPress(event: MotionEvent) { openSettings() }

    // Tooltip
    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        when(settingsIconShown) {
            true -> {
                hideSettingsIcon()
            }
            false -> showSettingsIcon()
        }
        return false
    }

    private fun showSettingsIcon(){
        activity_main_settings_icon.fadeRotateIn()
        activity_main_settings_icon.visibility = View.VISIBLE
        settingsIconShown = true

        tooltipTimer = fixedRateTimer("tooltipTimer", true, 10000, 1000) {
            this@MainActivity.runOnUiThread { hideSettingsIcon() }
        }
    }

    private fun hideSettingsIcon(){
        tooltipTimer.cancel()
        activity_main_settings_icon.fadeRotateOut()
        activity_main_settings_icon.visibility = View.INVISIBLE
        settingsIconShown = false
    }

    fun settingsIconOnTouch(view: View){ openSettings() }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) { false } else { super.onTouchEvent(event) }
    }

    /* TODO: Remove those. For now they are necessary
     *  because this inherits from GestureDetector.OnGestureListener */
    override fun onDoubleTap(event: MotionEvent): Boolean { return false }
    override fun onDoubleTapEvent(event: MotionEvent): Boolean { return false }
    override fun onDown(event: MotionEvent): Boolean { return false }
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean { return false }
    override fun onShowPress(event: MotionEvent) {}
    override fun onSingleTapUp(event: MotionEvent): Boolean { return false }
}
