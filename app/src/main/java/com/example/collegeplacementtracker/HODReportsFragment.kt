package com.example.collegeplacementtracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.SessionManager
import kotlinx.coroutines.launch

class HODReportsFragment : Fragment() {

    private lateinit var pdfGenerator: PDFReportGenerator
    private lateinit var database: AppDatabaseNew
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hod_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pdfGenerator = PDFReportGenerator(requireContext())
        database = AppDatabaseNew.getDatabase(requireContext(), lifecycleScope)
        sessionManager = SessionManager.getInstance(requireContext())

        setupReportGenerators(view)
    }

    private fun setupReportGenerators(view: View) {
        // Department Report
        view.findViewById<Button>(R.id.generateDeptReportButton)?.setOnClickListener {
            generateDepartmentReport()
        }

        // Student List Report
        view.findViewById<Button>(R.id.generateStudentListButton)?.setOnClickListener {
            generateStudentListReport()
        }

        // Placed Students Report
        view.findViewById<Button>(R.id.generatePlacedStudentsButton)?.setOnClickListener {
            generatePlacedStudentsReport()
        }

        // Application Status Report
        view.findViewById<Button>(R.id.generateApplicationStatusButton)?.setOnClickListener {
            generateApplicationStatusReport()
        }
    }

    private fun generateDepartmentReport() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                val students = database.studentDao().getAllStudentsSync()
                val companies = database.companyDao().getAllCompaniesSync()
                val applications = database.applicationDao().getAllApplicationsSync()

                val totalStudents = students.size
                val placedCount = applications.count { it.status == ApplicationStatus.SELECTED }
                val eligibleStudents = students.size // All students are eligible for simplicity

                val packages = companies.map { it.packageAmount }
                val highestPackage = packages.maxOrNull() ?: 0.0
                val averagePackage = if (packages.isNotEmpty()) packages.average() else 0.0

                val departmentName = sessionManager.getUserBranch() ?: "Department"

                val filePath = pdfGenerator.generateDepartmentReport(
                    departmentName,
                    totalStudents,
                    eligibleStudents,
                    placedCount,
                    highestPackage,
                    averagePackage
                )

                view?.post {
                    showLoading(false)
                    if (filePath != null) {
                        Toast.makeText(
                            requireContext(),
                            "Department report generated successfully! 📊",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to generate report",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                view?.post {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun generateStudentListReport() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                val users = database.userDao().getUsersByRole(UserRole.STUDENT)
                // Observe LiveData and get the value
                users.observe(viewLifecycleOwner) { studentList ->
                    lifecycleScope.launch {
                        val filePath = pdfGenerator.generateStudentListReport(studentList)

                        view?.post {
                            showLoading(false)
                            if (filePath != null) {
                                Toast.makeText(
                                    requireContext(),
                                    "Student list report generated! 👥",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to generate report",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    users.removeObservers(viewLifecycleOwner)
                }
            } catch (e: Exception) {
                view?.post {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun generatePlacedStudentsReport() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                val applications = database.applicationDao().getAllApplicationsSync()
                val placedApplications = applications.filter {
                    it.status == ApplicationStatus.SELECTED
                }

                val filePath = pdfGenerator.generatePlacedStudentsReport(placedApplications)

                view?.post {
                    showLoading(false)
                    if (filePath != null) {
                        Toast.makeText(
                            requireContext(),
                            "Placed students report generated! ✓",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to generate report",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                view?.post {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun generateApplicationStatusReport() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                val applications = database.applicationDao().getAllApplicationsSync()
                val filePath = pdfGenerator.generateApplicationStatusReport(applications)

                view?.post {
                    showLoading(false)
                    if (filePath != null) {
                        Toast.makeText(
                            requireContext(),
                            "Application status report generated! 📝",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to generate report",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                view?.post {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        view?.findViewById<Button>(R.id.generateDeptReportButton)?.isEnabled = !show
        view?.findViewById<Button>(R.id.generateStudentListButton)?.isEnabled = !show
        view?.findViewById<Button>(R.id.generatePlacedStudentsButton)?.isEnabled = !show
        view?.findViewById<Button>(R.id.generateApplicationStatusButton)?.isEnabled = !show
    }
}
