package com.devicesecurity.monitor.domain.model

data class NetworkInfo(
    val wifiSsid: String,
    val wifiSecurityType: String,
    val isWifiSecure: Boolean,
    val isVpnActive: Boolean,
    val connectionType: String,
    val suspiciousConnections: List<String> = emptyList()
)