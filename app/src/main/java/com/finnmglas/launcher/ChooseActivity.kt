package com.finnmglas.launcher

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

    /** Activity Lifecycle functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setTheme(
            when (getSavedTheme(this)) {
                "dark" -> R.style.darkTheme
                "finn" -> R.style.finnmglasTheme
                else -> R.style.finnmglasTheme
            }
        )
        setContentView(R.layout.activity_choose)

        // As older APIs somehow do not recognize the xml defined onClick
        close_chooser.setOnClickListener() { finish() }

        val bundle = intent.extras
        val action = bundle!!.getString("action") // why choose an app
        val forApp = bundle.getString("forApp") // which app we choose

        if (action == "launch")
            heading.text = getString(R.string.choose_title_launch)
        else if (action == "pick") {
            heading.text = getString(R.string.choose_title)
        }
        else if (action == "uninstall")
            heading.text = getString(R.string.choose_title_remove)

        /* Build Layout */

        for (resolveInfo in appsList) {
            val app = resolveInfo.activityInfo

            // creating TextView programmatically
            val tvdynamic = TextView(this)
            tvdynamic.textSize = 24f
            tvdynamic.text = app.loadLabel(packageManager).toString()
            tvdynamic.setTextColor(Color.parseColor("#cccccc"))

            if (action == "launch"){
                tvdynamic.setOnClickListener { startActivity(packageManager.getLaunchIntentForPackage(app.packageName)) }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, getString(R.string.choose_removed_toast), Toast.LENGTH_LONG).show()
                updateAppList(packageManager)
                finish()
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                Toast.makeText(this, getString(R.string.choose_not_removed_toast), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    /** onClick functions */

    fun backHome(view: View) { finish() }

}
