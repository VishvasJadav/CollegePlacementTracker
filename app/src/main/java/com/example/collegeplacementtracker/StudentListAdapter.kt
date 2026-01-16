package com.example.collegeplacementtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class StudentListAdapter :
    ListAdapter<User, StudentListAdapter.StudentViewHolder>(StudentComparator()) {

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_list, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(student)
        }
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.studentNameTextView)
        private val rollNumberTextView: TextView = itemView.findViewById(R.id.rollNumberTextView)
        private val cgpaTextView: TextView = itemView.findViewById(R.id.cgpaTextView)
        private val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)

        fun bind(student: User) {
            nameTextView.text = student.fullName
            rollNumberTextView.text = "Roll: ${student.rollNumber ?: "N/A"}"
            cgpaTextView.text = "CGPA: ${student.cgpa?.toString() ?: "N/A"}"
            emailTextView.text = student.email
        }
    }

    class StudentComparator : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}