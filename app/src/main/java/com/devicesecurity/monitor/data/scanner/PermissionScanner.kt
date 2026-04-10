package com.devicesecurity.monitor.data.scanner

import android.content.pm.PackageManager
import android.os.Build
import com.devicesecurity.monitor.domain.model.AppPermissionInfo
import com.devicesecurity.monitor.domain.model.Severity

class PermissionScanner(private val packageManager: PackageManager) {

    private val criticalPermissions = setOf(
        "android.permission.SEND_SMS",
        "android.permission.CALL_PHONE",
        "android.permission.READ_SMS",
        "android.permission.PROCESS_OUTGOING_CALLS"
    )

    private val highPermissions = setOf(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.CAMERA",
        "android.permission.RECORD_AUDIO",
        "android.permission.READ_PHONE_STATE"
    )

    private val mediumPermissions = setOf(
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.READ_CALENDAR",
        "android.permission.WRITE_CALENDAR",
        "android.permission.BODY_SENSORS",
        "android.permission.READ_CALL_LOG"
    )

    fun scan(): List<AppPermissionInfo> {
        val result = mutableListOf<AppPermissionInfo>()
        val apps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledApplications(0)
        }

        for (appInfo in apps) {
            if (appInfo.packageName.startsWith("com.android.") ||
                appInfo.packageName.startsWith("android.") ||
                appInfo.packageName == "com.devicesecurity.monitor"
            ) continue

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

                val requestedPermissions = packageInfo.requestedPermissions ?: continue
                val dangerousPerms = mutableListOf<String>()
                var maxSeverity = Severity.LOW

                for (perm in requestedPermissions) {
                    when {
                        criticalPermissions.contains(perm) -> {
                            dangerousPerms.add(perm)
                            maxSeverity = Severity.CRITICAL
                        }
                        highPermissions.contains(perm) -> {
                            dangerousPerms.add(perm)
                            if (maxSeverity < Severity.HIGH) maxSeverity = Severity.HIGH
                        }
                        mediumPermissions.contains(perm) -> {
                            dangerousPerms.add(perm)
                            if (maxSeverity < Severity.MEDIUM) maxSeverity = Severity.MEDIUM
                        }
                    }
                }

                if (dangerousPerms.isNotEmpty()) {
                    result.add(
                        AppPermissionInfo(
                            packageName = appInfo.packageName,
                            appName = appInfo.loadLabel(packageManager).toString(),
                            permissions = dangerousPerms,
                            severity = maxSeverity
                        )
                    )
                }
            } catch (_: Exception) {
            }
        }

        return result.sortedByDescending { it.severity }
    }
}