package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.SessionManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class TPOSettingsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var database: AppDatabaseNew

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tpo_settings)

        sessionManager = SessionManager.getInstance(this)
        database = AppDatabaseNew.getDatabase(this, lifecycleScope)

        setupToolbar()
        loadProfile()
        setupListeners()
        loadSettings()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun loadProfile() {
        val name = sessionManager.getUserName() ?: "TPO"
        val email = sessionManager.getUserEmail() ?: "tpo@college.edu"

        findViewById<TextView>(R.id.profileNameTextView).text = name
        findViewById<TextView>(R.id.profileEmailTextView).text = email
        findViewById<TextView>(R.id.profileInitialTextView).text =
            name.firstOrNull()?.toString()?.uppercase() ?: "T"
    }

    private fun setupListeners() {
        // Edit Profile
        findViewById<Button>(R.id.editProfileButton).setOnClickListener {
            showEditProfileDialog()
        }

        // Notification Settings
        findViewById<SwitchCompat>(R.id.newApplicationsSwitch).setOnCheckedChangeListener { _, isChecked ->
            savePreference("notify_new_applications", isChecked)
            Toast.makeText(
                this, if (isChecked) "Notifications enabled" else "Notifications disabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<SwitchCompat>(R.id.interviewRemindersSwitch).setOnCheckedChangeListener { _, isChecked ->
            savePreference("notify_interview_reminders", isChecked)
        }

        findViewById<SwitchCompat>(R.id.emailNotificationsSwitch).setOnCheckedChangeListener { _, isChecked ->
            savePreference("notify_email", isChecked)
        }

        // Placement Settings
        findViewById<LinearLayout>(R.id.minimumCgpaLayout).setOnClickListener {
            showMinimumCgpaDialog()
        }

        findViewById<SwitchCompat>(R.id.autoApprovalSwitch).setOnCheckedChangeListener { _, isChecked ->
            savePreference("auto_approval", isChecked)
            Toast.makeText(
                this,
                if (isChecked) "Auto-approval enabled" else "Auto-approval disabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Account
        findViewById<LinearLayout>(R.id.changePasswordLayout).setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.aboutLayout).setOnClickListener {
            showAboutDialog()
        }

        // Logout
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("tpo_settings", MODE_PRIVATE)

        findViewById<SwitchCompat>(R.id.newApplicationsSwitch).isChecked =
            prefs.getBoolean("notify_new_applications", true)
        findViewById<SwitchCompat>(R.id.interviewRemindersSwitch).isChecked =
            prefs.getBoolean("notify_interview_reminders", true)
        findViewById<SwitchCompat>(R.id.emailNotificationsSwitch).isChecked =
            prefs.getBoolean("notify_email", false)
        findViewById<SwitchCompat>(R.id.autoApprovalSwitch).isChecked =
            prefs.getBoolean("auto_approval", false)

        val minCgpa = prefs.getFloat("minimum_cgpa", 6.0f)
        findViewById<TextView>(R.id.minimumCgpaValueTextView).text = minCgpa.toString()
    }

    private fun savePreference(key: String, value: Boolean) {
        getSharedPreferences("tpo_settings", MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val nameEditText = dialogView.findViewById<TextInputEditText>(R.id.nameEditText)
        val phoneEditText = dialogView.findViewById<TextInputEditText>(R.id.phoneEditText)

        nameEditText.setText(sessionManager.getUserName())

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameEditText.text.toString()
                if (newName.isNotBlank()) {
                    sessionManager.saveUserName(newName)
                    loadProfile()

                    // Update in database
                    lifecycleScope.launch {
                        val email: String? = sessionManager.getUserEmail()
                        if (email != null) {
                            val user = database.userDao().getUserByEmail(email)
                            user?.let {
                                val updated = it.copy(
                                    fullName = newName,
                                    phone = phoneEditText.text.toString()
                                )
                                database.userDao().update(updated)
                            }
                        }
                    }

                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMinimumCgpaDialog() {
        val cgpaOptions = arrayOf("5.0", "5.5", "6.0", "6.5", "7.0", "7.5", "8.0")
        val currentCgpa = getSharedPreferences("tpo_settings", MODE_PRIVATE)
            .getFloat("minimum_cgpa", 6.0f)
        val selectedIndex = cgpaOptions.indexOf(currentCgpa.toString()).takeIf { it >= 0 } ?: 2

        AlertDialog.Builder(this)
            .setTitle("Select Minimum CGPA")
            .setSingleChoiceItems(cgpaOptions, selectedIndex) { dialog, which ->
                val selectedCgpa = cgpaOptions[which].toFloat()
                getSharedPreferences("tpo_settings", MODE_PRIVATE)
                    .edit()
                    .putFloat("minimum_cgpa", selectedCgpa)
                    .apply()
                findViewById<TextView>(R.id.minimumCgpaValueTextView).text = selectedCgpa.toString()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("About College Placement Tracker")
            .setMessage(
                """
                Version: 1.0.0
                
                A comprehensive placement management system for colleges.
                
                Features:
                • Student Management
                • Company Management
                • Application Tracking
                • Interview Scheduling
                • Reports & Analytics
                
                © 2024 College Placement Tracker
            """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                sessionManager.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
