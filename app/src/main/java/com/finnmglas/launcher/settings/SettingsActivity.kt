package com.finnmglas.launcher.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.finnmglas.launcher.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.settings.*

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.finnmglas.launcher.settings.actions.SettingsFragmentActions
import com.finnmglas.launcher.settings.meta.SettingsFragmentMeta
import com.finnmglas.launcher.settings.theme.SettingsFragmentTheme


var intendedSettingsPause = false // know when to close

class SettingsActivity : AppCompatActivity() {

    /** Activity Lifecycle functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(
            when (getSavedTheme(this)) {
                "dark" -> R.style.darkTheme
                "finn" -> R.style.finnmglasTheme
                else -> R.style.customTheme
            }
        )

        setContentView(R.layout.settings)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sectionsPagerAdapter = SettingsSectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.settings_viewpager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.settings_tabs)
        tabs.setupWithViewPager(viewPager)

        // As older APIs somehow do not recognize the xml defined onClick
        settings_close.setOnClickListener() { finish() }
        // open device settings (see https://stackoverflow.com/a/62092663/12787264)
        settings_system.setOnClickListener {
            intendedSettingsPause = true
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    override fun onStart() {
        super.onStart()

        if (getSavedTheme(this) == "custom") {
            settings_container.setBackgroundColor(dominantColor)
            settings_appbar.setBackgroundColor(dominantColor)

            settings_system.setTextColor(vibrantColor)
            settings_close.setTextColor(vibrantColor)
            settings_tabs.setSelectedTabIndicatorColor(vibrantColor)
        }
    }

    override fun onResume() {
        super.onResume()
        intendedSettingsPause = false
    }

    override fun onPause() {
        super.onPause()
        if (!intendedSettingsPause) finish()
    }

    fun backHome(view: View) { finish() }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHOOSE_APP -> {
                val value = data?.getStringExtra("value")
                val forApp = data?.getStringExtra("forApp") ?: return

                // Save the new App to Preferences
                val sharedPref = this.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE)

                val editor : SharedPreferences.Editor = sharedPref.edit()
                editor.putString("action_$forApp", value.toString())
                editor.apply()

                loadSettings(sharedPref)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

}

private val TAB_TITLES = arrayOf(
    R.string.settings_tab_app,
    R.string.settings_tab_theme,
    R.string.settings_tab_launcher
)

/** Returns the fragment corresponding to the selected tab.*/
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