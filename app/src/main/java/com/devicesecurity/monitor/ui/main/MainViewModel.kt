package com.devicesecurity.monitor.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicesecurity.monitor.data.db.entity.AlertEntity
import com.devicesecurity.monitor.data.db.entity.SecuritySnapshotEntity
import com.devicesecurity.monitor.domain.repository.SecurityScannerResult
import com.devicesecurity.monitor.domain.usecase.ExportReportUseCase
import com.devicesecurity.monitor.domain.usecase.GetActiveAlertsUseCase
import com.devicesecurity.monitor.domain.usecase.GetSecurityHistoryUseCase
import com.devicesecurity.monitor.domain.usecase.RunSecurityScanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = true,
    val isScanning: Boolean = false,
    val scanResult: SecurityScannerResult? = null,
    val recentAlerts: List<AlertEntity> = emptyList(),
    val snapshots: List<SecuritySnapshotEntity> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val runSecurityScanUseCase: RunSecurityScanUseCase,
    private val getSecurityHistoryUseCase: GetSecurityHistoryUseCase,
    private val getActiveAlertsUseCase: GetActiveAlertsUseCase,
    private val exportReportUseCase: ExportReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
        loadAlerts()
        runScan()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            getSecurityHistoryUseCase().collect { snapshots ->
                _uiState.update { it.copy(snapshots = snapshots) }
            }
        }
    }

    private fun loadAlerts() {
        viewModelScope.launch {
            getActiveAlertsUseCase().collect { alerts ->
                _uiState.update { it.copy(recentAlerts = alerts) }
            }
        }
    }

    fun runScan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, error = null) }
            try {
                val result = runSecurityScanUseCase()
                _uiState.update { it.copy(isScanning = false, isLoading = false, scanResult = result) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isScanning = false, isLoading = false, error = e.message) }
            }
        }
    }

    fun exportReport(): String {
        return _uiState.value.scanResult?.let { exportReportUseCase(it) } ?: ""
    }
}
