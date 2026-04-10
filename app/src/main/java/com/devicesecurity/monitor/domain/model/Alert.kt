package com.devicesecurity.monitor.domain.model

data class Alert(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String,
    val severity: Severity,
    val title: String,
    val message: String,
    val isDismissed: Boolean = false
)