package com.finnmglas.launcher

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import com.finnmglas.launcher.list.ListActivity
import com.finnmglas.launcher.list.apps.AppInfo
import com.finnmglas.launcher.list.apps.AppsRecyclerAdapter
import com.finnmglas.launcher.settings.SettingsActivity
import com.finnmglas.launcher.settings.intendedSettingsPause
import com.finnmglas.launcher.tutorial.TutorialActivity
import kotlin.math.roundToInt


/* Preferences (global, initialised when app is started) */
lateinit var launcherPreferences: SharedPreferences

/* Preference Key Constants */

const val ACTION_UP = "action_upApp"
const val ACTION_DOUBLE_UP = "action_doubleUpApp"
const val ACTION_DOWN = "action_downApp"
const val ACTION_DOUBLE_DOWN = "action_doubleDownApp"
const val ACTION_RIGHT = "action_rightApp"
const val ACTION_DOUBLE_RIGHT = "action_doubleRightApp"
const val ACTION_LEFT = "action_leftApp"
const val ACTION_DOUBLE_LEFT = "action_doubleLeftApp"

const val ACTION_VOL_UP = "action_volumeUpApp"
const val ACTION_VOL_DOWN = "action_volumeDownApp"
const val ACTION_DOUBLE_CLICK = "action_doubleClickApp"
const val ACTION_LONG_CLICK = "action_longClickApp"

const val ACTION_CALENDAR = "action_calendarApp"
const val ACTION_CLOCK = "action_clockApp"

val ACTIONS = listOf(
    ACTION_UP, ACTION_DOUBLE_UP,
    ACTION_DOWN, ACTION_DOUBLE_DOWN,
    ACTION_RIGHT, ACTION_LEFT,
    ACTION_VOL_UP, ACTION_VOL_DOWN,
    ACTION_DOUBLE_CLICK, ACTION_LONG_CLICK,
    ACTION_CALENDAR, ACTION_CLOCK
)

const val PREF_DOMINANT = "custom_dominant"
const val PREF_VIBRANT = "custom_vibrant"
const val PREF_WALLPAPER = "background_uri"
const val PREF_THEME = "theme"

const val PREF_SCREEN_TIMEOUT_DISABLED = "disableTimeout"
const val PREF_SCREEN_FULLSCREEN = "useFullScreen"
const val PREF_DATE_FORMAT = "dateFormat"

const val PREF_DOUBLE_ACTIONS_ENABLED = "enableDoubleActions"
const val PREF_SEARCH_AUTO_LAUNCH = "searchAutoLaunch"

const val PREF_STARTED = "startedBefore"
const val PREF_STARTED_TIME = "firstStartup"

const val PREF_VERSION = "version"

/* Objects used by multiple activities */
val appsList: MutableList<AppInfo> = ArrayList()

/* Variables containing settings */
val displayMetrics = DisplayMetrics()

var upApp = ""
var doubleUpApp = ""
var downApp = ""
var doubleDownApp = ""
var rightApp = ""
var doubleRightApp = ""
var leftApp = ""
var doubleLeftApp = ""
var volumeUpApp = ""
var volumeDownApp = ""
var doubleClickApp = ""
var longClickApp = ""

var calendarApp = ""
var clockApp = ""

var background : Bitmap? = null

var dominantColor = 0
var vibrantColor = 0

/* REQUEST CODES */

const val REQUEST_PICK_IMAGE = 1
const val REQUEST_CHOOSE_APP = 2
const val REQUEST_UNINSTALL = 3
const val REQUEST_PERMISSION_STORAGE = 4

/* Animate */

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

fun View.fadeIn(duration: Long = 300L) {
    startAnimation(AlphaAnimation(0f, 1f).also {
        it.interpolator = DecelerateInterpolator()
        it.duration = duration
    })
}

fun View.fadeOut(duration: Long = 300L) {
    startAnimation(AlphaAnimation(1f, 0f).also {
        it.interpolator = DecelerateInterpolator()
        it.duration = duration
    })
}

