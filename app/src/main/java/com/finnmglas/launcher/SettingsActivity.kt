package com.finnmglas.launcher

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.finnmglas.launcher.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.container
import kotlinx.android.synthetic.main.fragment_settings_theme.*
import java.io.FileNotFoundException
import java.io.IOException


class SettingsActivity : AppCompatActivity() {

    /** Activity Lifecycle functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(
            when (getSavedTheme(this)) {
                "dark" -> R.style.darkTheme
                "finn" -> R.style.finnmglasTheme
                else -> R.style.finnmglasTheme
            }
        )

        setContentView(R.layout.activity_settings)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        // Hide 'select' button for the selected theme
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab == tabs.getTabAt(1 )) {
                    when (getSavedTheme(container!!.context)) {
                        "dark" -> select_theme_dark.visibility = View.INVISIBLE
                        "finn" -> select_theme_finn.visibility = View.INVISIBLE
                        "custom" -> select_theme_custom.visibility = View.INVISIBLE
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // As older APIs somehow do not recognize the xml defined onClick
        close_settings.setOnClickListener() { finish() }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_CHOOSE_APP -> {
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

            REQUEST_PICK_IMAGE -> {

                if (resultCode == RESULT_OK) {
                    if (data != null) {

                        val selectedImage: Uri? = data.data
                        var bitmap: Bitmap? = null

                        try {
                            // different SDKs, different image choosing
                            if (Build.VERSION.SDK_INT >= 28) {
                                container.background = ImageDecoder.decodeDrawable(
                                    ImageDecoder.createSource(
                                        this.contentResolver, selectedImage!!))
                            } else {
                                val b = BitmapDrawable(
                                    MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                                )
                                b.gravity = Gravity.CENTER
                                container.background = b
                            }

                            Toast.makeText(this, "Chose", Toast.LENGTH_SHORT).show()

                            //val _image : ImageView = background_img
                            //_image.setImageBitmap(bitmap)

                        } catch (e: FileNotFoundException) {
                            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        } catch (e: IOException) {
                            Toast.makeText(this, "IO Except", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
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
        startActivityForResult(intent, REQUEST_CHOOSE_APP)
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
            AlertDialog.Builder(this, R.style.AlertDialogCustom)
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
        AlertDialog.Builder(this, R.style.AlertDialogCustom)
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

    /** Theme - related */

    fun chooseDarkTheme(view: View) {
        saveTheme(this, "dark")
        recreate()
    }

    fun chooseFinnTheme(view: View) {
        saveTheme(this, "finn")
        recreate()
    }

    fun chooseCustomTheme(view: View) {
        /*val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE)*/

         */

        // TODO: Runtime request permisson on newer APIs


        val intent : Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        intent.putExtra("crop", "true")
        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        startActivityForResult(intent, REQUEST_PICK_IMAGE)

    }

}
