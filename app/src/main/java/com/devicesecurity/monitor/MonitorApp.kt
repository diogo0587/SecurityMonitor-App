package com.devicesecurity.monitor

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.devicesecurity.monitor.ui.notification.SecurityNotificationHelper
import com.devicesecurity.monitor.util.ThemeHelper
import com.devicesecurity.monitor.worker.SecurityScanWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MonitorApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        ThemeHelper.applyTheme(this)
        SecurityNotificationHelper.createNotificationChannel(this)
        SecurityScanWorker.enqueue(this)
    }
}
