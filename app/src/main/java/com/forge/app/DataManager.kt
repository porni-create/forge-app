package com.forge.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class ForgeTask(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val category: String, // Routine, Body, Mind, Calm
    val alarmHour: Int = -1,
    val locked: Boolean = false
)

object DataManager {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun today(): String = sdf.format(Date())

    fun getTasks(ctx: Context): List<ForgeTask> {
        val prefs = ctx.getSharedPreferences("forge", Context.MODE_PRIVATE)
        val json = prefs.getString("forge_tasks", null) ?: return emptyList()
        return parseTasks(json)
    }

    fun saveTasks(ctx: Context, tasks: List<ForgeTask>) {
        val arr = JSONArray()
        tasks.forEach { arr.put(taskToJson(it)) }
        ctx.getSharedPreferences("forge", Context.MODE_PRIVATE)
            .edit().putString("forge_tasks", arr.toString()).apply()
    }

    fun getDoneTasks(ctx: Context, date: String = today()): Set<String> {
        val prefs = ctx.getSharedPreferences("forge", Context.MODE_PRIVATE)
        val json = prefs.getString("forge_done_$date", null) ?: return emptySet()
        val arr = JSONArray(json)
        val set = mutableSetOf<String>()
        for (i in 0 until arr.length()) set.add(arr.getString(i))
        return set
    }

    fun toggleDone(ctx: Context, taskId: String, date: String = today()) {
        val done = getDoneTasks(ctx, date).toMutableSet()
        if (done.contains(taskId)) done.remove(taskId) else done.add(taskId)
        val arr = JSONArray(done.toList())
        ctx.getSharedPreferences("forge", Context.MODE_PRIVATE)
            .edit().putString("forge_done_$date", arr.toString()).apply()
        recalcStreak(ctx)
    }

    fun getStreak(ctx: Context): Int {
        return ctx.getSharedPreferences("forge", Context.MODE_PRIVATE)
            .getInt("forge_streak", 0)
    }

    fun getBestStreak(ctx: Context): Int {
        return ctx.getSharedPreferences("forge", Context.MODE_PRIVATE)
            .getInt("forge_best", 0)
    }

    fun getAlarmsEnabled(ctx: Context): Boolean {
        return ctx.getSharedPreferences("forge", Context.MODE_PRIVATE)
            .getBoolean("forge_alarms", false)
    }

    fun setAlarmsEnabled(ctx: Context, enabled: Boolean) {
        ctx.getSharedPreferences("forge", Context.MODE_PRIVATE)
            .edit().putBoolean("forge_alarms", enabled).apply()
    }

    fun resetAll(ctx: Context) {
        ctx.getSharedPreferences("forge", Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun recalcStreak(ctx: Context) {
        val prefs = ctx.getSharedPreferences("forge", Context.MODE_PRIVATE)
        val tasks = getTasks(ctx).filter { !it.locked }
        val total = tasks.size
        if (total == 0) return

        val cal = Calendar.getInstance()
        var streak = 0
        var checking = today()

        for (i in 0..365) {
            val done = getDoneTasks(ctx, checking)
            val pct = done.intersect(tasks.map { it.id }.toSet()).size.toFloat() / total
            if (pct >= 1.0f) {
                streak++
                cal.time = sdf.parse(checking)!!
                cal.add(Calendar.DAY_OF_YEAR, -1)
                checking = sdf.format(cal.time)
            } else if (i == 0) {
                // today not done yet, check yesterday
                cal.time = sdf.parse(checking)!!
                cal.add(Calendar.DAY_OF_YEAR, -1)
                checking = sdf.format(cal.time)
            } else {
                break
            }
        }

        val best = maxOf(prefs.getInt("forge_best", 0), streak)
        prefs.edit()
            .putInt("forge_streak", streak)
            .putInt("forge_best", best)
            .apply()
    }

    fun getWeeklyData(ctx: Context): List<Float> {
        val tasks = getTasks(ctx).filter { !it.locked }
        val total = tasks.size.toFloat()
        val result = mutableListOf<Float>()
        val cal = Calendar.getInstance()

        // Go to Monday of current week
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMon = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_YEAR, -daysFromMon)

        for (i in 0..6) {
            val dateStr = sdf.format(cal.time)
            val done = getDoneTasks(ctx, dateStr)
            val pct = if (total > 0) done.intersect(tasks.map { it.id }.toSet()).size / total else 0f
            result.add(pct)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return result
    }

    fun getTodayPercentage(ctx: Context): Int {
        val tasks = getTasks(ctx).filter { !it.locked }
        if (tasks.isEmpty()) return 0
        val done = getDoneTasks(ctx)
        val count = done.intersect(tasks.map { it.id }.toSet()).size
        return ((count.toFloat() / tasks.size) * 100).toInt()
    }

    fun initDefaultTasks(ctx: Context) {
        val existing = getTasks(ctx)
        if (existing.isNotEmpty()) return
        val defaults = listOf(
            ForgeTask("t1", "Wake up no phone", "Start clear, start strong", "7:00 AM", "Routine", 7),
            ForgeTask("t2", "Light movement", "Stretch, walk, or mobility work", "7:20 AM", "Body", -1),
            ForgeTask("t3", "Self-education", "Read, listen, or learn something new", "8:00 AM", "Mind", 8),
            ForgeTask("t4", "Gym or sport", "Move your body with intensity", "10:00 AM", "Body", 10),
            ForgeTask("t5", "Your commitment", "3 PM – 10 PM locked in", "3:00 PM", "Routine", -1, locked = true),
            ForgeTask("t6", "Journal and read", "Reflect on the day, feed your mind", "10:00 PM", "Calm", 22),
            ForgeTask("t7", "Lights out", "Rest is part of the work", "11:00 PM", "Routine", 23)
        )
        saveTasks(ctx, defaults)
    }

    private fun parseTasks(json: String): List<ForgeTask> {
        val arr = JSONArray(json)
        val list = mutableListOf<ForgeTask>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(ForgeTask(
                id = o.getString("id"),
                title = o.getString("title"),
                description = o.optString("description", ""),
                time = o.getString("time"),
                category = o.getString("category"),
                alarmHour = o.optInt("alarmHour", -1),
                locked = o.optBoolean("locked", false)
            ))
        }
        return list
    }

    private fun taskToJson(t: ForgeTask): JSONObject {
        return JSONObject().apply {
            put("id", t.id)
            put("title", t.title)
            put("description", t.description)
            put("time", t.time)
            put("category", t.category)
            put("alarmHour", t.alarmHour)
            put("locked", t.locked)
        }
    }
}
