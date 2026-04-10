package com.devicesecurity.monitor.domain.model

data class AppCertInfo(
    val packageName: String,
    val appName: String,
    val isFromTrustedSource: Boolean,
    val installSource: String
)