package com.finnmglas.launcher.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.finnmglas.launcher.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.settings.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.finnmglas.launcher.settings.actions.SettingsFragmentActions
import com.finnmglas.launcher.settings.theme.SettingsFragmentTheme
import com.finnmglas.launcher.settings.meta.SettingsFragmentMeta


var intendedSettingsPause = false // know when to close

/**
 * The [SettingsActivity] is a tabbed activity:
 *
 * | Actions    |   Choose apps or intents to be launched   | [SettingsFragmentActions] |
 * | Theme      |   Select a theme / Customize              | [SettingsFragmentTheme]   |
 * | Meta       |   About Launcher / Contact etc.           | [SettingsFragmentMeta]    |
 *
 * Settings are closed automatically if the activity goes `onPause` unexpectedly.
 */
class SettingsActivity: AppCompatActivity(), UIObject {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise layout
        setContentView(R.layout.settings)

        // set up tabs and swiping in settings
        val sectionsPagerAdapter = SettingsSectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.settings_viewpager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.settings_tabs)
        tabs.setupWithViewPager(viewPager)
    }

    override fun onStart() {
        super<AppCompatActivity>.onStart()
        super<UIObject>.onStart()
    }

    override fun onResume() {
        super.onResume()
        intendedSettingsPause = false
    }

    override fun onPause() {
        super.onPause()
        if (!intendedSettingsPause) finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHOOSE_APP -> {
                val value = data?.getStringExtra("value")
                val forApp = data?.getStringExtra("forApp") ?: return

                launcherPreferences.edit()
                    .putString("action_$forApp", value.toString())
                    .apply()

                loadSettings()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun applyTheme() {
        settings_container.setBackgroundColor(dominantColor)
        settings_appbar.setBackgroundColor(dominantColor)

        settings_system.setTextColor(vibrantColor)
        settings_close.setTextColor(vibrantColor)
        settings_tabs.setSelectedTabIndicatorColor(vibrantColor)
    }

    override fun setOnClicks(){
        // As older APIs somehow do not recognize the xml defined onClick
        settings_close.setOnClickListener() { finish() }
        // open device settings (see https://stackoverflow.com/a/62092663/12787264)
        settings_system.setOnClickListener {
            intendedSettingsPause = true
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }
}

private val TAB_TITLES = arrayOf(
    R.string.settings_tab_app,
    R.string.settings_tab_theme,
    R.string.settings_tab_launcher
)

class SettingsSectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> SettingsFragmentActions()
            1 -> SettingsFragmentTheme()
            2 -> SettingsFragmentMeta()
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int { return 3 }
}