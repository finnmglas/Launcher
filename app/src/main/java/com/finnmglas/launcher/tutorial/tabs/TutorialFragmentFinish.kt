package com.finnmglas.launcher.tutorial.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finnmglas.launcher.*
import kotlinx.android.synthetic.main.tutorial_finish.*

/**
 * The [TutorialFragmentFinish] is a used as a tab in the TutorialActivity.
 *
 * It is used to display further resources and let the user start Launcher
 */
class TutorialFragmentFinish(): Fragment(), UIObject {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tutorial_finish, container, false)
    }

    override fun onStart() {
        super<Fragment>.onStart()
        super<UIObject>.onStart()
    }

    override fun applyTheme() {
        tutorial_finish_container.setBackgroundColor(dominantColor)
        setButtonColor(tutorial_finish_button_start, vibrantColor)
    }

    override fun setOnClicks() {
        super.setOnClicks()
        tutorial_finish_button_start.setOnClickListener{ finishTutorial() }
    }

    override fun adjustLayout() {
        super.adjustLayout()

        // Different text if opened again later (from settings)
        if (launcherPreferences.getBoolean("startedBefore", false))
            tutorial_finish_button_start.text = "Back to Settings"
    }

    private fun finishTutorial() {
        if (!launcherPreferences.getBoolean("startedBefore", false)){
            launcherPreferences.edit()
                .putBoolean("startedBefore", true) // never auto run this again
                .putLong("firstStartup", System.currentTimeMillis() / 1000L) // record first startup timestamp
                .apply()
        }
        activity!!.finish()
    }
}