package com.finnmglas.launcher

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

/** Variables for all of the app */
var upApp = ""
var downApp = ""
var rightApp = ""
var leftApp = ""
var volumeUpApp = ""
var volumeDownApp = ""

var calendarApp = ""
var clockApp = ""

var appsList : MutableList<ResolveInfo> = mutableListOf()

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

fun View.fadeIn(duration: Long = 1000L) {
    startAnimation(AlphaAnimation(0f, 1f).also {
        it.interpolator = DecelerateInterpolator()
        it.duration = duration
    })
}

fun View.fadeOut(duration: Long = 1000L) {
    startAnimation(AlphaAnimation(1f, 0f).also {
        it.interpolator = DecelerateInterpolator()
        it.duration = duration
    })
}

/** Activity related */

fun isInstalled(uri: String, context: Context): Boolean {
    try {
        context.packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
    }
    return false
}

fun updateAppList(pm : PackageManager) {
    val intent = Intent(Intent.ACTION_MAIN)
        .addCategory(Intent.CATEGORY_LAUNCHER)
    appsList = pm.queryIntentActivities(intent, 0)
    appsList.sortBy { it.activityInfo.loadLabel(pm).toString() }
}

private fun getIntent(packageName: String, context: Context): Intent? {
    val intent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
    intent?.addCategory(Intent.CATEGORY_LAUNCHER)
    return intent
}

fun launchApp(packageName: String, context: Context) {
    val intent1 = getIntent(packageName, context)

    if (intent1 != null) {
        context.startActivity(intent1)

        if (context is Activity) {
            context.overridePendingTransition(0, 0)
        }
    } else {
        if (isInstalled(packageName, context)){

            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.alert_cant_open_title))
                .setMessage(context.getString(R.string.alert_cant_open_message))
                .setPositiveButton(android.R.string.yes,
                    DialogInterface.OnClickListener { dialog, which ->
                        openAppSettings(packageName, context)
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

/** Settings related functions */

fun openAppSettings(pkg :String, context:Context){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.parse("package:$pkg")
    context.startActivity(intent)
}

fun loadSettings(sharedPref : SharedPreferences){
    upApp = sharedPref.getString("action_upApp", "").toString()
    downApp = sharedPref.getString("action_downApp", "").toString()
    rightApp = sharedPref.getString("action_rightApp", "").toString()
    leftApp = sharedPref.getString("action_leftApp", "").toString()
    volumeUpApp = sharedPref.getString("action_volumeUpApp", "").toString()
    volumeDownApp = sharedPref.getString("action_volumeDownApp", "").toString()

    calendarApp = sharedPref.getString("action_calendarApp", "").toString()
    clockApp = sharedPref.getString("action_clockApp", "").toString()
}

fun resetSettings(sharedPref : SharedPreferences, context: Context) : MutableList<String>{
    val defaultList :MutableList<String> = mutableListOf<String>()

    val editor: SharedPreferences.Editor = sharedPref.edit()

    val (chosenUpName, chosenUpPackage) = pickDefaultApp("action_upApp", context)
    editor.putString("action_upApp", chosenUpPackage)
    defaultList.add(chosenUpName)

    val (chosenDownName, chosenDownPackage) = pickDefaultApp("action_downApp", context)
    editor.putString("action_downApp", chosenDownPackage)
    defaultList.add(chosenDownName)

    val (chosenRightName, chosenRightPackage) = pickDefaultApp("action_rightApp", context)
    editor.putString("action_rightApp", chosenRightPackage)
    defaultList.add(chosenRightName)

    val (chosenLeftName, chosenLeftPackage) = pickDefaultApp("action_leftApp", context)
    editor.putString("action_leftApp", chosenLeftPackage)
    editor.putString("action_calendarApp", chosenLeftPackage)
    defaultList.add(chosenLeftName)

    val (chosenVolumeUpName, chosenVolumeUpPackage) = pickDefaultApp("action_volumeUpApp", context)
    editor.putString("action_volumeUpApp", chosenVolumeUpPackage)
    defaultList.add(chosenVolumeUpName)

    val (chosenVolumeDownName, chosenVolumeDownPackage) = pickDefaultApp("action_volumeDownApp", context)
    editor.putString("action_volumeDownApp", chosenVolumeDownPackage)
    defaultList.add(chosenVolumeDownName)

    val (_, chosenClockPackage) = pickDefaultApp("action_clockApp", context)
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
    for (entry in list!!){
        val splitResult = entry.split("|").toTypedArray()
        val pkgname = splitResult[0]
        val name = splitResult[1]

        if (isInstalled(pkgname, context)) return Pair(name, pkgname)
    }
    return Pair(context.getString(R.string.none_found), "")
}
