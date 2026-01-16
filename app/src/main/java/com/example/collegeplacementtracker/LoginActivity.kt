package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.NotificationHelper
import com.example.collegeplacementtracker.utils.SessionManager
import com.example.collegeplacementtracker.utils.UIHelper

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var identifierEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao

    private var loginAttempts = 0
    private val maxLoginAttempts = 5
    // Write a message to the database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager.getInstance(this)

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()

        initializeViews()
        setupListeners()
        setupRealtimeValidation()
    }

    private fun initializeViews() {
        identifierEditText = findViewById(R.id.identifierEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupTextView = findViewById(R.id.signupTextView)
        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }
    }

    private fun setupListeners() {
        loginButton.setOnClickListener {
            val identifier = identifierEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInput(identifier, password)) {
                performLogin(identifier, password)
            }
        }

        signupTextView.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // Forgot Password click listener
        findViewById<TextView>(R.id.forgotPasswordTextView).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun setupRealtimeValidation() {
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty() && s.toString().length < 6) {
                    passwordEditText.error = "Password must be at least 6 characters"
                }
            }
        })
    }

    private fun validateInput(identifier: String, password: String): Boolean {
        if (identifier.isEmpty()) {
            identifierEditText.error = "Email, Phone, or College ID is required"
            identifierEditText.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            passwordEditText.requestFocus()
            return false
        }

        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            passwordEditText.requestFocus()
            return false
        }

        // Check login attempts
        if (loginAttempts >= maxLoginAttempts) {
            UIHelper.showError(this, "Too many failed login attempts. Please try again later.")
            return false
        }

        return true
    }

    private fun performLogin(identifier: String, password: String) {
        // Disable button and show loading
        loginButton.isEnabled = false
        loginButton.text = "Logging in..."

        // Add a slight delay for better UX
        lifecycleScope.launch {
            delay(300)

            try {
                // First, find the user by identifier (email, phone, or collegeId)
                val user = userDao.getUserByIdentifier(identifier)

                if (user != null) {
                    // Check if password matches
                    if (user.password == password) {
                        if (user.isActive) {
                            // Update last login
                            userDao.updateLastLogin(user.id, System.currentTimeMillis())

                            // Save session
                            sessionManager.saveUserSession(user)

                            // Show success message
                            UIHelper.showSuccess(
                                this@LoginActivity,
                                "Welcome back, ${user.fullName}!"
                            )

                            // Small delay before navigation for better UX
                            delay(500)

                            // Navigate to dashboard
                            navigateToDashboard()
                            finish()
                        } else {
                            UIHelper.showError(
                                this@LoginActivity,
                                "Your account has been deactivated. Please contact the administrator."
                            )
                            loginAttempts++
                        }
                    } else {
                        // Password doesn't match
                        loginAttempts++
                        val attemptsLeft = maxLoginAttempts - loginAttempts

                        if (attemptsLeft > 0) {
                            UIHelper.showError(
                                this@LoginActivity,
                                "Invalid credentials. $attemptsLeft attempts remaining."
                            )
                        } else {
                            UIHelper.showError(
                                this@LoginActivity,
                                "Maximum login attempts reached. Please try again later."
                            )
                        }
                    }
                } else {
                    // User not found
                    loginAttempts++
                    val attemptsLeft = maxLoginAttempts - loginAttempts

                    if (attemptsLeft > 0) {
                        UIHelper.showError(
                            this@LoginActivity,
                            "Invalid credentials. $attemptsLeft attempts remaining."
                        )
                    } else {
                        UIHelper.showError(
                            this@LoginActivity,
                            "Maximum login attempts reached. Please try again later."
                        )
                    }
                }
            } catch (e: Exception) {
                UIHelper.showError(
                    this@LoginActivity,
                    "Login failed: ${e.message}"
                )
            } finally {
                loginButton.isEnabled = true
                loginButton.text = "Login"
            }
        }
    }

    private fun navigateToDashboard() {
        val intent = when (sessionManager.getUserRole()) {
            UserRole.STUDENT -> Intent(this, StudentDashboardTabsActivity::class.java)
            UserRole.HOD -> Intent(this, HODDashboardTabsActivity::class.java)
            UserRole.TPO -> Intent(this, TPODashboardActivityEnhanced::class.java)
            else -> {
                UIHelper.showError(this, "Invalid user role")
                return
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Add smooth transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onBackPressed() {
        // Clear login attempts when user goes back
        loginAttempts = 0
        super.onBackPressed()
    }
}
