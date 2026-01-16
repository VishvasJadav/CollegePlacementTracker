package com.example.collegeplacementtracker


import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial

class AddStudentActivity : AppCompatActivity() {

    private lateinit var studentViewModel: StudentViewModel
    private var isEditMode: Boolean = false
    private var studentId: Int = 0

    private lateinit var nameEditText: EditText
    private lateinit var rollNumberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var branchSpinner: Spinner
    private lateinit var cgpaEditText: EditText
    private lateinit var isPlacedSwitch: SwitchMaterial
    private lateinit var placementDetailsLayout: LinearLayout
    private lateinit var companyNameEditText: EditText
    private lateinit var packageAmountEditText: EditText
    private lateinit var jobRoleEditText: EditText
    private lateinit var placementDateEditText: EditText
    private lateinit var skillsEditText: EditText
    private lateinit var internshipsEditText: EditText
    private lateinit var projectsEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views
        initViews()

        // Setup ViewModel
        studentViewModel = ViewModelProvider(this).get(StudentViewModel::class.java)

        // Setup branch spinner
        setupBranchSpinner()

        // Check if edit mode
        isEditMode = intent.hasExtra("STUDENT_ID")
        if (isEditMode) {
            studentId = intent.getIntExtra("STUDENT_ID", 0)
            title = "Edit Student"
            loadStudentData()
        } else {
            title = "Add Student"
        }

        // Placement switch listener
        isPlacedSwitch.setOnCheckedChangeListener { _, isChecked ->
            placementDetailsLayout.visibility =
                if (isChecked) LinearLayout.VISIBLE else LinearLayout.GONE
        }

        // Save button click
        saveButton.setOnClickListener {
            saveStudent()
        }
    }

    private fun initViews() {
        nameEditText = findViewById(R.id.nameEditText)
        rollNumberEditText = findViewById(R.id.rollNumberEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        branchSpinner = findViewById(R.id.branchSpinner)
        cgpaEditText = findViewById(R.id.cgpaEditText)
        isPlacedSwitch = findViewById(R.id.isPlacedSwitch)
        placementDetailsLayout = findViewById(R.id.placementDetailsLayout)
        companyNameEditText = findViewById(R.id.companyNameEditText)
        packageAmountEditText = findViewById(R.id.packageAmountEditText)
        jobRoleEditText = findViewById(R.id.jobRoleEditText)
        placementDateEditText = findViewById(R.id.placementDateEditText)
        skillsEditText = findViewById(R.id.skillsEditText)
        internshipsEditText = findViewById(R.id.internshipsEditText)
        projectsEditText = findViewById(R.id.projectsEditText)
        saveButton = findViewById(R.id.saveButton)
    }

    private fun setupBranchSpinner() {
        val branches = arrayOf(
            "Computer Science",
            "Information Technology",
            "Electronics and Communication",
            "Mechanical Engineering",
            "Civil Engineering",
            "Electrical Engineering",
            "Chemical Engineering"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, branches)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        branchSpinner.adapter = adapter
    }

    private fun loadStudentData() {
        studentViewModel.getStudentById(studentId).observe(this) { student ->
            student?.let {
                nameEditText.setText(it.name)
                rollNumberEditText.setText(it.rollNumber)
                emailEditText.setText(it.email)
                phoneEditText.setText(it.phone)
                cgpaEditText.setText(it.cgpa.toString())
                isPlacedSwitch.isChecked = it.isPlaced

                if (it.isPlaced) {
                    companyNameEditText.setText(it.companyName)
                    packageAmountEditText.setText(it.packageAmount?.toString() ?: "")
                    jobRoleEditText.setText(it.jobRole)
                    placementDateEditText.setText(it.placementDate)
                }

                skillsEditText.setText(it.skills)
                internshipsEditText.setText(it.internships)
                projectsEditText.setText(it.projects)
            }
        }
    }

    private fun saveStudent() {
        // Validate inputs
        if (!validateInputs()) {
            return
        }

        val student = Student(
            id = if (isEditMode) studentId else 0,
            name = nameEditText.text.toString().trim(),
            rollNumber = rollNumberEditText.text.toString().trim(),
            email = emailEditText.text.toString().trim(),
            phone = phoneEditText.text.toString().trim(),
            branch = branchSpinner.selectedItem.toString(),
            cgpa = cgpaEditText.text.toString().toDouble(),
            isPlaced = isPlacedSwitch.isChecked,
            companyName = if (isPlacedSwitch.isChecked) companyNameEditText.text.toString()
                .trim() else null,
            packageAmount = if (isPlacedSwitch.isChecked && packageAmountEditText.text.isNotEmpty())
                packageAmountEditText.text.toString().toDouble() else null,
            jobRole = if (isPlacedSwitch.isChecked) jobRoleEditText.text.toString()
                .trim() else null,
            placementDate = if (isPlacedSwitch.isChecked) placementDateEditText.text.toString()
                .trim() else null,
            skills = skillsEditText.text.toString().trim(),
            internships = internshipsEditText.text.toString().trim(),
            projects = projectsEditText.text.toString().trim()
        )

        if (isEditMode) {
            studentViewModel.update(student)
            Toast.makeText(this, "Student updated", Toast.LENGTH_SHORT).show()
        } else {
            studentViewModel.insert(student)
            Toast.makeText(this, "Student added", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun validateInputs(): Boolean {
        if (nameEditText.text.isEmpty()) {
            nameEditText.error = "Name is required"
            return false
        }

        if (rollNumberEditText.text.isEmpty()) {
            rollNumberEditText.error = "Roll number is required"
            return false
        }

        if (emailEditText.text.isEmpty()) {
            emailEditText.error = "Email is required"
            return false
        }

        if (cgpaEditText.text.isEmpty()) {
            cgpaEditText.error = "CGPA is required"
            return false
        }

        val cgpa = cgpaEditText.text.toString().toDoubleOrNull()
        if (cgpa == null || cgpa < 0 || cgpa > 10) {
            cgpaEditText.error = "CGPA must be between 0 and 10"
            return false
        }

        if (isPlacedSwitch.isChecked && companyNameEditText.text.isEmpty()) {
            companyNameEditText.error = "Company name is required for placed students"
            return false
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
