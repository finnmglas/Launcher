package com.finnmglas.launcher.list.apps

import android.graphics.drawable.Drawable

/**
 * Stores information used in [AppsRecyclerAdapter] rows.
 */
class AppInfo {
    var label: CharSequence? = null
    var packageName: CharSequence? = null
    var icon: Drawable? = null
    var isSystemApp: Boolean = false
}