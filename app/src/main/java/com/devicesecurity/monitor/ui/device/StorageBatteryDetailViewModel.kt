package com.devicesecurity.monitor.ui.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicesecurity.monitor.domain.model.StorageBatteryInfo
import com.devicesecurity.monitor.domain.usecase.RunSecurityScanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageBatteryDetailViewModel @Inject constructor(
    private val runSecurityScanUseCase: RunSecurityScanUseCase
) : ViewModel() {

    private val _storageBatteryInfo = MutableStateFlow<StorageBatteryInfo?>(null)
    val storageBatteryInfo: StateFlow<StorageBatteryInfo?> = _storageBatteryInfo.asStateFlow()

    init {
        loadInfo()
    }

    private fun loadInfo() {
        viewModelScope.launch {
            val result = runSecurityScanUseCase()
            _storageBatteryInfo.value = result.storageBatteryInfo
        }
    }
}
