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
import kotlinx.coroutines.launch

class StudentDashboardActivityEnhanced : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao
    private lateinit var companyDao: CompanyDao
    private lateinit var applicationDao: ApplicationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard_enhanced)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
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

        // Setup Bottom Navigation
        setupBottomNavigation()

        findViewById<CardView>(R.id.profileSummaryCard).setOnClickListener {
            startActivity(Intent(this, StudentProfileActivity::class.java))
        }

        findViewById<CardView>(R.id.eligibleCompaniesCard).setOnClickListener {
            val intent = Intent(this, CompanyListActivity::class.java)
            intent.putExtra("SHOW_ELIGIBLE_ONLY", true)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.appliedCompaniesCard).setOnClickListener {
            startActivity(Intent(this, MyApplicationsActivity::class.java))
        }

        findViewById<TextView>(R.id.viewAllCompaniesButton).setOnClickListener {
            startActivity(Intent(this, CompanyListActivity::class.java))
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
        val userId = sessionManager.getUserId()

        userDao.getUserById(userId).observe(this) { user ->
            user?.let {
                findViewById<TextView>(R.id.profileNameTextView).text = user.fullName
                findViewById<TextView>(R.id.profileBranchTextView).text = user.branch ?: "N/A"
                findViewById<TextView>(R.id.profileCgpaTextView).text =
                    "CGPA: ${user.cgpa?.toString() ?: "N/A"}"

                val placementStatus = findViewById<TextView>(R.id.profilePlacementStatusTextView)
                placementStatus.text = "Not Placed"
                placementStatus.setTextColor(getColor(android.R.color.holo_orange_dark))

                findViewById<TextView>(R.id.profileResumeStatusTextView).text =
                    "Resume: Not Uploaded"

                loadEligibleCompanies(user.cgpa ?: 0.0, user.branch ?: "")
            }
        }

        applicationDao.getApplicationsByStudent(userId).observe(this) { applications ->
            findViewById<TextView>(R.id.appliedCountTextView).text =
                applications.size.toString()

            val pending = applications.count { it.status == ApplicationStatus.PENDING }
            val shortlisted = applications.count { it.status == ApplicationStatus.SHORTLISTED }
            val selected = applications.count { it.status == ApplicationStatus.SELECTED }

            findViewById<TextView>(R.id.appliedStatusTextView).text =
                "Applied: $pending | Shortlisted: $shortlisted | Selected: $selected"

            if (selected > 0) {
                val placementStatus = findViewById<TextView>(R.id.profilePlacementStatusTextView)
                placementStatus.text = "✓ Placed"
                placementStatus.setTextColor(getColor(android.R.color.holo_green_dark))

                applications.find { it.status == ApplicationStatus.SELECTED }?.let { app ->
                    displayPlacementOffer(app)
                }
            }
        }
    }

    private fun loadEligibleCompanies(cgpa: Double, branch: String) {
        lifecycleScope.launch {
            companyDao.getEligibleCompanies(cgpa)
                .observe(this@StudentDashboardActivityEnhanced) { companies ->
                    val eligibleForBranch = companies.filter { company ->
                        company.eligibleBranches.contains(branch, ignoreCase = true)
                    }

                    findViewById<TextView>(R.id.eligibleCountTextView).text =
                        eligibleForBranch.size.toString()
                    findViewById<TextView>(R.id.eligibleDescriptionTextView).text =
                        "Companies you can apply to"
                }
        }
    }

    private fun displayPlacementOffer(application: Application) {
        lifecycleScope.launch {
            companyDao.getCompanyById(application.companyId)
                .observe(this@StudentDashboardActivityEnhanced) { company ->
                    company?.let {
                        findViewById<CardView>(R.id.placementOfferCard).visibility =
                            CardView.VISIBLE
                        findViewById<TextView>(R.id.offerCompanyTextView).text = company.companyName
                        findViewById<TextView>(R.id.offerRoleTextView).text =
                            "Role: ${company.jobRole}"
                        findViewById<TextView>(R.id.offerPackageTextView).text =
                            "Package: ${application.offeredPackage ?: company.packageAmount} LPA"
                        findViewById<TextView>(R.id.offerStatusTextView).text =
                            "Status: Selected ✓"
                    }
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.student_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, StudentProfileActivity::class.java))
                true
            }

            R.id.action_refresh -> {
                loadDashboardData()
                UIHelper.showSuccess(this, "Refreshed")
                true
            }

            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
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

                else -> false
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
