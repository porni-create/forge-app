package com.forge.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

object AlarmScheduler {

    fun scheduleAll(context: Context) {
        val tasks = DataManager.getTasks(context)
        tasks.forEach { task ->
            if (task.alarmHour >= 0) {
                schedule(context, task)
            }
        }
    }

    fun schedule(context: Context, task: ForgeTask) {
        if (task.alarmHour < 0) return
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, task.alarmHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.forge.app.ALARM_TRIGGER"
            putExtra("task_title", task.title)
            putExtra("task_desc", task.description)
        }

        val pi = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        }
    }

    fun cancelAll(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val tasks = DataManager.getTasks(context)
        tasks.forEach { task ->
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = "com.forge.app.ALARM_TRIGGER"
            }
            val pi = PendingIntent.getBroadcast(
                context,
                task.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            am.cancel(pi)
        }
    }

    fun scheduleTest(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAt = System.currentTimeMillis() + 60_000L

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.forge.app.ALARM_TRIGGER"
            putExtra("task_title", "Test Alarm")
            putExtra("task_desc", "Forge alarms are working!")
        }

        val pi = PendingIntent.getBroadcast(
            context,
            999999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }
}
