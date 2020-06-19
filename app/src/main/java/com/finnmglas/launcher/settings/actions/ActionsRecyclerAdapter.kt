package com.finnmglas.launcher.settings.actions

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finnmglas.launcher.*
import com.finnmglas.launcher.list.ListActivity
import com.finnmglas.launcher.libraries.FontAwesome
import com.finnmglas.launcher.settings.intendedSettingsPause
import java.lang.Exception


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
        actionsList = ArrayList()
        actionsList.add(ActionInfo(activity.getString(R.string.settings_choose_up),"upApp",
            upApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_choose_down),"downApp",
            downApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_choose_left), "leftApp",
            leftApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_choose_right), "rightApp",
            rightApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_choose_vol_up), "volumeUpApp",
            volumeUpApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_choose_vol_down), "volumeDownApp",
            volumeDownApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_choose_double_click), "doubleClickApp",
            doubleClickApp
        ))
        actionsList.add(ActionInfo(activity.getString(R.string.settings_choose_long_click), "longClickApp",
            longClickApp
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