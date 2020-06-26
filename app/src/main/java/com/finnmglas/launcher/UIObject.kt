package com.finnmglas.launcher

import android.app.Activity
import android.view.WindowManager

/**
 * An interface implemented by every [Activity], Fragment etc. in Launcher.
 * It handles themes and window flags - a useful abstraction as it is the same everywhere.
 */
interface UIObject {
    fun onStart() {
        if (this is Activity) setWindowFlags(window)

        applyTheme()
        setOnClicks()
        adjustLayout()
    }

    // Don't use actual themes, rather create them on the fly for faster theme-switching
    fun applyTheme() { }
    fun setOnClicks() { }
    fun adjustLayout() { }
}