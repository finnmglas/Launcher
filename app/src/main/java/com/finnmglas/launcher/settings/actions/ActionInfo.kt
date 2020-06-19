package com.finnmglas.launcher.settings.actions

/**
 * Stores information used in [ActionsRecyclerAdapter] rows.
 *
 * Represents an action - something to be triggered by swiping, clicking etc.
 *
 * @param data - a string identifying the app / action / intent to be launched
 */
class ActionInfo(actionText: CharSequence, actionName: CharSequence, data: CharSequence) {
    val actionName: CharSequence? = actionName
    val actionText: CharSequence? = actionText
    val data: CharSequence? = data
}