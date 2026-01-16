package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.OTPUtils
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var resetButton: Button
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize database
        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        resetButton = findViewById(R.id.resetButton)

        // Setup action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reset Password"

        // Setup button click
        resetButton.setOnClickListener {
            val identifier = emailEditText.text.toString().trim()

            if (identifier.isEmpty()) {
                emailEditText.error = "Email, Phone, or College ID is required"
                return@setOnClickListener
            }

            verifyEmailAndReset(identifier)
        }
    }

    private fun verifyEmailAndReset(identifier: String) {
        lifecycleScope.launch {
            try {
                // Check if identifier exists (email, phone, or collegeId)
                val user = userDao.getUserByIdentifier(identifier)

                if (user != null) {
                    // Identifier exists, send OTP and navigate to OTP verification
                    if (sendOTPAndNavigate(identifier)) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "OTP sent successfully. Please check your device.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Failed to send OTP. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Account not found. Please check your email, phone, or college ID.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendOTPAndNavigate(identifier: String): Boolean {
        // Send OTP using OTPUtils
        val success = OTPUtils.sendOTP(this, identifier)

        if (success) {
            // Navigate to OTP verification screen
            val intent = Intent(this, OTPVerificationActivity::class.java)
            intent.putExtra("identifier", identifier)
            startActivity(intent)
            finish()
        }

        return success
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
