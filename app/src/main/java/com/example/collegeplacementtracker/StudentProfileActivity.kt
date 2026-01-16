package com.example.collegeplacementtracker

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.databinding.ActivityStudentProfileBinding
import kotlinx.coroutines.launch

class StudentProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao
    private var currentUser: User? = null

    private lateinit var binding: ActivityStudentProfileBinding
    private var isEditMode = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "My Profile"

        sessionManager = SessionManager(this)
        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()

        initViews()
        loadProfile()

        binding.editButton.setOnClickListener {
            toggleEditMode()
        }

        binding.saveButton.setOnClickListener {
            saveProfile()
        }
    }

    private fun initViews() {
        setEditMode(false)

        // Set up resume upload button click listener
        binding.uploadResumeButton.setOnClickListener {
            // Placeholder for resume upload functionality
            Toast.makeText(this, "Resume upload coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            val userId = sessionManager.getUserId()
            userDao.getUserById(userId).observe(this@StudentProfileActivity) { user ->
                user?.let {
                    currentUser = it
                    displayProfile(it)
                }
            }
        }
    }

    private fun displayProfile(user: User) {
        binding.nameEditText.setText(user.fullName)
        binding.emailTextView.text = user.email
        binding.phoneEditText.setText(user.phone)
        binding.rollNumberTextView.text = user.rollNumber ?: "N/A"
        binding.branchTextView.text = user.branch ?: "N/A"
        binding.cgpaEditText.setText(user.cgpa?.toString() ?: "")
        binding.professionalSummaryEditText.setText(user.professionalSummary ?: "")
        binding.skillsEditText.setText(user.skills ?: "")
        binding.internshipsEditText.setText(user.internships ?: "")
        binding.projectsEditText.setText(user.projects ?: "")

        binding.certificationsEditText.setText(user.certifications ?: "")
        binding.linkedinEditText.setText(user.linkedinUrl ?: "")

        // Update resume status
        if (!user.resumeUrl.isNullOrEmpty()) {
            binding.resumeStatusTextView.text =
                "Resume uploaded: ${'$'}{user.resumeUrl.substringAfterLast('/')}"
        } else {
            binding.resumeStatusTextView.text = "No resume uploaded"
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode
        setEditMode(isEditMode)

        if (isEditMode) {
            binding.editButton.text = "Cancel"
            binding.saveButton.visibility = Button.VISIBLE
        } else {
            binding.editButton.text = "Edit Profile"
            binding.saveButton.visibility = Button.GONE
            currentUser?.let { displayProfile(it) }
        }
    }

    private fun setEditMode(enabled: Boolean) {
        binding.nameEditText.isEnabled = enabled
        binding.phoneEditText.isEnabled = enabled
        binding.cgpaEditText.isEnabled = enabled
        binding.professionalSummaryEditText.isEnabled = enabled
        binding.skillsEditText.isEnabled = enabled
        binding.internshipsEditText.isEnabled = enabled
        binding.projectsEditText.isEnabled = enabled

        binding.certificationsEditText.isEnabled = enabled
        binding.linkedinEditText.isEnabled = enabled
    }

    private fun saveProfile() {
        if (!validateInput()) return

        lifecycleScope.launch {
            try {
                currentUser?.let { user ->
                    val updatedUser = user.copy(
                        fullName = binding.nameEditText.text.toString().trim(),
                        phone = binding.phoneEditText.text.toString().trim(),
                        cgpa = binding.cgpaEditText.text.toString().toDoubleOrNull(),
                        professionalSummary = binding.professionalSummaryEditText.text.toString()
                            .trim(),
                        skills = binding.skillsEditText.text.toString().trim(),
                        internships = binding.internshipsEditText.text.toString().trim(),
                        projects = binding.projectsEditText.text.toString().trim(),
                        // Additional fields from the second section would overwrite the first, so we'll only save from the first section
                        // additionalSkills = binding.additionalSkillsEditText.text.toString().trim(),
                        // additionalInternships = binding.additionalInternshipsEditText.text.toString().trim(),
                        // additionalProjects = binding.additionalProjectsEditText.text.toString().trim(),
                        certifications = binding.certificationsEditText.text.toString().trim(),
                        linkedinUrl = binding.linkedinEditText.text.toString().trim()
                    )

                    userDao.update(updatedUser)

                    Toast.makeText(
                        this@StudentProfileActivity,
                        "Profile updated successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    toggleEditMode()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@StudentProfileActivity,
                    "Failed to update profile: ${'$'}{e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateInput(): Boolean {
        val name = binding.nameEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()
        val cgpaText = binding.cgpaEditText.text.toString().trim()

        if (name.isEmpty()) {
            binding.nameEditText.error = "Name is required"
            return false
        }

        if (phone.isEmpty()) {
            binding.phoneEditText.error = "Phone is required"
            return false
        }

        if (phone.length != 10) {
            binding.phoneEditText.error = "Phone must be 10 digits"
            return false
        }

        if (cgpaText.isNotEmpty()) {
            val cgpa = cgpaText.toDoubleOrNull()
            if (cgpa == null || cgpa < 0 || cgpa > 10) {
                binding.cgpaEditText.error = "CGPA must be between 0 and 10"
                return false
            }
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}