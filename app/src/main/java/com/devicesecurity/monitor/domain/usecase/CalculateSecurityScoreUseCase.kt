package com.devicesecurity.monitor.domain.usecase

import com.devicesecurity.monitor.data.scanner.OutdatedAppsScanner
import com.devicesecurity.monitor.domain.model.*
import com.devicesecurity.monitor.util.ScoreCalculator
import javax.inject.Inject

class CalculateSecurityScoreUseCase @Inject constructor() {

    data class ScoreInput(
        val appPermissions: List<AppPermissionInfo>,
        val networkInfo: NetworkInfo,
        val systemInfo: SystemInfo,
        val storageBatteryInfo: StorageBatteryInfo,
        val bluetoothInfo: BluetoothInfo,
        val lockscreenInfo: LockscreenInfo,
        val outdatedApps: List<OutdatedAppsScanner.OutdatedAppInfo>
    )

    operator fun invoke(input: ScoreInput): SecurityScore {
        val permissionScore = ScoreCalculator.calculatePermissionScore(input.appPermissions)
        val networkScore = ScoreCalculator.calculateNetworkScore(input.networkInfo, input.bluetoothInfo)
        val systemScore = ScoreCalculator.calculateSystemScore(input.systemInfo, input.lockscreenInfo)
        val storageBatteryScore = ScoreCalculator.calculateStorageBatteryScore(input.storageBatteryInfo)

        val overall = ScoreCalculator.calculateOverallScore(
            permissionScore, networkScore, systemScore, storageBatteryScore
        )

        val outdatedPenalty = ScoreCalculator.calculateOutdatedAppsPenalty(input.outdatedApps)
        val finalScore = (overall - outdatedPenalty).coerceIn(0, 100)

        return SecurityScore(
            overallScore = finalScore,
            permissionScore = permissionScore,
            networkScore = networkScore,
            systemScore = systemScore,
            storageBatteryScore = storageBatteryScore
        )
    }
}
