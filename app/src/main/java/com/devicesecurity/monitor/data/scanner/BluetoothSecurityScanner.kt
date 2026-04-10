package com.devicesecurity.monitor.data.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.devicesecurity.monitor.domain.model.BluetoothInfo

class BluetoothSecurityScanner(private val context: Context) {

    @SuppressLint("MissingPermission")
    fun scan(): BluetoothInfo {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val bluetoothAdapter = bluetoothManager?.adapter

        val isBluetoothEnabled = bluetoothAdapter?.isEnabled == false

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            return BluetoothInfo(
                isBluetoothEnabled = false,
                isDiscoverable = false,
                pairedDeviceCount = 0,
                pairedDeviceNames = emptyList()
            )
        }

        val scanMode = bluetoothAdapter.scanMode
        val isDiscoverable = scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE

        val hasBluetoothPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val pairedDevices = if (hasBluetoothPermission) {
            try {
                bluetoothAdapter.bondedDevices?.toList() ?: emptyList()
            } catch (_: SecurityException) {
                emptyList()
            }
        } else {
            emptyList()
        }

        val pairedDeviceNames = pairedDevices.mapNotNull {
            try { it.name } catch (_: SecurityException) { null }
        }

        return BluetoothInfo(
            isBluetoothEnabled = true,
            isDiscoverable = isDiscoverable,
            pairedDeviceCount = pairedDevices.size,
            pairedDeviceNames = pairedDeviceNames
        )
    }
}