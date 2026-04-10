package com.devicesecurity.monitor.data.repository

import com.devicesecurity.monitor.data.db.dao.AlertDao
import com.devicesecurity.monitor.data.db.dao.AppPermissionDao
import com.devicesecurity.monitor.data.db.dao.SnapshotDao
import com.devicesecurity.monitor.data.db.entity.AlertEntity
import com.devicesecurity.monitor.data.db.entity.AppPermissionEntity
import com.devicesecurity.monitor.data.db.entity.SecuritySnapshotEntity
import com.devicesecurity.monitor.data.scanner.SecurityScannerFacade
import com.devicesecurity.monitor.domain.model.Alert
import com.devicesecurity.monitor.domain.model.AppPermissionInfo
import com.devicesecurity.monitor.domain.model.SecurityScore
import com.devicesecurity.monitor.domain.repository.SecurityRepository
import com.devicesecurity.monitor.domain.repository.SecurityScannerResult
import com.devicesecurity.monitor.util.ScoreCalculator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepositoryImpl @Inject constructor(
    private val scannerFacade: SecurityScannerFacade,
    private val snapshotDao: SnapshotDao,
    private val appPermissionDao: AppPermissionDao,
    private val alertDao: AlertDao
) : SecurityRepository {

    override suspend fun runFullScan(): SecurityScannerResult {
        val scanResult = scannerFacade.runFullScan()
        val permissionScore = ScoreCalculator.calculatePermissionScore(scanResult.appPermissions)
        val networkScore = ScoreCalculator.calculateNetworkScore(scanResult.networkInfo, scanResult.bluetoothInfo)
        val systemScore = ScoreCalculator.calculateSystemScore(scanResult.systemInfo, scanResult.lockscreenInfo)
        val storageBatteryScore = ScoreCalculator.calculateStorageBatteryScore(scanResult.storageBatteryInfo)
        val overallScore = ScoreCalculator.calculateOverallScore(permissionScore, networkScore, systemScore, storageBatteryScore)
        val securityScore = SecurityScore(overallScore, permissionScore, networkScore, systemScore, storageBatteryScore)
        return SecurityScannerResult(
            scanResult.appPermissions, scanResult.networkInfo, scanResult.systemInfo,
            scanResult.storageBatteryInfo, scanResult.bluetoothInfo, scanResult.lockscreenInfo,
            scanResult.cameraMicUsage, scanResult.outdatedApps, scanResult.untrustedApps, securityScore
        )
    }

    override suspend fun saveSnapshot(score: SecurityScore) {
        snapshotDao.insert(SecuritySnapshotEntity(timestamp = score.timestamp, overallScore = score.overallScore,
            permissionScore = score.permissionScore, networkScore = score.networkScore,
            systemScore = score.systemScore, storageBatteryScore = score.storageBatteryScore))
    }

    override suspend fun replaceAppPermissions(permissions: List<AppPermissionInfo>) {
        appPermissionDao.deleteAll()
        appPermissionDao.insertAll(
            permissions.map {
                AppPermissionEntity(
                    packageName = it.packageName,
                    appName = it.appName,
                    permissions = it.permissions.joinToString(","),
                    severity = it.severity.name,
                    recentlyGranted = it.recentlyGranted
                )
            }
        )
    }

    override fun getSnapshots(): Flow<List<SecuritySnapshotEntity>> = snapshotDao.getAllSnapshots()
    override fun getSavedAppPermissions(): Flow<List<AppPermissionEntity>> = appPermissionDao.getAllPermissions()
    override suspend fun getLatestSnapshot(): SecuritySnapshotEntity? = snapshotDao.getLatestSnapshot()

    override suspend fun saveAlerts(alerts: List<Alert>) {
        alertDao.insertAll(alerts.map { AlertEntity(timestamp = it.timestamp, category = it.category,
            severity = it.severity.name, title = it.title, message = it.message, isDismissed = it.isDismissed) })
    }

    override fun getActiveAlerts(): Flow<List<AlertEntity>> = alertDao.getActiveAlerts()
    override suspend fun dismissAlert(alertId: Long) = alertDao.dismissAlert(alertId)
}
