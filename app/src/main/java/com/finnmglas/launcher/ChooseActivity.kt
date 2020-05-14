package com.finnmglas.launcher

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_choose.*


class ChooseActivity : AppCompatActivity() {

    private fun listApps() {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pm = packageManager
        val apps = pm.getInstalledApplications(0)

        val installedApps: MutableList<ApplicationInfo> = ArrayList()

        // list
        for (app in apps) {

            if (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0) {
                //checks for flags; if flagged, check if updated system app
                installedApps.add(app);
            } else if (app.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                //it's a system app, not interested
            } else {
                //in this case, it should be a user-installed app
                installedApps.add(app)
            }
        }

        // ui
        for (app in installedApps) {
            //packageInfo.sourceDir
            pm.getLaunchIntentForPackage(app.packageName)

            // creating TextView programmatically
            val tvdynamic = TextView(this)
            tvdynamic.textSize = 20f
            tvdynamic.text = app.loadLabel(pm).toString()
            tvdynamic.setTextColor(Color.parseColor("#cccccc"));

            tvdynamic.setOnClickListener { startActivity(pm.getLaunchIntentForPackage(app.packageName)) }

            apps_list.addView(tvdynamic)
        }
    }

    @SuppressLint("SetTextI18n") // I do not care
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_choose)
        listApps()
    }
}
