package com.example.collegeplacementtracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionManager private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: SessionManager? = null

        private const val SESSION_TIMEOUT = 30 * 60 * 1000L // 30 minutes
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_SESSION_TOKEN = "session_token"
        private const val KEY_LAST_ACTIVITY = "last_activity"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        "secure_session_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.LoggedOut)
    val sessionState: StateFlow<SessionState> = _sessionState

    sealed class SessionState {
        object LoggedOut : SessionState()
        data class LoggedIn(val userId: Long, val userRole: String, val email: String) :
            SessionState()

        object Expired : SessionState()
    }

    fun createSession(userId: Long, userRole: String, email: String) {
        val sessionToken = generateSessionToken()
        encryptedPrefs.edit().apply {
            putLong(KEY_USER_ID, userId)
            putString(KEY_USER_ROLE, userRole)
            putString(KEY_USER_EMAIL, email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_SESSION_TOKEN, sessionToken)
            putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
            apply()
        }
        _sessionState.value = SessionState.LoggedIn(userId, userRole, email)
    }

    fun updateActivity() {
        if (isLoggedIn()) {
            encryptedPrefs.edit()
                .putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
                .apply()
        }
    }

    fun isSessionValid(): Boolean {
        if (!isLoggedIn()) return false

        val lastActivity = encryptedPrefs.getLong(KEY_LAST_ACTIVITY, 0)
        val currentTime = System.currentTimeMillis()

        return if (currentTime - lastActivity > SESSION_TIMEOUT) {
            clearSession()
            _sessionState.value = SessionState.Expired
            false
        } else {
            updateActivity()
            true
        }
    }

    fun isLoggedIn(): Boolean {
        return encryptedPrefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): Long {
        return encryptedPrefs.getLong(KEY_USER_ID, -1)
    }

    fun getUserRole(): String? {
        return encryptedPrefs.getString(KEY_USER_ROLE, null)
    }

    fun getUserEmail(): String? {
        return encryptedPrefs.getString(KEY_USER_EMAIL, null)
    }

    fun getSessionToken(): String? {
        return encryptedPrefs.getString(KEY_SESSION_TOKEN, null)
    }

    fun isBiometricEnabled(): Boolean {
        return encryptedPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        encryptedPrefs.edit()
            .putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            .apply()
    }

    fun clearSession() {
        encryptedPrefs.edit().clear().apply()
        _sessionState.value = SessionState.LoggedOut
    }

    private fun generateSessionToken(): String {
        return java.util.UUID.randomUUID().toString()
    }

    fun getSessionDuration(): Long {
        val lastActivity = encryptedPrefs.getLong(KEY_LAST_ACTIVITY, 0)
        return System.currentTimeMillis() - lastActivity
    }

    fun getRemainingSessionTime(): Long {
        val lastActivity = encryptedPrefs.getLong(KEY_LAST_ACTIVITY, 0)
        val elapsed = System.currentTimeMillis() - lastActivity
        return maxOf(0, SESSION_TIMEOUT - elapsed)
    }

    // Additional helper methods
    fun getUserName(): String? {
        // Get user name from preferences - we need to store it
        return encryptedPrefs.getString("user_name", null)
    }

    fun getUserBranch(): String? {
        return encryptedPrefs.getString("user_branch", null)
    }

    fun isHOD(): Boolean {
        return getUserRole() == "HOD"
    }

    fun isTPO(): Boolean {
        return getUserRole() == "TPO"
    }

    fun logout() {
        clearSession()
    }

    fun saveUserSession(user: com.example.collegeplacementtracker.User) {
        createSession(user.id.toLong(), user.role, user.email)
        encryptedPrefs.edit().apply {
            putString("user_name", user.fullName)
            putString("user_branch", user.branch)
            apply()
        }
    }

    fun saveUserName(name: String) {
        encryptedPrefs.edit()
            .putString("user_name", name)
            .apply()
    }

    fun saveNotificationPreference(enabled: Boolean) {
        encryptedPrefs.edit()
            .putBoolean("notification_enabled", enabled)
            .apply()
    }
}