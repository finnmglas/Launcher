package com.finnmglas.launcher.settings.meta

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finnmglas.launcher.*
import com.finnmglas.launcher.tutorial.TutorialActivity
import com.finnmglas.launcher.settings.intendedSettingsPause
import kotlinx.android.synthetic.main.settings_meta.*

/**
 * The [SettingsFragmentMeta] is a used as a tab in the SettingsActivity.
 *
 * It is used to change settings and access resources about Launcher,
 * that are not directly related to the behaviour of the app itself.
 *
 * (greek `meta` = above, next level)
 */
class SettingsFragmentMeta : Fragment(), UIObject {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_meta, container, false)
    }

    override fun onStart() {
        super<Fragment>.onStart()
        super<UIObject>.onStart()
    }

    // Rate App
    //  Just copied code from https://stackoverflow.com/q/10816757/12787264
    //   that is how we write good software ^^

    private fun rateIntentForUrl(url: String): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(String.format("%s?id=%s", url, this.context!!.packageName))
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

    override fun applyTheme() {
        settings_meta_container.setBackgroundColor(dominantColor)

        setButtonColor(settings_meta_button_select_launcher, vibrantColor)
        setButtonColor(settings_meta_button_view_tutorial, vibrantColor)
        setButtonColor(settings_meta_button_reset_settings, vibrantColor)
        setButtonColor(settings_meta_button_report_bug, vibrantColor)
        setButtonColor(settings_meta_button_contact, vibrantColor)
        setButtonColor(settings_meta_button_discord, vibrantColor)

        settings_meta_icon_github.setTextColor(vibrantColor)
        settings_meta_icon_store.setTextColor(vibrantColor)
        settings_meta_icon_donate.setTextColor(vibrantColor)
    }

    override fun setOnClicks() {

        // Button onClicks

        settings_meta_button_select_launcher.setOnClickListener {
            intendedSettingsPause = true
            // on newer sdk: choose launcher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val callHomeSettingIntent = Intent(Settings.ACTION_HOME_SETTINGS)
                startActivity(callHomeSettingIntent)
            }
            // on older sdk: manage app details
            else {
                AlertDialog.Builder(this.context!!, R.style.AlertDialogCustom)
                    .setTitle(getString(R.string.settings_meta_cant_select_launcher))
                    .setMessage(getString(R.string.settings_meta_cant_select_launcher_msg))
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { _, _ ->
                            try {
                                openAppSettings(
                                    this.context!!.packageName,
                                    this.context!!
                                )
                            } catch ( e : ActivityNotFoundException) {
                                val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                                startActivity(intent)
                            }
                        })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show()
            }
        }

        settings_meta_button_view_tutorial.setOnClickListener {
            intendedSettingsPause = true
            startActivity(Intent(this.context, TutorialActivity::class.java))
        }

        // prompting for settings-reset confirmation
        settings_meta_button_reset_settings.setOnClickListener {
            AlertDialog.Builder(this.context!!, R.style.AlertDialogCustom)
                .setTitle(getString(R.string.settings_meta_reset))
                .setMessage(getString(R.string.settings_meta_reset_confirm))
                .setPositiveButton(android.R.string.yes,
                    DialogInterface.OnClickListener { _, _ ->
                        resetSettings(this.context!!)
                        activity!!.finish()
                    })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        // Icon onClicks
        settings_meta_icon_github.setOnClickListener {
            intendedSettingsPause = true
            openNewTabWindow(
                getString(R.string.settings_meta_link_github),
                this.context!!
            )
        }
        // rate app / open store
        settings_meta_icon_store.setOnClickListener {
            try {
                val rateIntent = rateIntentForUrl("market://details")
                intendedSettingsPause = true
                startActivity(rateIntent)
            } catch (e: ActivityNotFoundException) {
                val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details")
                intendedSettingsPause = true
                startActivity(rateIntent)
            }
        }

        // report a bug
        settings_meta_button_report_bug.setOnClickListener {
            intendedSettingsPause = true
            openNewTabWindow(
                getString(R.string.settings_meta_report_bug_link),
                context!!
            )
        }

        // invite link to the discord server
        settings_meta_button_discord.setOnClickListener {
            intendedSettingsPause = true
            openNewTabWindow(
                getString(R.string.settings_meta_discord_url),
                context!!
            )
        }

        // contact developer
        settings_meta_button_contact.setOnClickListener {
            intendedSettingsPause = true
            openNewTabWindow(
                getString(R.string.settings_meta_contact_url),
                context!!
            )
        }

        // donate
        settings_meta_icon_donate.setOnClickListener {
            intendedSettingsPause = true
            openNewTabWindow(
                getString(R.string.settings_meta_donate_url),
                context!!
            )
        }
    }
}