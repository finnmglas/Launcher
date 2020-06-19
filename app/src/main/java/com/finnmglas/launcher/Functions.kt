package com.finnmglas.launcher

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.finnmglas.launcher.list.ListActivity
import com.finnmglas.launcher.list.apps.AppsRecyclerAdapter
import com.finnmglas.launcher.settings.SettingsActivity
import com.finnmglas.launcher.settings.intendedSettingsPause
import com.finnmglas.launcher.tutorial.TutorialActivity
import kotlin.math.roundToInt

/* Preferences (global, initialised when app is started) */
lateinit var launcherPreferences: SharedPreferences

/* Objects used by multiple activities */
lateinit var appListViewAdapter: AppsRecyclerAdapter

/* Variables containing settings */
val displayMetrics = DisplayMetrics()

var upApp = ""
var downApp = ""
var rightApp = ""
var leftApp = ""
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
        RotateAnimation(0F, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF,0.5f).also {
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
        RotateAnimation(0F, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF,0.5f).also {
            it.duration = duration
            it.interpolator = AccelerateInterpolator()
        }
    )

    startAnimation(combined)
}

/* Activity related */

fun isInstalled(uri: String, context: Context): Boolean {
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

fun launch(data: String, activity: Activity,
           animationIn: Int = android.R.anim.fade_in, animationOut: Int = android.R.anim.fade_out) {

    if (data.startsWith("launcher:")) // [type]:[info]
        when(data.split(":")[1]) {
            "settings" -> openSettings(activity)
            "choose" -> openAppsList(activity)
            "tutorial" -> openTutorial(activity)
        }
    else launchApp(data, activity) // app

    activity.overridePendingTransition(animationIn, animationOut)
}

fun launchApp(packageName: String, context: Context) {
    val intent = getIntent(packageName, context)

    if (intent != null) {
        context.startActivity(intent)
    } else {
        if (isInstalled(packageName, context)){

            AlertDialog.Builder(context,
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
            Toast.makeText( context, context.getString(R.string.toast_cant_open_message), Toast.LENGTH_SHORT).show()
        }
    }
}

fun openNewTabWindow(urls: String, context : Context) {
    val uris = Uri.parse(urls)
    val intents = Intent(Intent.ACTION_VIEW, uris)
    val b = Bundle()
    b.putBoolean("new_window", true)
    intents.putExtras(b)
    context.startActivity(intents)
}

/* Settings related functions */

fun getSavedTheme(context : Context) : String {
    return launcherPreferences.getString("theme", "finn").toString()
}

fun saveTheme(themeName : String) : String {
    launcherPreferences.edit()
        .putString("theme", themeName)
        .apply()

    return themeName
}

fun openAppSettings(pkg :String, context:Context){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.parse("package:$pkg")
    context.startActivity(intent)
}

fun openSettings(activity: Activity){
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

fun loadSettings(){
    upApp = launcherPreferences.getString("action_upApp", "").toString()
    downApp = launcherPreferences.getString("action_downApp", "").toString()
    rightApp = launcherPreferences.getString("action_rightApp", "").toString()
    leftApp = launcherPreferences.getString("action_leftApp", "").toString()
    volumeUpApp = launcherPreferences.getString("action_volumeUpApp", "").toString()
    volumeDownApp = launcherPreferences.getString("action_volumeDownApp", "").toString()

    doubleClickApp = launcherPreferences.getString("action_doubleClickApp", "").toString()
    longClickApp = launcherPreferences.getString("action_longClickApp", "").toString()

    calendarApp = launcherPreferences.getString("action_calendarApp", "").toString()
    clockApp = launcherPreferences.getString("action_clockApp", "").toString()

    dominantColor = launcherPreferences.getInt("custom_dominant", 0)
    vibrantColor = launcherPreferences.getInt("custom_vibrant", 0)
}

fun resetSettings(context: Context) : MutableList<String>{

    // set default theme
    saveTheme("finn")

    val defaultList :MutableList<String> = mutableListOf<String>()

    val editor = launcherPreferences.edit()

    val (chosenUpName, chosenUpPackage) = pickDefaultApp(
        "action_upApp",
        context
    )
    editor.putString("action_upApp", chosenUpPackage)
    defaultList.add(chosenUpName)

    val (chosenDownName, chosenDownPackage) = pickDefaultApp(
        "action_downApp",
        context
    )
    editor.putString("action_downApp", chosenDownPackage)
    defaultList.add(chosenDownName)

    val (chosenRightName, chosenRightPackage) = pickDefaultApp(
        "action_rightApp",
        context
    )
    editor.putString("action_rightApp", chosenRightPackage)
    defaultList.add(chosenRightName)

    val (chosenLeftName, chosenLeftPackage) = pickDefaultApp(
        "action_leftApp",
        context
    )
    editor.putString("action_leftApp", chosenLeftPackage)
    editor.putString("action_calendarApp", chosenLeftPackage)
    defaultList.add(chosenLeftName)

    val (chosenVolumeUpName, chosenVolumeUpPackage) = pickDefaultApp(
        "action_volumeUpApp",
        context
    )
    editor.putString("action_volumeUpApp", chosenVolumeUpPackage)
    defaultList.add(chosenVolumeUpName)

    val (chosenVolumeDownName, chosenVolumeDownPackage) = pickDefaultApp(
        "action_volumeDownApp",
        context
    )
    editor.putString("action_volumeDownApp", chosenVolumeDownPackage)
    defaultList.add(chosenVolumeDownName)

    editor.putString("action_doubleClickApp", "")
    editor.putString("action_longClickApp", "")

    val (_, chosenClockPackage) = pickDefaultApp(
        "action_clockApp",
        context
    )
    editor.putString("action_clockApp", chosenClockPackage)

    editor.apply()

    return defaultList // UP, DOWN, RIGHT, LEFT, VOLUME_UP, VOLUME_DOWN
}

fun pickDefaultApp(action: String, context: Context) : Pair<String, String>{
    val arrayResource = when (action) {
        "action_upApp" -> R.array.default_up
        "action_downApp" -> R.array.default_down
        "action_rightApp" -> R.array.default_right
        "action_leftApp" -> R.array.default_left
        "action_volumeUpApp" -> R.array.default_volume_up
        "action_volumeDownApp" -> R.array.default_volume_down
        "action_clockApp" -> R.array.default_clock
        else -> return Pair(context.getString(R.string.none_found), "") // just prevent crashing on unknown input
    }

    // Related question: https://stackoverflow.com/q/3013655/12787264 (Adjusted)
    val list = context.resources.getStringArray(arrayResource)
    for (entry in list){
        val splitResult = entry.split("|").toTypedArray()
        val pkgname = splitResult[0]
        val name = splitResult[1]

        if (isInstalled(pkgname, context)) return Pair(name, pkgname)
    }
    return Pair(context.getString(R.string.none_found), "")
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
    else {
        // not setting it here, unable to find a good alternative
        // TODO at some point (or you do it now)
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