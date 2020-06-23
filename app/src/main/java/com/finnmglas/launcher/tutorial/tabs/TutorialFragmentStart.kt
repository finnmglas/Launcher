package com.finnmglas.launcher.tutorial.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finnmglas.launcher.*
import kotlinx.android.synthetic.main.tutorial_start.*

/**
 * The [TutorialFragmentStart] is a used as a tab in the TutorialActivity.
 *
 * It displays info about the app and gets the user into the tutorial
 */
class TutorialFragmentStart(): Fragment(), UIObject {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tutorial_start, container, false)
    }

    override fun onStart(){
        super<Fragment>.onStart()
        super<UIObject>.onStart()
    }

    override fun applyTheme() {
        tutorial_start_container.setBackgroundColor(dominantColor)

        // set icons
        val rightIcon = getString(R.string.fas_angle_double_right)

        tutorial_start_icon_right.text = rightIcon.repeat(3)
        tutorial_start_icon_right.setTextColor(vibrantColor)
        tutorial_start_icon_right.blink()

        tutorial_start_icon_right_2.text = rightIcon.repeat(3)
        tutorial_start_icon_right_2.setTextColor(vibrantColor)
        tutorial_start_icon_right_2.blink()
    }
}