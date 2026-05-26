package com.forge.app

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var container: FrameLayout
    private lateinit var bottomNav: BottomNavigationView

    private val quotes = listOf(
        Pair("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Aristotle"),
        Pair("Float like a butterfly, sting like a bee. The hands can't hit what the eyes can't see.", "Muhammad Ali"),
        Pair("Give me six hours to chop down a tree and I will spend the first four sharpening the axe.", "Abraham Lincoln"),
        Pair("You do not rise to the level of your goals. You fall to the level of your systems.", "James Clear"),
        Pair("Hard choices, easy life. Easy choices, hard life.", "Jerzy Gregorek"),
        Pair("The man who moves a mountain begins by carrying away small stones.", "Confucius"),
        Pair("Pain is temporary. Quitting lasts forever.", "Lance Armstrong"),
        Pair("Small daily improvements over time lead to stunning results.", "Robin Sharma"),
        Pair("A year from now you may wish you had started today.", "Karen Lamb"),
        Pair("You have power over your mind, not outside events. Realize this, and you will find strength.", "Marcus Aurelius"),
        Pair("The impediment to action advances action. What stands in the way becomes the way.", "Marcus Aurelius"),
        Pair("Do what you have to do until you can do what you want to do.", "Sean Patrick Flanery"),
        Pair("Whatever you can do or dream you can, begin it. Boldness has genius, power and magic in it.", "Goethe"),
        Pair("The secret of getting ahead is getting started.", "Mark Twain"),
        Pair("You don't start out writing good stuff. You start out writing crap and thinking it's good stuff.", "Octavia Butler")
    )

    private var currentQuoteIndex = Random().nextInt(15)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0e0e12"))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        container = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }

        bottomNav = BottomNavigationView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.parseColor("#16161e"))
            itemIconTintList = null
            inflateMenu(R.menu.bottom_nav)
            itemTextColor = android.content.res.ColorStateList.valueOf(Color.WHITE)
          
        }

        root.addView(container)
        root.addView(bottomNav)
        setContentView(root)

        showHome()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { showHome(); true }
                R.id.nav_stats -> { showStats(); true }
                R.id.nav_settings -> { showSettings(); true }
                else -> false
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    // ─── HOME SCREEN ─────────────────────────────────────────────────────────

    private fun showHome() {
        container.removeAllViews()
        val scroll = ScrollView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        // Header row
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val name = getSharedPreferences("forge", MODE_PRIVATE).getString("forge_name", "Friend") ?: "Friend"
        val tvHello = TextView(this).apply {
            text = "Hey, $name 👋"
            textSize = 22f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val streak = DataManager.getStreak(this)
        val tvStreak = TextView(this).apply {
            text = "🔥 $streak"
            textSize = 14f
            setTextColor(Color.WHITE)
            background = makeRoundBg(Color.parseColor("#7c6fe0"), 40f)
            setPadding(24, 12, 24, 12)
        }
        headerRow.addView(tvHello)
        headerRow.addView(tvStreak)
        content.addView(headerRow)
        content.addView(spacer(24))

        // Quote card
        val quoteCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = makeRoundBg(Color.parseColor("#16161e"), 16f)
            setPadding(40, 32, 40, 32)
        }
        val leftBorder = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(8, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                setMargins(0, 0, 24, 0)
            }
            setBackgroundColor(Color.parseColor("#7c6fe0"))
        }
        val quoteRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }

        val quoteTexts = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val tvQuote = TextView(this).apply {
            text = "\"${quotes[currentQuoteIndex].first}\""
            textSize = 14f
            setTextColor(Color.parseColor("#cccccc"))
            typeface = android.graphics.Typeface.create("serif", android.graphics.Typeface.ITALIC)
        }
        val tvAuthor = TextView(this).apply {
            text = "— ${quotes[currentQuoteIndex].second}"
            textSize = 12f
            setTextColor(Color.parseColor("#7c6fe0"))
            setPadding(0, 8, 0, 0)
        }
        quoteTexts.addView(tvQuote)
        quoteTexts.addView(tvAuthor)

        val btnShuffle = Button(this).apply {
            text = "↺"
            textSize = 18f
            setTextColor(Color.parseColor("#7c6fe0"))
            background = null
            setOnClickListener {
                currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
                tvQuote.text = "\"${quotes[currentQuoteIndex].first}\""
                tvAuthor.text = "— ${quotes[currentQuoteIndex].second}"
            }
        }
        quoteRow.addView(leftBorder)
        quoteRow.addView(quoteTexts)
        quoteRow.addView(btnShuffle)
        quoteCard.addView(quoteRow)
        content.addView(quoteCard)
        content.addView(spacer(24))

        // Progress
        val tasks = DataManager.getTasks(this)
        val checkableTasks = tasks.filter { !it.locked }
        val done = DataManager.getDoneTasks(this)
        val doneCount = done.intersect(checkableTasks.map { it.id }.toSet()).size
        val total = checkableTasks.size
        val pct = if (total > 0) doneCount.toFloat() / total else 0f

        val tvProgress = TextView(this).apply {
            text = "Today's progress — $doneCount / $total tasks (${(pct * 100).toInt()}%)"
            textSize = 13f
            setTextColor(Color.parseColor("#aaaaaa"))
        }
        content.addView(tvProgress)
        content.addView(spacer(8))

        val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 16
            )
            max = 100
            progress = (pct * 100).toInt()
            progressDrawable.setColorFilter(
                Color.parseColor("#7c6fe0"),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
        content.addView(progressBar)
        content.addView(spacer(24))

        // All done message
        if (doneCount == total && total > 0) {
            val tvAllDone = TextView(this).apply {
                text = "🎉 All done for today! You forged today."
                textSize = 15f
                setTextColor(Color.parseColor("#7c6fe0"))
                gravity = Gravity.CENTER
                background = makeRoundBg(Color.parseColor("#1a1828"), 12f)
                setPadding(24, 20, 24, 20)
            }
            content.addView(tvAllDone)
            content.addView(spacer(16))
        }

        // Tasks
        tasks.forEach { task ->
            val isDone = done.contains(task.id)
            val taskCard = buildTaskCard(task, isDone) {
                if (!task.locked) {
                    DataManager.toggleDone(this, task.id)
                    showHome()
                }
            }
            content.addView(taskCard)
            content.addView(spacer(12))
        }

        content.addView(spacer(8))

        // Add task button
        val btnAdd = Button(this).apply {
            text = "+ Add Task"
            textSize = 15f
            setTextColor(Color.WHITE)
            background = makeRoundBg(Color.parseColor("#7c6fe0"), 12f)
            setPadding(0, 32, 0, 32)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { showAddTaskSheet() }
        }
        content.addView(btnAdd)

        scroll.addView(content)
        container.addView(scroll)
    }

    private fun buildTaskCard(task: ForgeTask, isDone: Boolean, onClick: () -> Unit): View {
        val catColors = mapOf(
            "Routine" to "#5b8dee",
            "Body" to "#e06b74",
            "Mind" to "#e5c07b",
            "Calm" to "#56b6c2"
        )
        val catColor = catColors[task.category] ?: "#888888"

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = makeRoundBg(Color.parseColor("#16161e"), 14f)
            setPadding(32, 24, 32, 24)
            gravity = Gravity.CENTER_VERTICAL
            alpha = if (isDone) 0.5f else 1f
            setOnClickListener { onClick() }
        }

        val checkbox = TextView(this).apply {
            text = if (isDone) "✓" else "○"
            textSize = 20f
            setTextColor(if (isDone) Color.parseColor("#7c6fe0") else Color.parseColor("#555555"))
            setPadding(0, 0, 24, 0)
        }

        val textBlock = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val tvTime = TextView(this).apply {
            text = task.time
            textSize = 11f
            setTextColor(Color.parseColor("#777777"))
            setPadding(0, 0, 12, 0)
        }
        val tvCat = TextView(this).apply {
            text = task.category
            textSize = 10f
            setTextColor(Color.parseColor(catColor))
            background = makeRoundBg(Color.parseColor(catColor + "33"), 20f)
            setPadding(16, 6, 16, 6)
        }
        topRow.addView(tvTime)
        topRow.addView(tvCat)

        val tvTitle = TextView(this).apply {
            text = if (task.locked) "🔒 ${task.title}" else task.title
            textSize = 15f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            paintFlags = if (isDone) paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG else paintFlags
        }
        val tvDesc = TextView(this).apply {
            text = task.description
            textSize = 12f
            setTextColor(Color.parseColor("#888888"))
        }

        textBlock.addView(topRow)
        textBlock.addView(spacer(4))
        textBlock.addView(tvTitle)
        textBlock.addView(tvDesc)

        card.addView(checkbox)
        card.addView(textBlock)

        if (!task.locked) {
            val btnDelete = TextView(this).apply {
                text = "✕"
                textSize = 16f
                setTextColor(Color.parseColor("#555555"))
                setPadding(16, 0, 0, 0)
                setOnClickListener {
                    val tasks = DataManager.getTasks(this@MainActivity).toMutableList()
                    tasks.removeAll { it.id == task.id }
                    DataManager.saveTasks(this@MainActivity, tasks)
                    showHome()
                }
            }
            card.addView(btnDelete)
        }

        return card
    }

    private fun showAddTaskSheet() {
        val sheet = BottomSheetDialog(this)
        val view = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#16161e"))
            setPadding(48, 48, 48, 64)
        }

        val tvTitle = TextView(this).apply {
            text = "Add Task"
            textSize = 18f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        view.addView(tvTitle)
        view.addView(spacer(24))

        val etTitle = EditText(this).apply {
            hint = "Task title"
            setHintTextColor(Color.parseColor("#555555"))
            setTextColor(Color.WHITE)
            background = makeRoundBg(Color.parseColor("#0e0e12"), 10f)
            setPadding(24, 20, 24, 20)
        }
        val etDesc = EditText(this).apply {
            hint = "Description"
            setHintTextColor(Color.parseColor("#555555"))
            setTextColor(Color.WHITE)
            background = makeRoundBg(Color.parseColor("#0e0e12"), 10f)
            setPadding(24, 20, 24, 20)
        }
        val etTime = EditText(this).apply {
            hint = "Time (e.g. 9:00 AM)"
            setHintTextColor(Color.parseColor("#555555"))
            setTextColor(Color.WHITE)
            background = makeRoundBg(Color.parseColor("#0e0e12"), 10f)
            setPadding(24, 20, 24, 20)
        }

        val categories = arrayOf("Routine", "Body", "Mind", "Calm")
        val spinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, categories)
            setBackgroundColor(Color.parseColor("#0e0e12"))
        }

        val etAlarm = EditText(this).apply {
            hint = "Alarm hour (0-23, or leave blank)"
            setHintTextColor(Color.parseColor("#555555"))
            setTextColor(Color.WHITE)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            background = makeRoundBg(Color.parseColor("#0e0e12"), 10f)
            setPadding(24, 20, 24, 20)
        }

        val btnSave = Button(this).apply {
            text = "Add Task"
            setTextColor(Color.WHITE)
            background = makeRoundBg(Color.parseColor("#7c6fe0"), 10f)
            setPadding(0, 28, 0, 28)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        listOf(etTitle, etDesc, etTime, spinner, etAlarm, btnSave).forEach {
            view.addView(it)
            view.addView(spacer(12))
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val alarmHour = etAlarm.text.toString().trim().toIntOrNull() ?: -1
            val task = ForgeTask(
                id = "t_${System.currentTimeMillis()}",
                title = title,
                description = etDesc.text.toString().trim(),
                time = etTime.text.toString().trim().ifEmpty { "--" },
                category = categories[spinner.selectedItemPosition],
                alarmHour = alarmHour
            )
            val tasks = DataManager.getTasks(this).toMutableList()
            tasks.add(task)
            DataManager.saveTasks(this, tasks)
            if (DataManager.getAlarmsEnabled(this) && alarmHour >= 0) {
                AlarmScheduler.schedule(this, task)
            }
            sheet.dismiss()
            showHome()
        }

        sheet.setContentView(view)
        sheet.show()
    }

    // ─── STATS SCREEN ─────────────────────────────────────────────────────────

    private fun showStats() {
        container.removeAllViews()
        val scroll = ScrollView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val tvTitle = TextView(this).apply {
            text = "Stats"
            textSize = 22f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        content.addView(tvTitle)
        content.addView(spacer(32))

        // Weekly dots
        val tvWeek = TextView(this).apply {
            text = "This Week"
            textSize = 14f
            setTextColor(Color.parseColor("#aaaaaa"))
        }
        content.addView(tvWeek)
        content.addView(spacer(12))

        val weekData = DataManager.getWeeklyData(this)
        val days = listOf("M", "T", "W", "T", "F", "S", "S")
        val todayDow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val todayIndex = if (todayDow == Calendar.SUNDAY) 6 else todayDow - Calendar.MONDAY

        val weekRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        weekData.forEachIndexed { i, pct ->
            val cell = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val dotColor = when {
                pct >= 1f -> Color.parseColor("#7c6fe0")
                pct > 0f -> Color.parseColor("#3d3570")
                else -> Color.parseColor("#222222")
            }
            val dot = View(this).apply {
                val size = 36
                layoutParams = LinearLayout.LayoutParams(size, size).apply { setMargins(0, 0, 0, 8) }
                background = makeCircle(dotColor, if (i == todayIndex) Color.parseColor("#7c6fe0") else Color.TRANSPARENT, 3f)
            }
            val label = TextView(this).apply {
                text = days[i]
                textSize = 11f
                setTextColor(if (i == todayIndex) Color.parseColor("#7c6fe0") else Color.parseColor("#555555"))
                gravity = Gravity.CENTER
            }
            cell.addView(dot)
            cell.addView(label)
            weekRow.addView(cell)
        }
        content.addView(weekRow)
        content.addView(spacer(32))

        // Stat cards
        val streak = DataManager.getStreak(this)
        val best = DataManager.getBestStreak(this)
        val todayPct = DataManager.getTodayPercentage(this)
        val totalTasks = DataManager.getTasks(this).size

        val statsGrid = GridLayout(this).apply {
            columnCount = 2
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        listOf(
            Pair("🔥 Day Streak", "$streak days"),
            Pair("🏆 Best Streak", "$best days"),
            Pair("✅ Today", "$todayPct%"),
            Pair("📋 Total Tasks", "$totalTasks tasks")
        ).forEach { (label, value) ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = makeRoundBg(Color.parseColor("#16161e"), 14f)
                setPadding(32, 28, 32, 28)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(0, 0, 12, 12)
                }
            }
            val tvLabel = TextView(this).apply {
                text = label
                textSize = 12f
                setTextColor(Color.parseColor("#888888"))
            }
            val tvValue = TextView(this).apply {
                text = value
                textSize = 24f
                setTextColor(Color.WHITE)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            card.addView(tvLabel)
            card.addView(tvValue)
            statsGrid.addView(card)
        }
        content.addView(statsGrid)

        scroll.addView(content)
        container.addView(scroll)
    }

    // ─── SETTINGS SCREEN ──────────────────────────────────────────────────────

    private fun showSettings() {
        container.removeAllViews()
        val scroll = ScrollView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val tvTitle = TextView(this).apply {
            text = "Settings"
            textSize = 22f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        content.addView(tvTitle)
        content.addView(spacer(32))

        val name = getSharedPreferences("forge", MODE_PRIVATE).getString("forge_name", "") ?: ""
        val tvName = TextView(this).apply {
            text = "👤 $name"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = makeRoundBg(Color.parseColor("#16161e"), 12f)
            setPadding(32, 24, 32, 24)
        }
        content.addView(tvName)
        content.addView(spacer(24))

        // Alarms toggle
        val alarmRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = makeRoundBg(Color.parseColor("#16161e"), 12f)
            setPadding(32, 24, 32, 24)
        }
        val tvAlarm = TextView(this).apply {
            text = "🔔 Daily Alarms"
            textSize = 15f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val toggle = Switch(this).apply {
            isChecked = DataManager.getAlarmsEnabled(this@MainActivity)
            thumbTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#7c6fe0"))
        }
        toggle.setOnCheckedChangeListener { _, checked ->
            DataManager.setAlarmsEnabled(this, checked)
            if (checked) AlarmScheduler.scheduleAll(this) else AlarmScheduler.cancelAll(this)
            Toast.makeText(this, if (checked) "Alarms enabled" else "Alarms disabled", Toast.LENGTH_SHORT).show()
        }
        alarmRow.addView(tvAlarm)
        alarmRow.addView(toggle)
        content.addView(alarmRow)
        content.addView(spacer(16))

        // Test alarm
        val btnTest = Button(this).apply {
            text = "⏰ Test Alarm (fires in 1 min)"
            setTextColor(Color.parseColor("#7c6fe0"))
            background = makeRoundBg(Color.parseColor("#16161e"), 12f)
            setPadding(0, 28, 0, 28)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                AlarmScheduler.scheduleTest(this@MainActivity)
                Toast.makeText(this@MainActivity, "Test alarm set for 1 minute from now", Toast.LENGTH_SHORT).show()
            }
        }
        content.addView(btnTest)
        content.addView(spacer(32))

        // Reset
        val btnReset = Button(this).apply {
            text = "🗑 Reset All Data"
            setTextColor(Color.WHITE)
            background = makeRoundBg(Color.parseColor("#8b0000"), 12f)
            setPadding(0, 28, 0, 28)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Reset everything?")
                    .setMessage("This will erase your name, tasks, streaks, and all progress. Cannot be undone.")
                    .setPositiveButton("Reset") { _, _ ->
                        AlarmScheduler.cancelAll(this@MainActivity)
                        DataManager.resetAll(this@MainActivity)
                        startActivity(android.content.Intent(this@MainActivity, OnboardingActivity::class.java))
                        finish()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
        content.addView(btnReset)

        scroll.addView(content)
        container.addView(scroll)
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private fun spacer(height: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height
            )
        }
    }

    private fun makeRoundBg(color: Int, radius: Float): android.graphics.drawable.GradientDrawable {
        return android.graphics.drawable.GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius
        }
    }

    private fun makeCircle(fillColor: Int, strokeColor: Int, strokeWidth: Float): android.graphics.drawable.GradientDrawable {
        return android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.OVAL
            setColor(fillColor)
            if (strokeColor != Color.TRANSPARENT) setStroke(strokeWidth.toInt(), strokeColor)
        }
    }
}
