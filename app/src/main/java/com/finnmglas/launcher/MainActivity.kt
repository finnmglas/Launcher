package com.finnmglas.launcher

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs


var upApp = "org.mozilla.firefox"
var downApp = "com.samsung.android.app.galaxyfinder"
var rightApp = "com.samsung.android.email.provider"
var leftApp = "com.google.android.calendar"
var volumeUpApp = "com.whatsapp"
var volumeDownApp = "com.sec.android.app.popupcalculator"

class MainActivity : AppCompatActivity() {
    // get device dimensions
    val displayMetrics = DisplayMetrics()

    private fun getIntent(packageName: String) : Intent? {
        val pm = applicationContext.packageManager
        val intent:Intent? = pm.getLaunchIntentForPackage(packageName)
        intent?.addCategory(Intent.CATEGORY_LAUNCHER)
        return intent;
    }

    private fun launchApp(packageName: String, fallback: String = "") {
        val intent1 = getIntent(packageName)

        if(intent1!=null){
            applicationContext.startActivity(intent1)
            overridePendingTransition(0,0)
        } else {
            val intent2 = getIntent(fallback)

            if(intent2!=null){
                applicationContext.startActivity(intent2)
                overridePendingTransition(0,0)
            } else {
                Toast.makeText(this, "Package '$packageName' not found. Change your Settings.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchCalendar(v: View){ launchApp("com.google.android.calendar", "com.samsung.android.calendar") }
    fun launchClock(v: View){ launchApp("com.sec.android.app.clockpackage") }

    fun launchUpApp() { launchApp(upApp) }
    fun launchDownApp() { launchApp(downApp) }
    fun lauchLeftApp() { launchApp(leftApp) }
    fun lauchRightApp() { launchApp(rightApp) }

    fun lauchVolumeUpApp() { launchApp(volumeUpApp) }
    fun lauchVolumeDownApp() { launchApp(volumeDownApp) }

    /* Overrides */

    var touchX : Float = 0F
    var touchY : Float = 0F
    var touchT : Long = 0L

    override fun onTouchEvent(event: MotionEvent): Boolean {

        return when (MotionEventCompat.getActionMasked(event)) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
                touchT = System.currentTimeMillis()

                true
            }
            MotionEvent.ACTION_MOVE -> {
                true
            }
            MotionEvent.ACTION_UP -> {
                windowManager.defaultDisplay.getMetrics(displayMetrics)

                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels

                val diffX = touchX - event.x
                val diffY = touchY - event.y
                val diffT = System.currentTimeMillis() - touchT

                val strictness = 4 // of direction

                /* Decide for an action */

                if (diffY > height/8
                    && abs(diffY) > strictness * abs(diffX))
                    launchUpApp()

                // Only open if the swipe was not from the phone edge
                else if (diffY < -height/8
                    && abs(diffY) > strictness * abs(diffX)
                    && touchY > 100)
                    launchDownApp()

                else if (diffX > width/4
                    && abs(diffX) > strictness * abs(diffY))
                    lauchLeftApp()

                else if (diffX < -width/4
                    && abs(diffX) > strictness * abs(diffY))
                    lauchRightApp()

                // Open Settings on LongPress
                else if (abs(diffX) < 10
                    && abs(diffX) < 10 && diffT > 750){
                    startActivity(Intent(this, SettingsActivity::class.java))
                }

                true
            }
            MotionEvent.ACTION_CANCEL -> {
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) lauchVolumeUpApp()
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) lauchVolumeDownApp()
        return true
    }

    @SuppressLint("SetTextI18n") // I do not care
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
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
    }
}
