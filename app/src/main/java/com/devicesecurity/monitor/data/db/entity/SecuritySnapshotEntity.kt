package com.devicesecurity.monitor.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_snapshots")
data class SecuritySnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val overallScore: Int,
    val permissionScore: Int,
    val networkScore: Int,
    val systemScore: Int,
    val storageBatteryScore: Int
)