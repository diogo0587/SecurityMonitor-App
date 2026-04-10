package com.devicesecurity.monitor.util

object Constants {
    const val CHANNEL_SECURITY_ALERTS = "security_alerts"
    const val CHANNEL_SECURITY_INFO = "security_info"
    const val NOTIFICATION_ID_SCORE_DROP = 1001
    const val NOTIFICATION_ID_NEW_PERMISSION = 1002
    const val NOTIFICATION_ID_OPEN_WIFI = 1003
    const val NOTIFICATION_ID_ROOT_DETECTED = 1004
    const val NOTIFICATION_ID_CAMERA_MIC = 1005
    const val SCAN_INTERVAL_HOURS = 6L
    const val SCORE_DROP_THRESHOLD = 10
    const val PREFS_NAME = "security_monitor_prefs"
    const val PREF_DARK_THEME = "dark_theme"
    const val PREF_SCAN_INTERVAL = "scan_interval"
    const val PREF_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val WIDGET_UPDATE_ACTION = "com.devicesecurity.monitor.WIDGET_UPDATE"
    const val SCORE_COLOR_GREEN = 0xFF03DAC6.toInt()
    const val SCORE_COLOR_YELLOW = 0xFFFFB74D.toInt()
    const val SCORE_COLOR_RED = 0xFFCF6679.toInt()
}
