package com.finnmglas.launcher.list.other

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finnmglas.launcher.R
import com.finnmglas.launcher.REQUEST_CHOOSE_APP
import com.finnmglas.launcher.libraries.*
import com.finnmglas.launcher.list.forApp

/**
 * The [OtherRecyclerAdapter] will only be displayed in the ListActivity,
 * if an app / intent / etc. is picked to be launched when an action is recognized.
 *
 * It lists `other` things to be launched that are not really represented by a URI,
 * rather by Launcher- internal conventions.
 */
class OtherRecyclerAdapter(val activity: Activity):
    RecyclerView.Adapter<OtherRecyclerAdapter.ViewHolder>() {

    private val othersList: MutableList<OtherInfo>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textView: TextView = itemView.findViewById(R.id.list_other_row_name)
        var iconView: FontAwesome = itemView.findViewById(R.id.list_other_row_icon)


        override fun onClick(v: View) {
            val pos = adapterPosition
            val content = othersList[pos]

            returnChoiceIntent(forApp, content.data.toString())
        }

        init { itemView.setOnClickListener(this) }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val otherLabel = othersList[i].label.toString()
        val icon = othersList[i].icon.toString()

        viewHolder.textView.text = otherLabel
        viewHolder.iconView.text = icon
    }

    override fun getItemCount(): Int { return othersList.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.list_other_row, parent, false)
        return ViewHolder(view)
    }

    init {
        othersList = ArrayList()
        othersList.add(
            OtherInfo(activity.getString(R.string.list_other_settings),
            "launcher:settings",
                activity.getString(R.string.fas_settings)))
        othersList.add(
            OtherInfo(activity.getString(R.string.list_other_list),
                "launcher:choose",
                activity.getString(R.string.fas_bars)))
        othersList.add(
            OtherInfo(activity.getString(R.string.list_other_volume_up),
                "launcher:volumeUp",
                activity.getString(R.string.fas_plus)))
        othersList.add(
            OtherInfo(activity.getString(R.string.list_other_volume_down),
                "launcher:volumeDown",
                activity.getString(R.string.fas_minus)))

        if (Build.VERSION.SDK_INT >= 19) { // requires Android KitKat +
            othersList.add(
                OtherInfo(
                    activity.getString(R.string.list_other_track_next),
                    "launcher:nextTrack",
                    activity.getString(R.string.fas_forward)
                )
            )
            othersList.add(
                OtherInfo(
                    activity.getString(R.string.list_other_track_previous),
                    "launcher:previousTrack",
                    activity.getString(R.string.fas_back)
                )
            )
        }
    }

    private fun returnChoiceIntent(forAction: String, value: String) {
        val returnIntent = Intent()
        returnIntent.putExtra("value", value)
        returnIntent.putExtra("forApp", forApp)
        activity.setResult(REQUEST_CHOOSE_APP, returnIntent)
        activity.finish()
    }
}