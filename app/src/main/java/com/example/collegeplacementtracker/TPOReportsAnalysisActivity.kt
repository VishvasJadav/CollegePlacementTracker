package com.example.collegeplacementtracker

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.SessionManager
import kotlinx.coroutines.launch

class TPOReportsAnalysisActivity : AppCompatActivity() {

    private lateinit var database: AppDatabaseNew
    private lateinit var pdfGenerator: PDFReportGenerator
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tpo_reports_analysis)

        database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        pdfGenerator = PDFReportGenerator(this)
        sessionManager = SessionManager.getInstance(this)

        setupToolbar()
        loadStatistics()
        setupReportButtons()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            try {
                // Load students
                database.userDao().getAllUsers().observe(this@TPOReportsAnalysisActivity) { users ->
                    val students = users.filter { it.role == UserRole.STUDENT }
                    val totalStudents = students.size
                    findViewById<TextView>(R.id.totalStudentsTextView).text =
                        totalStudents.toString()

                    // Load applications and calculate stats
                    lifecycleScope.launch {
                        val applications = database.applicationDao().getAllApplicationsSync()
                        val companies = database.companyDao().getAllCompaniesSync()

                        // Calculate placement stats
                        val selectedApplications =
                            applications.filter { it.status == ApplicationStatus.SELECTED }
                        val placedStudents =
                            selectedApplications.map { it.studentId }.distinct().size
                        val placementRate = if (totalStudents > 0) {
                            ((placedStudents.toFloat() / totalStudents) * 100).toInt()
                        } else 0

                        // Package stats
                        val packages = companies.map { it.packageAmount }
                        val highestPackage = packages.maxOrNull() ?: 0.0
                        val averagePackage = if (packages.isNotEmpty()) packages.average() else 0.0
                        val lowestPackage = packages.minOrNull() ?: 0.0

                        // Application status counts
                        val pendingCount =
                            applications.count { it.status == ApplicationStatus.PENDING }
                        val selectedCount =
                            applications.count { it.status == ApplicationStatus.SELECTED }
                        val rejectedCount =
                            applications.count { it.status == ApplicationStatus.REJECTED }
                        val totalApplications = applications.size

                        runOnUiThread {
                            // Update overview stats
                            findViewById<TextView>(R.id.placedStudentsTextView).text =
                                placedStudents.toString()
                            findViewById<TextView>(R.id.totalCompaniesTextView).text =
                                companies.size.toString()
                            findViewById<TextView>(R.id.placementRateTextView).text =
                                "$placementRate%"

                            // Update package stats
                            findViewById<TextView>(R.id.highestPackageTextView).text =
                                "₹${String.format("%.1f", highestPackage)} LPA"
                            findViewById<TextView>(R.id.averagePackageTextView).text =
                                "₹${String.format("%.1f", averagePackage)} LPA"
                            findViewById<TextView>(R.id.lowestPackageTextView).text =
                                "₹${String.format("%.1f", lowestPackage)} LPA"
                            findViewById<TextView>(R.id.totalOffersTextView).text =
                                selectedCount.toString()

                            // Update application status
                            findViewById<TextView>(R.id.pendingCountTextView).text =
                                pendingCount.toString()
                            findViewById<TextView>(R.id.selectedCountTextView).text =
                                selectedCount.toString()
                            findViewById<TextView>(R.id.rejectedCountTextView).text =
                                rejectedCount.toString()

                            // Update progress bars
                            if (totalApplications > 0) {
                                findViewById<ProgressBar>(R.id.pendingProgressBar).progress =
                                    ((pendingCount.toFloat() / totalApplications) * 100).toInt()
                                findViewById<ProgressBar>(R.id.selectedProgressBar).progress =
                                    ((selectedCount.toFloat() / totalApplications) * 100).toInt()
                                findViewById<ProgressBar>(R.id.rejectedProgressBar).progress =
                                    ((rejectedCount.toFloat() / totalApplications) * 100).toInt()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@TPOReportsAnalysisActivity,
                    "Error loading statistics",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupReportButtons() {
        findViewById<Button>(R.id.generateOverallReportButton).setOnClickListener {
            generateOverallReport()
        }

        findViewById<Button>(R.id.generateStudentReportButton).setOnClickListener {
            generateStudentReport()
        }

        findViewById<Button>(R.id.generateCompanyReportButton).setOnClickListener {
            generateCompanyReport()
        }

        findViewById<Button>(R.id.generateApplicationReportButton).setOnClickListener {
            generateApplicationReport()
        }
    }

    private fun generateOverallReport() {
        lifecycleScope.launch {
            try {
                val students = database.studentDao().getAllStudentsSync()
                val companies = database.companyDao().getAllCompaniesSync()
                val applications = database.applicationDao().getAllApplicationsSync()

                val totalStudents = students.size
                val eligibleStudents = students.size
                val placedStudents = applications.count { it.status == ApplicationStatus.SELECTED }

                val packages = companies.map { it.packageAmount }
                val highestPackage = packages.maxOrNull() ?: 0.0
                val averagePackage = if (packages.isNotEmpty()) packages.average() else 0.0

                val filePath = pdfGenerator.generateDepartmentReport(
                    "All Departments",
                    totalStudents,
                    eligibleStudents,
                    placedStudents,
                    highestPackage,
                    averagePackage
                )

                runOnUiThread {
                    if (filePath != null) {
                        Toast.makeText(
                            this@TPOReportsAnalysisActivity,
                            "Overall report generated! 📊", Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@TPOReportsAnalysisActivity,
                            "Failed to generate report", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@TPOReportsAnalysisActivity,
                        "Error: ${e.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun generateStudentReport() {
        database.userDao().getUsersByRole(UserRole.STUDENT).observe(this) { students ->
            lifecycleScope.launch {
                try {
                    val filePath = pdfGenerator.generateStudentListReport(students)
                    runOnUiThread {
                        if (filePath != null) {
                            Toast.makeText(
                                this@TPOReportsAnalysisActivity,
                                "Student report generated! 👥", Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@TPOReportsAnalysisActivity,
                                "Failed to generate report", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun generateCompanyReport() {
        Toast.makeText(this, "Company report generation coming soon!", Toast.LENGTH_SHORT).show()
    }

    private fun generateApplicationReport() {
        lifecycleScope.launch {
            try {
                val applications = database.applicationDao().getAllApplicationsSync()
                val filePath = pdfGenerator.generateApplicationStatusReport(applications)

                runOnUiThread {
                    if (filePath != null) {
                        Toast.makeText(
                            this@TPOReportsAnalysisActivity,
                            "Application report generated! 📋", Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@TPOReportsAnalysisActivity,
                            "Failed to generate report", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@TPOReportsAnalysisActivity,
                        "Error: ${e.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
