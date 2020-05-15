package com.finnmglas.launcher

import android.content.SharedPreferences

fun resetSettings(sharedPref : SharedPreferences){
    val editor: SharedPreferences.Editor = sharedPref.edit()

    // Set Defaults
    editor.putString("action_upApp", "org.mozilla.firefox")
    editor.putString("action_downApp", "com.samsung.android.app.galaxyfinder")
    editor.putString("action_rightApp", "com.samsung.android.email.provider")
    editor.putString("action_leftApp", "com.google.android.calendar")
    editor.putString("action_volumeUpApp", "com.whatsapp")
    editor.putString("action_volumeDownApp", "com.sec.android.app.popupcalculator")

    editor.putBoolean("startedBefore", true) // never run this again
    editor.apply()
}

fun loadSettings(sharedPref : SharedPreferences){
    upApp = sharedPref.getString("action_upApp", "").toString()
    downApp = sharedPref.getString("action_downApp", "").toString()
    rightApp = sharedPref.getString("action_rightApp", "").toString()
    leftApp = sharedPref.getString("action_leftApp", "").toString()
    volumeUpApp = sharedPref.getString("action_volumeUpApp", "").toString()
    volumeDownApp = sharedPref.getString("action_volumeDownApp", "").toString()
}