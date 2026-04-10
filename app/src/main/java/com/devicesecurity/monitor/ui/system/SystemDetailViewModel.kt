package com.devicesecurity.monitor.ui.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicesecurity.monitor.domain.model.SystemInfo
import com.devicesecurity.monitor.domain.usecase.RunSecurityScanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SystemDetailViewModel @Inject constructor(
    private val runSecurityScanUseCase: RunSecurityScanUseCase
) : ViewModel() {

    private val _systemInfo = MutableStateFlow<SystemInfo?>(null)
    val systemInfo: StateFlow<SystemInfo?> = _systemInfo.asStateFlow()

    init {
        loadSystemInfo()
    }

    private fun loadSystemInfo() {
        viewModelScope.launch {
            val result = runSecurityScanUseCase()
            _systemInfo.value = result.systemInfo
        }
    }
}
