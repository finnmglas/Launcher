package com.finnmglas.launcher.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.finnmglas.launcher.R
import com.finnmglas.launcher.extern.*
import kotlinx.android.synthetic.main.fragment_settings_theme.*

/** The 'Theme' Tab associated Fragment in Settings */

class SettingsFragmentTheme : Fragment() {

    /** Lifecycle functions */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_theme, container, false)
    }

    override fun onStart(){
        // Hide 'select' button for the selected theme or allow customisation
        when (getSavedTheme(this.context!!)) {
            "dark" -> fragment_settings_theme_select_dark_btn.visibility = View.INVISIBLE
            "finn" -> fragment_settings_theme_select_finn_btn.visibility = View.INVISIBLE
            "custom" -> {
                fragment_settings_theme_select_custom_btn.text = getString(R.string.settings_select_image)
            }
        }

        // Theme changing buttons
        fragment_settings_theme_select_dark_btn.setOnClickListener {
            saveTheme(this.context!!, "dark")
            activity!!.recreate()
        }
        fragment_settings_theme_select_finn_btn.setOnClickListener {
            saveTheme(this.context!!, "finn")
            activity!!.recreate()
        }
        fragment_settings_theme_select_custom_btn.setOnClickListener {
            // Request permission (on newer APIs)
            if (Build.VERSION.SDK_INT >= 23) {
                when {
                    ContextCompat.checkSelfPermission(this.context!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    -> letUserPickImage()
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    -> {}
                    else
                    -> requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_STORAGE)
                }
            }
            else letUserPickImage()
        }

        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_PERMISSION_STORAGE -> letUserPickImage()
            REQUEST_PICK_IMAGE -> handlePickedImage(resultCode, data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /** Extra functions */

    private fun letUserPickImage(crop: Boolean = false) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK // other option: Intent.ACTION_GET_CONTENT
        if (crop) intent.putExtra("crop", "true")
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    private fun handlePickedImage(resultCode: Int, data: Intent?) {

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (data == null) return

            val imageUri = data.data

            /* Save image Uri as string */
            val editor: SharedPreferences.Editor = context!!.getSharedPreferences(
                context!!.getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit()
            editor.putString("background_uri", imageUri.toString())
            editor.apply()

            background = MediaStore.Images.Media.getBitmap(this.context!!.contentResolver, imageUri)

            saveTheme(this.context!!, "custom")
            activity!!.recreate()
        }
    }
}