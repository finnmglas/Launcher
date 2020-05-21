package com.finnmglas.launcher

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/** The 'Launcher' Tab associated Fragment in Settings */

class SettingsFragmentLauncher : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings_launcher, container, false)
    }
}