package com.example.collegeplacementtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ApplicationAdapter :
    ListAdapter<ApplicationWithCompany, ApplicationAdapter.ApplicationViewHolder>(
        ApplicationComparator()
    ) {

    private var onItemClickListener: ((ApplicationWithCompany) -> Unit)? = null

    fun setOnItemClickListener(listener: (ApplicationWithCompany) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_application_card, parent, false)
        return ApplicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }
    }

    class ApplicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val companyNameTextView: TextView = itemView.findViewById(R.id.companyNameTextView)
        private val jobRoleTextView: TextView = itemView.findViewById(R.id.jobRoleTextView)
        private val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        private val packageTextView: TextView = itemView.findViewById(R.id.packageTextView)
        private val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        private val appliedDateTextView: TextView = itemView.findViewById(R.id.appliedDateTextView)

        // Timeline status icons
        private val appliedStatusIcon: TextView = itemView.findViewById(R.id.appliedStatusIcon)
        private val shortlistedStatusIcon: TextView =
            itemView.findViewById(R.id.shortlistedStatusIcon)
        private val interviewStatusIcon: TextView = itemView.findViewById(R.id.interviewStatusIcon)
        private val finalStatusIcon: TextView = itemView.findViewById(R.id.finalStatusIcon)

        fun bind(item: ApplicationWithCompany) {
            val application = item.application
            val company = item.company

            // Set company details if available
            if (company != null) {
                companyNameTextView.text = company.companyName
                jobRoleTextView.text = company.jobRole
                packageTextView.text = "💰 ${company.packageAmount} LPA"
                locationTextView.text = "📍 ${company.location}"
            } else {
                companyNameTextView.text = "Company not found"
                jobRoleTextView.text = "N/A"
                packageTextView.text = "N/A"
                locationTextView.text = "N/A"
            }

            // Update status badge
            val statusColor = when (application.status) {
                ApplicationStatus.PENDING -> "#F0AD4E" // Orange
                ApplicationStatus.SHORTLISTED -> "#5DADE2" // Blue
                ApplicationStatus.SELECTED -> "#58B368" // Green
                ApplicationStatus.REJECTED -> "#D9534F" // Red
                else -> "#95A5A6" // Gray
            }

            statusTextView.text = application.status
            statusTextView.setBackgroundColor(android.graphics.Color.parseColor(statusColor))

            // Format applied date
            val dateFormat = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())
            appliedDateTextView.text = dateFormat.format(application.appliedAt)

            // Update timeline based on application status
            updateTimeline(application)
        }

        private fun updateTimeline(application: Application) {
            // Applied status is always completed
            appliedStatusIcon.text = "✅"
            appliedStatusIcon.setTextColor(android.graphics.Color.parseColor("#58B368"))

            // Update other timeline icons based on status
            when (application.status) {
                ApplicationStatus.PENDING -> {
                    shortlistedStatusIcon.text = "⏳"
                    shortlistedStatusIcon.setTextColor(android.graphics.Color.parseColor("#F0AD4E"))
                    interviewStatusIcon.text = "⏳"
                    interviewStatusIcon.setTextColor(android.graphics.Color.parseColor("#BDC3C7"))
                    finalStatusIcon.text = "⏳"
                    finalStatusIcon.setTextColor(android.graphics.Color.parseColor("#BDC3C7"))
                }

                ApplicationStatus.SHORTLISTED -> {
                    shortlistedStatusIcon.text = "✅"
                    shortlistedStatusIcon.setTextColor(android.graphics.Color.parseColor("#58B368"))
                    interviewStatusIcon.text = "⏳"
                    interviewStatusIcon.setTextColor(android.graphics.Color.parseColor("#F0AD4E"))
                    finalStatusIcon.text = "⏳"
                    finalStatusIcon.setTextColor(android.graphics.Color.parseColor("#BDC3C7"))
                }

                ApplicationStatus.SELECTED -> {
                    shortlistedStatusIcon.text = "✅"
                    shortlistedStatusIcon.setTextColor(android.graphics.Color.parseColor("#58B368"))
                    interviewStatusIcon.text = "✅"
                    interviewStatusIcon.setTextColor(android.graphics.Color.parseColor("#58B368"))
                    finalStatusIcon.text = "✅"
                    finalStatusIcon.setTextColor(android.graphics.Color.parseColor("#58B368"))
                }

                ApplicationStatus.REJECTED -> {
                    shortlistedStatusIcon.text = if (application.currentRound > 1) "✅" else "❌"
                    shortlistedStatusIcon.setTextColor(
                        if (application.currentRound > 1) android.graphics.Color.parseColor(
                            "#58B368"
                        ) else android.graphics.Color.parseColor("#D9534F")
                    )
                    interviewStatusIcon.text = if (application.currentRound > 2) "✅" else "❌"
                    interviewStatusIcon.setTextColor(
                        if (application.currentRound > 2) android.graphics.Color.parseColor(
                            "#58B368"
                        ) else android.graphics.Color.parseColor("#D9534F")
                    )
                    finalStatusIcon.text = "❌"
                    finalStatusIcon.setTextColor(android.graphics.Color.parseColor("#D9534F"))
                }

                else -> {
                    shortlistedStatusIcon.text = "⏳"
                    shortlistedStatusIcon.setTextColor(android.graphics.Color.parseColor("#BDC3C7"))
                    interviewStatusIcon.text = "⏳"
                    interviewStatusIcon.setTextColor(android.graphics.Color.parseColor("#BDC3C7"))
                    finalStatusIcon.text = "⏳"
                    finalStatusIcon.setTextColor(android.graphics.Color.parseColor("#BDC3C7"))
                }
            }
        }
    }

    class ApplicationComparator : DiffUtil.ItemCallback<ApplicationWithCompany>() {
        override fun areItemsTheSame(
            oldItem: ApplicationWithCompany,
            newItem: ApplicationWithCompany
        ): Boolean {
            return oldItem.application.id == newItem.application.id
        }

        override fun areContentsTheSame(
            oldItem: ApplicationWithCompany,
            newItem: ApplicationWithCompany
        ): Boolean {
            return oldItem == newItem
        }
    }
}