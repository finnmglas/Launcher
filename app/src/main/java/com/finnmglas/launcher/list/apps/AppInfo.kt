package com.finnmglas.launcher.list.apps

import android.graphics.drawable.Drawable

/**
 * Stores information used to create [AppsRecyclerAdapter] rows.
 *
 * Represents an app installed on the users device.
 */
class AppInfo {
    var label: CharSequence? = null
    var packageName: CharSequence? = null
    var icon: Drawable? = null
    var isSystemApp: Boolean = false
}