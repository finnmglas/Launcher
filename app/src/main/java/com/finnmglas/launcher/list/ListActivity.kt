package com.finnmglas.launcher.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.finnmglas.launcher.*
import com.finnmglas.launcher.settings.intendedSettingsPause
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.list.*

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.finnmglas.launcher.list.apps.ChooseFragmentApps
import com.finnmglas.launcher.list.other.ChooseFragmentOther

var intendedChoosePause = false // know when to close

// TODO: Better solution for this (used in list-fragments)
var action = "view"
var forApp = ""


class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setTheme(
            when (getSavedTheme(this)) {
                "dark" -> R.style.darkTheme
                "finn" -> R.style.finnmglasTheme
                else -> R.style.finnmglasTheme
            }
        )
        setContentView(R.layout.list)

        if (getSavedTheme(this) == "custom") {
            list_container.setBackgroundColor(dominantColor)
            list_appbar.setBackgroundColor(dominantColor)
            list_close.setTextColor(vibrantColor)

            list_tabs.setSelectedTabIndicatorColor(vibrantColor)
        }

        // As older APIs somehow do not recognize the xml defined onClick
        list_close.setOnClickListener() { finish() }

        // get info about which action this activity is open for
        val bundle = intent.extras
        if (bundle != null) {
            action = bundle.getString("action")!! // why choose an app
            if (action != "view")
                forApp = bundle.getString("forApp")!! // which app we choose
        }

        // Hide tabs for the "view" action
        if (action == "view") {
            list_tabs.visibility = View.GONE
        }

        when (action) {
            "view" -> list_heading.text = getString(R.string.choose_title_view)
            "pick" -> list_heading.text = getString(R.string.choose_title)
        }

        val sectionsPagerAdapter = ListSectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.list_viewpager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.list_tabs)
        tabs.setupWithViewPager(viewPager)
    }

    override fun onPause() {
        super.onPause()
        intendedSettingsPause = false
        if(!intendedChoosePause) finish()
    }

    override fun onResume() {
        super.onResume()
        intendedChoosePause = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UNINSTALL) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, getString(R.string.choose_removed_toast), Toast.LENGTH_LONG).show()
                finish()
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                Toast.makeText(this, getString(R.string.choose_not_removed_toast), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    /** onClick functions */

    fun backHome(view: View) { finish() }

}

private val TAB_TITLES = arrayOf(
    R.string.choose_tab_app,
    R.string.choose_tab_other
)

/** Returns the fragment corresponding to the selected tab.*/
class ListSectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> ChooseFragmentApps()
            1 -> ChooseFragmentOther()
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return when (action) {
            "view" -> 1
            else -> 2
        }
    }
}