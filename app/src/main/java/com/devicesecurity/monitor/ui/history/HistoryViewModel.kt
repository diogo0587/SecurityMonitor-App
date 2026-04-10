package com.devicesecurity.monitor.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicesecurity.monitor.data.db.entity.SecuritySnapshotEntity
import com.devicesecurity.monitor.domain.usecase.GetSecurityHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getSecurityHistoryUseCase: GetSecurityHistoryUseCase
) : ViewModel() {

    val snapshots: StateFlow<List<SecuritySnapshotEntity>> = getSecurityHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
