package com.devicesecurity.monitor.domain.model

data class SystemInfo(
    val isRooted: Boolean,
    val isBootloaderUnlocked: Boolean,
    val selinuxStatus: String,
    val isUnknownSourcesEnabled: Boolean,
    val isDeveloperOptionsEnabled: Boolean,
    val isAdbEnabled: Boolean,
    val rootDetails: List<String> = emptyList()
)