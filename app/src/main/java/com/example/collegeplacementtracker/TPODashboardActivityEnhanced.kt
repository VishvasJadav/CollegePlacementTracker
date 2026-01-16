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
import com.example.collegeplacementtracker.utils.UIHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class TPODashboardActivityEnhanced : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao
    private lateinit var companyDao: CompanyDao
    private lateinit var applicationDao: ApplicationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tpo_dashboard_enhanced)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn() || !sessionManager.isTPO()) {
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
        findViewById<TextView>(R.id.welcomeTextView).text =
            "Welcome, ${sessionManager.getUserName()}!"

        // Setup Logout Button
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            showLogoutConfirmation()
        }

        findViewById<CardView>(R.id.overallSummaryCard).setOnClickListener {
            UIHelper.showInfo(this, "Overall Summary")
        }

        findViewById<CardView>(R.id.companyManagementCard).setOnClickListener {
            startActivity(Intent(this, TPOCompanyManagementActivity::class.java))
        }

        findViewById<CardView>(R.id.studentManagementCard).setOnClickListener {
            startActivity(Intent(this, TPOStudentManagementActivity::class.java))
        }

        findViewById<CardView>(R.id.applicationManagementCard).setOnClickListener {
            startActivity(Intent(this, TPOApplicationManagementActivity::class.java))
        }

        findViewById<CardView>(R.id.interviewManagementCard).setOnClickListener {
            startActivity(Intent(this, TPOInterviewManagementActivity::class.java))
        }

        findViewById<CardView>(R.id.reportsCard).setOnClickListener {
            startActivity(Intent(this, TPOReportsAnalysisActivity::class.java))
        }

        findViewById<FloatingActionButton>(R.id.addCompanyFab).setOnClickListener {
            startActivity(Intent(this, AddCompanyActivity::class.java))
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
        loadOverallStatistics()
        loadCompanyStatistics()
        loadApplicationStatistics()
    }

    private fun loadOverallStatistics() {
        userDao.getUserCountByRole(UserRole.STUDENT).observe(this) { count ->
            findViewById<TextView>(R.id.totalStudentsTextView).text = count.toString()
        }

        lifecycleScope.launch {
            var placedCount = 0
            var totalPackage = 0.0
            var highestPackage = 0.0

            userDao.getUsersByRole(UserRole.STUDENT)
                .observe(this@TPODashboardActivityEnhanced) { students ->
                    for (student in students) {
                        applicationDao.getApplicationsByStudent(student.id)
                            .observe(this@TPODashboardActivityEnhanced) { applications ->
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
    }

    private fun loadCompanyStatistics() {
        companyDao.getActiveCompanyCount().observe(this) { count ->
            findViewById<TextView>(R.id.activeCompaniesTextView).text = count.toString()
        }

        companyDao.getAllCompanies().observe(this) { companies ->
            findViewById<TextView>(R.id.totalCompaniesTextView).text = companies.size.toString()
        }
    }

    private fun loadApplicationStatistics() {
        applicationDao.getPendingTPOApprovalsCount().observe(this) { count ->
            findViewById<TextView>(R.id.pendingApprovalsTextView).text = count.toString()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tpo_menu, menu)
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

            R.id.action_settings -> {
                startActivity(Intent(this, TPOSettingsActivity::class.java))
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
