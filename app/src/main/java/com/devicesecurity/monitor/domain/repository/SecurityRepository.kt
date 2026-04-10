package com.devicesecurity.monitor.domain.repository

import com.devicesecurity.monitor.data.db.entity.AlertEntity
import com.devicesecurity.monitor.data.db.entity.AppPermissionEntity
import com.devicesecurity.monitor.data.db.entity.SecuritySnapshotEntity
import com.devicesecurity.monitor.domain.model.*
import kotlinx.coroutines.flow.Flow

interface SecurityRepository {
    suspend fun runFullScan(): SecurityScannerResult
    suspend fun saveSnapshot(score: SecurityScore)
    suspend fun replaceAppPermissions(permissions: List<AppPermissionInfo>)
    fun getSnapshots(): Flow<List<SecuritySnapshotEntity>>
    fun getSavedAppPermissions(): Flow<List<AppPermissionEntity>>
    suspend fun getLatestSnapshot(): SecuritySnapshotEntity?
    suspend fun saveAlerts(alerts: List<Alert>)
    fun getActiveAlerts(): Flow<List<AlertEntity>>
    suspend fun dismissAlert(alertId: Long)
}

data class SecurityScannerResult(
    val appPermissions: List<AppPermissionInfo>,
    val networkInfo: NetworkInfo,
    val systemInfo: SystemInfo,
    val storageBatteryInfo: StorageBatteryInfo,
    val bluetoothInfo: BluetoothInfo,
    val lockscreenInfo: LockscreenInfo,
    val cameraMicUsage: List<CameraMicUsageInfo>,
    val outdatedApps: List<com.devicesecurity.monitor.data.scanner.OutdatedAppsScanner.OutdatedAppInfo>,
    val untrustedApps: List<AppCertInfo>,
    val securityScore: SecurityScore
)
