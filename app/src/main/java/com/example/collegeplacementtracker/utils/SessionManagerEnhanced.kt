package com.example.collegeplacementtracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Enhanced Session Manager with:
 * - Encrypted storage using EncryptedSharedPreferences
 * - Session timeout (30 minutes of inactivity)
 * - State flow for reactive updates
 * - Biometric authentication support
 * - Session token generation
 *
 * Usage:
 * ```
 * val sessionManager = SessionManagerEnhanced.getInstance(context)
 * sessionManager.createSession(userId, userRole, email)
 * if (sessionManager.isSessionValid()) {
 *     // Session is active
 * }
 * ```
 */
class SessionManagerEnhanced private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: SessionManagerEnhanced? = null

        private const val SESSION_TIMEOUT = 30 * 60 * 1000L // 30 minutes
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_SESSION_TOKEN = "session_token"
        private const val KEY_LAST_ACTIVITY = "last_activity"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_REMEMBER_ME = "remember_me"

        fun getInstance(context: Context): SessionManagerEnhanced {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManagerEnhanced(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val encryptedPrefs: SharedPreferences = try {
        EncryptedSharedPreferences.create(
            "secure_session_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback to regular SharedPreferences if encryption fails
        context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
    }

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.LoggedOut)
    val sessionState: StateFlow<SessionState> = _sessionState

    sealed class SessionState {
        object LoggedOut : SessionState()
        data class LoggedIn(val userId: Long, val userRole: String, val email: String) :
            SessionState()

        object Expired : SessionState()
    }

    init {
        // Initialize session state from saved data
        if (isLoggedIn()) {
            val userId = getUserId()
            val userRole = getUserRole() ?: ""
            val email = getUserEmail() ?: ""
            _sessionState.value = SessionState.LoggedIn(userId, userRole, email)
        }
    }

    /**
     * Create a new session for the user
     */
    fun createSession(userId: Long, userRole: String, email: String, rememberMe: Boolean = false) {
        val sessionToken = generateSessionToken()
        encryptedPrefs.edit().apply {
            putLong(KEY_USER_ID, userId)
            putString(KEY_USER_ROLE, userRole)
            putString(KEY_USER_EMAIL, email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_SESSION_TOKEN, sessionToken)
            putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            apply()
        }
        _sessionState.value = SessionState.LoggedIn(userId, userRole, email)
    }

    /**
     * Update last activity timestamp
     * Call this on every user interaction to keep session alive
     */
    fun updateActivity() {
        if (isLoggedIn()) {
            encryptedPrefs.edit()
                .putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
                .apply()
        }
    }

    /**
     * Check if session is still valid (not expired)
     * @return true if session is valid, false if expired
     */
    fun isSessionValid(): Boolean {
        if (!isLoggedIn()) return false

        // If remember me is enabled, skip timeout check
        if (isRememberMeEnabled()) {
            updateActivity()
            return true
        }

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

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return encryptedPrefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Get current user ID
     */
    fun getUserId(): Long {
        return encryptedPrefs.getLong(KEY_USER_ID, -1)
    }

    /**
     * Get current user role
     */
    fun getUserRole(): String? {
        return encryptedPrefs.getString(KEY_USER_ROLE, null)
    }

    /**
     * Get current user email
     */
    fun getUserEmail(): String? {
        return encryptedPrefs.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Get session token
     */
    fun getSessionToken(): String? {
        return encryptedPrefs.getString(KEY_SESSION_TOKEN, null)
    }

    /**
     * Check if biometric authentication is enabled
     */
    fun isBiometricEnabled(): Boolean {
        return encryptedPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    /**
     * Enable/disable biometric authentication
     */
    fun setBiometricEnabled(enabled: Boolean) {
        encryptedPrefs.edit()
            .putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            .apply()
    }

    /**
     * Check if remember me is enabled
     */
    fun isRememberMeEnabled(): Boolean {
        return encryptedPrefs.getBoolean(KEY_REMEMBER_ME, false)
    }

    /**
     * Clear current session and logout user
     */
    fun clearSession() {
        encryptedPrefs.edit().clear().apply()
        _sessionState.value = SessionState.LoggedOut
    }

    /**
     * Generate a unique session token
     */
    private fun generateSessionToken(): String {
        return java.util.UUID.randomUUID().toString()
    }

    /**
     * Get total session duration
     */
    fun getSessionDuration(): Long {
        val lastActivity = encryptedPrefs.getLong(KEY_LAST_ACTIVITY, 0)
        return System.currentTimeMillis() - lastActivity
    }

    /**
     * Get remaining session time before expiry
     */
    fun getRemainingSessionTime(): Long {
        if (isRememberMeEnabled()) return Long.MAX_VALUE

        val lastActivity = encryptedPrefs.getLong(KEY_LAST_ACTIVITY, 0)
        val elapsed = System.currentTimeMillis() - lastActivity
        return maxOf(0, SESSION_TIMEOUT - elapsed)
    }

    /**
     * Get remaining session time in minutes
     */
    fun getRemainingSessionTimeMinutes(): Int {
        return (getRemainingSessionTime() / (60 * 1000)).toInt()
    }
}
