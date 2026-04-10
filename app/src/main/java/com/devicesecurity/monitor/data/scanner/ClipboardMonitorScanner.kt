package com.devicesecurity.monitor.data.scanner

import android.content.Context
import android.content.pm.PackageManager

class ClipboardMonitorScanner(private val context: Context) {

    data class ClipboardAccessInfo(
        val packageName: String,
        val appName: String,
        val accessType: String
    )

    fun scan(): List<ClipboardAccessInfo> {
        return emptyList()
    }
}