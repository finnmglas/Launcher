package com.finnmglas.launcher.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.finnmglas.launcher.ChooseActivity
import com.finnmglas.launcher.R
import com.finnmglas.launcher.extern.*
import kotlinx.android.synthetic.main.fragment_settings_apps.*


/** The 'Apps' Tab associated Fragment in Settings */

class SettingsFragmentApps : Fragment() {

    /** Lifecycle functions */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings_apps, container, false)
    }

    override fun onStart() {

        if (getSavedTheme(context!!) == "custom") {
            fragment_settings_apps_container.setBackgroundColor(dominantColor)

            setButtonColor(fragment_settings_apps_choose_up_btn, vibrantColor)
            setButtonColor(fragment_settings_apps_choose_down_btn, vibrantColor)
            setButtonColor(fragment_settings_apps_choose_left_btn, vibrantColor)
            setButtonColor(fragment_settings_apps_choose_right_btn, vibrantColor)
            setButtonColor(fragment_settings_apps_choose_vol_up_btn, vibrantColor)
            setButtonColor(fragment_settings_apps_choose_vol_down_btn, vibrantColor)
            setButtonColor(fragment_settings_apps_choose_double_click_btn, vibrantColor)
            setButtonColor(fragment_settings_apps_choose_long_click_btn, vibrantColor)

            setButtonColor(fragment_settings_apps_btn, vibrantColor)
            setButtonColor(fragment_settings_apps_install_btn, vibrantColor)
        }

        /* TODO: Simplify and put all this repetitive stuff in a loop */

        if (upApp != "") {
            val appIcon = context!!.packageManager.getApplicationIcon(upApp)
            fragment_settings_apps_up_icon.setImageDrawable(appIcon)

            fragment_settings_apps_up_icon.visibility = View.VISIBLE
            fragment_settings_apps_choose_up_btn.visibility = View.GONE

            fragment_settings_apps_up_icon.setOnClickListener{ chooseApp("upApp") }
        } else {
            fragment_settings_apps_choose_up_btn.setOnClickListener{ chooseApp("upApp") }
        }

        if (upApp != "") {
            val appIcon = context!!.packageManager.getApplicationIcon(upApp)
            fragment_settings_apps_up_icon.setImageDrawable(appIcon)

            fragment_settings_apps_up_icon.visibility = View.VISIBLE
            fragment_settings_apps_choose_up_btn.visibility = View.GONE

            fragment_settings_apps_up_icon.setOnClickListener{ chooseApp("upApp") }
        } else {
            fragment_settings_apps_choose_up_btn.setOnClickListener{ chooseApp("upApp") }
        }

        if (downApp != "") {
            val appIcon = context!!.packageManager.getApplicationIcon(downApp)
            fragment_settings_apps_down_icon.setImageDrawable(appIcon)

            fragment_settings_apps_down_icon.visibility = View.VISIBLE
            fragment_settings_apps_choose_down_btn.visibility = View.GONE

            fragment_settings_apps_down_icon.setOnClickListener{ chooseApp("downApp") }
        } else {
            fragment_settings_apps_choose_down_btn.setOnClickListener{ chooseApp("downApp") }
        }

        if (leftApp != "") {
            val appIcon = context!!.packageManager.getApplicationIcon(leftApp)
            fragment_settings_apps_left_icon.setImageDrawable(appIcon)

            fragment_settings_apps_left_icon.visibility = View.VISIBLE
            fragment_settings_apps_choose_left_btn.visibility = View.GONE

            fragment_settings_apps_left_icon.setOnClickListener{ chooseApp("leftApp") }
        } else {
            fragment_settings_apps_choose_left_btn.setOnClickListener{ chooseApp("leftApp") }
        }

        if (rightApp != "") {
            val appIcon = context!!.packageManager.getApplicationIcon(rightApp)
            fragment_settings_apps_right_icon.setImageDrawable(appIcon)

            fragment_settings_apps_right_icon.visibility = View.VISIBLE
            fragment_settings_apps_choose_right_btn.visibility = View.GONE

            fragment_settings_apps_right_icon.setOnClickListener{ chooseApp("rightApp") }
        } else {
            fragment_settings_apps_choose_right_btn.setOnClickListener{ chooseApp("rightApp") }
        }

        if (volumeUpApp != "") {
            val appIcon = context!!.packageManager.getApplicationIcon(volumeUpApp)
            fragment_settings_apps_vol_up_icon.setImageDrawable(appIcon)

            fragment_settings_apps_vol_up_icon.visibility = View.VISIBLE
            fragment_settings_apps_choose_vol_up_btn.visibility = View.GONE

            fragment_settings_apps_vol_up_icon.setOnClickListener{ chooseApp("volumeUpApp") }
        } else {
            fragment_settings_apps_choose_vol_up_btn.setOnClickListener{ chooseApp("volumeUpApp") }
        }

        if (volumeDownApp != "") {
            val appIcon = context!!.packageManager.getApplicationIcon(volumeDownApp)
            fragment_settings_apps_vol_down_icon.setImageDrawable(appIcon)

            fragment_settings_apps_vol_down_icon.visibility = View.VISIBLE
            fragment_settings_apps_choose_vol_down_btn.visibility = View.GONE

            fragment_settings_apps_vol_down_icon.setOnClickListener{ chooseApp("volumeDownApp") }
        } else {
            fragment_settings_apps_choose_vol_down_btn.setOnClickListener{ chooseApp("volumeDownApp") }
        }

        if (doubleClickApp != "") {
            val appIcon = context!!.packageManager.getApplicationIcon(doubleClickApp)
            fragment_settings_apps_double_click_icon.setImageDrawable(appIcon)

            fragment_settings_apps_double_click_icon.visibility = View.VISIBLE
            fragment_settings_apps_choose_double_click_btn.visibility = View.GONE

            fragment_settings_apps_double_click_icon.setOnClickListener{ chooseApp("doubleClickApp") }
        } else {
            fragment_settings_apps_choose_double_click_btn.setOnClickListener{ chooseApp("doubleClickApp") }
        }

        if (longClickApp != "") {
            val appIcon = context!!.packageManager.getApplicationIcon(longClickApp)
            fragment_settings_apps_long_click_icon.setImageDrawable(appIcon)

            fragment_settings_apps_long_click_icon.visibility = View.VISIBLE
            fragment_settings_apps_choose_long_click_btn.visibility = View.GONE

            fragment_settings_apps_long_click_icon.setOnClickListener{ chooseApp("longClickApp") }
        } else {
            fragment_settings_apps_choose_long_click_btn.setOnClickListener{ chooseApp("longClickApp") }
        }

        /* TODO * End * */

        // App management buttons
        fragment_settings_apps_btn.setOnClickListener{
            val intent = Intent(this.context, ChooseActivity::class.java)
            intent.putExtra("action", "view")
            startActivity(intent)
        }
        fragment_settings_apps_install_btn.setOnClickListener{
            try {
                val rateIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/"))
                startActivity(rateIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this.context, getString(R.string.settings_toast_store_not_found), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHOOSE_APP -> {
                val value = data?.getStringExtra("value")
                val forApp = data?.getStringExtra("forApp") ?: return

                // Save the new App to Preferences
                val sharedPref = this.context!!.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE)

                val editor : SharedPreferences.Editor = sharedPref.edit()
                editor.putString("action_$forApp", value.toString())
                editor.apply()

                loadSettings(sharedPref)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /** Extra functions */

    private fun chooseApp(forAction: String) {
        val intent = Intent(this.context, ChooseActivity::class.java)
        intent.putExtra("action", "pick")
        intent.putExtra("forApp", forAction) // for which action we choose the app
        startActivityForResult(intent, REQUEST_CHOOSE_APP)
    }

}