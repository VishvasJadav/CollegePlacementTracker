package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.SessionManager
import com.example.collegeplacementtracker.utils.UIHelper
import kotlinx.coroutines.launch

class HODDashboardActivityEnhanced : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao
    private lateinit var companyDao: CompanyDao
    private lateinit var applicationDao: ApplicationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hod_dashboard_enhanced)

        sessionManager = SessionManager.getInstance(this)

        if (!sessionManager.isLoggedIn() || !sessionManager.isHOD()) {
            navigateToLogin()
            return
        }

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()
        companyDao = database.companyDao()
        applicationDao = database.applicationDao()

        setupUI()
        loadDashboardData()
    }

    private fun setupUI() {
        // Set welcome message
        findViewById<TextView>(R.id.welcomeTextView).text =
            "Welcome, ${sessionManager.getUserName()}!"
        findViewById<TextView>(R.id.departmentTextView).text =
            "Department: ${sessionManager.getUserBranch()}"

        // Setup Logout Button (Visible Button at Top)
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            showLogoutConfirmation()
        }

        // Setup card click listeners
        findViewById<CardView>(R.id.departmentOverviewCard).setOnClickListener {
            startActivity(Intent(this, DepartmentStudentsActivity::class.java))
        }

        findViewById<CardView>(R.id.placementStatsCard).setOnClickListener {
            UIHelper.showInfo(this, "Detailed Statistics - Coming Soon")
        }

        findViewById<CardView>(R.id.companyVisitsCard).setOnClickListener {
            UIHelper.showInfo(this, "Company Visits - Coming Soon")
        }

        findViewById<CardView>(R.id.studentStatusCard).setOnClickListener {
            startActivity(Intent(this, DepartmentStudentsActivity::class.java))
        }

        findViewById<CardView>(R.id.pendingApprovalsCard).setOnClickListener {
            startActivity(Intent(this, HODApprovalsActivity::class.java))
        }

        findViewById<CardView>(R.id.reportsCard).setOnClickListener {
            UIHelper.showInfo(this, "Reports & Analytics - Coming Soon")
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun performLogout() {
        sessionManager.logout()
        UIHelper.showSuccess(this, "Logged out successfully")
        navigateToLogin()
    }

    private fun loadDashboardData() {
        val branch = sessionManager.getUserBranch() ?: return

        userDao.getStudentsByBranch(branch).observe(this) { students ->
            val totalStudents = students.size
            val eligibleStudents = students.count { (it.cgpa ?: 0.0) >= 6.0 }

            findViewById<TextView>(R.id.totalStudentsTextView).text =
                totalStudents.toString()
            findViewById<TextView>(R.id.eligibleStudentsTextView).text =
                eligibleStudents.toString()

            loadPlacedStudents(students)
        }

        applicationDao.getPendingApplicationsByBranch(branch).observe(this) { applications ->
            findViewById<TextView>(R.id.pendingApprovalsCountTextView).text =
                applications.size.toString()
        }

        loadCompanyStatistics(branch)
    }

    private fun loadPlacedStudents(students: List<User>) {
        lifecycleScope.launch {
            var placedCount = 0
            var totalPackage = 0.0
            var highestPackage = 0.0

            for (student in students) {
                applicationDao.getApplicationsByStudent(student.id.toLong())
                    .observe(this@HODDashboardActivityEnhanced) { applications ->
                        val selectedApps = applications.filter {
                            it.status == ApplicationStatus.SELECTED
                        }

                        if (selectedApps.isNotEmpty()) {
                            placedCount++

                            selectedApps.forEach { app ->
                                val pkg = app.offeredPackage ?: 0.0
                                totalPackage += pkg
                                if (pkg > highestPackage) {
                                    highestPackage = pkg
                                }
                            }
                        }
                    }
            }

            findViewById<TextView>(R.id.placedStudentsTextView).text =
                placedCount.toString()

            val placementPercentage = if (students.isNotEmpty()) {
                (placedCount * 100.0 / students.size)
            } else 0.0

            findViewById<TextView>(R.id.placementPercentageTextView).text =
                "${"%.1f".format(placementPercentage)}%"

            findViewById<TextView>(R.id.highestPackageTextView).text =
                "${"%.2f".format(highestPackage)} LPA"

            val averagePackage = if (placedCount > 0) {
                totalPackage / placedCount
            } else 0.0

            findViewById<TextView>(R.id.averagePackageTextView).text =
                "${"%.2f".format(averagePackage)} LPA"
        }
    }

    private fun loadCompanyStatistics(branch: String) {
        companyDao.getAllActiveCompanies().observe(this) { companies ->
            val departmentCompanies = companies.filter {
                it.eligibleBranches.contains(branch, ignoreCase = true)
            }

            findViewById<TextView>(R.id.companiesCountTextView).text =
                departmentCompanies.size.toString()

            val rolesOffered = departmentCompanies.map { it.jobRole }.distinct()
            findViewById<TextView>(R.id.rolesOfferedTextView).text =
                rolesOffered.take(3).joinToString(", ")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.hod_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                UIHelper.showInfo(this, "Profile - Coming Soon")
                true
            }

            R.id.action_refresh -> {
                loadDashboardData()
                UIHelper.showSuccess(this, "Data refreshed")
                true
            }

            R.id.action_logout -> {
                showLogoutConfirmation()
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
