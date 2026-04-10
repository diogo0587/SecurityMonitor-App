package com.devicesecurity.monitor.ui.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicesecurity.monitor.domain.model.BluetoothInfo
import com.devicesecurity.monitor.domain.model.NetworkInfo
import com.devicesecurity.monitor.domain.usecase.RunSecurityScanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkDetailViewModel @Inject constructor(
    private val runSecurityScanUseCase: RunSecurityScanUseCase
) : ViewModel() {

    private val _networkInfo = MutableStateFlow<NetworkInfo?>(null)
    val networkInfo: StateFlow<NetworkInfo?> = _networkInfo.asStateFlow()

    private val _bluetoothInfo = MutableStateFlow<BluetoothInfo?>(null)
    val bluetoothInfo: StateFlow<BluetoothInfo?> = _bluetoothInfo.asStateFlow()

    init {
        loadNetworkInfo()
    }

    private fun loadNetworkInfo() {
        viewModelScope.launch {
            val result = runSecurityScanUseCase()
            _networkInfo.value = result.networkInfo
            _bluetoothInfo.value = result.bluetoothInfo
        }
    }
}
