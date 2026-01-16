package com.example.collegeplacementtracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyApplicationsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var applicationDao: ApplicationDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: ApplicationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_applications)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "My Applications"

        sessionManager = SessionManager(this)

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        applicationDao = database.applicationDao()

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
            showApplicationDetails(application)
        }
    }

    private fun loadApplications() {
        val userId = sessionManager.getUserId()

        // Use the new query that returns ApplicationWithCompany
        applicationDao.getApplicationsWithCompanyByStudent(userId)
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

    private fun showApplicationDetails(application: ApplicationWithCompany) {
        val company = application.company // Get the company from the ApplicationWithCompany object

        // Check if company exists
        if (company == null) {
            AlertDialog.Builder(this@MyApplicationsActivity)
                .setTitle("Error")
                .setMessage("Company details not found")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val statusColor = when (application.application.status) {
            ApplicationStatus.PENDING -> "⏳ Pending"
            ApplicationStatus.SHORTLISTED -> "🎉 Shortlisted"
            ApplicationStatus.SELECTED -> "🎊 Selected"
            ApplicationStatus.REJECTED -> "❌ Rejected"
            else -> application.application.status
        }

        val message = """
            Company: ${company.companyName}
            Role: ${company.jobRole}
            Package: ${company.packageAmount} LPA
            Location: ${company.location}
            
            Application Status: $statusColor
            Applied On: ${
            java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                .format(application.application.appliedAt)
        }
            
            HOD Approval: ${if (application.application.hodApproved) "✓ Approved" else "⏳ Pending"}
            TPO Approval: ${if (application.application.tpoApproved) "✓ Approved" else "⏳ Pending"}
            
            ${
            if (application.application.status == ApplicationStatus.SELECTED) {
                """
                🎉 Congratulations! 🎉
                Offered Package: ${application.application.offeredPackage ?: company.packageAmount} LPA
                Joining Date: ${application.application.joiningDate ?: "TBD"}
                """.trimIndent()
            } else ""
        }
        """.trimIndent()

        AlertDialog.Builder(this@MyApplicationsActivity)
            .setTitle("Application Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
