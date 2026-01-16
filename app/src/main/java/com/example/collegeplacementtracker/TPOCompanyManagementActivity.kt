package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class TPOCompanyManagementActivity : AppCompatActivity() {

    private lateinit var companyDao: CompanyDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: CompanyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tpo_company_management)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Manage Companies"

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        companyDao = database.companyDao()

        recyclerView = findViewById(R.id.companiesRecyclerView)
        emptyView = findViewById(R.id.emptyView)

        setupRecyclerView()
        loadCompanies()

        findViewById<FloatingActionButton>(R.id.addCompanyFab).setOnClickListener {
            startActivity(Intent(this, AddCompanyActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = CompanyAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener { company ->
            showCompanyOptions(company)
        }
    }

    private fun loadCompanies() {
        companyDao.getAllCompanies().observe(this) { companies ->
            if (companies.isEmpty()) {
                recyclerView.visibility = RecyclerView.GONE
                emptyView.visibility = TextView.VISIBLE
            } else {
                recyclerView.visibility = RecyclerView.VISIBLE
                emptyView.visibility = TextView.GONE
                adapter.submitList(companies)
            }
        }
    }

    private fun showCompanyOptions(company: Company) {
        AlertDialog.Builder(this)
            .setTitle(company.companyName)
            .setItems(arrayOf("View Details", "Edit", "Delete", "Toggle Status")) { _, which ->
                when (which) {
                    0 -> showCompanyDetails(company)
                    1 -> Toast.makeText(this, "Edit - Coming Soon", Toast.LENGTH_SHORT).show()
                    2 -> deleteCompany(company)
                    3 -> toggleCompanyStatus(company)
                }
            }
            .show()
    }

    private fun showCompanyDetails(company: Company) {
        val message = """
            Role: ${company.jobRole}
            Package: ${company.packageAmount} LPA
            Location: ${company.location}
            Type: ${company.companyType}
            
            Eligibility:
            Branches: ${company.eligibleBranches}
            Min CGPA: ${company.minimumCGPA}
            Max Backlogs: ${company.backlogs}
            
            Positions: ${company.filledPositions}/${company.totalPositions}
            Status: ${if (company.isActive) "Active" else "Inactive"}
            
            Deadline: ${company.applicationDeadline}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle(company.companyName)
            .setMessage(message)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun deleteCompany(company: Company) {
        AlertDialog.Builder(this)
            .setTitle("Delete Company?")
            .setMessage("Are you sure you want to delete ${company.companyName}?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    companyDao.delete(company)
                    Toast.makeText(
                        this@TPOCompanyManagementActivity,
                        "Company deleted", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toggleCompanyStatus(company: Company) {
        lifecycleScope.launch {
            companyDao.updateCompanyStatus(company.id, !company.isActive)
            val status = if (!company.isActive) "activated" else "deactivated"
            Toast.makeText(
                this@TPOCompanyManagementActivity,
                "Company $status", Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}