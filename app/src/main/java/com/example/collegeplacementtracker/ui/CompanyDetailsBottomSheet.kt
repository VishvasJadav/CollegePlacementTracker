package com.example.collegeplacementtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.Application
import com.example.collegeplacementtracker.ApplicationDao
import com.example.collegeplacementtracker.ApplicationStatus
import com.example.collegeplacementtracker.Company
import com.example.collegeplacementtracker.R
import com.example.collegeplacementtracker.SessionManager
import com.example.collegeplacementtracker.utils.DateUtils
import com.example.collegeplacementtracker.utils.NotificationHelper
import com.example.collegeplacementtracker.utils.UIHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class CompanyDetailsBottomSheet(
    private val company: Company,
    private val applicationDao: ApplicationDao
) : BottomSheetDialogFragment() {

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_company_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        setupViews(view)
        setupButtons(view)
    }

    private fun setupViews(view: View) {
        view.findViewById<TextView>(R.id.companyNameText).text = company.companyName
        view.findViewById<TextView>(R.id.jobRoleText).text = company.jobRole
        view.findViewById<TextView>(R.id.packageText).text = "${company.packageAmount} LPA"
        view.findViewById<TextView>(R.id.locationText).text = company.location
        view.findViewById<TextView>(R.id.companyTypeText).text = company.companyType

        view.findViewById<TextView>(R.id.eligibleBranchesText).text = company.eligibleBranches
        view.findViewById<TextView>(R.id.minimumCGPAText).text = company.minimumCGPA.toString()
        view.findViewById<TextView>(R.id.backlogsText).text = company.backlogs.toString()

        view.findViewById<TextView>(R.id.selectionProcessText).text = company.selectionProcess
        view.findViewById<TextView>(R.id.numberOfRoundsText).text =
            company.numberOfRounds.toString()

        view.findViewById<TextView>(R.id.deadlineText).text = company.applicationDeadline
        view.findViewById<TextView>(R.id.totalPositionsText).text =
            "${company.totalPositions - company.filledPositions} / ${company.totalPositions} available"

        view.findViewById<TextView>(R.id.descriptionText).text = company.jobDescription

        // Deadline warning
        val daysUntil = DateUtils.getDaysUntil(company.applicationDeadline)
        val deadlineWarning = view.findViewById<TextView>(R.id.deadlineWarningText)
        when {
            daysUntil < 0 -> {
                deadlineWarning.text = "❌ Deadline passed"
                deadlineWarning.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                deadlineWarning.visibility = View.VISIBLE
            }

            daysUntil <= 3 -> {
                deadlineWarning.text = "⚠️ Only $daysUntil days left!"
                deadlineWarning.setTextColor(resources.getColor(android.R.color.holo_orange_dark))
                deadlineWarning.visibility = View.VISIBLE
            }

            else -> {
                deadlineWarning.visibility = View.GONE
            }
        }
    }

    private fun setupButtons(view: View) {
        val applyButton = view.findViewById<Button>(R.id.applyButton)
        val shareButton = view.findViewById<Button>(R.id.shareButton)
        val closeButton = view.findViewById<Button>(R.id.closeButton)

        applyButton.setOnClickListener {
            applyToCompany()
        }

        shareButton.setOnClickListener {
            shareCompany()
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun applyToCompany() {
        lifecycleScope.launch {
            try {
                val userId = sessionManager.getUserId()

                val existingApp = applicationDao.getExistingApplication(userId, company.id)
                if (existingApp != null) {
                    UIHelper.showInfo(requireContext(), "You have already applied to this company")
                    return@launch
                }

                val application = Application(
                    studentId = userId,
                    companyId = company.id,
                    status = ApplicationStatus.PENDING
                )

                applicationDao.insert(application)

                NotificationHelper.sendApplicationStatusNotification(
                    requireContext(),
                    company.companyName,
                    "Applied",
                    application.id
                )

                UIHelper.showSuccess(requireContext(), "Application submitted! 🎉")
                dismiss()

            } catch (e: Exception) {
                UIHelper.showError(requireContext(), "Failed to apply: ${e.message}")
            }
        }
    }

    private fun shareCompany() {
        val shareText = """
            🏢 ${company.companyName}
            
            💼 Role: ${company.jobRole}
            💰 Package: ${company.packageAmount} LPA
            📍 Location: ${company.location}
            📅 Deadline: ${company.applicationDeadline}
            
            Apply through College Placement Tracker!
        """.trimIndent()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Company"))
    }

    companion object {
        const val TAG = "CompanyDetailsBottomSheet"
    }
}
