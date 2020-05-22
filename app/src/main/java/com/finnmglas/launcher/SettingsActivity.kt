package com.finnmglas.launcher

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
import androidx.viewpager.widget.ViewPager
import com.finnmglas.launcher.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    /** Activity Lifecycle functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

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

    /** onClick functions for Settings */
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

    fun chooseInstallApp(view : View) {
        try {
            val rateIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/"))
            startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this,getString(R.string.settings_toast_store_not_found), Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun openFinnWebsite(view: View) { openNewTabWindow(getString(R.string.settings_footer_web), this) }
    fun openGithubRepo(view: View) { openNewTabWindow(getString(R.string.settings_footer_repo), this) }

    // Rate App
    //  Just copied code from https://stackoverflow.com/q/10816757/12787264
    //   that is how we write good software ^
    fun rateApp(view: View) {
        try {
            val rateIntent = rateIntentForUrl("market://details")
            startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details")
            startActivity(rateIntent)
        }
    }

    private fun rateIntentForUrl(url: String): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(String.format("%s?id=%s", url, packageName))
        )
        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags = if (Build.VERSION.SDK_INT >= 21) {
            flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        } else {
            flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
        }
        intent.addFlags(flags)
        return intent
    }

    fun backHome(view: View) { finish() }

    fun setLauncher(view: View) {
        // on newer sdk: choose launcher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val callHomeSettingIntent = Intent(Settings.ACTION_HOME_SETTINGS)
            startActivity(callHomeSettingIntent)
        }
        // on older sdk: manage app details
        else {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_cant_choose_launcher))
                .setMessage(getString(R.string.alert_cant_choose_launcher_message))
                .setPositiveButton(android.R.string.yes,
                    DialogInterface.OnClickListener { dialog, which ->
                        try {
                            openAppSettings(packageName, this)
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

    fun viewTutorial (view: View){
        startActivity(Intent(this, FirstStartupActivity::class.java))
    }

    // Show a dialog prompting for confirmation
    fun resetSettingsClick(view: View) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_reset))
            .setMessage(getString(R.string.settings_reset_message))
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    resetSettings(this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE), this)
                    finish()
                })
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}
