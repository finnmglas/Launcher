package com.finnmglas.launcher.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

            setButtonColor(fragment_settings_apps_btn, vibrantColor)
            setButtonColor(fragment_settings_apps_install_btn, vibrantColor)
        }

        // Action - selecting buttons
        fragment_settings_apps_choose_up_btn.setOnClickListener{ chooseApp("upApp") }
        fragment_settings_apps_choose_down_btn.setOnClickListener{ chooseApp("downApp") }
        fragment_settings_apps_choose_left_btn.setOnClickListener{ chooseApp("leftApp") }
        fragment_settings_apps_choose_right_btn.setOnClickListener{ chooseApp("rightApp") }
        fragment_settings_apps_choose_vol_up_btn.setOnClickListener{ chooseApp("volumeUpApp")}
        fragment_settings_apps_choose_vol_down_btn.setOnClickListener{ chooseApp("volumeDownApp")}
        fragment_settings_apps_choose_double_click_btn.setOnClickListener { chooseApp("doubleClickApp") }

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