fun View.fadeRotateIn(duration: Long = 500L) {
    val combined = AnimationSet(false)
    combined.addAnimation(
        AlphaAnimation(0f, 1F).also {
            it.interpolator = DecelerateInterpolator()
            it.duration = duration
        }
    )
    combined.addAnimation(
        RotateAnimation(
            0F, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        ).also {
            it.duration = duration * 2
            it.interpolator = DecelerateInterpolator()
        }
    )

    startAnimation(combined)
}

fun View.fadeRotateOut(duration: Long = 500L) {
    val combined = AnimationSet(false)
    combined.addAnimation(
        AlphaAnimation(1F, 0F).also {
            it.interpolator = AccelerateInterpolator()
            it.duration = duration
        }
    )
    combined.addAnimation(
        RotateAnimation(
            0F, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        ).also {
            it.duration = duration
            it.interpolator = AccelerateInterpolator()
        }
    )

    startAnimation(combined)
}

/* Activity related */

fun isInstalled(uri: String, context: Context): Boolean {
    if (uri.startsWith("launcher:")) return true // All internal actions

    try {
        context.packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
    }
    return false
}

private fun getIntent(packageName: String, context: Context): Intent? {
    val intent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
    intent?.addCategory(Intent.CATEGORY_LAUNCHER)
    return intent
}

fun launch(
    data: String, activity: Activity,
    animationIn: Int = android.R.anim.fade_in, animationOut: Int = android.R.anim.fade_out
) {

    if (data.startsWith("launcher:")) // [type]:[info]
        when(data.split(":")[1]) {
            "settings" -> openSettings(activity)
            "choose" -> openAppsList(activity)
            "volumeUp" -> audioVolumeUp(activity)
            "volumeDown" -> audioVolumeDown(activity)
            "tutorial" -> openTutorial(activity)
        }
    else launchApp(data, activity) // app

    activity.overridePendingTransition(animationIn, animationOut)
}

fun audioVolumeUp(activity: Activity) {
    val audioManager =
        activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    audioManager.adjustStreamVolume(
        AudioManager.STREAM_MUSIC,
        AudioManager.ADJUST_RAISE,
        AudioManager.FLAG_SHOW_UI
    )
}

fun audioVolumeDown(activity: Activity) {
    val audioManager =
        activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    audioManager.adjustStreamVolume(
        AudioManager.STREAM_MUSIC,
        AudioManager.ADJUST_LOWER,
        AudioManager.FLAG_SHOW_UI
    )
}

