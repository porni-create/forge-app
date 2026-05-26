package com.forge.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                if (DataManager.getAlarmsEnabled(context)) {
                    AlarmScheduler.scheduleAll(context)
                }
            }
            "com.forge.app.ALARM_TRIGGER" -> {
                val title = intent.getStringExtra("task_title") ?: "Time to forge"
                val desc = intent.getStringExtra("task_desc") ?: "Complete your task"
                showNotification(context, title, desc)
            }
        }
    }

    private fun showNotification(context: Context, title: String, desc: String) {
        val channelId = "forge_alarms"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "Forge Alarms", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Daily task reminders"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }
            nm.createNotificationChannel(ch)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("🔥 $title")
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setAutoCancel(true)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}
