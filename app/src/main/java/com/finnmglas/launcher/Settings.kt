package com.finnmglas.launcher

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager


fun isInstalled(uri: String, context: Context): Boolean {
    val pm: PackageManager = context.packageManager
    try {
        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
    }
    return false
}

fun initSettings(sharedPref : SharedPreferences, context: Context){
    val editor: SharedPreferences.Editor = sharedPref.edit()
    editor.putBoolean("startedBefore", true) // never run this again
    editor.apply()

    resetSettings(sharedPref, context)
}

// Some magical default settings are set here ^^
fun resetSettings(sharedPref : SharedPreferences, context: Context){
    val editor: SharedPreferences.Editor = sharedPref.edit()


        /* upApp -> Browser */

    //  #1 -> Firefox
    if(isInstalled("org.mozilla.firefox", context))
        editor.putString("action_upApp", "org.mozilla.firefox")

    //  #2 -> Chrome
    else if(isInstalled("com.android.chrome", context))
        editor.putString("action_upApp", "com.android.chrome")

    //  #3 -> Samsung Internet
    else if(isInstalled("com.sec.android.app.sbrowser", context))
        editor.putString("action_upApp", "com.sec.android.app.sbrowser")

    else
        editor.putString("action_upApp", "")


        /* downApp -> Search Apps */

    //  #1 -> Galaxyfinder -> newer Devices
    if(isInstalled("com.samsung.android.app.galaxyfinder", context))
        editor.putString("action_downApp", "com.samsung.android.app.galaxyfinder")

    //  #2 -> Speechsearch -> older Devices
    else if(isInstalled("com.prometheusinteractive.voice_launcher", context))
        editor.putString("action_downApp", "com.prometheusinteractive.voice_launcher")

    else
        editor.putString("action_downApp", "")


        /* rightApp -> Mail */

    //  #1 -> Web DE Mail -> people having it installed likely want it first
    if(isInstalled("de.web.mobile.android.mail", context))
        editor.putString("action_rightApp", "de.web.mobile.android.mail")

    //  #2 -> Samsung Mail
    else if(isInstalled("com.samsung.android.email.provider", context))
        editor.putString("action_rightApp", "com.samsung.android.email.provider")

    //  #3 -> Google Mail
    else if(isInstalled("com.google.android.gm", context))
        editor.putString("action_rightApp", "com.google.android.gm")

    else
        editor.putString("action_rightApp", "")


        /* leftApp, calendarApp -> Calendar */

    //  #1 -> Google Calendar
    if(isInstalled("com.google.android.calendar", context)){
        editor.putString("action_leftApp", "com.google.android.calendar")
        editor.putString("action_calendarApp", "com.google.android.calendar")
    }

    //  #2 -> Samsung Calendar
    else if(isInstalled("com.samsung.android.calendar", context)){
        editor.putString("action_leftApp", "com.samsung.android.calendar")
        editor.putString("action_calendarApp", "com.samsung.android.calendar")
    }

    else
        editor.putString("action_leftApp", "")


        /* volumeUpApp -> Messenger */

    //  #1 -> Whatsapp
    if(isInstalled("com.whatsapp", context))
        editor.putString("action_volumeUpApp", "com.whatsapp")

    //  #2 -> FB Messenger
    else if(isInstalled("com.facebook.orca", context))
        editor.putString("action_volumeUpApp", "com.facebook.orca")

    //  #3 -> Viber
    else if(isInstalled("com.viber.voip", context))
        editor.putString("action_volumeUpApp", "com.viber.voip")

    //  #4 -> Skype
    else if(isInstalled("com.skype.raider", context))
        editor.putString("action_volumeUpApp", "com.skype.raider")

    //  #5 -> Snapchat
    else if(isInstalled("com.snapchat.android", context))
        editor.putString("action_volumeUpApp", "com.snapchat.android")

    //  #6 -> Instagram
    else if(isInstalled("com.instagram.android", context))
        editor.putString("action_volumeUpApp", "com.instagram.android")

    //  #7 -> SMS
    else if(isInstalled("com.samsung.android.messaging", context))
        editor.putString("action_volumeUpApp", "com.samsung.android.messaging")

    else
        editor.putString("action_volumeUpApp", "")


        /* volumeDownApp -> Util */

    //  #1 -> Github App
    if(isInstalled("com.github.android", context))
        editor.putString("action_volumeDownApp", "com.github.android")

    //  #2 -> Soundbrenner App
    else if(isInstalled("com.soundbrenner.pulse", context))
        editor.putString("action_volumeDownApp", "com.soundbrenner.pulse")

    //  #3 -> Calculator
    else if(isInstalled("com.sec.android.app.popupcalculator", context))
        editor.putString("action_volumeDownApp", "com.sec.android.app.popupcalculator")

    else
        editor.putString("action_volumeDownApp", "")

        /* clockApp default */
    editor.putString("action_clockApp", "com.sec.android.app.clockpackage")

    editor.apply()

    // TODO showInfo()
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