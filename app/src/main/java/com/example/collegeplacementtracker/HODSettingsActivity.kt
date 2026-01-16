package com.example.collegeplacementtracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.SessionManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class HODSettingsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao

    private lateinit var hodNameEditText: TextInputEditText
    private lateinit var hodEmailEditText: TextInputEditText
    private lateinit var hodPhoneEditText: TextInputEditText
    private lateinit var saveSettingsButton: Button
    private lateinit var changePasswordButton: Button

    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hod_settings)

        // Setup action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        // Initialize
        sessionManager = SessionManager.getInstance(this)
        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        userDao = database.userDao()

        // Initialize views
        hodNameEditText = findViewById(R.id.hodNameEditText)
        hodEmailEditText = findViewById(R.id.hodEmailEditText)
        hodPhoneEditText = findViewById(R.id.hodPhoneEditText)
        saveSettingsButton = findViewById(R.id.saveSettingsButton)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        loadUserData()
        setupButtons()
    }

    private fun loadUserData() {
        val userId = sessionManager.getUserId()

        lifecycleScope.launch {
            userDao.getUserById(userId).observe(this@HODSettingsActivity) { user ->
                user?.let {
                    currentUser = it
                    hodNameEditText.setText(it.fullName)  // ← Changed from 'name' to 'fullName'
                    hodEmailEditText.setText(it.email)
                    hodPhoneEditText.setText(it.phone)     // ← Changed from 'phoneNumber' to 'phone'
                }
            }
        }
    }

    private fun setupButtons() {
        saveSettingsButton.setOnClickListener {
            saveSettings()
        }

        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun saveSettings() {
        val fullName = hodNameEditText.text.toString().trim()
        val phone = hodPhoneEditText.text.toString().trim()

        if (fullName.isEmpty()) {
            hodNameEditText.error = "Name cannot be empty"
            return
        }

        lifecycleScope.launch {
            try {
                currentUser?.let { user ->
                    val updatedUser = user.copy(
                        fullName = fullName,  // ← Changed from 'name' to 'fullName'
                        phone = phone         // ← Changed from 'phoneNumber' to 'phone'
                    )
                    userDao.update(updatedUser)

                    // Update session
                    sessionManager.saveUserName(fullName)

                    Toast.makeText(
                        this@HODSettingsActivity,
                        "Settings saved successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@HODSettingsActivity,
                    "Error saving settings: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reset_password, null)
        val newPasswordEditText =
            dialogView.findViewById<TextInputEditText>(R.id.newPasswordEditText)
        val confirmPasswordEditText =
            dialogView.findViewById<TextInputEditText>(R.id.confirmPasswordEditText)

        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setMessage("Enter your new password")
            .setView(dialogView)
            .setPositiveButton("Change") { dialog, _ ->
                val newPassword = newPasswordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                when {
                    newPassword.isEmpty() -> {
                        Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                    }

                    newPassword.length < 6 -> {
                        Toast.makeText(
                            this,
                            "Password must be at least 6 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    newPassword != confirmPassword -> {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        changePassword(newPassword)
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun changePassword(newPassword: String) {
        lifecycleScope.launch {
            try {
                currentUser?.let { user ->
                    val updatedUser = user.copy(password = newPassword)
                    userDao.update(updatedUser)

                    Toast.makeText(
                        this@HODSettingsActivity,
                        "Password changed successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@HODSettingsActivity,
                    "Error changing password: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
