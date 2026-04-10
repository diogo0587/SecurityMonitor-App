package com.devicesecurity.monitor.domain.model

data class LockscreenInfo(
    val isSecure: Boolean,
    val lockType: String,
    val hasBiometric: Boolean
)