package com.devicesecurity.monitor.util

import com.devicesecurity.monitor.data.scanner.OutdatedAppsScanner
import com.devicesecurity.monitor.domain.model.*

object ScoreCalculator {
    fun calculatePermissionScore(appPermissions: List<AppPermissionInfo>): Int {
        var score = 100
        val criticalApps = appPermissions.filter { it.severity == Severity.CRITICAL }
        val highApps = appPermissions.filter { it.severity == Severity.HIGH }
        val mediumApps = appPermissions.filter { it.severity == Severity.MEDIUM }
        score -= minOf(criticalApps.size * 2, 10)
        score -= minOf(highApps.size, 8)
        score -= minOf(mediumApps.size, 5)
        return score.coerceIn(0, 100)
    }

    fun calculateNetworkScore(networkInfo: NetworkInfo, bluetoothInfo: BluetoothInfo): Int {
        var score = 100
        if (!networkInfo.isWifiSecure) score -= 10
        if (!networkInfo.isVpnActive && networkInfo.connectionType == "WiFi") score -= 5
        if (bluetoothInfo.isDiscoverable) score -= 5
        return score.coerceIn(0, 100)
    }

    fun calculateSystemScore(systemInfo: SystemInfo, lockscreenInfo: LockscreenInfo): Int {
        var score = 100
        if (!lockscreenInfo.isSecure) score -= 30
        else if (lockscreenInfo.lockType == "Swipe") score -= 15
        if (systemInfo.isRooted) score -= 20
        if (systemInfo.isBootloaderUnlocked) score -= 15
        if (systemInfo.selinuxStatus == "Permissive") score -= 10
        if (systemInfo.isUnknownSourcesEnabled) score -= 10
        if (systemInfo.isAdbEnabled) score -= 5
        if (systemInfo.isDeveloperOptionsEnabled) score -= 3
        return score.coerceIn(0, 100)
    }

    fun calculateStorageBatteryScore(storageBatteryInfo: StorageBatteryInfo): Int {
        var score = 100
        if (!storageBatteryInfo.isEncrypted) score -= 15
        if (storageBatteryInfo.batteryHealth != "Good") score -= 5
        return score.coerceIn(0, 100)
    }

    fun calculateOverallScore(permissionScore: Int, networkScore: Int, systemScore: Int, storageBatteryScore: Int): Int {
        val weighted = (permissionScore * 0.3f + networkScore * 0.25f + systemScore * 0.35f + storageBatteryScore * 0.1f)
        return weighted.toInt().coerceIn(0, 100)
    }

    fun calculateOutdatedAppsPenalty(outdatedApps: List<OutdatedAppsScanner.OutdatedAppInfo>): Int {
        return minOf(outdatedApps.size, 5)
    }

    fun getScoreColor(score: Int): Int {
        return when {
            score >= 80 -> Constants.SCORE_COLOR_GREEN
            score >= 50 -> Constants.SCORE_COLOR_YELLOW
            else -> Constants.SCORE_COLOR_RED
        }
    }

    fun getScoreLabel(score: Int): String {
        return when {
            score >= 80 -> "Secure"
            score >= 50 -> "Caution"
            else -> "At Risk"
        }
    }
}
