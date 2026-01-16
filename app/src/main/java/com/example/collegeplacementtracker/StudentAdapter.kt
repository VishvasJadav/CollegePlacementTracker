package com.example.collegeplacementtracker


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentComparator()) {

    private var onItemClickListener: ((Student) -> Unit)? = null
    private var onDeleteClickListener: ((Student) -> Unit)? = null

    fun setOnItemClickListener(listener: (Student) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (Student) -> Unit) {
        onDeleteClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(student)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClickListener?.invoke(student)
        }
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val rollNumberTextView: TextView = itemView.findViewById(R.id.rollNumberTextView)
        private val branchTextView: TextView = itemView.findViewById(R.id.branchTextView)
        private val cgpaTextView: TextView = itemView.findViewById(R.id.cgpaTextView)
        private val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        private val companyTextView: TextView = itemView.findViewById(R.id.companyTextView)
        private val packageTextView: TextView = itemView.findViewById(R.id.packageTextView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(student: Student) {
            nameTextView.text = student.name
            rollNumberTextView.text = student.rollNumber
            branchTextView.text = student.branch
            cgpaTextView.text = "CGPA: ${student.cgpa}"

            if (student.isPlaced) {
                statusTextView.text = "✓ Placed"
                statusTextView.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                companyTextView.visibility = View.VISIBLE
                packageTextView.visibility = View.VISIBLE
                companyTextView.text = student.companyName ?: "N/A"
                packageTextView.text = "${student.packageAmount ?: 0.0} LPA"
            } else {
                statusTextView.text = "● Not Placed"
                statusTextView.setTextColor(itemView.context.getColor(android.R.color.holo_orange_dark))
                companyTextView.visibility = View.GONE
                packageTextView.visibility = View.GONE
            }
        }
    }

    class StudentComparator : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
}
