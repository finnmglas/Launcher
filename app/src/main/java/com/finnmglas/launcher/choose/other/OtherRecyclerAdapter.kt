package com.finnmglas.launcher.choose.other

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finnmglas.launcher.R
import com.finnmglas.launcher.extern.*
import com.finnmglas.launcher.choose.forApp

/* Will only be used if an app / action is picked */
class OtherRecyclerAdapter(val activity: Activity):
    RecyclerView.Adapter<OtherRecyclerAdapter.ViewHolder>() {

    private val othersList: MutableList<OtherInfo>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textView: TextView = itemView.findViewById(R.id.row_other_name)


        override fun onClick(v: View) {
            val pos = adapterPosition
            val content = othersList[pos]

            returnChoiceIntent(forApp, content.data.toString())
        }

        init { itemView.setOnClickListener(this) }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val otherLabel = othersList[i].label.toString()
        val otherData = othersList[i].data.toString()

        viewHolder.textView.text = otherLabel
    }

    override fun getItemCount(): Int { return othersList.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.recycler_other_row, parent, false)
        return ViewHolder(view)
    }

    init {
        othersList = ArrayList()
        othersList.add(OtherInfo("Launcher Settings", "launcher:settings"))
        othersList.add(OtherInfo("Launcher AppsList", "launcher:choose"))
    }

    /*  */
    private fun returnChoiceIntent(forAction: String, value: String) {
        val returnIntent = Intent()
        returnIntent.putExtra("value", value)
        returnIntent.putExtra("forApp", forApp)
        activity.setResult(REQUEST_CHOOSE_APP, returnIntent)
        activity.finish()
    }
}