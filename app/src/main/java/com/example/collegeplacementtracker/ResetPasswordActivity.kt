package com.example.collegeplacementtracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.ValidationUtils
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var resetButton: Button
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        resetButton = findViewById(R.id.resetButton)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reset Password"
    }

    private fun setupListeners() {
        resetButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val identifier = intent.getStringExtra("identifier") ?: ""

            if (validateInput(newPassword, confirmPassword)) {
                resetPassword(identifier, newPassword)
            }
        }
    }

    private fun validateInput(newPassword: String, confirmPassword: String): Boolean {
        val (passwordValid, passwordMessage) = ValidationUtils.isStrongPassword(newPassword)
        if (!passwordValid) {
            newPasswordEditText.error = passwordMessage
            newPasswordEditText.requestFocus()
            return false
        }

        if (newPassword != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            confirmPasswordEditText.requestFocus()
            return false
        }

        return true
    }

    private fun resetPassword(identifier: String, newPassword: String) {
        resetButton.isEnabled = false
        resetButton.text = "Resetting Password..."

        lifecycleScope.launch {
            try {
                // Find user by identifier (email, phone, or collegeId)
                val user = userDao.getUserByIdentifier(identifier)

                if (user != null) {
                    // Check if this is for password change (from ChangePasswordActivity)
                    val prefs = getSharedPreferences("temp_password_change", MODE_PRIVATE)
                    val tempNewPassword = prefs.getString("new_password", null)

                    val finalPassword = if (tempNewPassword != null) {
                        // This is a password change, use the temporary password
                        tempNewPassword
                    } else {
                        // This is a forgot password reset, use the new password from the form
                        newPassword
                    }

                    // Update the user's password
                    val updatedUser = user.copy(password = finalPassword)
                    userDao.update(updatedUser)

                    // Clear temporary password if it exists
                    prefs.edit().clear().apply()

                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Password reset successful! You can now login with your new password.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Go back to login
                    finish()
                } else {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "User not found. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Error resetting password: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                resetButton.isEnabled = true
                resetButton.text = "Reset Password"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}