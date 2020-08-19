package com.finnmglas.launcher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.finnmglas.launcher.tutorial.TutorialActivity
import kotlinx.android.synthetic.main.home.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs
import com.finnmglas.launcher.BuildConfig.VERSION_NAME

/**
 * [HomeActivity] is the actual application Launcher,
 * what makes this application special / unique.
 *
 * In this activity we display the date and time,
 * and we listen for actions like tapping, swiping or button presses.
 *
 * As it also is the first thing that is started when someone opens Launcher,
 * it also contains some logic related to the overall application:
 * - Setting global variables (preferences etc.)
 * - Opening the [TutorialActivity] on new installations
 */
class HomeActivity: UIObject, AppCompatActivity(),
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private lateinit var mDetector: GestureDetectorCompat

    // timers
    private var clockTimer = Timer()
    private var tooltipTimer = Timer()

    private var settingsIconShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise globals
        launcherPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        windowManager.defaultDisplay.getMetrics(displayMetrics)

        loadSettings()

        // First time opening the app: show Tutorial, else: check versions
        if (!launcherPreferences.getBoolean(PREF_STARTED, false))
            startActivity(Intent(this, TutorialActivity::class.java))
        else when (launcherPreferences.getString(PREF_VERSION, "")) {
            // Check versions, make sure transitions between versions go well

            VERSION_NAME -> { /* the version installed and used previously are the same */ }
            "" -> { /* The version used before was pre- v1.3.0,
                        as version tracking started then */

                /*
                 * before, the dominant and vibrant color of the `finn` and `dark` theme
                 * were not stored anywhere. Now they have to be stored:
                 * -> we just reset them using newly implemented functions
                 */
                when (getSavedTheme(this)) {
                    "finn" -> resetToDefaultTheme(this)
                    "dark" -> resetToDarkTheme(this)
                }

                launcherPreferences.edit()
                    .putString(PREF_VERSION, VERSION_NAME) // save new version
                    .apply()

                // show the new tutorial
                startActivity(Intent(this, TutorialActivity::class.java))
            }
        }

        // Preload apps to speed up the Apps Recycler
        AsyncTask.execute { loadApps(packageManager) }

        // Initialise layout
        setContentView(R.layout.home)
    }

    override fun onStart(){
        super<AppCompatActivity>.onStart()

        mDetector = GestureDetectorCompat(this, this)
        mDetector.setOnDoubleTapListener(this)

        // for if the settings changed
        loadSettings()
        super<UIObject>.onStart()
    }

    override fun onResume() {
        super.onResume()

        if (home_background_image != null && getSavedTheme(this) == "custom")
            home_background_image.setImageBitmap(background)

        // Applying the date / time format (changeable in settings)
        val dFormat = launcherPreferences.getInt(PREF_DATE_FORMAT, 0)
        val upperFMT = resources.getStringArray(R.array.settings_launcher_time_formats_upper)
        val lowerFMT = resources.getStringArray(R.array.settings_launcher_time_formats_lower)

        val dateFormat = SimpleDateFormat(upperFMT[dFormat], Locale.getDefault())
        val timeFormat = SimpleDateFormat(lowerFMT[dFormat], Locale.getDefault())

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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { if (settingsIconShown) hideSettingsIcon() }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
            launch(volumeUpApp, this,0, 0)
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            launch(volumeDownApp, this,0, 0)
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean {

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val diffX = e1.x - e2.x
        val diffY = e1.y - e2.y

        val strictness = 4 // how distinguished the swipe has to be to be accepted

        // Only open if the swipe was not from the phones top edge
        if (diffY < -height / 8 && abs(diffY) > strictness * abs(diffX) && e1.y > 100)
            launch(downApp,this, R.anim.top_down)
        else if (diffY > height / 8 && abs(diffY) > strictness * abs(diffX))
            launch(upApp, this, R.anim.bottom_up)
        else if (diffX > width / 4 && abs(diffX) > strictness * abs(diffY))
            launch(leftApp,this, R.anim.right_left)
        else if (diffX < -width / 4 && abs(diffX) > strictness * abs(diffY))
            launch(rightApp, this, R.anim.left_right)

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

    override fun applyTheme() {
        // Start by showing the settings icon
        if (!settingsIconShown) showSettingsIcon()

        home_settings_icon.setTextColor(vibrantColor)
        home_container.setBackgroundColor(dominantColor)

        if (launcherPreferences.getString(PREF_WALLPAPER, "") != "") {
            try {
                background = MediaStore.Images.Media.getBitmap(
                    this.contentResolver, Uri.parse(
                        launcherPreferences.getString(PREF_WALLPAPER, "")
                    )
                )
            } catch (e: Exception) { }

            // Background image was deleted or something unexpected happened
            if (background == null) resetToDefaultTheme(this)

            home_background_image.visibility = View.VISIBLE
        } else {
            home_background_image.visibility = View.INVISIBLE
        }
    }

    override fun setOnClicks() {
        home_settings_icon.setOnClickListener() {
            launch("launcher:settings", this, R.anim.bottom_up)
        }

        home_date_view.setOnClickListener() {
            launch(calendarApp, this)
        }

        home_time_view.setOnClickListener() {
            launch(clockApp,this)
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
