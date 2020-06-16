package com.finnmglas.launcher.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finnmglas.launcher.R
import com.finnmglas.launcher.choose.other.OtherRecyclerAdapter
import com.finnmglas.launcher.extern.dominantColor
import com.finnmglas.launcher.extern.getSavedTheme
import kotlinx.android.synthetic.main.fragment_choose_other.*

/** The 'Other' Tab associated Fragment in the Chooser */

class ChooseFragmentOther : Fragment() {

    /** Lifecycle functions */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_other, container, false)
    }

    override fun onStart() {
        if (getSavedTheme(context!!) == "custom") {
            fragment_choose_other_container.setBackgroundColor(dominantColor)
        }

        // set up the list / recycler
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = OtherRecyclerAdapter(activity!!)

        fragment_choose_other_recycler_view.apply {
            // improve performance (since content changes don't change the layout size)
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        super.onStart()
    }
}