fun launchApp(packageName: String, context: Context) {
    val intent = getIntent(packageName, context)

    if (intent != null) {
        context.startActivity(intent)
    } else {
        if (isInstalled(packageName, context)){

            AlertDialog.Builder(
                context,
                R.style.AlertDialogCustom
            )
                .setTitle(context.getString(R.string.alert_cant_open_title))
                .setMessage(context.getString(R.string.alert_cant_open_message))
                .setPositiveButton(android.R.string.yes,
                    DialogInterface.OnClickListener { dialog, which ->
                        openAppSettings(
                            packageName,
                            context
                        )
                    })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show()
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.toast_cant_open_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun openNewTabWindow(urls: String, context: Context) {
    val uris = Uri.parse(urls)
    val intents = Intent(Intent.ACTION_VIEW, uris)
    val b = Bundle()
    b.putBoolean("new_window", true)
    intents.putExtras(b)
    context.startActivity(intents)
}

/* Settings related functions */

fun getSavedTheme(context: Context) : String {
    return launcherPreferences.getString(PREF_THEME, "finn").toString()
}

fun saveTheme(themeName: String) : String {
    launcherPreferences.edit()
        .putString(PREF_THEME, themeName)
        .apply()

    return themeName
}

fun resetToDefaultTheme(activity: Activity) {
    dominantColor = activity.resources.getColor(R.color.finnmglasTheme_background_color)
    vibrantColor = activity.resources.getColor(R.color.finnmglasTheme_accent_color)

    launcherPreferences.edit()
        .putString(PREF_WALLPAPER, "")
        .putInt(PREF_DOMINANT, dominantColor)
        .putInt(PREF_VIBRANT, vibrantColor)
        .apply()

    saveTheme("finn")
    loadSettings()

    intendedSettingsPause = true
    activity.recreate()
}

fun resetToDarkTheme(activity: Activity) {
    dominantColor = activity.resources.getColor(R.color.darkTheme_background_color)
    vibrantColor = activity.resources.getColor(R.color.darkTheme_accent_color)

    launcherPreferences.edit()
        .putString(PREF_WALLPAPER, "")
        .putInt(PREF_DOMINANT, dominantColor)
        .putInt(PREF_VIBRANT, vibrantColor)
        .apply()

    saveTheme("dark")

    intendedSettingsPause = true
    activity.recreate()
}


fun openAppSettings(pkg: String, context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.parse("package:$pkg")
    context.startActivity(intent)
}

fun openSettings(activity: Activity) {
    activity.startActivity(Intent(activity, SettingsActivity::class.java))
}

fun openTutorial(activity: Activity){
    activity.startActivity(Intent(activity, TutorialActivity::class.java))
}

fun openAppsList(activity: Activity){
    val intent = Intent(activity, ListActivity::class.java)
    intent.putExtra("intention", "view")
    intendedSettingsPause = true
    activity.startActivity(intent)
}

/**
 * [loadApps] is used to speed up the [AppsRecyclerAdapter] loading time,
 * as it caches all the apps and allows for fast access to the data.
 */
fun loadApps(packageManager: PackageManager) {
    val loadList = mutableListOf<AppInfo>()

    val i = Intent(Intent.ACTION_MAIN, null)
    i.addCategory(Intent.CATEGORY_LAUNCHER)
    val allApps = packageManager.queryIntentActivities(i, 0)
    for (ri in allApps) {
        val app = AppInfo()
        app.label = ri.loadLabel(packageManager)
        app.packageName = ri.activityInfo.packageName
        app.icon = ri.activityInfo.loadIcon(packageManager)
        loadList.add(app)
    }
    loadList.sortBy { it.label.toString() }

    appsList.clear()
    appsList.addAll(loadList)
}

fun loadSettings() {
    upApp = launcherPreferences.getString(ACTION_UP, "")!!
    doubleUpApp = launcherPreferences.getString(ACTION_DOUBLE_UP, "")!!
    downApp = launcherPreferences.getString(ACTION_DOWN, "")!!
    doubleDownApp = launcherPreferences.getString(ACTION_DOUBLE_DOWN, "")!!
    rightApp = launcherPreferences.getString(ACTION_RIGHT, "")!!
    doubleRightApp = launcherPreferences.getString(ACTION_DOUBLE_RIGHT, "")!!
    leftApp = launcherPreferences.getString(ACTION_LEFT, "")!!
    doubleLeftApp = launcherPreferences.getString(ACTION_DOUBLE_LEFT, "")!!
    volumeUpApp = launcherPreferences.getString(ACTION_VOL_UP, "")!!
    volumeDownApp = launcherPreferences.getString(ACTION_VOL_DOWN, "")!!

    doubleClickApp = launcherPreferences.getString(ACTION_DOUBLE_CLICK, "")!!
    longClickApp = launcherPreferences.getString(ACTION_LONG_CLICK, "")!!

    calendarApp = launcherPreferences.getString(ACTION_CALENDAR, "")!!
    clockApp = launcherPreferences.getString(ACTION_CLOCK, "")!!

    dominantColor = launcherPreferences.getInt(PREF_DOMINANT, 0)
    vibrantColor = launcherPreferences.getInt(PREF_VIBRANT, 0)
}

fun resetSettings(context: Context) {

    val editor = launcherPreferences.edit()

    // set default theme
    dominantColor = context.resources.getColor(R.color.finnmglasTheme_background_color)
    vibrantColor = context.resources.getColor(R.color.finnmglasTheme_accent_color)

    editor
        .putString(PREF_WALLPAPER, "")
        .putInt(PREF_DOMINANT, dominantColor)
        .putInt(PREF_VIBRANT, vibrantColor)
        .putString(PREF_THEME, "finn")
        .putBoolean(PREF_SCREEN_TIMEOUT_DISABLED, false)
        .putBoolean(PREF_SEARCH_AUTO_LAUNCH, false)
        .putInt(PREF_DATE_FORMAT, 0)
        .putBoolean(PREF_SCREEN_FULLSCREEN, true)
        .putBoolean(PREF_DOUBLE_ACTIONS_ENABLED, false)

    // load action defaults
    for (actionKey in ACTIONS)
        editor.putString(actionKey, pickDefaultApp(actionKey, context))

    editor.apply()
}

fun setWindowFlags(window: Window) {
    window.setFlags(0, 0) // clear flags

    // Display notification bar
    if (launcherPreferences.getBoolean(PREF_SCREEN_FULLSCREEN, true))
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    else window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

    // Screen Timeout
    if (launcherPreferences.getBoolean(PREF_SCREEN_TIMEOUT_DISABLED, false))
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}


fun pickDefaultApp(action: String, context: Context) : String {
    val arrayResource = when (action) {
        ACTION_UP -> R.array.default_up
        ACTION_DOUBLE_UP -> R.array.default_double_up
        ACTION_DOWN -> R.array.default_down
        ACTION_DOUBLE_DOWN -> R.array.default_double_down
        ACTION_RIGHT -> R.array.default_right
        ACTION_DOUBLE_RIGHT -> R.array.default_double_right
        ACTION_LEFT -> R.array.default_left
        ACTION_DOUBLE_LEFT -> R.array.default_double_left
        ACTION_VOL_UP -> R.array.default_volume_up
        ACTION_VOL_DOWN -> R.array.default_volume_down
        ACTION_DOUBLE_CLICK -> R.array.default_double_click
        ACTION_LONG_CLICK -> R.array.default_long_click
        ACTION_CLOCK -> R.array.default_clock
        ACTION_CALENDAR -> R.array.default_left

        else -> return "" // just prevent crashing on unknown input
    }

    val list = context.resources.getStringArray(arrayResource)
    for (packageName in list)
        if (isInstalled(packageName, context)) return packageName
    return ""
}

// Used in Tutorial and Settings `ActivityOnResult`
fun saveListActivityChoice(data: Intent?) {
    val value = data?.getStringExtra("value")
    val forApp = data?.getStringExtra("forApp") ?: return

    launcherPreferences.edit()
        .putString("action_$forApp", value.toString())
        .apply()

    loadSettings()
}

// Taken form https://stackoverflow.com/a/50743764/12787264
fun openSoftKeyboard(context: Context, view: View) {
    view.requestFocus()
    // open the soft keyboard
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

/* Bitmaps */

fun setButtonColor(btn: Button, color: Int) {
    if (Build.VERSION.SDK_INT >= 29)
        btn.background.colorFilter = BlendModeColorFilter(color, BlendMode.MULTIPLY)
    else if(Build.VERSION.SDK_INT >= 21) {
        // tested with API 17 (Android 4.4.2 on S4 mini) -> fails
        // tested with API 28 (Android 9 on S8) -> necessary
        btn.background.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
    // not setting it in any other case (yet), unable to find a good solution
}

fun setSwitchColor(sw: Switch, trackColor: Int) {
    if (Build.VERSION.SDK_INT >= 29) {
        sw.trackDrawable.colorFilter = BlendModeColorFilter(trackColor, BlendMode.MULTIPLY)
    }
    else if(Build.VERSION.SDK_INT >= 21) {
        sw.trackDrawable.colorFilter = PorterDuffColorFilter(trackColor, PorterDuff.Mode.SRC_ATOP)
    }
}

// Taken from: https://stackoverflow.com/a/33072575/12787264
fun manipulateColor(color: Int, factor: Float): Int {
    val a = Color.alpha(color)
    val r = (Color.red(color) * factor).roundToInt()
    val g = (Color.green(color) * factor).roundToInt()
    val b = (Color.blue(color) * factor).roundToInt()
    return Color.argb(
        a,
        r.coerceAtMost(255),
        g.coerceAtMost(255),
        b.coerceAtMost(255)
    )
}

// Taken from: https://stackoverflow.com/a/30340794/12787264
fun transformGrayscale(imageView: ImageView){
    val matrix = ColorMatrix()
    matrix.setSaturation(0f)

    val filter = ColorMatrixColorFilter(matrix)
    imageView.colorFilter = filter
}