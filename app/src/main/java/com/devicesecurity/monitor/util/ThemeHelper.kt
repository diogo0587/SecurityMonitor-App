package com.devicesecurity.monitor.util

import android.content.Context
import android.content.SharedPreferences

object ThemeHelper {
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isDarkTheme(context: Context): Boolean {
        return getPrefs(context).getBoolean(Constants.PREF_DARK_THEME, true)
    }

    fun setDarkTheme(context: Context, isDark: Boolean) {
        getPrefs(context).edit().putBoolean(Constants.PREF_DARK_THEME, isDark).apply()
    }
}
