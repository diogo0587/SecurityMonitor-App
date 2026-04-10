package com.devicesecurity.monitor.domain.model

data class SecurityScore(
    val overallScore: Int,
    val permissionScore: Int,
    val networkScore: Int,
    val systemScore: Int,
    val storageBatteryScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)