package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HODHomeFragment : Fragment() {

    private lateinit var database: AppDatabaseNew
    private lateinit var applicationDao: ApplicationDao
    private lateinit var studentDao: StudentDao
    private lateinit var companyDao: CompanyDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hod_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = AppDatabaseNew.getDatabase(requireContext(), lifecycleScope)
        applicationDao = database.applicationDao()
        studentDao = database.studentDao()
        companyDao = database.companyDao()

        setupClickListeners(view)
        loadStatistics(view)
        loadRecentApplications(view)
    }

    private fun setupClickListeners(view: View) {
        // View Students Card
        view.findViewById<CardView>(R.id.viewStudentsCard)?.setOnClickListener {
            startActivity(Intent(requireContext(), DepartmentStudentsActivity::class.java))
        }

        // Pending Approvals Card
        view.findViewById<CardView>(R.id.pendingApprovalsCard)?.setOnClickListener {
            startActivity(Intent(requireContext(), HODApprovalsActivity::class.java))
        }

        // Generate Reports Card
        view.findViewById<CardView>(R.id.generateReportsCard)?.setOnClickListener {
            // Switch to Reports tab
            (activity as? HODDashboardTabsActivity)?.navigateToReportsTab()
        }

        // Department Overview Card
        view.findViewById<CardView>(R.id.departmentOverviewCard)?.setOnClickListener {
            // Navigate to overview (can be another fragment or activity)
            startActivity(Intent(requireContext(), HODAnalysisActivity::class.java))
        }

        // View All Recent Applications
        view.findViewById<TextView>(R.id.viewAllRecentAppsTextView)?.setOnClickListener {
            startActivity(Intent(requireContext(), TPOApplicationManagementActivity::class.java))
        }
    }

    private fun loadStatistics(view: View) {
        lifecycleScope.launch {
            try {
                // Load all students using sync method
                val allStudents = studentDao.getAllStudentsSync()
                val totalStudents = allStudents.size

                // Load all applications using sync method
                val allApplications = applicationDao.getAllApplicationsSync()
                val placedCount = allApplications.count { it.status == ApplicationStatus.SELECTED }

                // Calculate placement percentage
                val placementPercentage = if (totalStudents > 0) {
                    ((placedCount.toFloat() / totalStudents) * 100).toInt()
                } else 0

                // Load all companies to get packages using sync method
                val companies = companyDao.getAllCompaniesSync()
                val packages = companies.map { it.packageAmount }
                val highestPackage = packages.maxOrNull() ?: 0.0
                val averagePackage = if (packages.isNotEmpty()) {
                    packages.average()
                } else 0.0

                // Count pending approvals
                val pendingApprovals = allApplications.count {
                    it.status == ApplicationStatus.PENDING
                }

                // Update UI on main thread
                view.post {
                    view.findViewById<TextView>(R.id.placementPercentageTextView)?.text =
                        "$placementPercentage%"

                    view.findViewById<android.widget.ProgressBar>(R.id.placementProgressBar)?.progress =
                        placementPercentage

                    view.findViewById<TextView>(R.id.highestPackageTextView)?.text =
                        "₹${String.format("%.1f", highestPackage)} LPA"

                    view.findViewById<TextView>(R.id.averagePackageTextView)?.text =
                        "₹${String.format("%.1f", averagePackage)} LPA"

                    view.findViewById<TextView>(R.id.approvalsBadgeTextView)?.apply {
                        text = pendingApprovals.toString()
                        visibility = if (pendingApprovals > 0) View.VISIBLE else View.GONE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadRecentApplications(view: View) {
        lifecycleScope.launch {
            try {
                val applications = applicationDao.getAllApplicationsSync()
                    .sortedByDescending { it.appliedAt }
                    .take(5)

                view.post {
                    val recyclerView =
                        view.findViewById<RecyclerView>(R.id.recentApplicationsRecyclerView)
                    val emptyState = view.findViewById<CardView>(R.id.emptyStateCard)

                    if (applications.isEmpty()) {
                        recyclerView?.visibility = View.GONE
                        emptyState?.visibility = View.VISIBLE
                    } else {
                        recyclerView?.visibility = View.VISIBLE
                        emptyState?.visibility = View.GONE

                        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
                        val adapter = ApplicationAdapter()

                        // Load company details for each application and update the adapter
                        lifecycleScope.launch {
                            val applicationsWithCompany = applications.map { application ->
                                val company = companyDao.getCompanyByIdSync(application.companyId)
                                ApplicationWithCompany(application, company)
                            }
                            adapter.submitList(applicationsWithCompany)
                        }
                        recyclerView?.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        view?.let { v ->
            loadStatistics(v)
            loadRecentApplications(v)
        }
    }
}
