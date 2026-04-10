package com.devicesecurity.monitor.worker

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SecurityScanService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        SecurityScanWorker.enqueueOneTime(this, 100)
        stopSelf()
        return START_NOT_STICKY
    }
}
