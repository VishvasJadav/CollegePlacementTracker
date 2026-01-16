package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.OTPUtils
import com.example.collegeplacementtracker.utils.ValidationUtils
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var changePasswordButton: Button
    private lateinit var userDao: UserDao
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        sessionManager = SessionManager(this)
        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()

        initializeViews()
        setupListeners()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Change Password"
    }

    private fun initializeViews() {
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        changePasswordButton = findViewById(R.id.changePasswordButton)
    }

    private fun setupListeners() {
        changePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (validateInput(currentPassword, newPassword, confirmPassword)) {
                changePassword(currentPassword, newPassword)
            }
        }
    }

    private fun validateInput(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Boolean {
        if (currentPassword.isEmpty()) {
            currentPasswordEditText.error = "Current password is required"
            currentPasswordEditText.requestFocus()
            return false
        }

        val (passwordValid, passwordMessage) = ValidationUtils.isStrongPassword(newPassword)
        if (!passwordValid) {
            newPasswordEditText.error = passwordMessage
            newPasswordEditText.requestFocus()
            return false
        }

        if (newPassword != confirmPassword) {
            confirmPasswordEditText.error = "New passwords do not match"
            confirmPasswordEditText.requestFocus()
            return false
        }

        return true
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        changePasswordButton.isEnabled = false
        changePasswordButton.text = "Changing Password..."

        lifecycleScope.launch {
            try {
                val userId = sessionManager.getUserId()

                if (userId != -1) {
                    // Get the full user data from the database
                    val user = userDao.getUserByIdSync(userId)

                    if (user != null) {
                        // Verify current password
                        if (user.password == currentPassword) {
                            // Send OTP for verification before changing password
                            if (OTPUtils.sendOTP(this@ChangePasswordActivity, user.email)) {
                                // Store new password temporarily in preferences for OTP verification
                                val prefs =
                                    getSharedPreferences("temp_password_change", MODE_PRIVATE)
                                prefs.edit().putString("new_password", newPassword).apply()

                                // Navigate to OTP verification screen
                                val intent = Intent(
                                    this@ChangePasswordActivity,
                                    OTPVerificationActivity::class.java
                                )
                                intent.putExtra("identifier", user.email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@ChangePasswordActivity,
                                    "Failed to send OTP for verification. Please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@ChangePasswordActivity,
                                "Current password is incorrect.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "User data not found. Please login again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "User session not found. Please login again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ChangePasswordActivity,
                    "Error changing password: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                changePasswordButton.isEnabled = true
                changePasswordButton.text = "Change Password"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}