package com.devicesecurity.monitor.ui.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicesecurity.monitor.domain.model.Severity
import com.devicesecurity.monitor.domain.model.AppPermissionInfo
import com.devicesecurity.monitor.domain.usecase.GetSavedPermissionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PermissionDetailViewModel @Inject constructor(
    getSavedPermissionsUseCase: GetSavedPermissionsUseCase
) : ViewModel() {

    val permissions: StateFlow<List<AppPermissionInfo>> = getSavedPermissionsUseCase()
        .map { entities ->
            entities.map { entity ->
                AppPermissionInfo(
                    packageName = entity.packageName,
                    appName = entity.appName,
                    permissions = entity.permissions.split(",").filter { it.isNotBlank() },
                    severity = Severity.valueOf(entity.severity),
                    recentlyGranted = entity.recentlyGranted
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
