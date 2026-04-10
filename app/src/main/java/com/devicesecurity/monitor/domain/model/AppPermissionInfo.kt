package com.devicesecurity.monitor.domain.model

data class AppPermissionInfo(
    val packageName: String,
    val appName: String,
    val permissions: List<String>,
    val severity: Severity,
    val recentlyGranted: Boolean = false
)