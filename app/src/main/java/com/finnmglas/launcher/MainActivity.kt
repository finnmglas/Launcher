package com.finnmglas.launcher

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs

// App Launch Actions
var upApp = ""
var downApp = ""
var rightApp = ""
var leftApp = ""
var volumeUpApp = ""
var volumeDownApp = ""

class MainActivity : AppCompatActivity(),
GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener {


    private lateinit var mDetector: GestureDetectorCompat

    // get device dimensions
    val displayMetrics = DisplayMetrics()

    private fun getIntent(packageName: String): Intent? {
        val pm = applicationContext.packageManager
        val intent: Intent? = pm.getLaunchIntentForPackage(packageName)
        intent?.addCategory(Intent.CATEGORY_LAUNCHER)
        return intent
    }

    private fun launchApp(packageName: String, fallback: String = "") {
        val intent1 = getIntent(packageName)

        if (intent1 != null) {
            applicationContext.startActivity(intent1)
            overridePendingTransition(0, 0)
        } else {
            val intent2 = getIntent(fallback)

            if (intent2 != null) {
                applicationContext.startActivity(intent2)
                overridePendingTransition(0, 0)
            } else {
                Toast.makeText(
                    this,
                    "Package '$packageName' not found. Change your Settings.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun launchCalendar(v: View) {
        launchApp("com.google.android.calendar", "com.samsung.android.calendar")
    }

    fun launchClock(v: View) {
        launchApp("com.sec.android.app.clockpackage")
    }

    fun launchUpApp() {
        launchApp(upApp)
    }

    fun launchDownApp() {
        launchApp(downApp)
    }

    fun lauchLeftApp() {
        launchApp(leftApp)
    }

    fun lauchRightApp() {
        launchApp(rightApp)
    }

    fun lauchVolumeUpApp() {
        launchApp(volumeUpApp)
    }

    fun lauchVolumeDownApp() {
        launchApp(volumeDownApp)
    }

    /* Overrides */

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) lauchVolumeUpApp()
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) lauchVolumeDownApp()
        return true
    }

    @SuppressLint("SetTextI18n") // I do not care
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Preferences
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // First Startup
        if (!sharedPref.getBoolean("startedBefore", false))
            resetSettings(sharedPref)

        loadSettings(sharedPref)

        // Flags

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        fixedRateTimer("timer", false, 0L, 1000) {
            this@MainActivity.runOnUiThread {
                dateView.text = dateFormat.format(Date())
                timeView.text = timeFormat.format(Date()) // not " GMT"
            }
        }

        setContentView(R.layout.activity_main)

        mDetector = GestureDetectorCompat(this, this)
        mDetector.setOnDoubleTapListener(this)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onDown(event: MotionEvent): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        differenceX: Float,
        differenceY: Float
    ): Boolean {

        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val diffX = e1.x - e2.x
        val diffY = e1.y - e2.y

        val strictness = 4 // of direction

        /* Decide for an action */

        if (diffY > height / 8 && abs(diffY) > strictness * abs(diffX)) launchUpApp()
        // Only open if the swipe was not from the phone edge
        else if (diffY < -height / 8 && abs(diffY) > strictness * abs(diffX) && e1.y > 100) launchDownApp()
        else if (diffX > width / 4 && abs(diffX) > strictness * abs(diffY)) lauchLeftApp()
        else if (diffX < -width / 4 && abs(diffX) > strictness * abs(diffY)) lauchRightApp()

        return true
    }

    // Open Settings
    override fun onLongPress(event: MotionEvent) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        diffX: Float,
        diffY: Float
    ): Boolean {
        return true
    }

    override fun onShowPress(event: MotionEvent) {

    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {

        return true
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {

        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent): Boolean {

        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {

        return true
    }

}
