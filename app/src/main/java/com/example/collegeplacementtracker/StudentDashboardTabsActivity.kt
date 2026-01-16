package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class StudentDashboardTabsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao
    private lateinit var applicationDao: ApplicationDao
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard_tabs)

        sessionManager = SessionManager.getInstance(this)
        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()
        applicationDao = database.applicationDao()

        setupUI()
        loadUserData()
        setupBottomNavigation()
    }

    private fun setupUI() {
        // Set user name
        val userName = sessionManager.getUserName()
        findViewById<TextView>(R.id.userNameTextView).text = userName

        // Set profile initial
        val initial = userName?.firstOrNull()?.toString() ?: "S"
        findViewById<TextView>(R.id.profileInitialTextView).text = initial

        //  Setup logout button
        findViewById<View>(R.id.logoutButton).setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Setup quick action cards
        findViewById<View>(R.id.browseCompaniesCard).setOnClickListener {
            startActivity(Intent(this, CompanyListActivity::class.java))
        }

        findViewById<View>(R.id.editProfileCard).setOnClickListener {
            startActivity(Intent(this, StudentProfileActivity::class.java))
        }

        // Setup professional insights cards
        findViewById<View>(R.id.skillsGapCard).setOnClickListener {
            // Navigate to skills gap analysis
            Toast.makeText(this, "Skills Gap Analysis coming soon!", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.interviewPrepCard).setOnClickListener {
            // Navigate to interview preparation
            startActivity(Intent(this, StudentResourcesActivity::class.java))
        }
    }

    private fun loadUserData() {
        val userId = sessionManager.getUserId()

        // Load user details
        userDao.getUserById(userId).observe(this) { user ->
            user?.let {
                findViewById<TextView>(R.id.cgpaValueTextView).text =
                    user.cgpa?.toString() ?: "N/A"
            }
        }

        // Load applications count
        applicationDao.getApplicationsByStudent(userId).observe(this) { applications ->
            findViewById<TextView>(R.id.applicationsCountTextView).text =
                applications.size.toString()

            val selectedCount = applications.count {
                it.status == ApplicationStatus.SELECTED
            }
            findViewById<TextView>(R.id.offersCountTextView).text =
                selectedCount.toString()
        }
    }

    private fun setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home
                    true
                }

                R.id.nav_companies -> {
                    startActivity(Intent(this, CompanyListActivity::class.java))
                    true
                }

                R.id.nav_applications -> {
                    startActivity(Intent(this, MyApplicationsActivity::class.java))
                    true
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, StudentProfileActivity::class.java))
                    true
                }

                R.id.nav_resources -> {
                    startActivity(Intent(this, StudentResourcesActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}