package com.devicesecurity.monitor.data.db.dao

import androidx.room.*
import com.devicesecurity.monitor.data.db.entity.SecuritySnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SnapshotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snapshot: SecuritySnapshotEntity): Long

    @Query("SELECT * FROM security_snapshots ORDER BY timestamp DESC")
    fun getAllSnapshots(): Flow<List<SecuritySnapshotEntity>>

    @Query("SELECT * FROM security_snapshots ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentSnapshots(limit: Int): List<SecuritySnapshotEntity>

    @Query("SELECT * FROM security_snapshots ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSnapshot(): SecuritySnapshotEntity?

    @Query("DELETE FROM security_snapshots WHERE timestamp < :cutoffTime")
    suspend fun deleteOlderThan(cutoffTime: Long)
}