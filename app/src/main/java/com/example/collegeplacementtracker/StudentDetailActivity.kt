package com.example.collegeplacementtracker


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.card.MaterialCardView

class StudentDetailActivity : AppCompatActivity() {

    private lateinit var studentViewModel: StudentViewModel
    private var studentId: Int = 0
    private var currentStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        studentId = intent.getIntExtra("STUDENT_ID", 0)

        studentViewModel = ViewModelProvider(this).get(StudentViewModel::class.java)

        loadStudentDetails()
    }

    private fun loadStudentDetails() {
        studentViewModel.getStudentById(studentId).observe(this) { student ->
            student?.let {
                currentStudent = it
                displayStudentDetails(it)
            }
        }
    }

    private fun displayStudentDetails(student: Student) {
        title = student.name

        findViewById<TextView>(R.id.nameTextView).text = student.name
        findViewById<TextView>(R.id.rollNumberTextView).text = "Roll: ${student.rollNumber}"
        findViewById<TextView>(R.id.emailTextView).text = student.email
        findViewById<TextView>(R.id.phoneTextView).text = student.phone
        findViewById<TextView>(R.id.branchTextView).text = student.branch
        findViewById<TextView>(R.id.cgpaTextView).text = "CGPA: ${student.cgpa}"

        val placementCard = findViewById<MaterialCardView>(R.id.placementCard)
        val statusTextView = findViewById<TextView>(R.id.statusTextView)

        if (student.isPlaced) {
            statusTextView.text = "✓ Placed"
            statusTextView.setTextColor(getColor(android.R.color.holo_green_dark))
            placementCard.visibility = MaterialCardView.VISIBLE

            findViewById<TextView>(R.id.companyTextView).text = student.companyName ?: "N/A"
            findViewById<TextView>(R.id.packageTextView).text =
                "${student.packageAmount ?: 0.0} LPA"
            findViewById<TextView>(R.id.roleTextView).text = student.jobRole ?: "N/A"
            findViewById<TextView>(R.id.dateTextView).text = student.placementDate ?: "N/A"
        } else {
            statusTextView.text = "● Not Placed"
            statusTextView.setTextColor(getColor(android.R.color.holo_orange_dark))
            placementCard.visibility = MaterialCardView.GONE
        }

        findViewById<TextView>(R.id.skillsTextView).text = student.skills ?: "Not specified"
        findViewById<TextView>(R.id.internshipsTextView).text =
            student.internships ?: "Not specified"
        findViewById<TextView>(R.id.projectsTextView).text = student.projects ?: "Not specified"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val intent = Intent(this, AddStudentActivity::class.java)
                intent.putExtra("STUDENT_ID", studentId)
                startActivity(intent)
                true
            }

            R.id.action_delete -> {
                currentStudent?.let {
                    studentViewModel.delete(it)
                    finish()
                }
                true
            }

            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
