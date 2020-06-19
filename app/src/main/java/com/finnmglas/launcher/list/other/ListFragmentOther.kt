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

/**
 * The [ListFragmentOther] is used as a tab in ListActivity,
 * when the `intention` for launching the ListActivity was to select something to be launched.
 *
 * It lists `other` things like internal activities to be launched as an action.
 */
class ListFragmentOther : Fragment() {

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