package com.example.collegeplacementtracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DepartmentStudentsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao
    private lateinit var applicationDao: ApplicationDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: StudentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department_students)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Department Students"

        sessionManager = SessionManager(this)

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        applicationDao = database.applicationDao()

        recyclerView = findViewById(R.id.studentsRecyclerView)
        emptyView = findViewById(R.id.emptyView)

        setupRecyclerView()
        loadStudents()
    }

    private fun setupRecyclerView() {
        adapter = StudentListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener { student ->
            showStudentDetails(student)
        }
    }

    private fun loadStudents() {
        val branch = sessionManager.getUserBranch() ?: return

        userDao.getStudentsByBranch(branch).observe(this) { students ->
            if (students.isEmpty()) {
                recyclerView.visibility = RecyclerView.GONE
                emptyView.visibility = TextView.VISIBLE
            } else {
                recyclerView.visibility = RecyclerView.VISIBLE
                emptyView.visibility = TextView.GONE
                adapter.submitList(students)
            }
        }
    }

    private fun showStudentDetails(student: User) {
        applicationDao.getApplicationsByStudent(student.id).observe(this) { applications ->
            val appliedCount = applications.size
            val selectedCount = applications.count { it.status == ApplicationStatus.SELECTED }
            val placementStatus = if (selectedCount > 0) "✅ Placed" else "⏳ Not Placed"

            val message = """
                Name: ${student.fullName}
                Roll Number: ${student.rollNumber}
                Email: ${student.email}
                Phone: ${student.phone}
                CGPA: ${student.cgpa ?: "N/A"}
                
                Placement Status: $placementStatus
                Applications: $appliedCount
                Selected: $selectedCount
                
                Skills: ${student.skills ?: "Not specified"}
                Internships: ${student.internships ?: "Not specified"}
                Projects: ${student.projects ?: "Not specified"}
            """.trimIndent()

            AlertDialog.Builder(this)
                .setTitle(student.fullName)
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}