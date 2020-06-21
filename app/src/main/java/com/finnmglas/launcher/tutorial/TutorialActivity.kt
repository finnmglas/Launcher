package com.finnmglas.launcher.tutorial

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.finnmglas.launcher.*
import com.finnmglas.launcher.tutorial.tab.TutorialFragmentTab
import kotlinx.android.synthetic.main.tutorial.*

/**
 * The [TutorialActivity] is displayed automatically on new installations.
 * It can also be opened from Settings.
 *
 * It tells the user about the concept behind launcher
 * and helps with the setup process (on new installations)
 */
class TutorialActivity: AppCompatActivity(), UIObject {

    private var defaultApps = mutableListOf<String>()
    private var isFirstTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise layout
        setContentView(R.layout.tutorial)

        // Check if it is the first time starting the app
        isFirstTime = !launcherPreferences.getBoolean("startedBefore", false)
        if (isFirstTime)
            defaultApps = resetSettings(this) // UP, DOWN, RIGHT, LEFT, VOLUME_UP, VOLUME_DOWN
        else tutorial_appbar.visibility = View.VISIBLE

        // set up tabs and swiping in settings
        val sectionsPagerAdapter = TutorialSectionsPagerAdapter(this, supportFragmentManager, defaultApps, isFirstTime)
        val viewPager: ViewPager = findViewById(R.id.tutorial_viewpager)
        viewPager.adapter = sectionsPagerAdapter
        //val tabs: TabLayout = findViewById(R.id.tutorial_tabs)
        //tabs.setupWithViewPager(viewPager)
    }

    override fun onStart() {
        super<AppCompatActivity>.onStart()
        super<UIObject>.onStart()
    }

    override fun applyTheme() {
        tutorial_appbar.setBackgroundColor(dominantColor)
        tutorial_container.setBackgroundColor(dominantColor)
        tutorial_close.setTextColor(vibrantColor)
    }

    override fun setOnClicks() {
        tutorial_close.setOnClickListener() { finish() }
    }
}

class TutorialSectionsPagerAdapter(private val context: Context, fm: FragmentManager,
                                   val defaultApps: MutableList<String>, val isFirstTime: Boolean)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> TutorialFragmentTab(defaultApps, isFirstTime, position)
            1 -> TutorialFragmentTab(defaultApps, isFirstTime, position)
            else -> TutorialFragmentTab(defaultApps, isFirstTime, position)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return position.toString()
    }

    override fun getCount(): Int { return 20 }
}