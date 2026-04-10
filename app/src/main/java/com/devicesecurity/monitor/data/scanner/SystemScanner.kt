package com.devicesecurity.monitor.data.scanner

import android.content.Context
import android.os.Build
import com.devicesecurity.monitor.domain.model.SystemInfo
import java.io.File

class SystemScanner(private val context: Context) {

    fun scan(): SystemInfo {
        return SystemInfo(
            isRooted = checkRooted(),
            isBootloaderUnlocked = checkBootloaderUnlocked(),
            selinuxStatus = checkSelinuxStatus(),
            isUnknownSourcesEnabled = checkUnknownSources(),
            isDeveloperOptionsEnabled = checkDeveloperOptions(),
            isAdbEnabled = checkAdbEnabled(),
            rootDetails = getRootDetails()
        )
    }

    private fun checkRooted(): Boolean {
        val paths = listOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su",
            "/magisk",
            "/sbin/magisk",
            "/system/xbin/magisk",
            "/data/adb/magisk"
        )

        for (path in paths) {
            if (File(path).exists()) return true
        }

        try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val result = process.inputStream.bufferedReader().readText()
            if (result.isNotEmpty()) return true
        } catch (_: Exception) {
        }

        try {
            val process = Runtime.getRuntime().exec("su")
            process.outputStream.write("exit\n".toByteArray())
            process.outputStream.flush()
            process.waitFor()
            if (process.exitValue() == 0) return true
        } catch (_: Exception) {
        }

        return false
    }

    private fun getRootDetails(): List<String> {
        val details = mutableListOf<String>()
        val rootPaths = listOf(
            Pair("/system/app/Superuser.apk", "Superuser.apk found"),
            Pair("/sbin/su", "su binary in /sbin"),
            Pair("/system/bin/su", "su binary in /system/bin"),
            Pair("/system/xbin/su", "su binary in /system/xbin"),
            Pair("/magisk", "Magisk directory found"),
            Pair("/sbin/magisk", "Magisk binary found"),
            Pair("/system/xbin/magisk", "Magisk binary in xbin"),
            Pair("/data/adb/magisk", "Magisk in /data/adb")
        )

        for ((path, detail) in rootPaths) {
            if (File(path).exists()) details.add(detail)
        }
        return details
    }

    private fun checkBootloaderUnlocked(): Boolean {
        val props = mapOf(
            "ro.boot.vbmeta.device_state" to "unlocked",
            "ro.boot.verifiedbootstate" to "orange",
            "ro.boot.flash.locked" to "0",
            "ro.boot.veritymode" to "disabled"
        )

        for ((prop, value) in props) {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("getprop", prop))
                val result = process.inputStream.bufferedReader().readText().trim()
                if (result.equals(value, ignoreCase = true)) return true
            } catch (_: Exception) {
            }
        }
        return false
    }

    private fun checkSelinuxStatus(): String {
        try {
            val file = File("/sys/fs/selinux/enforce")
            if (file.exists()) {
                val content = file.readText().trim()
                return if (content == "1") "Enforcing" else "Permissive"
            }
        } catch (_: Exception) {
        }

        try {
            val process = Runtime.getRuntime().exec("getenforce")
            val result = process.inputStream.bufferedReader().readText().trim()
            return result
        } catch (_: Exception) {
        }

        return "Unknown"
    }

    private fun checkUnknownSources(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                !context.packageManager.canRequestPackageInstalls()
            } else {
                @Suppress("DEPRECATION")
                android.provider.Settings.Secure.getInt(
                    context.contentResolver,
                    android.provider.Settings.Secure.INSTALL_NON_MARKET_APPS
                ) == 1
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun checkDeveloperOptions(): Boolean {
        return try {
            android.provider.Settings.Global.getInt(
                context.contentResolver,
                android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) == 1
        } catch (_: Exception) {
            false
        }
    }

    private fun checkAdbEnabled(): Boolean {
        return try {
            android.provider.Settings.Global.getInt(
                context.contentResolver,
                android.provider.Settings.Global.ADB_ENABLED,
                0
            ) == 1
        } catch (_: Exception) {
            false
        }
    }
}