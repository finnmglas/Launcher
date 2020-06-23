package com.finnmglas.launcher.tutorial.tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finnmglas.launcher.*
import kotlinx.android.synthetic.main.tutorial_setup.*

/**
 * The [TutorialFragmentSetup] is a used as a tab in the TutorialActivity.
 *
 * It is used to display info in the tutorial
 */
class TutorialFragmentSetup(): Fragment(), UIObject {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tutorial_setup, container, false)
    }

    override fun onStart(){
        var defaultApps = mutableListOf<String>()
        defaultApps = resetSettings(context!!) // UP, DOWN, RIGHT, LEFT, VOLUME_UP, VOLUME_DOWN

        super<Fragment>.onStart()
        super<UIObject>.onStart()
    }

    override fun applyTheme() {
        tutorial_setup_container.setBackgroundColor(dominantColor)
    }
}