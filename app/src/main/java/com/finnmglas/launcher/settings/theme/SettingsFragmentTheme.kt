package com.finnmglas.launcher.settings.theme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.finnmglas.launcher.*
import com.finnmglas.launcher.settings.intendedSettingsPause
import kotlinx.android.synthetic.main.settings_theme.*

/**
 * The [SettingsFragmentTheme] is a used as a tab in the SettingsActivity.
 *
 * It is used to change themes, select wallpapers ... theme related stuff
 */
class SettingsFragmentTheme : Fragment(), UIObject {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_theme, container, false)
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

        settings_theme_container.setBackgroundColor(dominantColor)
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
    }
}