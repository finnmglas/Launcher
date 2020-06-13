package com.finnmglas.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.finnmglas.launcher.choose.AppsRecyclerAdapter
import com.finnmglas.launcher.extern.*
import kotlinx.android.synthetic.main.activity_choose.*

var intendedChoosePause = false // know when to close

class ChooseActivity : AppCompatActivity() {

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

        if (getSavedTheme(this) == "custom") {
            activity_choose_container.setBackgroundColor(dominantColor)
            activity_choose_app_bar.setBackgroundColor(dominantColor)
            activity_choose_close.setTextColor(vibrantColor)
        }

        // As older APIs somehow do not recognize the xml defined onClick
        activity_choose_close.setOnClickListener() { finish() }

        // get info about which action this activity is open for
        val bundle = intent.extras
        val action = bundle!!.getString("action") // why choose an app
        val forApp = bundle.getString("forApp") // which app we choose

        when (action) {
            "view" -> activity_choose_heading.text = getString(R.string.choose_title_view)
            "pick" -> activity_choose_heading.text = getString(R.string.choose_title)
        }

        // set up the list / recycler
        viewManager = LinearLayoutManager(this)
        viewAdapter = AppsRecyclerAdapter( this, action, forApp)

        /*activity_choose_apps_recycler_view.apply {
            // improve performance (since content changes don't change the layout size)
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }*/
    }

    override fun onPause() {
        super.onPause()
        intendedSettingsPause = false
        if(!intendedChoosePause) finish()
    }

    override fun onResume() {
        super.onResume()
        intendedChoosePause = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UNINSTALL) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, getString(R.string.choose_removed_toast), Toast.LENGTH_LONG).show()
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
