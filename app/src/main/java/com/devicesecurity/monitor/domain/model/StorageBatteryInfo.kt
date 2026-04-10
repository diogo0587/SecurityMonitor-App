package com.devicesecurity.monitor.domain.model

data class StorageBatteryInfo(
    val isEncrypted: Boolean,
    val encryptionStatus: String,
    val totalStorageGb: Float,
    val freeStorageGb: Float,
    val batteryLevel: Int,
    val batteryHealth: String,
    val batteryTemperature: Float,
    val isCharging: Boolean
)