package com.example.collegeplacementtracker

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "PlacementTrackerPrefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_BRANCH = "user_branch"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveUserSession(user: User) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, user.id)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_NAME, user.fullName)
            putString(KEY_USER_ROLE, user.role)
            putString(KEY_USER_BRANCH, user.branch)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    fun getUserBranch(): String? {
        return prefs.getString(KEY_USER_BRANCH, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    // ADD THIS METHOD
    fun clearSession() {
        prefs.edit().apply {
            clear()
            apply()
        }
    }

    fun isStudent(): Boolean {
        return getUserRole() == UserRole.STUDENT
    }

    fun isHOD(): Boolean {
        return getUserRole() == UserRole.HOD
    }

    fun isTPO(): Boolean {
        return getUserRole() == UserRole.TPO
    }

    fun getCurrentUser(): User? {
        return if (isLoggedIn()) {
            User(
                id = getUserId(),
                email = getUserEmail() ?: "",
                password = "", // Password is not stored in session for security
                fullName = getUserName() ?: "",
                phone = "", // Phone is not stored in session
                role = getUserRole() ?: "",
                branch = getUserBranch(),
                collegeId = null, // College ID is not stored in session
                rollNumber = null, // Roll number is not stored in session
                cgpa = null // CGPA is not stored in session
            )
        } else {
            null
        }
    }

    fun saveUserName(name: String) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, name)
            apply()
        }
    }


    fun saveNotificationPreference(enabled: Boolean) {
        prefs.edit().apply {
            putBoolean("notifications_enabled", enabled)
            apply()
        }
    }

    fun getNotificationPreference(): Boolean {
        return prefs.getBoolean("notifications_enabled", true)
    }
}