package com.devicesecurity.monitor.data.scanner

import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.os.Build
import com.devicesecurity.monitor.domain.model.AppPermissionInfo
import com.devicesecurity.monitor.domain.model.Severity
import java.util.concurrent.TimeUnit

class RecentPermissionScanner(private val packageManager: PackageManager) {

    private val dangerousPermissions = setOf(
        "android.permission.SEND_SMS",
        "android.permission.CALL_PHONE",
        "android.permission.READ_SMS",
        "android.permission.PROCESS_OUTGOING_CALLS",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.CAMERA",
        "android.permission.RECORD_AUDIO",
        "android.permission.READ_PHONE_STATE",
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.READ_CALENDAR",
        "android.permission.WRITE_CALENDAR",
        "android.permission.BODY_SENSORS",
        "android.permission.READ_CALL_LOG"
    )

    fun scan(): List<AppPermissionInfo> {
        val recentWindowStart = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        val apps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledApplications(0)
        }

        return buildList {
            for (appInfo in apps) {
                if (appInfo.packageName.startsWith("com.android.") ||
                    appInfo.packageName.startsWith("android.") ||
                    appInfo.packageName == "com.devicesecurity.monitor"
                ) {
                    continue
                }

                try {
                    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        packageManager.getPackageInfo(
                            appInfo.packageName,
                            PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        packageManager.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS)
                    }

                    if (packageInfo.lastUpdateTime < recentWindowStart) {
                        continue
                    }

                    val requestedPermissions = packageInfo.requestedPermissions.orEmpty()
                    val requestedFlags = packageInfo.requestedPermissionsFlags.orEmpty()
                    val grantedDangerousPermissions = requestedPermissions.mapIndexedNotNull { index, permission ->
                        val isGranted = requestedFlags.getOrNull(index)?.and(PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0
                        if (isGranted && permission in dangerousPermissions) permission else null
                    }

                    if (grantedDangerousPermissions.isNotEmpty()) {
                        add(
                            AppPermissionInfo(
                                packageName = appInfo.packageName,
                                appName = appInfo.loadLabel(packageManager).toString(),
                                permissions = grantedDangerousPermissions,
                                severity = grantedDangerousPermissions.maxSeverity(),
                                recentlyGranted = true
                            )
                        )
                    }
                } catch (_: Exception) {
                }
            }
        }.sortedByDescending { it.severity }
    }

    private fun List<String>.maxSeverity(): Severity {
        return when {
            any {
                it == "android.permission.SEND_SMS" ||
                    it == "android.permission.CALL_PHONE" ||
                    it == "android.permission.READ_SMS" ||
                    it == "android.permission.PROCESS_OUTGOING_CALLS"
            } -> Severity.CRITICAL
            any {
                it == "android.permission.ACCESS_FINE_LOCATION" ||
                    it == "android.permission.ACCESS_COARSE_LOCATION" ||
                    it == "android.permission.CAMERA" ||
                    it == "android.permission.RECORD_AUDIO" ||
                    it == "android.permission.READ_PHONE_STATE"
            } -> Severity.HIGH
            else -> Severity.MEDIUM
        }
    }
}
