package com.finnmglas.launcher.list.apps

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finnmglas.launcher.R
import com.finnmglas.launcher.UIObject
import com.finnmglas.launcher.dominantColor
import com.finnmglas.launcher.list.forApp
import com.finnmglas.launcher.list.intention
import kotlinx.android.synthetic.main.list_apps.*


/**
 * The [ListFragmentApps] is used as a tab in ListActivity.
 *
 * It is a list of all installed applications that are can be launched.
 */
class ListFragmentApps : Fragment(), UIObject {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_apps, container, false)
    }

    override fun onStart() {
        super<Fragment>.onStart()
        super<UIObject>.onStart()
    }

    override fun applyTheme() {
        list_apps_container.setBackgroundColor(dominantColor)

        val id: Int = list_apps_searchview.context.resources
            .getIdentifier("android:id/search_src_text", null, null)
        list_apps_searchview.findViewById<TextView>(id).setTextColor(Color.WHITE)
    }

    override fun setOnClicks() { }

    override fun adjustLayout() {

        val appsRViewAdapter = AppsRecyclerAdapter(activity!!, intention, forApp)

        // set up the list / recycler
        list_apps_rview.apply {
            // improve performance (since content changes don't change the layout size)
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = appsRViewAdapter
        }

        list_apps_searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                appsRViewAdapter.filter(query);
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                appsRViewAdapter.filter(newText);
                return false
            }

        })
    }
}