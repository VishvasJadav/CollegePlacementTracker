package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var welcomeTextView: TextView
    private lateinit var profileCard: CardView
    private lateinit var companiesCard: CardView
    private lateinit var applicationsCard: CardView
    private lateinit var profileCompletionCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        sessionManager = SessionManager(this)

        // Check if logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        // Initialize views
        welcomeTextView = findViewById(R.id.welcomeTextView)
        profileCard = findViewById(R.id.profileCard)
        companiesCard = findViewById(R.id.companiesCard)
        applicationsCard = findViewById(R.id.applicationsCard)
        profileCompletionCard = findViewById(R.id.profileCompletionCard)

        // Set welcome message
        welcomeTextView.text = "Welcome, ${sessionManager.getUserName()}!"

        // Set branch and CGPA if available
        val branchTextView = findViewById<TextView>(R.id.branchTextView)
        branchTextView.text = sessionManager.getUserBranch() ?: "N/A"

        val cgpaTextView = findViewById<TextView>(R.id.cgpaTextView)
        cgpaTextView.text = "CGPA: N/A" // Will be loaded from database later

        // Card click listeners - Show "Coming Soon" messages for now
        profileCard.setOnClickListener {
            Toast.makeText(this, "Profile - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        companiesCard.setOnClickListener {
            Toast.makeText(this, "Browse Companies - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        applicationsCard.setOnClickListener {
            Toast.makeText(this, "My Applications - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        profileCompletionCard.setOnClickListener {
            Toast.makeText(this, "Complete Profile - Coming Soon!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Add logout option to menu
        menu.add(0, 1, 0, "Logout")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            1 -> {
                // Logout
                sessionManager.logout()
                navigateToLogin()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}