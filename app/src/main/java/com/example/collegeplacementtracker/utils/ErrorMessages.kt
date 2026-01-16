package com.example.collegeplacementtracker.utils

/**
 * Centralized error messages for consistent error handling across the application
 *
 * This object provides user-friendly error messages that can be displayed to users
 * and logged for debugging purposes.
 */
object ErrorMessages {
    // ==================== NETWORK ERRORS ====================
    const val NETWORK_ERROR = "Network error. Please check your internet connection."
    const val TIMEOUT_ERROR = "Request timed out. Please try again."
    const val CONNECTION_ERROR = "Unable to connect. Please check your internet connection."
    const val SERVER_ERROR = "Server error. Please try again later."

    // ==================== AUTHENTICATION ERRORS ====================
    const val LOGIN_FAILED = "Login failed. Please check your credentials."
    const val USER_NOT_FOUND = "User not found"
    const val INVALID_CREDENTIALS = "Invalid email or password"
    const val MAX_ATTEMPTS_REACHED = "Maximum login attempts reached. Please try again later."
    const val SESSION_EXPIRED = "Your session has expired. Please login again."
    const val ACCOUNT_LOCKED = "Account is locked. Please contact administrator."

    // ==================== VALIDATION ERRORS ====================
    const val INVALID_EMAIL = "Please enter a valid email address"
    const val INVALID_PASSWORD = "Password must be at least 6 characters"
    const val PASSWORD_MISMATCH = "Passwords do not match"
    const val INVALID_CGPA = "CGPA must be between 0.0 and 10.0"
    const val INVALID_PACKAGE = "Package amount must be greater than 0"
    const val INVALID_PHONE = "Please enter a valid phone number"
    const val INVALID_ROLL_NUMBER = "Please enter a valid roll number"
    const val REQUIRED_FIELD = "This field is required"
    const val INVALID_DATE = "Please select a valid date"
    const val INVALID_INPUT = "Please check your input and try again."

    // ==================== DATABASE ERRORS ====================
    const val SAVE_FAILED = "Failed to save. Please try again."
    const val DELETE_FAILED = "Failed to delete. Please try again."
    const val UPDATE_FAILED = "Failed to update. Please try again."
    const val LOAD_FAILED = "Failed to load data. Please try again."
    const val DATABASE_ERROR = "Database error occurred. Please try again."

    // ==================== APPLICATION ERRORS ====================
    const val DUPLICATE_APPLICATION = "You have already applied to this company."
    const val NOT_ELIGIBLE = "You are not eligible for this company."
    const val DEADLINE_PASSED = "Application deadline has passed."
    const val POSITIONS_FULL = "All positions for this company have been filled."
    const val APPLICATION_NOT_FOUND = "Application not found"
    const val CANNOT_WITHDRAW = "Cannot withdraw application in current status"

    // ==================== COMPANY ERRORS ====================
    const val COMPANY_NOT_FOUND = "Company not found"
    const val COMPANY_ALREADY_EXISTS = "Company with this name already exists"
    const val INVALID_COMPANY_NAME = "Please enter a valid company name"

    // ==================== FILE ERRORS ====================
    const val FILE_TOO_LARGE = "File size exceeds the maximum limit"
    const val INVALID_FILE_TYPE = "Invalid file type. Please select a valid file."
    const val FILE_UPLOAD_FAILED = "Failed to upload file. Please try again."
    const val FILE_DELETE_FAILED = "Failed to delete file. Please try again."

    // ==================== PERMISSION ERRORS ====================
    const val PERMISSION_DENIED = "Permission denied. Please grant the required permission."
    const val STORAGE_PERMISSION_REQUIRED = "Storage permission is required to access files"
    const val CAMERA_PERMISSION_REQUIRED = "Camera permission is required to take photos"

    // ==================== GENERAL ERRORS ====================
    const val UNKNOWN_ERROR = "An unexpected error occurred"
    const val OPERATION_FAILED = "Operation failed. Please try again."
    const val RETRY_LATER = "Please try again later"
    const val NO_DATA_FOUND = "No data found"

    // ==================== SUCCESS MESSAGES ====================
    const val LOGIN_SUCCESS = "Login successful"
    const val SAVE_SUCCESS = "Saved successfully"
    const val UPDATE_SUCCESS = "Updated successfully"
    const val DELETE_SUCCESS = "Deleted successfully"
    const val APPLICATION_SUBMITTED = "Application submitted successfully"
    const val PASSWORD_CHANGED = "Password changed successfully"
    const val PROFILE_UPDATED = "Profile updated successfully"

    /**
     * Get user-friendly error message from exception
     */
    fun getErrorMessage(error: Throwable?): String {
        return when (error) {
            is java.io.IOException -> NETWORK_ERROR
            is java.net.SocketTimeoutException -> TIMEOUT_ERROR
            is java.net.UnknownHostException -> CONNECTION_ERROR
            is android.database.sqlite.SQLiteException -> DATABASE_ERROR
            is java.lang.IllegalArgumentException -> INVALID_INPUT
            is java.lang.NullPointerException -> UNKNOWN_ERROR
            else -> error?.localizedMessage ?: UNKNOWN_ERROR
        }
    }
}

