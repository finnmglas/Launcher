package com.finnmglas.launcher

import android.content.Context
import android.content.Intent
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

/** Variables for all of the app */
var upApp = ""
var downApp = ""
var rightApp = ""
var leftApp = ""
var volumeUpApp = ""
var volumeDownApp = ""

var calendarApp = ""
var clockApp = ""

class MainActivity : AppCompatActivity(),
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    /** Variables for this activity */
    private lateinit var mDetector: GestureDetectorCompat

    // get device dimensions
    private val displayMetrics = DisplayMetrics()
    private var clockTimer = Timer();

    /** Activity Lifecycle functions */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Preferences
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // First Startup
        if (!sharedPref.getBoolean("startedBefore", false))
            startActivity(Intent(this, FirstStartupActivity::class.java))

        // Flags
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_main)
    }

    override fun onStart(){
        super.onStart()

        // Preferences
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        loadSettings(sharedPref)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        clockTimer = fixedRateTimer("timer", true, 0L, 1000) {
            this@MainActivity.runOnUiThread {
                dateView.text = dateFormat.format(Date())
                timeView.text = timeFormat.format(Date())
            }
        }

        mDetector = GestureDetectorCompat(this, this)
        mDetector.setOnDoubleTapListener(this)
    }

    /** Touch- and Key-related functions to start activities */

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) launchApp(volumeUpApp, this)
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) launchApp(volumeDownApp, this)
        return true
    }

    fun dateViewOnTouch(v: View) { launchApp(calendarApp, this) }
    fun timeViewOnTouch(v: View) { launchApp(clockApp, this) }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean {

        windowManager.defaultDisplay.getMetrics(displayMetrics)
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

    // Open Settings Activity
    override fun onLongPress(event: MotionEvent) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) { true } else { super.onTouchEvent(event) }
    }

    /* TODO: Remove those. For now they are necessary
     *  because this inherits from GestureDetector.OnGestureListener */
    override fun onDoubleTap(event: MotionEvent): Boolean { return true }
    override fun onDoubleTapEvent(event: MotionEvent): Boolean { return true }
    override fun onDown(event: MotionEvent): Boolean { return true }
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean { return true }
    override fun onShowPress(event: MotionEvent) {}
    override fun onSingleTapUp(event: MotionEvent): Boolean { return true }
    override fun onSingleTapConfirmed(event: MotionEvent): Boolean { return true }
}
