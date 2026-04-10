package com.devicesecurity.monitor.domain.model

enum class SecurityCategory(val displayName: String) {
    PERMISSIONS("Permissions"),
    NETWORK("Network"),
    SYSTEM("System"),
    DEVICE("Device")
}