package com.devicesecurity.monitor

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.devicesecurity.monitor.data.db.AppDatabase
import com.devicesecurity.monitor.ui.notification.SecurityNotificationHelper
import com.devicesecurity.monitor.util.ThemeHelper
import com.devicesecurity.monitor.worker.SecurityScanWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MonitorApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        ThemeHelper.applyTheme(this)
        SecurityNotificationHelper.createNotificationChannel(this)
        SecurityScanWorker.enqueue(this)
        cleanupOldNotifications()
    }

    private fun cleanupOldNotifications() {
        applicationScope.launch {
            try {
                val cutoffTime = System.currentTimeMillis() -
                    TimeUnit.DAYS.toMillis(7)
                AppDatabase.getInstance(applicationContext)
                    .notificationDao()
                    .deleteOld(cutoffTime)
            } catch (_: Exception) {
            }
        }
    }
}
