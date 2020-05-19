package com.finnmglas.launcher

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

/** Activity related */

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

fun launchApp(packageName: String, context: Context) {
    val intent1 = getIntent(packageName, context)

    if (intent1 != null) {
        context.startActivity(intent1)
        //overridePendingTransition(0, 0)
    } else {
        if (isInstalled(packageName, context)){

            AlertDialog.Builder(context)
                .setTitle("Can't open app")
                .setMessage("Want to change its settings ('add it to the apps screen')?")
                .setPositiveButton(android.R.string.yes,
                    DialogInterface.OnClickListener { dialog, which ->
                        openAppSettings(packageName, context)
                    })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show()
        } else {
            Toast.makeText( context, "Open settings to choose an app for this action", Toast.LENGTH_SHORT).show()
        }
    }
}

/** Settings related */

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

    val (chosenUpName, chosenUpPackage) = pickDefaultUpApp(context)
    editor.putString("action_upApp", chosenUpPackage)
    defaultList.add(chosenUpName)

    val (chosenDownName, chosenDownPackage) = pickDefaultDownApp(context)
    editor.putString("action_downApp", chosenDownPackage)
    defaultList.add(chosenDownName)

    val (chosenRightName, chosenRightPackage) = pickDefaultRightApp(context)
    editor.putString("action_rightApp", chosenRightPackage)
    defaultList.add(chosenRightName)

    val (chosenLeftName, chosenLeftPackage) = pickDefaultLeftApp(context)
    editor.putString("action_leftApp", chosenLeftPackage)
    editor.putString("action_calendarApp", chosenLeftPackage)
    defaultList.add(chosenLeftName)

    val (chosenVolumeUpName, chosenVolumeUpPackage) = pickDefaultVolumeUpApp(context)
    editor.putString("action_volumeUpApp", chosenVolumeUpPackage)
    defaultList.add(chosenVolumeUpName)

    val (chosenVolumeDownName, chosenVolumeDownPackage) = pickDefaultVolumeDownApp(context)
    editor.putString("action_volumeDownApp", chosenVolumeDownPackage)
    defaultList.add(chosenVolumeDownName)

    // clockApp default
    editor.putString("action_clockApp", "com.sec.android.app.clockpackage")

    editor.apply()

    return defaultList // UP, DOWN, RIGHT, LEFT, VOLUME_UP, VOLUME_DOWN
}

// Default upApps are Browsers
fun pickDefaultUpApp(context :Context) : Pair<String, String>{
    if(isInstalled("org.mozilla.firefox", context))
        return Pair("Firefox", "org.mozilla.firefox")
    else if(isInstalled("com.android.chrome", context))
        return Pair("Chrome", "com.android.chrome")
    else if(isInstalled("com.sec.android.app.sbrowser", context))
        return Pair("Samsung Internet", "com.sec.android.app.sbrowser")
    else
        return Pair(context.getString(R.string.none_found), "")
}

// Default downApps are Internal Search Apps
fun pickDefaultDownApp(context :Context) : Pair<String, String>{
    if(isInstalled("com.samsung.android.app.galaxyfinder", context))
        return Pair("GalaxyFinder", "com.samsung.android.app.galaxyfinder")
    else if(isInstalled("com.prometheusinteractive.voice_launcher", context))
        return Pair("VoiceSearch", "com.prometheusinteractive.voice_launcher")
    else
        return Pair(context.getString(R.string.none_found), "")
}

// Default rightApps are Mailing Applications
fun pickDefaultRightApp(context :Context) : Pair<String, String>{
    if(isInstalled("de.web.mobile.android.mail", context))
        return Pair("WebMail", "de.web.mobile.android.mail")
    else if(isInstalled("com.samsung.android.email.provider", context))
        return Pair("Samsung Mail", "com.samsung.android.email.provider")
    else if(isInstalled("com.google.android.gm", context))
        return Pair("Google Mail", "com.google.android.gm")
    else
        return Pair(context.getString(R.string.none_found), "")
}

// Default leftApps are Calendar Applications
fun pickDefaultLeftApp(context :Context) : Pair<String, String>{
    if(isInstalled("com.google.android.calendar", context))
        return Pair("Google Calendar", "com.google.android.calendar")
    else if(isInstalled("com.samsung.android.calendar", context))
        return Pair("Samsung Calendar", "com.samsung.android.calendar")
    else
        return Pair(context.getString(R.string.none_found), "")
}

// Default volumeUpApps are Messengers
fun pickDefaultVolumeUpApp(context: Context) : Pair<String, String>{
    if(isInstalled("com.whatsapp", context))
        return Pair("WhatsApp", "com.whatsapp")
    else if(isInstalled("com.facebook.orca", context))
        return Pair("Facebook Messenger", "com.facebook.orca")
    else if(isInstalled("com.viber.voip", context))
        return Pair("Viber", "com.viber.voip")
    else if(isInstalled("com.skype.raider", context))
        return Pair("Skype", "com.skype.raider")
    else if(isInstalled("com.snapchat.android", context))
        return Pair("Snapchat", "com.snapchat.android")
    else if(isInstalled("com.instagram.android", context))
        return Pair("Instagram", "com.instagram.android")
    else if(isInstalled("com.samsung.android.messaging", context))
        return Pair("Samsung SMS", "com.samsung.android.messaging")
    else
        return Pair(context.getString(R.string.none_found), "")
}

// Default volumeDownApps are Utilities
fun pickDefaultVolumeDownApp(context: Context) : Pair<String, String>{
    if(isInstalled("com.github.android", context))
        return Pair("GitHub", "com.github.android")
    else if(isInstalled("com.soundbrenner.pulse", context))
        return Pair("Soundbrenner Metronome", "com.soundbrenner.pulse")
    else if(isInstalled("com.sec.android.app.popupcalculator", context))
        return Pair("Calculator", "com.sec.android.app.popupcalculator")
    else
        return Pair(context.getString(R.string.none_found), "")
}
