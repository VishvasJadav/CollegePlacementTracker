package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegeplacementtracker.utils.SessionManager
import kotlinx.coroutines.launch

class StudentHomeFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var database: AppDatabaseNew
    private lateinit var applicationDao: ApplicationDao
    private lateinit var studentDao: StudentDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager.getInstance(requireContext())
        database = AppDatabaseNew.getDatabase(requireContext(), lifecycleScope)
        applicationDao = database.applicationDao()
        studentDao = database.studentDao()

        setupClickListeners(view)
        loadProfileCompletion(view)
        loadRecentApplications(view)
    }

    private fun setupClickListeners(view: View) {
        // Browse Companies Card
        view.findViewById<CardView>(R.id.browseCompaniesCard)?.setOnClickListener {
            startActivity(Intent(requireContext(), CompanyListActivity::class.java))
        }

        // Edit Profile Card
        view.findViewById<CardView>(R.id.editProfileCard)?.setOnClickListener {
            startActivity(Intent(requireContext(), StudentProfileActivity::class.java))
        }

        // View All Applications
        view.findViewById<TextView>(R.id.viewAllApplicationsTextView)?.setOnClickListener {
            startActivity(Intent(requireContext(), MyApplicationsActivity::class.java))
        }

        // Browse Companies Button (in empty state)
        view.findViewById<Button>(R.id.browseCompaniesButton)?.setOnClickListener {
            startActivity(Intent(requireContext(), CompanyListActivity::class.java))
        }
    }

    private fun loadProfileCompletion(view: View) {
        lifecycleScope.launch {
            try {
                val email: String? = sessionManager.getUserEmail()
                if (email != null) {
                    val student = studentDao.getStudentByEmail(email)

                    student?.let { s ->
                        var completionScore = 0
                        val totalFields = 6

                        if (s.name.isNotBlank()) completionScore++
                        if (s.email.isNotBlank()) completionScore++
                        if (s.rollNumber.isNotBlank()) completionScore++
                        if (s.branch.isNotBlank()) completionScore++
                        if (s.cgpa > 0.0) completionScore++
                        if (s.phone.isNotBlank()) completionScore++

                        val percentage = ((completionScore.toFloat() / totalFields) * 100).toInt()

                        view.post {
                            view.findViewById<TextView>(R.id.profileCompletionPercentage)?.text =
                                "$percentage%"
                            view.findViewById<ProgressBar>(R.id.profileProgressBar)?.progress =
                                percentage
                        }
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
                val email: String? = sessionManager.getUserEmail()
                if (email != null) {
                    val student = studentDao.getStudentByEmail(email)

                    student?.let { s ->
                        val applications = applicationDao.getApplicationsByStudentId(s.id)
                            .sortedByDescending { it.appliedAt }
                            .take(3)

                        view.post {
                            val recyclerView = view.findViewById<RecyclerView>(
                                R.id.recentApplicationsRecyclerView
                            )
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
                                        val company = database.companyDao()
                                            .getCompanyByIdSync(application.companyId)
                                        ApplicationWithCompany(application, company)
                                    }
                                    adapter.submitList(applicationsWithCompany)
                                }
                                recyclerView?.adapter = adapter
                            }
                        }
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
            loadProfileCompletion(v)
            loadRecentApplications(v)
        }
    }
}
