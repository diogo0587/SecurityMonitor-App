package com.devicesecurity.monitor.domain.usecase

import com.devicesecurity.monitor.domain.repository.SecurityScannerResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExportReportUseCase @Inject constructor() {
    operator fun invoke(result: SecurityScannerResult): String {
        val sb = StringBuilder()
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sb.appendLine("=== Device Security Monitor Report ===")
        sb.appendLine("Generated: ${df.format(Date())}")
        sb.appendLine()
        sb.appendLine("--- Overall Score: ${result.securityScore.overallScore}/100 ---")
        sb.appendLine("Permissions: ${result.securityScore.permissionScore}/100")
        sb.appendLine("Network: ${result.securityScore.networkScore}/100")
        sb.appendLine("System: ${result.securityScore.systemScore}/100")
        sb.appendLine("Device: ${result.securityScore.storageBatteryScore}/100")
        sb.appendLine()
        sb.appendLine("--- System Security ---")
        sb.appendLine("Rooted: ${result.systemInfo.isRooted}")
        sb.appendLine("Bootloader Unlocked: ${result.systemInfo.isBootloaderUnlocked}")
        sb.appendLine("SELinux: ${result.systemInfo.selinuxStatus}")
        sb.appendLine("Screen Lock: ${result.lockscreenInfo.lockType}")
        sb.appendLine()
        sb.appendLine("--- Network ---")
        sb.appendLine("WiFi: ${result.networkInfo.wifiSsid} (${result.networkInfo.wifiSecurityType})")
        sb.appendLine("VPN: ${result.networkInfo.isVpnActive}")
        sb.appendLine("Bluetooth: ${result.bluetoothInfo.isBluetoothEnabled}")
        sb.appendLine()
        sb.appendLine("--- Permissions (${result.appPermissions.size} apps) ---")
        result.appPermissions.take(20).forEach { app ->
            sb.appendLine("  ${app.appName}: ${app.permissions.size} dangerous permissions")
        }
        return sb.toString()
    }
}
