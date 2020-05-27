package com.finnmglas.launcher.choose

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finnmglas.launcher.R
import com.finnmglas.launcher.extern.*


class AppsRecyclerAdapter(val activity: Activity, val action: String?, val forApp: String?):
    RecyclerView.Adapter<AppsRecyclerAdapter.ViewHolder>() {

    private val appsList: MutableList<AppInfo>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textView: TextView = itemView.findViewById(R.id.choose_row_app_name)
        var img: ImageView = itemView.findViewById(R.id.choose_row_app_icon) as ImageView

        override fun onClick(v: View) {
            val pos = adapterPosition
            val context: Context = v.context
            val appPackageName = appsList[pos].packageName.toString()

            when (action){
                "launch" -> {
                    val launchIntent: Intent = context.packageManager
                        .getLaunchIntentForPackage(appPackageName)!!
                    context.startActivity(launchIntent)
                }
                "pick" -> {
                    val returnIntent = Intent()
                    returnIntent.putExtra("value", appPackageName)
                    returnIntent.putExtra("forApp", forApp)
                    activity.setResult(REQUEST_CHOOSE_APP, returnIntent)
                    activity.finish()
                }
                "uninstall" -> {
                    val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
                    intent.data = Uri.parse("package:$appPackageName")
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
                    activity.startActivityForResult(intent, REQUEST_UNINSTALL)
                }
            }
        }

        init { itemView.setOnClickListener(this) }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val appLabel = appsList[i].label.toString()
        val appPackage = appsList[i].packageName.toString()
        val appIcon = appsList[i].icon
        val textView = viewHolder.textView
        textView.text = appLabel
        val imageView: ImageView = viewHolder.img
        imageView.setImageDrawable(appIcon)
    }

    override fun getItemCount(): Int { return appsList.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.recycler_apps_row, parent, false)
        return ViewHolder(view)
    }

    init {
        val pm: PackageManager = activity.packageManager
        appsList = ArrayList()
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)
        val allApps = pm.queryIntentActivities(i, 0)
        for (ri in allApps) {
            val app = AppInfo()
            app.label = ri.loadLabel(pm)
            app.packageName = ri.activityInfo.packageName
            app.icon = ri.activityInfo.loadIcon(pm)
            appsList.add(app)
        }
    }
}