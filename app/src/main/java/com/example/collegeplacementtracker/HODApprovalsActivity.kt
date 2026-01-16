package com.example.collegeplacementtracker

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegeplacementtracker.utils.SessionManager
import kotlinx.coroutines.launch

class HODApprovalsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var applicationDao: ApplicationDao
    private lateinit var userDao: UserDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: ApplicationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hod_approvals)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Pending Approvals"

        sessionManager = SessionManager.getInstance(this)

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        applicationDao = database.applicationDao()
        userDao = database.userDao()

        recyclerView = findViewById(R.id.approvalsRecyclerView)
        emptyView = findViewById(R.id.emptyView)

        setupRecyclerView()
        loadPendingApprovals()
    }

    private fun setupRecyclerView() {
        adapter = ApplicationAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener { application ->
            showApprovalDialog(application)
        }
    }

    private fun loadPendingApprovals() {
        val branch = sessionManager.getUserBranch() ?: return

        // Use the new query that returns ApplicationWithCompany
        applicationDao.getPendingApplicationsWithCompanyByBranch(branch)
            .observe(this) { applications ->
                if (applications.isEmpty()) {
                    recyclerView.visibility = RecyclerView.GONE
                    emptyView.visibility = TextView.VISIBLE
                    emptyView.text = "No pending approvals"
                } else {
                    recyclerView.visibility = RecyclerView.VISIBLE
                    emptyView.visibility = TextView.GONE
                    adapter.submitList(applications)
                }
            }
    }

    private fun showApprovalDialog(application: ApplicationWithCompany) {
        val app = application.application
        val company = application.company

        // Check if company exists
        if (company == null) {
            Toast.makeText(
                this@HODApprovalsActivity,
                "Error loading company details",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        lifecycleScope.launch {
            userDao.getUserById(app.studentId)
                .observe(this@HODApprovalsActivity) { student ->
                    if (student == null) {
                        Toast.makeText(
                            this@HODApprovalsActivity,
                            "Error loading student details", Toast.LENGTH_SHORT
                        ).show()
                        return@observe
                    }

                    val message = """
                Student: ${student.fullName}
                Roll Number: ${student.rollNumber}
                CGPA: ${student.cgpa}
                
                Company: ${company.companyName}
                Role: ${company.jobRole}
                Package: ${company.packageAmount} LPA
                
                Eligibility:
                Min CGPA: ${company.minimumCGPA}
                Eligible Branches: ${company.eligibleBranches}
                
                Student is ${
                        if (isEligible(
                                student,
                                company
                            )
                        ) "ELIGIBLE ✓" else "NOT ELIGIBLE ✗"
                    }
            """.trimIndent()

                    runOnUiThread {
                        AlertDialog.Builder(this@HODApprovalsActivity)
                            .setTitle("Approve Application?")
                            .setMessage(message)
                            .setPositiveButton("Approve") { _, _ ->
                                approveApplication(app)
                            }
                            .setNegativeButton("Reject") { _, _ ->
                                rejectApplication(app)
                            }
                            .setNeutralButton("Cancel", null)
                            .show()
                    }
                }
        }
    }

    private fun isEligible(student: User, company: Company): Boolean {
        val cgpaEligible = (student.cgpa ?: 0.0) >= company.minimumCGPA
        val branchEligible = company.eligibleBranches.contains(
            student.branch ?: "",
            ignoreCase = true
        )
        return cgpaEligible && branchEligible
    }

    private fun approveApplication(application: Application) {
        lifecycleScope.launch {
            try {
                applicationDao.updateHODApproval(
                    application.id,
                    true,
                    System.currentTimeMillis()
                )

                Toast.makeText(
                    this@HODApprovalsActivity,
                    "Application approved",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@HODApprovalsActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun rejectApplication(application: Application) {
        lifecycleScope.launch {
            try {
                applicationDao.updateApplicationStatus(
                    application.id,
                    ApplicationStatus.REJECTED,
                    System.currentTimeMillis()
                )

                Toast.makeText(
                    this@HODApprovalsActivity,
                    "Application rejected",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@HODApprovalsActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
