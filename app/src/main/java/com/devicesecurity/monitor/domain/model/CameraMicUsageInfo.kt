package com.devicesecurity.monitor.domain.model

data class CameraMicUsageInfo(
    val packageName: String,
    val appName: String,
    val sensorType: String,
    val lastUsedTimestamp: Long
)