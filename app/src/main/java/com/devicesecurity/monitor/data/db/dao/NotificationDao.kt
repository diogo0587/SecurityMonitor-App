package com.devicesecurity.monitor.data.db.dao

import androidx.room.*
import com.devicesecurity.monitor.data.db.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity): Long

    @Query("SELECT * FROM captured_notifications ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentNotifications(limit: Int = 50): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM captured_notifications WHERE packageName = :packageName ORDER BY timestamp DESC")
    fun getNotificationsByApp(packageName: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM captured_notifications WHERE isSensitive = 1 ORDER BY timestamp DESC")
    fun getSensitiveNotifications(): Flow<List<NotificationEntity>>

    @Query("UPDATE captured_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("DELETE FROM captured_notifications WHERE timestamp < :cutoffTime")
    suspend fun deleteOld(cutoffTime: Long)

    @Query("SELECT COUNT(*) FROM captured_notifications WHERE timestamp >= :since")
    suspend fun countSince(since: Long): Int
}
