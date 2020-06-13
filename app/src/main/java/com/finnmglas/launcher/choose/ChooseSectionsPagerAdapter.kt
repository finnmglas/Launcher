package com.finnmglas.launcher.choose

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.finnmglas.launcher.*

private val TAB_TITLES = arrayOf(
    R.string.choose_tab_app,
    R.string.choose_tab_other
)

/** Returns the fragment corresponding to the selected tab.*/
class ChooseSectionsPagerAdapter(private val context: Context, fm: FragmentManager)
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