package com.example.collegeplacementtracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.SessionManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddCompanyActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var companyDao: CompanyDao

    private lateinit var companyNameEditText: TextInputEditText
    private lateinit var jobRoleEditText: TextInputEditText
    private lateinit var jobDescriptionEditText: TextInputEditText
    private lateinit var packageEditText: TextInputEditText
    private lateinit var locationEditText: TextInputEditText
    private lateinit var eligibleBranchesEditText: TextInputEditText
    private lateinit var minimumCGPAEditText: TextInputEditText
    private lateinit var backlogsEditText: TextInputEditText
    private lateinit var selectionProcessEditText: TextInputEditText
    private lateinit var numberOfRoundsEditText: TextInputEditText
    private lateinit var deadlineEditText: TextInputEditText
    private lateinit var totalPositionsEditText: TextInputEditText
    private lateinit var companyTypeSpinner: Spinner
    private lateinit var postButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_company)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Post New Company"

        sessionManager = SessionManager.getInstance(this)
        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        companyDao = database.companyDao()

        initViews()
        setupCompanyTypeSpinner()

        postButton.setOnClickListener {
            postCompany()
        }
    }

    private fun initViews() {
        companyNameEditText = findViewById(R.id.companyNameEditText)
        jobRoleEditText = findViewById(R.id.jobRoleEditText)
        jobDescriptionEditText = findViewById(R.id.jobDescriptionEditText)
        packageEditText = findViewById(R.id.packageEditText)
        locationEditText = findViewById(R.id.locationEditText)
        eligibleBranchesEditText = findViewById(R.id.eligibleBranchesEditText)
        minimumCGPAEditText = findViewById(R.id.minimumCGPAEditText)
        backlogsEditText = findViewById(R.id.backlogsEditText)
        selectionProcessEditText = findViewById(R.id.selectionProcessEditText)
        numberOfRoundsEditText = findViewById(R.id.numberOfRoundsEditText)
        deadlineEditText = findViewById(R.id.deadlineEditText)
        totalPositionsEditText = findViewById(R.id.totalPositionsEditText)
        companyTypeSpinner = findViewById(R.id.companyTypeSpinner)
        postButton = findViewById(R.id.postButton)
    }

    private fun setupCompanyTypeSpinner() {
        val types = arrayOf("Product", "Service", "Startup")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        companyTypeSpinner.adapter = adapter
    }

    private fun postCompany() {
        if (!validateInput()) return

        postButton.isEnabled = false
        postButton.text = "Posting..."

        lifecycleScope.launch {
            try {
                val company = Company(
                    companyName = companyNameEditText.text.toString().trim(),
                    jobRole = jobRoleEditText.text.toString().trim(),
                    jobDescription = jobDescriptionEditText.text.toString().trim(),
                    packageAmount = packageEditText.text.toString().toDouble(),
                    location = locationEditText.text.toString().trim(),
                    eligibleBranches = eligibleBranchesEditText.text.toString().trim(),
                    minimumCGPA = minimumCGPAEditText.text.toString().toDouble(),
                    backlogs = backlogsEditText.text.toString().toIntOrNull() ?: 0,
                    selectionProcess = selectionProcessEditText.text.toString().trim(),
                    numberOfRounds = numberOfRoundsEditText.text.toString().toInt(),
                    applicationDeadline = deadlineEditText.text.toString().trim(),
                    totalPositions = totalPositionsEditText.text.toString().toInt(),
                    companyType = companyTypeSpinner.selectedItem.toString(),
                    postedBy = sessionManager.getUserId().toInt()
                )

                companyDao.insert(company)

                Toast.makeText(
                    this@AddCompanyActivity,
                    "Company posted successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@AddCompanyActivity,
                    "Failed to post company: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

                postButton.isEnabled = true
                postButton.text = "Post Company"
            }
        }
    }

    private fun validateInput(): Boolean {
        val companyName = companyNameEditText.text.toString().trim()
        val jobRole = jobRoleEditText.text.toString().trim()
        val packageText = packageEditText.text.toString().trim()
        val cgpaText = minimumCGPAEditText.text.toString().trim()
        val roundsText = numberOfRoundsEditText.text.toString().trim()
        val positionsText = totalPositionsEditText.text.toString().trim()
        val deadline = deadlineEditText.text.toString().trim()

        if (companyName.isEmpty()) {
            companyNameEditText.error = "Company name is required"
            return false
        }

        if (jobRole.isEmpty()) {
            jobRoleEditText.error = "Job role is required"
            return false
        }

        if (packageText.isEmpty()) {
            packageEditText.error = "Package is required"
            return false
        }

        val packageAmount = packageText.toDoubleOrNull()
        if (packageAmount == null || packageAmount <= 0) {
            packageEditText.error = "Invalid package amount"
            return false
        }

        if (cgpaText.isEmpty()) {
            minimumCGPAEditText.error = "Minimum CGPA is required"
            return false
        }

        val cgpa = cgpaText.toDoubleOrNull()
        if (cgpa == null || cgpa < 0 || cgpa > 10) {
            minimumCGPAEditText.error = "CGPA must be between 0 and 10"
            return false
        }

        if (roundsText.isEmpty()) {
            numberOfRoundsEditText.error = "Number of rounds is required"
            return false
        }

        if (positionsText.isEmpty()) {
            totalPositionsEditText.error = "Total positions is required"
            return false
        }

        if (deadline.isEmpty()) {
            deadlineEditText.error = "Application deadline is required"
            return false
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}