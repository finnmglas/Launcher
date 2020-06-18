package com.finnmglas.launcher.list.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finnmglas.launcher.R
import com.finnmglas.launcher.UIObject
import com.finnmglas.launcher.list.action
import com.finnmglas.launcher.list.forApp
import com.finnmglas.launcher.dominantColor
import com.finnmglas.launcher.getSavedTheme
import kotlinx.android.synthetic.main.list_apps.*


/** The 'Apps' Tab associated Fragment for the List */

class ListFragmentApps : Fragment(), UIObject {

    /** Lifecycle functions */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_apps, container, false)
    }

    override fun onStart() {
        super<Fragment>.onStart()
        super<UIObject>.onStart()

        setTheme()
        configure()
    }

    override fun setTheme() {
        if (getSavedTheme(context!!) == "custom") {
            list_apps_container.setBackgroundColor(dominantColor)
        }
    }

    override fun setOnClicks() { }

    override fun configure() {
        // set up the list / recycler
        list_apps_rview.apply {
            // improve performance (since content changes don't change the layout size)
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = AppsRecyclerAdapter(activity!!, action, forApp)
        }
    }
}