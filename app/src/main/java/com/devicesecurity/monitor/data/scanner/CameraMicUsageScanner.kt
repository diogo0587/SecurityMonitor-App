package com.devicesecurity.monitor.data.scanner

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.devicesecurity.monitor.domain.model.CameraMicUsageInfo

class CameraMicUsageScanner(private val context: Context) {

    fun scan(): List<CameraMicUsageInfo> {
        val result = mutableListOf<CameraMicUsageInfo>()
        val packageManager = context.packageManager

        val cameraApps = getAppsUsingSensor(packageManager, android.Manifest.permission.CAMERA, "Camera")
        val micApps = getAppsUsingSensor(packageManager, android.Manifest.permission.RECORD_AUDIO, "Microphone")

        result.addAll(cameraApps)
        result.addAll(micApps)
        return result
    }

    private fun getAppsUsingSensor(pm: PackageManager, permission: String, sensorType: String): List<CameraMicUsageInfo> {
        val apps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getInstalledApplications(0)
        }

        val result = mutableListOf<CameraMicUsageInfo>()

        for (appInfo in apps) {
            if (appInfo.packageName.startsWith("com.android.") ||
                appInfo.packageName.startsWith("android.") ||
                appInfo.packageName == "com.devicesecurity.monitor"
            ) continue

            try {
                if (pm.checkPermission(permission, appInfo.packageName) == PackageManager.PERMISSION_GRANTED) {
                    val lastUsed = getAppLastUsage(appInfo.packageName)
                    if (lastUsed > 0) {
                        result.add(
                            CameraMicUsageInfo(
                                packageName = appInfo.packageName,
                                appName = appInfo.loadLabel(pm).toString(),
                                sensorType = sensorType,
                                lastUsedTimestamp = lastUsed
                            )
                        )
                    }
                }
            } catch (_: Exception) {
            }
        }

        return result.sortedByDescending { it.lastUsedTimestamp }
    }

    private fun getAppLastUsage(packageName: String): Long {
        return try {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return 0L

            val now = System.currentTimeMillis()
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                now - 24 * 60 * 60 * 1000,
                now
            )

            stats.find { it.packageName == packageName }?.lastTimeUsed ?: 0L
        } catch (_: Exception) {
            0L
        }
    }
}