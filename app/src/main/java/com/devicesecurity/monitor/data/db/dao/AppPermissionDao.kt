package com.devicesecurity.monitor.data.db.dao

import androidx.room.*
import com.devicesecurity.monitor.data.db.entity.AppPermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppPermissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(permissions: List<AppPermissionEntity>)

    @Query("SELECT * FROM app_permissions ORDER BY severity DESC, timestamp DESC")
    fun getAllPermissions(): Flow<List<AppPermissionEntity>>

    @Query("SELECT * FROM app_permissions WHERE recentlyGranted = 1 ORDER BY timestamp DESC")
    fun getRecentlyGranted(): Flow<List<AppPermissionEntity>>

    @Query("DELETE FROM app_permissions")
    suspend fun deleteAll()
}