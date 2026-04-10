package com.devicesecurity.monitor.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.devicesecurity.monitor.R
import com.devicesecurity.monitor.ui.main.MainActivity
import com.devicesecurity.monitor.util.ScoreCalculator

class SecurityScoreWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, SecurityScoreWidget::class.java)
            )
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    companion object {
        const val ACTION_UPDATE = "com.devicesecurity.monitor.WIDGET_UPDATE"
        const val EXTRA_SCORE = "extra_score"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, score: Int = 75) {
            val views = RemoteViews(context.packageName, R.layout.widget_security_score)

            val color = ScoreCalculator.getScoreColor(score)
            val label = ScoreCalculator.getScoreLabel(score)

            views.setTextViewText(R.id.widget_score, score.toString())
            views.setTextViewText(R.id.widget_label, label)
            views.setTextColor(R.id.widget_score, color)
            views.setTextColor(R.id.widget_label, color)

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun sendUpdateBroadcast(context: Context, score: Int) {
            val intent = Intent(context, SecurityScoreWidget::class.java).apply {
                action = ACTION_UPDATE
                putExtra(EXTRA_SCORE, score)
            }
            context.sendBroadcast(intent)
        }
    }
}
