package com.example.collegeplacementtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CompanyAdapter : ListAdapter<Company, CompanyAdapter.CompanyViewHolder>(CompanyComparator()) {

    private var onItemClickListener: ((Company) -> Unit)? = null
    private var onApplyClickListener: ((Company) -> Unit)? = null

    fun setOnItemClickListener(listener: (Company) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnApplyClickListener(listener: (Company) -> Unit) {
        onApplyClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_company, parent, false)
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = getItem(position)
        holder.bind(company)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(company)
        }

        holder.applyButton.setOnClickListener {
            onApplyClickListener?.invoke(company)
        }
    }

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val companyNameTextView: TextView = itemView.findViewById(R.id.companyNameTextView)
        private val jobRoleTextView: TextView = itemView.findViewById(R.id.jobRoleTextView)
        private val packageTextView: TextView = itemView.findViewById(R.id.packageTextView)
        private val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        private val eligibilityTextView: TextView = itemView.findViewById(R.id.eligibilityTextView)
        private val deadlineTextView: TextView = itemView.findViewById(R.id.deadlineTextView)
        private val companyTypeTextView: TextView = itemView.findViewById(R.id.companyTypeTextView)
        private val employeesCountTextView: TextView =
            itemView.findViewById(R.id.employeesCountTextView)
        private val workFromHomeTextView: TextView =
            itemView.findViewById(R.id.workFromHomeTextView)
        private val learningOpportunitiesTextView: TextView =
            itemView.findViewById(R.id.learningOpportunitiesTextView)
        private val growthPotentialTextView: TextView =
            itemView.findViewById(R.id.growthPotentialTextView)
        val applyButton: Button = itemView.findViewById(R.id.applyButton)

        fun bind(company: Company) {
            companyNameTextView.text = company.companyName
            jobRoleTextView.text = company.jobRole
            packageTextView.text = "${company.packageAmount} LPA"
            locationTextView.text = "📍 ${company.location}"
            eligibilityTextView.text =
                "Min CGPA: ${company.minimumCGPA} | ${company.eligibleBranches}"
            deadlineTextView.text = "Apply by: ${company.applicationDeadline}"

            // Set professional insights
            companyTypeTextView.text = company.companyType ?: "Product"
            employeesCountTextView.text = "${company.employeesCount ?: "50+"} employees"

            // Show/hide professional insights based on availability
            if (company.workFromHomePolicy == true) {
                workFromHomeTextView.visibility = View.VISIBLE
            } else {
                workFromHomeTextView.visibility = View.GONE
            }

            if (company.learningOpportunities == true) {
                learningOpportunitiesTextView.visibility = View.VISIBLE
            } else {
                learningOpportunitiesTextView.visibility = View.GONE
            }

            if (company.growthPotential == true) {
                growthPotentialTextView.visibility = View.VISIBLE
            } else {
                growthPotentialTextView.visibility = View.GONE
            }
        }
    }

    class CompanyComparator : DiffUtil.ItemCallback<Company>() {
        override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem == newItem
        }
    }
}