package com.finnmglas.launcher.tutorial.tab

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finnmglas.launcher.*
import kotlinx.android.synthetic.main.tutorial_tab.*

/**
 * The [TutorialFragmentTab] is a used as a tab in the TutorialActivity.
 *
 * It is used to display info in the tutorial
 */
class TutorialFragmentTab(var defaultApps: MutableList<String>, val isFirstTime: Boolean, val n: Int): Fragment(), UIObject {

    private var menuNumber = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tutorial_tab, container, false)
    }

    override fun onStart(){
        menuNumber = n
        loadMenu(context!!)

        super<Fragment>.onStart()
        super<UIObject>.onStart()
    }

    override fun applyTheme() {
        tutorial_tab_container.setBackgroundColor(dominantColor)
    }

    private fun loadMenu(context: Context) { // Context needed for packageManager
        val intro = resources.getStringArray(R.array.intro)

        if (menuNumber < intro.size){
            val entry = intro[menuNumber].split("|").toTypedArray() //heading|infoText|hintText|size

            tutorial_tab_heading.text = entry[0]
            if (entry[4] == "1" && isFirstTime)
                tutorial_tab_text.text = String.format(entry[1],
                    defaultApps[0], defaultApps[1], defaultApps[2], defaultApps[3], defaultApps[4], defaultApps[5])
            else if (entry[4] == "1" && !isFirstTime)
                tutorial_tab_text.text = String.format(entry[1],
                    "-", "-", "-", "-", "-", "-")
            else tutorial_tab_text.text = entry[1]
            tutorial_tab_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, entry[3].toFloat())

        } else if (menuNumber > intro.size) { // End intro
            if (isFirstTime){
                launcherPreferences.edit()
                    .putBoolean("startedBefore", true) // never auto run this again
                    .putLong("firstStartup", System.currentTimeMillis() / 1000L) // record first startup timestamp
                    .apply()
            }
            activity!!.finish()
        }
    }
}