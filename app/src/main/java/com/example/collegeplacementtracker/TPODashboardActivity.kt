package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.collegeplacementtracker.utils.SessionManager

class TPODashboardActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager.getInstance(this)

        // Check if logged in and is TPO
        if (!sessionManager.isLoggedIn() || !sessionManager.isTPO()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Create layout programmatically since we don't have XML yet
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            gravity = Gravity.CENTER
        }

        // Welcome text
        val welcomeText = TextView(this).apply {
            text = "TPO Dashboard\n\nWelcome ${sessionManager.getUserName()}!\n\nFull System Access"
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 40)
        }
        mainLayout.addView(welcomeText)

        // Info text
        val infoText = TextView(this).apply {
            text =
                "Full TPO Dashboard UI Coming Soon!\n\nYou are logged in as Training Placement Officer."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 40)
        }
        mainLayout.addView(infoText)

        // Logout button
        val logoutButton = Button(this).apply {
            text = "Logout"
            setOnClickListener {
                sessionManager.logout()
                val intent = Intent(this@TPODashboardActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        mainLayout.addView(logoutButton)

        setContentView(mainLayout)
    }
}