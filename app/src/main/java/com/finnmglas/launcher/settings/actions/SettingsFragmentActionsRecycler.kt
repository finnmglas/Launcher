package com.finnmglas.launcher.settings.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finnmglas.launcher.*
import com.finnmglas.launcher.list.ListActivity
import kotlinx.android.synthetic.main.settings_actions_recycler.*
import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finnmglas.launcher.libraries.FontAwesome
import com.finnmglas.launcher.settings.intendedSettingsPause
import java.lang.Exception

/**
 *  The [SettingsFragmentActionsRecycler] is a fragment containing the [ActionsRecyclerAdapter],
 *  which displays all selected actions / apps.
 *
 *  It is used in the Tutorial and in Settings
 */
class SettingsFragmentActionsRecycler : Fragment(), UIObject {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_actions_recycler, container, false)
    }

    override fun onStart() {
        super<Fragment>.onStart()

        // set up the list / recycler
        val actionViewManager = LinearLayoutManager(context)
        val actionViewAdapter = ActionsRecyclerAdapter( activity!! )

        settings_actions_rview.apply {
            // improve performance (since content changes don't change the layout size)
            setHasFixedSize(true)
            layoutManager = actionViewManager
            adapter = actionViewAdapter
        }

        super<UIObject>.onStart()
    }
}

class ActionsRecyclerAdapter(val activity: Activity):
    RecyclerView.Adapter<ActionsRecyclerAdapter.ViewHolder>() {

    private val actionsList: MutableList<ActionInfo>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textView: TextView = itemView.findViewById(R.id.settings_actions_row_name)
        var fontAwesome: FontAwesome = itemView.findViewById(R.id.settings_actions_row_icon)
        var img: ImageView = itemView.findViewById(R.id.settings_actions_row_icon_img) as ImageView
        var chooseButton: Button = itemView.findViewById(R.id.settings_actions_row_button_choose)
        var removeAction: FontAwesome = itemView.findViewById(R.id.settings_actions_row_remove)

        override fun onClick(v: View) { }

        init { itemView.setOnClickListener(this) }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val actionText = actionsList[i].actionText
        val actionName = actionsList[i].actionName
        val content = actionsList[i].data

        viewHolder.textView.text = actionText

        viewHolder.removeAction.setOnClickListener{

            launcherPreferences.edit()
                .putString("action_$actionName", "") // clear it
                .apply()

            loadSettings() // apply new settings to the app

            viewHolder.fontAwesome.visibility = View.INVISIBLE
            viewHolder.img.visibility = View.INVISIBLE
            viewHolder.removeAction.visibility = View.GONE
            viewHolder.chooseButton.visibility = View.VISIBLE
            viewHolder.chooseButton.setOnClickListener{ chooseApp(actionName.toString()) }

            setButtonColor(viewHolder.chooseButton, vibrantColor)
        }

        if (content!!.startsWith("launcher")) {
            // Set fontAwesome icon
            viewHolder.fontAwesome.visibility = View.VISIBLE
            viewHolder.fontAwesome.setOnClickListener{ chooseApp(actionName.toString()) }

            when (content.split(":")[1]) {
                "settings" ->
                    viewHolder.fontAwesome.text = activity.getString(R.string.fas_settings)
                "choose" ->
                    viewHolder.fontAwesome.text = activity.getString(R.string.fas_bars)
                "volumeUp" ->
                    viewHolder.fontAwesome.text = activity.getString(R.string.fas_plus)
                "volumeDown" ->
                    viewHolder.fontAwesome.text = activity.getString(R.string.fas_minus)
                "nextTrack" ->
                    viewHolder.fontAwesome.text = activity.getString(R.string.fas_forward)
                "previousTrack" ->
                    viewHolder.fontAwesome.text = activity.getString(R.string.fas_back)
            }
        } else {
            // Set image icon (by packageName)
            try {
                viewHolder.img.setImageDrawable(activity.packageManager.getApplicationIcon(content.toString()))
                viewHolder.img.setOnClickListener{ chooseApp(actionName.toString()) }

                if (getSavedTheme(activity) == "dark") transformGrayscale(
                    viewHolder.img
                )

            } catch (e : Exception) { // the button is shown, user asked to select an action
                viewHolder.img.visibility = View.INVISIBLE
                viewHolder.removeAction.visibility = View.GONE
                viewHolder.chooseButton.visibility = View.VISIBLE
                viewHolder.chooseButton.setOnClickListener{ chooseApp(actionName.toString()) }
                setButtonColor(viewHolder.chooseButton, vibrantColor)
            }
        }
    }

    override fun getItemCount(): Int { return actionsList.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.settings_actions_row, parent, false)
        return ViewHolder(view)
    }

    init {
        val doubleActions = launcherPreferences.getBoolean(PREF_DOUBLE_ACTIONS_ENABLED, false)

        actionsList = ArrayList()
        actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_up),"upApp",
            upApp
        ))
        if ( doubleActions) actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_double_up), "doubleUpApp",
            doubleUpApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_down),"downApp",
            downApp
        ))
        if ( doubleActions) actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_double_down), "doubleDownApp",
            doubleDownApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_left), "leftApp",
            leftApp
        ))
        if ( doubleActions) actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_double_left), "doubleLeftApp",
            doubleLeftApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_right), "rightApp",
            rightApp
        ))
        if ( doubleActions) actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_double_right), "doubleRightApp",
            doubleRightApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_vol_up), "volumeUpApp",
            volumeUpApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_vol_down), "volumeDownApp",
            volumeDownApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_double_click), "doubleClickApp",
            doubleClickApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_long_click), "longClickApp",
            longClickApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_time), "timeApp",
            timeApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_apps_date), "dateApp",
            dateApp
        ))
    }

    /*  */
    private fun chooseApp(forAction: String) {
        val intent = Intent(activity, ListActivity::class.java)
        intent.putExtra("intention", "pick")
        intent.putExtra("forApp", forAction) // for which action we choose the app
        intendedSettingsPause = true
        activity.startActivityForResult(intent,
            REQUEST_CHOOSE_APP
        )
    }
}