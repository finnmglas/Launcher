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
lateinit var viewManager: RecyclerView.LayoutManager

class HomeActivity : AppCompatActivity(),
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
        setContentView(R.layout.home)

        // Start by showing the settings icon
        showSettingsIcon()

        // As older APIs somehow do not recognize the xml defined onClick
        home_settings_icon.setOnClickListener() {
            openSettings(this)
            overridePendingTransition(R.anim.bottom_up, android.R.anim.fade_out)
        }

        // Load apps list first - speed up settings that way
        AsyncTask.execute { viewAdapter =
            AppsRecyclerAdapter(this, "", "")
        }

        // First Startup
        if (!sharedPref.getBoolean("startedBefore", false)){
            startActivity(Intent(this, TutorialActivity::class.java))
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
            home_settings_icon.setTextColor(vibrantColor)
        }

        mDetector = GestureDetectorCompat(this, this)
        mDetector.setOnDoubleTapListener(this)

        windowManager.defaultDisplay.getMetrics(displayMetrics)
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
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) launch(
            volumeUpApp,
            this
        )
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) launch(
            volumeDownApp,
            this
        )
        return true
    }

    fun dateViewOnTouch(v: View) {
        launch(
            calendarApp,
            this
        )
    }
    fun timeViewOnTouch(v: View) {
        launch(
            clockApp,
            this
        )
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean {

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val diffX = e1.x - e2.x
        val diffY = e1.y - e2.y

        val strictness = 4 // how distinguished the swipe has to be to be accepted

        // Only open if the swipe was not from the phones top edge
        if (diffY < -height / 8 && abs(diffY) > strictness * abs(diffX) && e1.y > 100) launch(
            downApp,
            this
        )
        else if (diffY > height / 8 && abs(diffY) > strictness * abs(diffX)) {
            launch(
                upApp,
                this
            )
            overridePendingTransition(R.anim.bottom_up, android.R.anim.fade_out)
        }
        else if (diffX > width / 4 && abs(diffX) > strictness * abs(diffY)) launch(
            leftApp,
            this
        )
        else if (diffX < -width / 4 && abs(diffX) > strictness * abs(diffY)) launch(
            rightApp,
            this
        )

        return true
    }

    override fun onLongPress(event: MotionEvent) {
        if(longClickApp != "") launch(
            longClickApp,
            this
        )
        else openSettings(this)
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        launch(
            doubleClickApp,
            this
        )
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

    fun settingsIconOnTouch(view: View){
        openSettings(this)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) { false } else { super.onTouchEvent(event) }
    }

    /* TODO: Remove those. For now they are necessary
     *  because this inherits from GestureDetector.OnGestureListener */
    override fun onDoubleTapEvent(event: MotionEvent): Boolean { return false }
    override fun onDown(event: MotionEvent): Boolean { return false }
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean { return false }
    override fun onShowPress(event: MotionEvent) {}
    override fun onSingleTapUp(event: MotionEvent): Boolean { return false }
}
