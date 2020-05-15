package com.finnmglas.launcher

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity


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

            // Update running App
            if (forApp == "downApp") downApp = value.toString()
            else if (forApp == "upApp") upApp = value.toString()
            else if (forApp == "leftApp") leftApp = value.toString()
            else if (forApp == "rightApp") rightApp = value.toString()
            else if (forApp == "volumeDownApp") volumeDownApp = value.toString()
            else if (forApp == "volumeUpApp") volumeUpApp = value.toString()

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
        intent.putExtra("action", "pick") // why choose an app
        intent.putExtra("forApp", forAction) // which app we choose
        startActivityForResult(intent, 5000)
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
        openNewTabWindow("https://github.com/finnmglas/Launcher", this)
    }

    fun backHome(view: View) {
        finish()
    }

    // Show a dialog prompting for confirmation
    fun resetSettingsClick(view: View) {
        AlertDialog.Builder(this)
            .setTitle("Reset Settings")
            .setMessage("This will discard all your App Choices. Sure you want to continue?")
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    resetSettings(this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE))
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
