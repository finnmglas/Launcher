package com.finnmglas.launcher.list.apps

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finnmglas.launcher.*
import com.finnmglas.launcher.libraries.*
import com.finnmglas.launcher.list.intendedChoosePause

/**
 * A [RecyclerView] (efficient scrollable list) containing all apps on the users device.
 * The apps details are represented by [AppInfo].
 *
 * @param activity - the activity this is in
 * @param intention - why the list is displayed ("view", "pick")
 * @param forApp - the action which an app is chosen for (when the intention is "pick")
 */
class AppsRecyclerAdapter(val activity: Activity,
                          val intention: String? = "view",
                          val forApp: String? = ""):
    RecyclerView.Adapter<AppsRecyclerAdapter.ViewHolder>() {

    private val appsList: MutableList<AppInfo>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textView: TextView = itemView.findViewById(R.id.list_apps_row_name)
        var img: ImageView = itemView.findViewById(R.id.list_apps_row_icon) as ImageView
        var menuDots: FontAwesome = itemView.findViewById(R.id.list_apps_row_menu)

        override fun onClick(v: View) {
            val pos = adapterPosition
            val context: Context = v.context
            val appPackageName = appsList[pos].packageName.toString()

            when (intention){
                "view" -> {
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
            }
        }

        init { itemView.setOnClickListener(this) }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val appLabel = appsList[i].label.toString()
        val appPackageName = appsList[i].packageName.toString()
        val appIcon = appsList[i].icon
        val isSystemApp = appsList[i].isSystemApp

        viewHolder.textView.text = appLabel
        viewHolder.img.setImageDrawable(appIcon)

        if (getSavedTheme(activity) == "dark") transformGrayscale(
            viewHolder.img
        )

        // decide when to show the options popup menu about
        if (isSystemApp || intention == "pick") {
            viewHolder.menuDots.visibility = View.INVISIBLE
        }
        else {
            viewHolder.menuDots.visibility = View.VISIBLE

            viewHolder.menuDots.setOnClickListener{ showOptionsPopup(viewHolder, appPackageName) }
            viewHolder.menuDots.setOnLongClickListener{ showOptionsPopup(viewHolder, appPackageName) }
            viewHolder.textView.setOnLongClickListener{ showOptionsPopup(viewHolder, appPackageName) }
            viewHolder.img.setOnLongClickListener{ showOptionsPopup(viewHolder, appPackageName) }

            // ensure onClicks are actually caught
            viewHolder.textView.setOnClickListener{ viewHolder.onClick(viewHolder.textView) }
            viewHolder.img.setOnClickListener{ viewHolder.onClick(viewHolder.img) }
        }
    }

    private fun showOptionsPopup(viewHolder: ViewHolder, appPackageName: String): Boolean {
        //create the popup menu

        val popup = PopupMenu(activity, viewHolder.menuDots)
        popup.inflate(R.menu.menu_app)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.app_menu_delete -> { // delete
                    intendedChoosePause = true
                    val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
                    intent.data = Uri.parse("package:$appPackageName")
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
                    activity.startActivityForResult(intent,
                        REQUEST_UNINSTALL
                    )

                    true
                }
                R.id.app_menu_info -> { // open app settings
                    intendedChoosePause = true
                    openAppSettings(
                        appPackageName,
                        activity
                    )
                    true
                }
                else -> false
            }
        }

        popup.show()
        return true
    }

    override fun getItemCount(): Int { return appsList.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.list_apps_row, parent, false)
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
        appsList.sortBy { it.label.toString() }
    }
}
