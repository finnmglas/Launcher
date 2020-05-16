package com.finnmglas.launcher

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


//TODO Make Settings scrollable as soon as more are added

class SettingsActivity : AppCompatActivity() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 5000)
        {
            val value = data?.getStringExtra("value")
            val forApp = data?.getStringExtra("forApp") ?: return

            // Save the new App to Preferences
            val sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE)

            val editor :SharedPreferences.Editor = sharedPref.edit()
            editor.putString("action_$forApp", value.toString())
            editor.apply()

            loadSettings(sharedPref)
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun chooseDownApp(view: View) {chooseApp("downApp")}
    fun chooseUpApp(view: View) {chooseApp("upApp")}
    fun chooseLeftApp(view: View) {chooseApp("leftApp")}
    fun chooseRightApp(view: View) {chooseApp("rightApp")}
    fun chooseVolumeDownApp(view: View) {chooseApp("volumeDownApp")}
    fun chooseVolumeUpApp(view: View) {chooseApp("volumeUpApp")}

    fun chooseApp(forAction :String) {
        val intent = Intent(this, ChooseActivity::class.java)
        intent.putExtra("action", "pick")
        intent.putExtra("forApp", forAction) // for which action we choose the app
        startActivityForResult(intent, 5000)
    }

    fun chooseUninstallApp(view: View) {
        val intent = Intent(this, ChooseActivity::class.java)
        intent.putExtra("action", "uninstall")
        startActivity(intent)
    }

    fun chooseLaunchApp(view: View) {
        val intent = Intent(this, ChooseActivity::class.java)
        intent.putExtra("action", "launch")
        startActivity(intent)
    }

    fun openNewTabWindow(urls: String, context : Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }

    fun openFinnWebsite(view: View) {
        openNewTabWindow("https://www.finnmglas.com/", this)
    }

    fun openGithubRepo(view: View) {
        openNewTabWindow("https://github.com/finnmglas/Launcher#en", this)
    }

    fun backHome(view: View) {
        finish()
    }

    fun setLauncher(view: View) {
        // on newer sdk: choose launcher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val callHomeSettingIntent = Intent(Settings.ACTION_HOME_SETTINGS)
            startActivity(callHomeSettingIntent)
        }
        // on older sdk: manage app details
        else {
            AlertDialog.Builder(this)
                .setTitle("App Info")
                .setMessage("Your device does not support this feature. Manage application details instead?")
                .setPositiveButton(android.R.string.yes,
                    DialogInterface.OnClickListener { dialog, which ->
                        try {
                            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:$packageName")
                            startActivity(intent)
                        } catch ( e : ActivityNotFoundException) {
                            val intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                            startActivity(intent)
                        }
                    })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show()
        }
    }

    // Show a dialog prompting for confirmation
    fun resetSettingsClick(view: View) {
        AlertDialog.Builder(this)
            .setTitle("Reset Settings")
            .setMessage("This will discard all your App Choices. Sure you want to continue?")
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    resetSettings(this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE), this)
                    finish()
                })
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    @SuppressLint("SetTextI18n") // I do not care
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_settings)
    }
}
