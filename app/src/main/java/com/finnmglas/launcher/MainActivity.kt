package com.finnmglas.launcher

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer


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
        val intent1 = getIntent(packageName);

        if(intent1!=null){
            applicationContext.startActivity(intent1)
            overridePendingTransition(0,0);
        } else {
            val intent2 = getIntent(fallback);

            if(intent2!=null){
                applicationContext.startActivity(intent2)
                overridePendingTransition(0,0);
            } else {
                Toast.makeText(this, "Package '$packageName' not found.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchInstagram(v: View){ launchApp("com.instagram.android") }
    fun launchWhatsapp(v: View){ launchApp("com.whatsapp") }

    fun launchFinder(v: View){ launchApp("com.samsung.android.app.galaxyfinder") }
    fun launchMail(v: View){ launchApp("com.samsung.android.email.provider") }
    fun launchCalendar(v: View){ launchApp("com.google.android.calendar") }
    fun launchClock(v: View){ launchApp("com.sec.android.app.clockpackage") }
    fun launchBrowser(v: View){ launchApp("org.mozilla.firefox") }

    fun launchUpApp() { launchBrowser(container) }
    fun launchDownApp() { launchFinder(container) }
    fun lauchLeftApp() { launchCalendar(container) }
    fun lauchRightApp() { launchMail(container) }

    // Overrides

    var touchX : Float = 0F
    var touchY : Float = 0F

    override fun onTouchEvent(event: MotionEvent): Boolean {

        return when (MotionEventCompat.getActionMasked(event)) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
                true
            }
            MotionEvent.ACTION_MOVE -> {
                true
            }
            MotionEvent.ACTION_UP -> {
                windowManager.defaultDisplay.getMetrics(displayMetrics)

                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels

                val diffX = touchX - event.x;
                val diffY = touchY - event.y;

                val strictness = 4 // of direction

                // Decide which one to open

                if (diffY > height/8
                    && abs(diffY) > strictness * abs(diffX))
                    launchUpApp()

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
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            val intent = Intent(this, ChooseActivity::class.java);
            startActivity(intent)
        }
        return true
    }

    @SuppressLint("SetTextI18n") // I do not care
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        //dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        //timeFormat.timeZone = TimeZone.getTimeZone("GMT")

        fixedRateTimer("timer", false, 0L, 1000) {
            this@MainActivity.runOnUiThread {
                dateView.text = dateFormat.format(Date())
                timeView.text = timeFormat.format(Date()) // not " GMT"
            }
        }

        setContentView(R.layout.activity_main)

    }
}
