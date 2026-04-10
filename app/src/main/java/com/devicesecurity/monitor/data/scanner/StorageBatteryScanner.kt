package com.devicesecurity.monitor.data.scanner

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import com.devicesecurity.monitor.domain.model.StorageBatteryInfo
import java.io.File

class StorageBatteryScanner(private val context: Context) {

    fun scan(): StorageBatteryInfo {
        return StorageBatteryInfo(
            isEncrypted = checkEncryption(),
            encryptionStatus = getEncryptionStatus(),
            totalStorageGb = getTotalStorage(),
            freeStorageGb = getFreeStorage(),
            batteryLevel = getBatteryLevel(),
            batteryHealth = getBatteryHealth(),
            batteryTemperature = getBatteryTemperature(),
            isCharging = isCharging()
        )
    }

    private fun checkEncryption(): Boolean {
        try {
            val state = File("/sys/fs/selinux/enforce").readText().trim()
            return true
        } catch (_: Exception) {
        }

        try {
            val process = Runtime.getRuntime().exec("getprop ro.crypto.state")
            val result = process.inputStream.bufferedReader().readText().trim()
            return result.equals("encrypted", ignoreCase = true)
        } catch (_: Exception) {
        }

        return true
    }

    private fun getEncryptionStatus(): String {
        try {
            val process = Runtime.getRuntime().exec("getprop ro.crypto.state")
            val result = process.inputStream.bufferedReader().readText().trim()
            return when {
                result.equals("encrypted", ignoreCase = true) -> "Encrypted (File-Based)"
                result.equals("unsupported", ignoreCase = true) -> "Unsupported"
                else -> result.ifEmpty { "Unknown" }
            }
        } catch (_: Exception) {
            return "Unknown"
        }
    }

    private fun getTotalStorage(): Float {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val total = stat.totalBytes
            (total / (1024f * 1024f * 1024f))
        } catch (_: Exception) {
            0f
        }
    }

    private fun getFreeStorage(): Float {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val free = stat.availableBytes
            (free / (1024f * 1024f * 1024f))
        } catch (_: Exception) {
            0f
        }
    }

    private fun getBatteryLevel(): Int {
        return try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            intent?.let {
                val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                (level * 100 / scale.toFloat()).toInt()
            } ?: 0
        } catch (_: Exception) {
            0
        }
    }

    private fun getBatteryHealth(): String {
        return try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            intent?.let {
                val health = it.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                when (health) {
                    BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                    BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
                    BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                    else -> "Unknown"
                }
            } ?: "Unknown"
        } catch (_: Exception) {
            "Unknown"
        }
    }

    private fun getBatteryTemperature(): Float {
        return try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            intent?.let {
                val temp = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                temp / 10f
            } ?: 0f
        } catch (_: Exception) {
            0f
        }
    }

    private fun isCharging(): Boolean {
        return try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            intent?.let {
                val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
            } ?: false
        } catch (_: Exception) {
            false
        }
    }
}