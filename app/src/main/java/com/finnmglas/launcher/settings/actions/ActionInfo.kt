package com.finnmglas.launcher.settings.actions

/**
 * Stores information used in [ActionsRecyclerAdapter] rows.
 */
class ActionInfo(actionText: CharSequence, actionName: CharSequence, content: CharSequence) {
    val actionName: CharSequence? = actionName
    val actionText: CharSequence? = actionText
    val content: CharSequence? = content
}