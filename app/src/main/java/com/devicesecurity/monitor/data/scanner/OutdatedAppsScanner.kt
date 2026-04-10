package com.devicesecurity.monitor.data.scanner

import android.content.pm.PackageManager
import android.os.Build
import java.util.concurrent.TimeUnit

class OutdatedAppsScanner(private val packageManager: PackageManager) {

    data class OutdatedAppInfo(
        val packageName: String,
        val appName: String,
        val lastUpdateTime: Long,
        val daysSinceUpdate: Long
    )

    fun scan(): List<OutdatedAppInfo> {
        val result = mutableListOf<OutdatedAppInfo>()
        val apps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledApplications(0)
        }

        val now = System.currentTimeMillis()
        val ninetyDaysMs = TimeUnit.DAYS.toMillis(90)

        for (appInfo in apps) {
            if (appInfo.packageName.startsWith("com.android.") ||
                appInfo.packageName.startsWith("android.")
            ) continue

            try {
                val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(
                        appInfo.packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(appInfo.packageName, 0)
                }

                val lastUpdate = packageInfo.lastUpdateTime
                val daysSince = TimeUnit.MILLISECONDS.toDays(now - lastUpdate)

                if (now - lastUpdate > ninetyDaysMs) {
                    result.add(
                        OutdatedAppInfo(
                            packageName = appInfo.packageName,
                            appName = appInfo.loadLabel(packageManager).toString(),
                            lastUpdateTime = lastUpdate,
                            daysSinceUpdate = daysSince
                        )
                    )
                }
            } catch (_: Exception) {
            }
        }

        return result.sortedBy { it.daysSinceUpdate }
    }
}