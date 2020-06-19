package com.finnmglas.launcher.list.other

/**
 * Stores information used in [OtherRecyclerAdapter] rows.
 *
 * Represents an `other` action - something that can be selected to be launched
 * when an action is recognized.
 *
 * @param data - a string identifying the thing to be launched
*/
class OtherInfo(label: String, data: String, icon: String) {
    var label: CharSequence? = label
    var data: CharSequence? = data
    var icon: CharSequence? = icon
}