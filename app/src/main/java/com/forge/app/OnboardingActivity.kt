package com.forge.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("forge", MODE_PRIVATE)
        val name = prefs.getString("forge_name", null)
        if (!name.isNullOrBlank()) {
            startMain()
            return
        }

        setContentView(R.layout.activity_onboarding)

        val etName = findViewById<EditText>(R.id.etName)
        val btnStart = findViewById<Button>(R.id.btnStart)

        btnStart.setOnClickListener {
            val input = etName.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefs.edit().putString("forge_name", input).apply()
            DataManager.initDefaultTasks(this)
            startMain()
        }
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
