package com.devicesecurity.monitor.domain.usecase

import com.devicesecurity.monitor.domain.model.Alert
import com.devicesecurity.monitor.domain.model.Severity
import com.devicesecurity.monitor.domain.repository.SecurityRepository
import com.devicesecurity.monitor.domain.repository.SecurityScannerResult
import javax.inject.Inject

class RunSecurityScanUseCase @Inject constructor(private val repository: SecurityRepository) {
    suspend operator fun invoke(): SecurityScannerResult {
        val result = repository.runFullScan()
        repository.replaceAppPermissions(result.appPermissions)
        repository.saveSnapshot(result.securityScore)
        val alerts = generateAlerts(result)
        if (alerts.isNotEmpty()) repository.saveAlerts(alerts)
        return result
    }

    private fun generateAlerts(result: SecurityScannerResult): List<Alert> {
        val alerts = mutableListOf<Alert>()
        val score = result.securityScore
        if (score.overallScore < 50) {
            alerts.add(Alert(category = "overall", severity = Severity.CRITICAL,
                title = "Low Security Score", message = "Device security score is ${score.overallScore}/100"))
        }
        if (result.systemInfo.isRooted) {
            alerts.add(Alert(category = "system", severity = Severity.CRITICAL,
                title = "Root Access Detected", message = "This device appears to be rooted"))
        }
        if (!result.networkInfo.isWifiSecure) {
            alerts.add(Alert(category = "network", severity = Severity.HIGH,
                title = "Insecure WiFi", message = "Connected to an insecure WiFi network"))
        }
        if (!result.lockscreenInfo.isSecure) {
            alerts.add(Alert(category = "system", severity = Severity.CRITICAL,
                title = "No Screen Lock", message = "No screen lock is set"))
        }
        return alerts
    }
}
