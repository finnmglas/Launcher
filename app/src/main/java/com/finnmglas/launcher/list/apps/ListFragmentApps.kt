package com.finnmglas.launcher.list.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finnmglas.launcher.R
import com.finnmglas.launcher.UIObject
import com.finnmglas.launcher.dominantColor
import com.finnmglas.launcher.list.forApp
import com.finnmglas.launcher.list.intention
import com.finnmglas.launcher.openSoftKeyboard
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
        list_apps_searchview.setBackgroundColor(dominantColor)
        list_apps_searchbar.setBackgroundColor(dominantColor)
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

        list_apps_searchview.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                appsRViewAdapter.filter(query);
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                appsRViewAdapter.filter(newText);
                return false
            }
        })

        openSoftKeyboard(context!!, list_apps_searchview)
    }
}