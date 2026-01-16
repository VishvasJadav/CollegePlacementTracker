package com.example.collegeplacementtracker

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class TPOApplicationManagementActivity : AppCompatActivity() {

    private lateinit var applicationDao: ApplicationDao
    private lateinit var companyDao: CompanyDao
    private lateinit var userDao: UserDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: ApplicationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tpo_application_management)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Manage Applications"

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        applicationDao = database.applicationDao()
        companyDao = database.companyDao()
        userDao = database.userDao()

        recyclerView = findViewById(R.id.applicationsRecyclerView)
        emptyView = findViewById(R.id.emptyView)

        setupRecyclerView()
        loadApplications()
    }

    private fun setupRecyclerView() {
        adapter = ApplicationAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener { application ->
            showApplicationManagementDialog(application)
        }
    }

    private fun loadApplications() {
        // Use the new query that returns ApplicationWithCompany
        applicationDao.getAllApplicationsWithCompany()
            .observe(this) { applications ->
                if (applications.isEmpty()) {
                    recyclerView.visibility = RecyclerView.GONE
                    emptyView.visibility = TextView.VISIBLE
                } else {
                    recyclerView.visibility = RecyclerView.VISIBLE
                    emptyView.visibility = TextView.GONE
                    adapter.submitList(applications)
                }
            }
    }

    private fun showApplicationManagementDialog(application: ApplicationWithCompany) {
        val app = application.application
        val company = application.company

        // Check if company exists
        if (company == null) {
            runOnUiThread {
                Toast.makeText(
                    this@TPOApplicationManagementActivity,
                    "Error loading details",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        lifecycleScope.launch {
            val student = userDao.getUserByIdSync(app.studentId)

            if (student == null) {
                runOnUiThread {
                    Toast.makeText(
                        this@TPOApplicationManagementActivity,
                        "Error loading details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            val message = """
                Student: ${student.fullName}
                Roll Number: ${student.rollNumber}
                Branch: ${student.branch}
                CGPA: ${student.cgpa}
                
                Company: ${company.companyName}
                Role: ${company.jobRole}
                Package: ${company.packageAmount} LPA
                
                Current Status: ${app.status}
                HOD Approved: ${if (app.hodApproved) "Yes" else "No"}
                TPO Approved: ${if (app.tpoApproved) "Yes" else "No"}
            """.trimIndent()

            runOnUiThread {
                AlertDialog.Builder(this@TPOApplicationManagementActivity)
                    .setTitle("Update Application Status")
                    .setMessage(message)
                    .setPositiveButton("Approve") { _, _ ->
                        approveApplication(app)
                    }
                    .setNeutralButton("Shortlist") { _, _ ->
                        updateStatus(app, ApplicationStatus.SHORTLISTED)
                    }
                    .setNegativeButton("Reject") { _, _ ->
                        updateStatus(app, ApplicationStatus.REJECTED)
                    }
                    .show()
            }
        }
    }

    private fun approveApplication(application: Application) {
        lifecycleScope.launch {
            try {
                applicationDao.updateTPOApproval(
                    application.id,
                    true,
                    System.currentTimeMillis()
                )

                runOnUiThread {
                    Toast.makeText(
                        this@TPOApplicationManagementActivity,
                        "Application approved",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@TPOApplicationManagementActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateStatus(application: Application, newStatus: String) {
        lifecycleScope.launch {
            try {
                applicationDao.updateApplicationStatus(
                    application.id,
                    newStatus,
                    System.currentTimeMillis()
                )

                runOnUiThread {
                    Toast.makeText(
                        this@TPOApplicationManagementActivity,
                        "Status updated to $newStatus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@TPOApplicationManagementActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
