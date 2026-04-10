package com.devicesecurity.monitor.domain.model

data class BluetoothInfo(
    val isBluetoothEnabled: Boolean,
    val isDiscoverable: Boolean,
    val pairedDeviceCount: Int,
    val pairedDeviceNames: List<String> = emptyList()
)