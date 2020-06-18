package com.finnmglas.launcher

import android.app.Activity
import android.view.WindowManager

interface UIObject {
    fun onStart() {
        if (this is Activity){
            window.setFlags(0,0) // clear flags
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    fun setTheme()
    fun setOnClicks()
    fun configure() {}
}