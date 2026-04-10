package com.devicesecurity.monitor.data.scanner

import android.content.Context
import com.devicesecurity.monitor.domain.model.*

class SecurityScannerFacade(private val context: Context) {

    private val permissionScanner by lazy { PermissionScanner(context.packageManager) }
    private val networkScanner by lazy { NetworkScanner(context) }
    private val systemScanner by lazy { SystemScanner(context) }
    private val storageBatteryScanner by lazy { StorageBatteryScanner(context) }
    private val cameraMicUsageScanner by lazy { CameraMicUsageScanner(context) }
    private val outdatedAppsScanner by lazy { OutdatedAppsScanner(context.packageManager) }
    private val bluetoothSecurityScanner by lazy { BluetoothSecurityScanner(context) }
    private val lockscreenScanner by lazy { LockscreenScanner(context) }
    private val clipboardMonitorScanner by lazy { ClipboardMonitorScanner(context) }
    private val appCertificateScanner by lazy { AppCertificateScanner(context.packageManager) }
    private val recentPermissionScanner by lazy { RecentPermissionScanner(context.packageManager) }

    data class FullScanResult(
        val appPermissions: List<AppPermissionInfo>,
        val networkInfo: NetworkInfo,
        val systemInfo: SystemInfo,
        val storageBatteryInfo: StorageBatteryInfo,
        val bluetoothInfo: BluetoothInfo,
        val lockscreenInfo: LockscreenInfo,
        val cameraMicUsage: List<CameraMicUsageInfo>,
        val outdatedApps: List<OutdatedAppsScanner.OutdatedAppInfo>,
        val untrustedApps: List<AppCertInfo>
    )

    fun runFullScan(): FullScanResult {
        val basePermissions = permissionScanner.scan()
        val recentPermissions = recentPermissionScanner.scan()
            .associateBy { it.packageName }

        return FullScanResult(
            appPermissions = buildList {
                addAll(basePermissions.map { permission ->
                    val recentMatch = recentPermissions[permission.packageName]
                    if (recentMatch == null) {
                        permission
                    } else {
                        permission.copy(
                            permissions = (permission.permissions + recentMatch.permissions).distinct(),
                            severity = maxOf(permission.severity, recentMatch.severity),
                            recentlyGranted = true
                        )
                    }
                })
                addAll(recentPermissions.values.filter { recentPermission ->
                    basePermissions.none { it.packageName == recentPermission.packageName }
                })
            }.sortedByDescending { it.severity },
            networkInfo = networkScanner.scan(),
            systemInfo = systemScanner.scan(),
            storageBatteryInfo = storageBatteryScanner.scan(),
            bluetoothInfo = bluetoothSecurityScanner.scan(),
            lockscreenInfo = lockscreenScanner.scan(),
            cameraMicUsage = cameraMicUsageScanner.scan(),
            outdatedApps = outdatedAppsScanner.scan(),
            untrustedApps = appCertificateScanner.scan()
        )
    }
}
