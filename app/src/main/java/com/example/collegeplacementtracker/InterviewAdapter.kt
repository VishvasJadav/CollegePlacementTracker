package com.example.collegeplacementtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class InterviewAdapter(
    private val onEditClick: (Interview) -> Unit,
    private val onCancelClick: (Interview) -> Unit,
    private val onCompleteClick: (Interview) -> Unit
) : ListAdapter<InterviewAdapter.InterviewWithDetails, InterviewAdapter.InterviewViewHolder>(
    InterviewComparator()
) {

    data class InterviewWithDetails(
        val interview: Interview,
        val companyName: String,
        val studentName: String
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_interview, parent, false)
        return InterviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: InterviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InterviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val companyNameTextView: TextView = itemView.findViewById(R.id.companyNameTextView)
        private val studentNameTextView: TextView = itemView.findViewById(R.id.studentNameTextView)
        private val statusBadge: TextView = itemView.findViewById(R.id.statusBadge)
        private val dateTimeTextView: TextView = itemView.findViewById(R.id.dateTimeTextView)
        private val roundTypeTextView: TextView = itemView.findViewById(R.id.roundTypeTextView)
        private val modeLocationTextView: TextView =
            itemView.findViewById(R.id.modeLocationTextView)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val cancelButton: Button = itemView.findViewById(R.id.cancelButton)
        private val completeButton: Button = itemView.findViewById(R.id.completeButton)

        fun bind(item: InterviewWithDetails) {
            val interview = item.interview

            companyNameTextView.text = item.companyName
            studentNameTextView.text = item.studentName
            dateTimeTextView.text = "${interview.interviewDate}, ${interview.interviewTime}"
            roundTypeTextView.text = "${interview.roundType} - Round ${interview.interviewRound}"
            modeLocationTextView.text =
                "${interview.interviewMode} - ${interview.interviewLocation}"

            // Set status badge
            statusBadge.text = interview.status.name
            val badgeColor = when (interview.status) {
                InterviewStatus.SCHEDULED -> android.R.color.holo_orange_dark
                InterviewStatus.COMPLETED -> android.R.color.holo_green_dark
                InterviewStatus.CANCELLED -> android.R.color.holo_red_dark
                InterviewStatus.RESCHEDULED -> android.R.color.holo_blue_dark
                InterviewStatus.NO_SHOW -> android.R.color.darker_gray
            }
            statusBadge.backgroundTintList =
                ContextCompat.getColorStateList(itemView.context, badgeColor)

            // Show/hide buttons based on status
            val isActionable = interview.status == InterviewStatus.SCHEDULED ||
                    interview.status == InterviewStatus.RESCHEDULED
            editButton.visibility = if (isActionable) View.VISIBLE else View.GONE
            cancelButton.visibility = if (isActionable) View.VISIBLE else View.GONE
            completeButton.visibility = if (isActionable) View.VISIBLE else View.GONE

            editButton.setOnClickListener { onEditClick(interview) }
            cancelButton.setOnClickListener { onCancelClick(interview) }
            completeButton.setOnClickListener { onCompleteClick(interview) }
        }
    }

    class InterviewComparator : DiffUtil.ItemCallback<InterviewWithDetails>() {
        override fun areItemsTheSame(
            oldItem: InterviewWithDetails,
            newItem: InterviewWithDetails
        ): Boolean {
            return oldItem.interview.interviewId == newItem.interview.interviewId
        }

        override fun areContentsTheSame(
            oldItem: InterviewWithDetails,
            newItem: InterviewWithDetails
        ): Boolean {
            return oldItem == newItem
        }
    }
}
