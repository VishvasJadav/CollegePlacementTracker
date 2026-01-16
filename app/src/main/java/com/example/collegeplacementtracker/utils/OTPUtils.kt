package com.example.collegeplacementtracker.utils

import android.content.Context
import kotlin.random.Random

object OTPUtils {
    private const val PREF_NAME = "otp_preferences"
    private const val OTP_KEY = "otp_code"
    private const val OTP_EXPIRY_KEY = "otp_expiry"
    private const val OTP_IDENTIFIER_KEY = "otp_identifier"

    private const val OTP_LENGTH = 6
    private const val OTP_EXPIRY_TIME = 5 * 60 * 1000L // 5 minutes in milliseconds

    /**
     * Generate a random OTP code
     */
    fun generateOTP(): String {
        val otp = StringBuilder()
        repeat(OTP_LENGTH) {
            otp.append(Random.nextInt(0, 10))
        }
        return otp.toString()
    }

    /**
     * Store OTP with expiry time and identifier
     */
    fun storeOTP(context: Context, identifier: String, otp: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val expiryTime = System.currentTimeMillis() + OTP_EXPIRY_TIME

        prefs.edit()
            .putString(OTP_KEY, otp)
            .putLong(OTP_EXPIRY_KEY, expiryTime)
            .putString(OTP_IDENTIFIER_KEY, identifier)
            .apply()
    }

    /**
     * Verify OTP code
     */
    fun verifyOTP(context: Context, identifier: String, otp: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val storedOTP = prefs.getString(OTP_KEY, null)
        val expiryTime = prefs.getLong(OTP_EXPIRY_KEY, 0)
        val storedIdentifier = prefs.getString(OTP_IDENTIFIER_KEY, null)

        // Check if OTP exists, matches, is not expired, and is for the same identifier
        return storedOTP != null &&
                storedOTP == otp &&
                System.currentTimeMillis() < expiryTime &&
                storedIdentifier == identifier
    }

    /**
     * Check if OTP is expired
     */
    fun isOTPExpired(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val expiryTime = prefs.getLong(OTP_EXPIRY_KEY, 0)
        return System.currentTimeMillis() >= expiryTime
    }

    /**
     * Get the identifier for which OTP was generated
     */
    fun getOTPIdentifier(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(OTP_IDENTIFIER_KEY, null)
    }

    /**
     * Clear stored OTP
     */
    fun clearOTP(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(OTP_KEY)
            .remove(OTP_EXPIRY_KEY)
            .remove(OTP_IDENTIFIER_KEY)
            .apply()
    }

    /**
     * Send OTP (simulated - in real app this would call an API)
     */
    fun sendOTP(context: Context, identifier: String): Boolean {
        try {
            val otp = generateOTP()
            storeOTP(context, identifier, otp)

            // In a real application, this would send the OTP via SMS or email
            // For this local app, we're just storing it in preferences

            // Simulate sending OTP (logging for demonstration)
            println("OTP sent to $identifier: $otp")

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}