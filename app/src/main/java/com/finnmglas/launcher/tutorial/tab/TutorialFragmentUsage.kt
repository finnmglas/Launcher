package com.finnmglas.launcher.tutorial.tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finnmglas.launcher.*
import kotlinx.android.synthetic.main.tutorial_usage.*

/**
 * The [TutorialFragmentUsage] is a used as a tab in the TutorialActivity.
 *
 * Tells the user how his screen will look and how the app can be used
 */
class TutorialFragmentUsage(): Fragment(), UIObject {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tutorial_usage, container, false)
    }

    override fun onStart(){
        super<Fragment>.onStart()
        super<UIObject>.onStart()
    }

    override fun applyTheme() {
        tutorial_usage_container.setBackgroundColor(dominantColor)
    }
}