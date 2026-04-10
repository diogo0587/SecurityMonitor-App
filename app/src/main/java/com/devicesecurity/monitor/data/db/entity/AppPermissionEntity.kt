package com.devicesecurity.monitor.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_permissions")
data class AppPermissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val permissions: String,
    val severity: String,
    val recentlyGranted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)