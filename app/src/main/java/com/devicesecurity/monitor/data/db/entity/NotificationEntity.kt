package com.devicesecurity.monitor.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "captured_notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val packageName: String,
    val packageLabel: String,
    val title: String,
    val body: String,
    val isSensitive: Boolean = false,
    val isRead: Boolean = false
)
