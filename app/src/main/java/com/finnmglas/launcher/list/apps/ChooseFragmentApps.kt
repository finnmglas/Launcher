package com.finnmglas.launcher.list.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finnmglas.launcher.R
import com.finnmglas.launcher.list.action
import com.finnmglas.launcher.list.forApp
import com.finnmglas.launcher.dominantColor
import com.finnmglas.launcher.getSavedTheme
import kotlinx.android.synthetic.main.list_apps.*


/** The 'Apps' Tab associated Fragment in the Chooser */

class ChooseFragmentApps : Fragment() {

    /** Lifecycle functions */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_apps, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (getSavedTheme(context!!) == "custom") {
            list_apps_container.setBackgroundColor(dominantColor)
        }

        // set up the list / recycler
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = AppsRecyclerAdapter(
            activity!!,
            action,
            forApp
        )

        list_apps_rview.apply {
            // improve performance (since content changes don't change the layout size)
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }
}