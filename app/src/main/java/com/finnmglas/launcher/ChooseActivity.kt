package com.finnmglas.launcher

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_choose.*


class ChooseActivity : AppCompatActivity() {

    val UNINSTALL_REQUEST_CODE = 1

    fun backHome(view: View) {
        finish()
    }

    @SuppressLint("SetTextI18n") // I do not care
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_choose)

        val bundle = intent.extras
        val action = bundle!!.getString("action") // why choose an app
        val forApp = bundle.getString("forApp") // which app we choose

        if (action == "launch")
            heading.text = "Launch Apps"
        else if (action == "pick") {
            heading.text = "Choose App"
            subheading.text = forApp
        }
        else if (action == "uninstall")
            heading.text = "Uninstall Apps"

        /* Build Layout */

        // TODO: Make this more efficient, faster, generate the list before

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pm = packageManager
        val i = Intent(Intent.ACTION_MAIN)
        i.addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(i, 0)

        apps.sortBy { it.activityInfo.loadLabel(pm).toString() }

        for (resolveInfo in apps) {
            val app = resolveInfo.activityInfo
            pm.getLaunchIntentForPackage(app.packageName)

            // creating TextView programmatically
            val tvdynamic = TextView(this)
            tvdynamic.textSize = 24f
            tvdynamic.text = app.loadLabel(pm).toString()
            tvdynamic.setTextColor(Color.parseColor("#cccccc"))

            if (action == "launch"){
                tvdynamic.setOnClickListener { startActivity(pm.getLaunchIntentForPackage(app.packageName)) }
            }
            else if (action == "pick"){
                tvdynamic.setOnClickListener {
                    val returnIntent = Intent()
                    returnIntent.putExtra("value", app.packageName)
                    returnIntent.putExtra("forApp", forApp)
                    setResult(
                        5000,
                        returnIntent
                    )
                    finish()
                }
            }
            else if (action == "uninstall"){
                tvdynamic.setOnClickListener {
                    val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
                    intent.data = Uri.parse("package:" + app.packageName)
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
                    startActivityForResult(intent, UNINSTALL_REQUEST_CODE)
                }
            }
            apps_list.addView(tvdynamic)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    this,
                    "Removed the selected application",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                Toast.makeText(
                    this,
                    "Can't remove this app",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
}
