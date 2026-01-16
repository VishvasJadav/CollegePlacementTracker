package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.collegeplacementtracker.utils.SessionManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class HODProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userDao: UserDao
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hod_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager.getInstance(requireContext())
        val database = AppDatabaseNew.getDatabase(requireContext(), lifecycleScope)
        userDao = database.userDao()

        loadUserData(view)
        setupClickListeners(view)
    }

    private fun loadUserData(view: View) {
        lifecycleScope.launch {
            try {
                val email: String? = sessionManager.getUserEmail()
                if (email != null) {
                    currentUser = userDao.getUserByEmail(email)

                    view.post {
                        currentUser?.let { user ->
                            // Update profile header
                            view.findViewById<TextView>(R.id.profileNameTextView)?.text =
                                user.fullName
                            view.findViewById<TextView>(R.id.profileDepartmentTextView)?.text =
                                user.branch ?: "Not Set"
                            view.findViewById<TextView>(R.id.profileEmailTextView)?.text =
                                user.email

                            // Set initial
                            val initial =
                                user.fullName.firstOrNull()?.toString()?.uppercase() ?: "H"
                            view.findViewById<TextView>(R.id.profileInitialLargeTextView)?.text =
                                initial

                            // Fill form fields
                            view.findViewById<TextInputEditText>(R.id.fullNameEditText)
                                ?.setText(user.fullName)
                            view.findViewById<TextInputEditText>(R.id.emailEditText)
                                ?.setText(user.email)
                            view.findViewById<TextInputEditText>(R.id.phoneEditText)
                                ?.setText(user.phone)
                            view.findViewById<TextInputEditText>(R.id.departmentEditText)
                                ?.setText(user.branch ?: "")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupClickListeners(view: View) {
        // Save Profile Button
        view.findViewById<Button>(R.id.saveProfileButton)?.setOnClickListener {
            saveProfile(view)
        }

        // Change Password
        view.findViewById<LinearLayout>(R.id.changePasswordLayout)?.setOnClickListener {
            startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
        }

        // Notifications Toggle
        view.findViewById<Switch>(R.id.notificationsSwitch)
            ?.setOnCheckedChangeListener { _, isChecked ->
                // Save notification preference
                sessionManager.saveNotificationPreference(isChecked)
            }

        // Logout
        view.findViewById<LinearLayout>(R.id.logoutLayout)?.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun saveProfile(view: View) {
        lifecycleScope.launch {
            try {
                val name =
                    view.findViewById<TextInputEditText>(R.id.fullNameEditText)?.text.toString()
                val phone =
                    view.findViewById<TextInputEditText>(R.id.phoneEditText)?.text.toString()

                currentUser?.let { user ->
                    val updatedUser = user.copy(fullName = name, phone = phone)
                    userDao.update(updatedUser)

                    // Update session
                    sessionManager.saveUserName(name)

                    view.post {
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Profile updated successfully ✓",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()

                        // Reload data
                        loadUserData(view)
                    }
                }
            } catch (e: Exception) {
                view.post {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Failed to update profile",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun showLogoutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        sessionManager.clearSession()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
