package com.finnmglas.launcher.list.other

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finnmglas.launcher.R
import com.finnmglas.launcher.dominantColor
import com.finnmglas.launcher.getSavedTheme
import kotlinx.android.synthetic.main.list_other.*

/** The 'Other' Tab associated Fragment in the Chooser */

class ListFragmentOther : Fragment() {

    /** Lifecycle functions */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_other, container, false)
    }

    override fun onStart() {
        if (getSavedTheme(context!!) == "custom") {
            list_other_container.setBackgroundColor(dominantColor)
        }

        // set up the list / recycler
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = OtherRecyclerAdapter(activity!!)

        list_other_rview.apply {
            // improve performance (since content changes don't change the layout size)
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        super.onStart()
    }
}