package com.finnmglas.launcher.settings.actions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finnmglas.launcher.ChooseActivity
import com.finnmglas.launcher.R
import com.finnmglas.launcher.extern.FontAwesome
import com.finnmglas.launcher.extern.*
import java.lang.Exception


class ActionsRecyclerAdapter(val activity: Activity):
    RecyclerView.Adapter<ActionsRecyclerAdapter.ViewHolder>() {

    private val actionsList: MutableList<ActionInfo>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textView: TextView = itemView.findViewById(R.id.row_action_name)
        var img: ImageView = itemView.findViewById(R.id.row_app_icon) as ImageView
        var chooseButton: Button = itemView.findViewById(R.id.row_choose_button)
        var removeAction: FontAwesome = itemView.findViewById(R.id.row_remove_action)


        override fun onClick(v: View) {
            val pos = adapterPosition
            val context: Context = v.context
            val content = actionsList[pos]

        }

        init { itemView.setOnClickListener(this) }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val actionText = actionsList[i].actionText
        val actionName = actionsList[i].actionName
        val content = actionsList[i].content

        viewHolder.textView.text = actionText
        try {
            viewHolder.img.setImageDrawable(activity.packageManager.getApplicationIcon(content.toString()))
            viewHolder.img.setOnClickListener{ chooseApp(actionName.toString()) }
            viewHolder.removeAction.setOnClickListener{
                val sharedPref = activity.getSharedPreferences(
                    activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

                val editor : SharedPreferences.Editor = sharedPref.edit()
                editor.putString("action_$actionName", "") // clear it
                editor.apply()

                viewHolder.img.visibility = View.INVISIBLE
                viewHolder.removeAction.visibility = View.GONE
                viewHolder.chooseButton.visibility = View.VISIBLE
                viewHolder.chooseButton.setOnClickListener{ chooseApp(actionName.toString()) }
                if (getSavedTheme(activity) =="custom")
                    setButtonColor(viewHolder.chooseButton, vibrantColor)
            }

        } catch (e : Exception) {
            viewHolder.img.visibility = View.INVISIBLE
            viewHolder.removeAction.visibility = View.GONE
            viewHolder.chooseButton.visibility = View.VISIBLE
            viewHolder.chooseButton.setOnClickListener{ chooseApp(actionName.toString()) }
            if (getSavedTheme(activity) =="custom")
                setButtonColor(viewHolder.chooseButton, vibrantColor)
        }
    }

    override fun getItemCount(): Int { return actionsList.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.recycler_actions_row, parent, false)
        return ViewHolder(view)
    }

    init {
        actionsList = ArrayList()
        actionsList.add(ActionInfo("Swipe Up","upApp", upApp))
        actionsList.add(ActionInfo("Swipe Down","downApp", downApp))
        actionsList.add(ActionInfo("Swipe Left", "leftApp", leftApp))
        actionsList.add(ActionInfo("Swipe Right", "rightApp", rightApp))
        actionsList.add(ActionInfo("Volume Up", "volumeUpApp", volumeUpApp))
        actionsList.add(ActionInfo("Volume Down", "volumeDownApp", volumeDownApp))
        actionsList.add(ActionInfo("Double Click", "doubleClickApp", doubleClickApp))
        actionsList.add(ActionInfo("Long Click", "longClickApp", longClickApp))
    }

    /*  */
    private fun chooseApp(forAction: String) {
        val intent = Intent(activity, ChooseActivity::class.java)
        intent.putExtra("action", "pick")
        intent.putExtra("forApp", forAction) // for which action we choose the app
        activity.startActivityForResult(intent, REQUEST_CHOOSE_APP)
    }
}