package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HODDashboardTabsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao
    private lateinit var applicationDao: ApplicationDao
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var pdfGenerator: PDFReportGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hod_dashboard_tabs)

        sessionManager = SessionManager(this)
        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()
        applicationDao = database.applicationDao()
        pdfGenerator = PDFReportGenerator(this)

        setupUI()
        loadStatistics()
        setupBottomNavigation()
    }

    private fun setupUI() {
        // Set HOD name
        val hodName = sessionManager.getUserName()
        findViewById<TextView>(R.id.hodNameTextView)?.text = hodName ?: "HOD"

        // Set department
        findViewById<TextView>(R.id.departmentTextView)?.text = "Department: Computer Science"

        // Set profile initial
        val initial = hodName?.firstOrNull()?.toString() ?: "H"
        findViewById<TextView>(R.id.hodProfileInitialTextView)?.text = initial

        // Setup logout button
        findViewById<ImageView>(R.id.hodLogoutButton)?.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            try {
                // Get all users
                userDao.getAllUsers().observe(this@HODDashboardTabsActivity) { allUsers ->
                    // Filter by role = "STUDENT" (String comparison)
                    val students = allUsers.filter { it.role == "STUDENT" }
                    val totalStudents = students.size

                    // Count eligible students (CGPA >= 6.0)
                    val eligibleStudents = students.count { (it.cgpa ?: 0.0) >= 6.0 }

                    // Update UI
                    findViewById<TextView>(R.id.totalStudentsTextView)?.text =
                        totalStudents.toString()
                    findViewById<TextView>(R.id.eligibleStudentsTextView)?.text =
                        eligibleStudents.toString()

                    // For now, set placed to 0
                    findViewById<TextView>(R.id.placedStudentsTextView)?.text = "0"
                }

                // Get pending approvals
                applicationDao.getAllApplications().observe(this@HODDashboardTabsActivity) { apps ->
                    // Count applications where hodApproved is false
                    val pendingCount = apps.count { !it.hodApproved }
                    findViewById<TextView>(R.id.pendingApprovalsTextView)?.text =
                        pendingCount.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@HODDashboardTabsActivity,
                    "Error loading statistics", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNav = findViewById(R.id.hodBottomNavigation)
        bottomNav.selectedItemId = R.id.hod_nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.hod_nav_home -> true

                R.id.hod_nav_approvals -> {
                    startActivity(Intent(this, HODApprovalsActivity::class.java))
                    true
                }

                R.id.hod_nav_analytics -> {
                    startActivity(Intent(this, HODAnalysisActivity::class.java))
                    true
                }

                R.id.hod_nav_settings -> {
                    startActivity(Intent(this, HODSettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    fun navigateToReportsTab() {
        // Navigate to HOD Analysis Activity which contains reports
        startActivity(Intent(this, HODAnalysisActivity::class.java))
    }
}
