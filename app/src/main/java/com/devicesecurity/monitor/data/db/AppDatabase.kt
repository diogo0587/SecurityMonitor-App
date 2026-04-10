package com.devicesecurity.monitor.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.devicesecurity.monitor.data.db.dao.AlertDao
import com.devicesecurity.monitor.data.db.dao.AppPermissionDao
import com.devicesecurity.monitor.data.db.dao.SnapshotDao
import com.devicesecurity.monitor.data.db.entity.AlertEntity
import com.devicesecurity.monitor.data.db.entity.AppPermissionEntity
import com.devicesecurity.monitor.data.db.entity.SecuritySnapshotEntity

@Database(
    entities = [SecuritySnapshotEntity::class, AppPermissionEntity::class, AlertEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun snapshotDao(): SnapshotDao
    abstract fun appPermissionDao(): AppPermissionDao
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "security_monitor_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}