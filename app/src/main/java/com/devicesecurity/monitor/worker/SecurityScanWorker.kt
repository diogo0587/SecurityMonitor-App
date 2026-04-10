package com.devicesecurity.monitor.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.devicesecurity.monitor.domain.repository.SecurityRepository
import com.devicesecurity.monitor.ui.notification.SecurityNotificationHelper
import com.devicesecurity.monitor.util.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SecurityScanWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SecurityRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val result = repository.runFullScan()
            repository.saveSnapshot(result.securityScore)
            
            val previousSnapshot = inputData.getInt("previous_score", 100)
            val scoreDrop = previousSnapshot - result.securityScore.overallScore
            
            if (scoreDrop > Constants.SCORE_DROP_THRESHOLD) {
                SecurityNotificationHelper.showScoreDropNotification(
                    applicationContext,
                    result.securityScore.overallScore,
                    scoreDrop
                )
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "security_scan_work"

        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<SecurityScanWorker>(
                Constants.SCAN_INTERVAL_HOURS, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun enqueueOneTime(context: Context, previousScore: Int) {
            val inputData = workDataOf("previous_score" to previousScore)
            val workRequest = OneTimeWorkRequestBuilder<SecurityScanWorker>()
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
