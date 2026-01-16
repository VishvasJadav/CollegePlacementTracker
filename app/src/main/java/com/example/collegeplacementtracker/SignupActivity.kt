package com.example.collegeplacementtracker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var collegeIdEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var branchSpinner: Spinner
    private lateinit var rollNumberEditText: EditText
    private lateinit var cgpaEditText: EditText
    private lateinit var studentFieldsLayout: LinearLayout
    private lateinit var signupButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Create Account"

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()

        initViews()
        setupRoleSpinner()
        setupBranchSpinner()

        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val role = parent?.getItemAtPosition(position).toString()
                studentFieldsLayout.visibility = if (role == "Student") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        signupButton.setOnClickListener {
            if (validateInput()) {
                performSignup()
            }
        }

        loginTextView.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        collegeIdEditText = findViewById(R.id.collegeIdEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        roleSpinner = findViewById(R.id.roleSpinner)
        branchSpinner = findViewById(R.id.branchSpinner)
        rollNumberEditText = findViewById(R.id.rollNumberEditText)
        cgpaEditText = findViewById(R.id.cgpaEditText)
        studentFieldsLayout = findViewById(R.id.studentFieldsLayout)
        signupButton = findViewById(R.id.signupButton)
        loginTextView = findViewById(R.id.loginTextView)
    }

    private fun setupRoleSpinner() {
        val roles = arrayOf("Student", "HOD", "TPO")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter
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

    private fun validateInput(): Boolean {
        val fullName = fullNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val collegeId = collegeIdEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val selectedRole = roleSpinner.selectedItem.toString()

        if (fullName.isEmpty()) {
            fullNameEditText.error = "Full name is required"
            return false
        }

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Invalid email format"
            return false
        }

        // Validate College ID if provided
        if (collegeId.isNotEmpty() && collegeId.length < 3) {
            collegeIdEditText.error = "College ID must be at least 3 characters"
            return false
        }

        if (phone.isEmpty()) {
            phoneEditText.error = "Phone number is required"
            return false
        }

        if (phone.length != 10) {
            phoneEditText.error = "Phone number must be 10 digits"
            return false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            return false
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            return false
        }

        if (selectedRole == "Student") {
            val rollNumber = rollNumberEditText.text.toString().trim()
            val cgpaText = cgpaEditText.text.toString().trim()

            if (rollNumber.isEmpty()) {
                rollNumberEditText.error = "Roll number is required"
                return false
            }

            if (cgpaText.isEmpty()) {
                cgpaEditText.error = "CGPA is required"
                return false
            }

            val cgpa = cgpaText.toDoubleOrNull()
            if (cgpa == null || cgpa < 0 || cgpa > 10) {
                cgpaEditText.error = "CGPA must be between 0 and 10"
                return false
            }
        }

        return true
    }

    private fun performSignup() {
        signupButton.isEnabled = false
        signupButton.text = "Creating Account..."

        lifecycleScope.launch {
            try {
                val email = emailEditText.text.toString().trim()
                val phone = phoneEditText.text.toString().trim()
                val collegeId = collegeIdEditText.text.toString().trim()

                // Check if user already exists with same email, phone, or college ID
                val existingUser = userDao.getUserByEmailPhoneOrCollegeId(
                    email,
                    phone,
                    collegeId.takeIf { it.isNotEmpty() })
                if (existingUser != null) {
                    val message = when {
                        existingUser.email == email -> "Email already registered"
                        existingUser.phone == phone -> "Phone number already registered"
                        existingUser.collegeId == collegeId -> "College ID already registered"
                        else -> "Account already exists with provided details"
                    }

                    Toast.makeText(
                        this@SignupActivity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val roleDisplay = roleSpinner.selectedItem.toString()
                val role = when (roleDisplay) {
                    "Student" -> UserRole.STUDENT
                    "HOD" -> UserRole.HOD
                    "TPO" -> UserRole.TPO
                    else -> UserRole.STUDENT
                }

                val user = User(
                    email = email,
                    password = passwordEditText.text.toString().trim(),
                    fullName = fullNameEditText.text.toString().trim(),
                    phone = phoneEditText.text.toString().trim(),
                    role = role,
                    collegeId = if (collegeId.isNotEmpty()) collegeId else null,
                    rollNumber = if (role == UserRole.STUDENT) rollNumberEditText.text.toString()
                        .trim() else null,
                    branch = if (role == UserRole.STUDENT || role == UserRole.HOD)
                        branchSpinner.selectedItem.toString() else "All",
                    cgpa = if (role == UserRole.STUDENT) cgpaEditText.text.toString()
                        .toDouble() else null
                )

                userDao.insert(user)

                Toast.makeText(
                    this@SignupActivity,
                    "Account created successfully! Please login.",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@SignupActivity,
                    "Signup failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                signupButton.isEnabled = true
                signupButton.text = "Sign Up"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}