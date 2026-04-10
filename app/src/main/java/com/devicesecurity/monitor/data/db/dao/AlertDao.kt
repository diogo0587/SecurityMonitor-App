package com.devicesecurity.monitor.data.db.dao

import androidx.room.*
import com.devicesecurity.monitor.data.db.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<AlertEntity>)

    @Query("SELECT * FROM alerts WHERE isDismissed = 0 ORDER BY timestamp DESC")
    fun getActiveAlerts(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Query("UPDATE alerts SET isDismissed = 1 WHERE id = :alertId")
    suspend fun dismissAlert(alertId: Long)

    @Query("DELETE FROM alerts WHERE isDismissed = 1 AND timestamp < :cutoffTime")
    suspend fun deleteOldDismissed(cutoffTime: Long)
}