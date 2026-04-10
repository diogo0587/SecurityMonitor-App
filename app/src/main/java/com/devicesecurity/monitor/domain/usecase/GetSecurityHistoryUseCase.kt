package com.devicesecurity.monitor.domain.usecase

import com.devicesecurity.monitor.data.db.entity.SecuritySnapshotEntity
import com.devicesecurity.monitor.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSecurityHistoryUseCase @Inject constructor(private val repository: SecurityRepository) {
    operator fun invoke(): Flow<List<SecuritySnapshotEntity>> = repository.getSnapshots()
}
