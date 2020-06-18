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
import androidx.recyclerview.widget.RecyclerView
import com.finnmglas.launcher.list.apps.AppsRecyclerAdapter
import com.finnmglas.launcher.tutorial.TutorialActivity
import kotlinx.android.synthetic.main.home.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs

// used for the apps drawer / menu (ChooseActivity)
lateinit var viewAdapter: RecyclerView.Adapter<*>

class HomeActivity: UIObject, AppCompatActivity(),
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private var currentTheme = "" // keep track of theme changes

    /** Variables for this activity */
    private lateinit var mDetector: GestureDetectorCompat

    // get device dimensions
    private val displayMetrics = DisplayMetrics()

    // timers
    private var clockTimer = Timer()
    private var tooltipTimer = Timer()

    private var settingsIconShown = false

    /** Activity Lifecycle functions */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launcherPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        loadSettings(launcherPreferences)
        currentTheme = getSavedTheme(this)

        // TODO: Don't use actual themes, rather create them on the fly
        setTheme(
            when (getSavedTheme(this)) {
                "dark" -> R.style.darkTheme
                "finn" -> R.style.finnmglasTheme
                else -> R.style.customTheme
            }
        )
        setContentView(R.layout.home)
        setTheme()
        setOnClicks()

        // Load apps list first - speed up settings that way
        AsyncTask.execute { viewAdapter =
            AppsRecyclerAdapter(this, "", "")
        }

        // First Startup
        if (!launcherPreferences.getBoolean("startedBefore", false)){
            startActivity(Intent(this, TutorialActivity::class.java))
            tooltipTimer.cancel()
        }
    }

    override fun onStart(){
        super<AppCompatActivity>.onStart()
        super<UIObject>.onStart()

        mDetector = GestureDetectorCompat(this, this)
        mDetector.setOnDoubleTapListener(this)

        windowManager.defaultDisplay.getMetrics(displayMetrics)

        // for if the settings changed
        loadSettings(launcherPreferences)
    }

    override fun onResume() {
        super.onResume()

        // TODO: do this immediately after changing preferences
        if (currentTheme != getSavedTheme(this)) recreate()
        if (home_background_image != null && getSavedTheme(
                this
            ) == "custom")
            home_background_image.setImageBitmap(background)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        clockTimer = fixedRateTimer("clockTimer", true, 0L, 100) {
            this@HomeActivity.runOnUiThread {
                val t = timeFormat.format(Date())
                if (home_time_view.text != t)
                    home_time_view.text = t

                val d = dateFormat.format(Date())
                if (home_date_view.text != d)
                    home_date_view.text = d
            }
        }
    }

    override fun onPause() {
        super.onPause()
        clockTimer.cancel()
    }

    /** Touch- and Key-related functions to start activities */

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { if (settingsIconShown) hideSettingsIcon() }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            launch(volumeUpApp, this)
            overridePendingTransition(0, 0)
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            launch(volumeDownApp, this)
            overridePendingTransition(0, 0)
        }
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean {

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val diffX = e1.x - e2.x
        val diffY = e1.y - e2.y

        val strictness = 4 // how distinguished the swipe has to be to be accepted

        // Only open if the swipe was not from the phones top edge
        if (diffY < -height / 8 && abs(diffY) > strictness * abs(diffX) && e1.y > 100) {
            launch(downApp,this)
            overridePendingTransition(R.anim.top_down, android.R.anim.fade_out)
        }
        else if (diffY > height / 8 && abs(diffY) > strictness * abs(diffX)) {
            launch(upApp, this)
            overridePendingTransition(R.anim.bottom_up, android.R.anim.fade_out)
        }
        else if (diffX > width / 4 && abs(diffX) > strictness * abs(diffY)) {
            launch(leftApp,this)
            overridePendingTransition(R.anim.right_left, android.R.anim.fade_out)
        }
        else if (diffX < -width / 4 && abs(diffX) > strictness * abs(diffY)) {
            launch(rightApp, this)
            overridePendingTransition(R.anim.left_right, android.R.anim.fade_out)
        }

        return true
    }

    override fun onLongPress(event: MotionEvent) {
        launch(longClickApp, this)
        overridePendingTransition(0, 0)
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        launch(doubleClickApp, this)
        overridePendingTransition(0, 0)
        return false
    }

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
        home_settings_icon.fadeRotateIn()
        home_settings_icon.visibility = View.VISIBLE
        settingsIconShown = true

        tooltipTimer = fixedRateTimer("tooltipTimer", true, 10000, 1000) {
            this@HomeActivity.runOnUiThread { hideSettingsIcon() }
        }
    }

    private fun hideSettingsIcon(){
        tooltipTimer.cancel()
        home_settings_icon.fadeRotateOut()
        home_settings_icon.visibility = View.INVISIBLE
        settingsIconShown = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) { false } else { super.onTouchEvent(event) }
    }

    override fun setTheme() {
        // Start by showing the settings icon
        showSettingsIcon()

        if (currentTheme == "custom") {
            home_settings_icon.setTextColor(vibrantColor)
            try {
                background = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(
                    launcherPreferences.getString("background_uri", "")))
            } catch (e: Exception) {  }

            if (background == null)
                currentTheme = saveTheme(this, "finn")
        }
    }

    override fun setOnClicks() {
        home_settings_icon.setOnClickListener() {
            openSettings(this)
            overridePendingTransition(R.anim.bottom_up, android.R.anim.fade_out)
        }

        home_date_view.setOnClickListener() {
            launch(calendarApp, this)
            overridePendingTransition(0, 0)
        }

        home_time_view.setOnClickListener() {
            launch(clockApp,this)
            overridePendingTransition(0, 0)
        }
    }

    /* TODO: Remove those. For now they are necessary
     *  because this inherits from GestureDetector.OnGestureListener */
    override fun onDoubleTapEvent(event: MotionEvent): Boolean { return false }
    override fun onDown(event: MotionEvent): Boolean { return false }
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean { return false }
    override fun onShowPress(event: MotionEvent) {}
    override fun onSingleTapUp(event: MotionEvent): Boolean { return false }
}
