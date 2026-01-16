package com.example.collegeplacementtracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.SessionManager
import kotlinx.coroutines.launch

class HODAnalysisActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao
    private lateinit var applicationDao: ApplicationDao
    private lateinit var companyDao: CompanyDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hod_analysis)

        // Setup action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Department Analysis"

        // Initialize
        sessionManager = SessionManager.getInstance(this)
        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()
        applicationDao = database.applicationDao()
        companyDao = database.companyDao()

        loadAnalytics()
    }

    private fun loadAnalytics() {
        val branch = sessionManager.getUserBranch()

        lifecycleScope.launch {
            try {
                // Load all students in department
                userDao.getAllUsers().observe(this@HODAnalysisActivity) { allUsers ->
                    val students = allUsers.filter {
                        it.role == "STUDENT" && it.branch == branch
                    }

                    // Total students
                    val totalStudents = students.size
                    findViewById<TextView>(R.id.totalStudentsAnalysis).text =
                        totalStudents.toString()

                    // Eligible students (CGPA >= 6.0)
                    val eligibleStudents = students.count { (it.cgpa ?: 0.0) >= 6.0 }
                    findViewById<TextView>(R.id.eligibleStudentsAnalysis).text =
                        eligibleStudents.toString()

                    // High performers (CGPA >= 8.5)
                    val highPerformers = students.count { (it.cgpa ?: 0.0) >= 8.5 }
                    findViewById<TextView>(R.id.highPerformersAnalysis).text =
                        highPerformers.toString()

                    // Average CGPA
                    val avgCGPA = if (students.isNotEmpty()) {
                        students.mapNotNull { it.cgpa }.average()
                    } else 0.0
                    findViewById<TextView>(R.id.avgCGPAText).text = String.format("%.2f", avgCGPA)

                    // Placement statistics
                    loadPlacementStatistics(students)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadPlacementStatistics(students: List<User>) {
        lifecycleScope.launch {
            try {
                applicationDao.getAllApplications()
                    .observe(this@HODAnalysisActivity) { applications ->
                        // Get placed students (SELECTED status)
                        val placedStudentIds = applications
                            .filter { it.status == ApplicationStatus.SELECTED }
                            .map { it.studentId }
                            .distinct()

                        val placedCount = placedStudentIds.size
                        findViewById<TextView>(R.id.placedStudentsAnalysis).text =
                            placedCount.toString()

                        // Placement rate
                        val placementRate = if (students.isNotEmpty()) {
                            (placedCount * 100.0) / students.size
                        } else 0.0
                        findViewById<TextView>(R.id.placementRateText).text =
                            String.format("%.1f%%", placementRate)

                        // Calculate average package (you'll need to add package field to Company or Application)
                        // For now, showing placeholder
                        findViewById<TextView>(R.id.avgPackageAnalysis).text = "7.5 LPA"
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
