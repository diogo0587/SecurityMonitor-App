package com.devicesecurity.monitor.data.scanner

import android.content.pm.PackageManager
import android.os.Build
import com.devicesecurity.monitor.domain.model.AppCertInfo

class AppCertificateScanner(private val packageManager: PackageManager) {

    fun scan(): List<AppCertInfo> {
        val result = mutableListOf<AppCertInfo>()
        val apps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledApplications(0)
        }

        for (appInfo in apps) {
            if (appInfo.packageName.startsWith("com.android.") ||
                appInfo.packageName.startsWith("android.")
            ) continue

            try {
                val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageManager.getPackageInfo(
                        appInfo.packageName,
                        PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNING_CERTIFICATES.toLong())
                    )
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(appInfo.packageName, PackageManager.GET_SIGNATURES)
                }

                val installSource = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val installer = packageManager.getInstallSourceInfo(appInfo.packageName)
                    installer.installingPackageName ?: "Unknown"
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getInstallerPackageName(appInfo.packageName) ?: "Sideloaded"
                }

                val isTrusted = installSource in listOf(
                    "com.android.vending",
                    "com.google.android.feedback",
                    "com.samsung.android.app.galaxygallery"
                )

                result.add(
                    AppCertInfo(
                        packageName = appInfo.packageName,
                        appName = appInfo.loadLabel(packageManager).toString(),
                        isFromTrustedSource = isTrusted,
                        installSource = installSource
                    )
                )
            } catch (_: Exception) {
            }
        }

        return result.filter { !it.isFromTrustedSource }
    }
}