package com.example.collegeplacementtracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TPOStudentManagementActivity : AppCompatActivity() {

    private lateinit var userDao: UserDao
    private lateinit var applicationDao: ApplicationDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: StudentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tpo_student_management)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "All Students"

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()
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
        userDao.getUsersByRole(UserRole.STUDENT).observe(this) { students ->
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
        applicationDao.getApplicationsByStudent(student.id.toLong()).observe(this) { applications ->
            val appliedCount = applications.size
            val selectedCount = applications.count { it.status == ApplicationStatus.SELECTED }

            val message = """
                Name: ${student.fullName}
                Roll: ${student.rollNumber}
                Branch: ${student.branch}
                CGPA: ${student.cgpa ?: "N/A"}
                Email: ${student.email}
                Phone: ${student.phone}
                
                Applications: $appliedCount
                Selected: $selectedCount
                Status: ${if (selectedCount > 0) "✅ Placed" else "⏳ Not Placed"}
                
                Skills: ${student.skills ?: "Not specified"}
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