package com.finnmglas.launcher.settings.launcher

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.finnmglas.launcher.*
import com.finnmglas.launcher.settings.intendedSettingsPause
import kotlinx.android.synthetic.main.settings_launcher.*


/**
 * The [SettingsFragmentLauncher] is a used as a tab in the SettingsActivity.
 *
 * It is used to change themes, select wallpapers ... theme related stuff
 */
class SettingsFragmentLauncher : Fragment(), UIObject {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_launcher, container, false)
    }

    override fun onStart(){
        super<Fragment>.onStart()
        super<UIObject>.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_PERMISSION_STORAGE -> letUserPickImage()
            REQUEST_PICK_IMAGE -> handlePickedImage(resultCode, data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun letUserPickImage(crop: Boolean = false) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK // other option: Intent.ACTION_GET_CONTENT

        if (crop) { // crop to for the target devices screen
            intent.putExtra("crop", "true")
            val displayMetrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            intent.putExtra("aspectX", displayMetrics.widthPixels)
            intent.putExtra("aspectY", displayMetrics.heightPixels)
        }
        
        intendedSettingsPause = true
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    private fun handlePickedImage(resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (data == null) return

            val imageUri = data.data
            background = MediaStore.Images.Media.getBitmap(context!!.contentResolver, imageUri)

            Palette.Builder(background!!).generate {
                it?.let { palette ->
                    dominantColor = palette.getDominantColor(ContextCompat.getColor(context!!, R.color.darkTheme_accent_color))
                    vibrantColor = palette.getVibrantColor(ContextCompat.getColor(context!!, R.color.darkTheme_accent_color))

                    // never let dominantColor equal vibrantColor
                    if(dominantColor == vibrantColor) {
                        vibrantColor =
                            manipulateColor(
                                vibrantColor,
                                1.2F
                            )
                        dominantColor =
                            manipulateColor(
                                dominantColor,
                                0.8F
                            )
                    }

                    /* Save image Uri as string */
                    launcherPreferences.edit()
                        .putString(PREF_WALLPAPER, imageUri.toString())
                        .putInt(PREF_DOMINANT, dominantColor)
                        .putInt(PREF_VIBRANT, vibrantColor)
                        .apply()

                    saveTheme("custom")
                    intendedSettingsPause = true
                    activity!!.recreate()
                }
            }
        }
    }

    override fun applyTheme() {
        // Hide 'select' button for the selected theme or allow customisation
        when (getSavedTheme(context!!)) {
            "dark" -> settings_theme_dark_button_select.visibility = View.INVISIBLE
            "finn" -> settings_theme_finn_button_select.visibility = View.INVISIBLE
            "custom" ->
                settings_theme_custom_button_select.text = getString(R.string.settings_select_image)
        }

        setSwitchColor(settings_launcher_switch_screen_timeout, vibrantColor)

        settings_launcher_container.setBackgroundColor(dominantColor)
        setButtonColor(settings_theme_finn_button_select, vibrantColor)
        setButtonColor(settings_theme_dark_button_select, vibrantColor)
        setButtonColor(settings_theme_custom_button_select, vibrantColor)
        setButtonColor(settings_theme_custom_button_examples, vibrantColor)
    }

    override fun setOnClicks() {
        // Theme changing buttons
        settings_theme_dark_button_select.setOnClickListener {
            resetToDarkTheme(activity!!)
        }
        settings_theme_finn_button_select.setOnClickListener {
            resetToDefaultTheme(activity!!)
        }
        settings_theme_custom_button_select.setOnClickListener {
            intendedSettingsPause = true
            // Request permission (on newer APIs)
            if (Build.VERSION.SDK_INT >= 23) {
                when {
                    ContextCompat.checkSelfPermission(context!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    -> letUserPickImage(true)
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    -> {}
                    else
                    -> requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION_STORAGE
                    )
                }
            }
            else letUserPickImage()
        }
        settings_theme_custom_button_examples.setOnClickListener {
            intendedSettingsPause = true
            // Show example usage
            openNewTabWindow(
                "https://github.com/finnmglas/Launcher/blob/master/docs/README.md",
                context!!
            )
        }
        settings_launcher_switch_screen_timeout.setOnClickListener { // Toggle screen timeout
            launcherPreferences.edit()
                .putBoolean(PREF_SCREEN_TIMEOUT_DISABLED,
                    !launcherPreferences.getBoolean(PREF_SCREEN_TIMEOUT_DISABLED, false))
                .apply()

            setWindowFlags(activity!!.window)
        }
    }

    override fun adjustLayout() {
        // visually load settings
        settings_launcher_switch_screen_timeout.isChecked =
            launcherPreferences.getBoolean(PREF_SCREEN_TIMEOUT_DISABLED, false)

        // Load values into the date-format spinner
        val staticAdapter = ArrayAdapter.createFromResource(
                activity!!, R.array.settings_launcher_time_formats,
                android.R.layout.simple_spinner_item )

        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        settings_launcher_format_spinner.adapter = staticAdapter

        settings_launcher_format_spinner.setSelection(launcherPreferences.getInt(PREF_DATE_FORMAT, 0))

        settings_launcher_format_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                launcherPreferences.edit()
                    .putInt(PREF_DATE_FORMAT, position)
                    .apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
}