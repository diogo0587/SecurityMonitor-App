package com.devicesecurity.monitor.ui.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.devicesecurity.monitor.databinding.ActivitySettingsBinding
import com.devicesecurity.monitor.util.Constants
import com.devicesecurity.monitor.util.ThemeHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.darkThemeSwitch.isChecked = ThemeHelper.isDarkTheme(this)
        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            ThemeHelper.setDarkTheme(this, isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.usageAccessButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        binding.packageAccessButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES))
        }

        binding.notificationSwitch.isChecked = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
            .getBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, true)
        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                .edit().putBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, isChecked).apply()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
