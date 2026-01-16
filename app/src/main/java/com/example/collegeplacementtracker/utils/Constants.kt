package com.example.collegeplacementtracker.utils

/**
 * Centralized constants for the application
 *
 * This object contains all application-wide constants to avoid magic numbers/strings
 * throughout the codebase and improve maintainability.
 */
object Constants {
    // ==================== APP INFO ====================
    const val APP_NAME = "College Placement Tracker"
    const val APP_VERSION = "3.0"

    // ==================== DATABASE ====================
    const val DATABASE_NAME = "placement_tracker_db"
    const val DATABASE_VERSION = 1

    // ==================== AUTHENTICATION ====================
    const val MAX_LOGIN_ATTEMPTS = 5
    const val SESSION_TIMEOUT_MINUTES = 30L
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_PASSWORD_LENGTH = 50

    // ==================== VALIDATION ====================
    const val MIN_CGPA = 0.0
    const val MAX_CGPA = 10.0
    const val MIN_PACKAGE_AMOUNT = 0.0
    const val MAX_PACKAGE_AMOUNT = 100.0 // 100 LPA max

    // ==================== PACKAGE RANGES ====================
    const val PACKAGE_RANGE_LOW = 5.0
    const val PACKAGE_RANGE_MID = 10.0

    // ==================== APPLICATION STATUS ====================
    const val STATUS_PENDING = "PENDING"
    const val STATUS_SHORTLISTED = "SHORTLISTED"
    const val STATUS_REJECTED = "REJECTED"
    const val STATUS_SELECTED = "SELECTED"
    const val STATUS_WITHDRAWN = "WITHDRAWN"

    // ==================== USER ROLES ====================
    const val ROLE_STUDENT = "STUDENT"
    const val ROLE_HOD = "HOD"
    const val ROLE_TPO = "TPO"

    // ==================== NOTIFICATION CHANNELS ====================
    const val CHANNEL_APPLICATIONS = "application_updates"
    const val CHANNEL_COMPANIES = "new_companies"
    const val CHANNEL_DEADLINES = "deadline_reminders"
    const val CHANNEL_APPROVALS = "approval_requests"

    // Notification Channel Names (for display)
    const val CHANNEL_NAME_APPLICATIONS = "Application Updates"
    const val CHANNEL_NAME_COMPANIES = "New Companies"
    const val CHANNEL_NAME_DEADLINES = "Deadline Reminders"
    const val CHANNEL_NAME_APPROVALS = "Approval Requests"

    // ==================== SHARED PREFERENCES KEYS ====================
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_USER_ROLE = "user_role"
    const val PREF_USER_NAME = "user_name"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    const val PREF_LOGIN_ATTEMPTS = "login_attempts"
    const val PREF_LAST_LOGIN = "last_login"
    const val PREF_SESSION_START = "session_start"
    const val PREF_BIOMETRIC_ENABLED = "biometric_enabled"

    // ==================== DATE FORMATS ====================
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy"
    const val DATE_FORMAT_FULL = "dd MMM yyyy, hh:mm a"
    const val DATE_FORMAT_STORAGE = "yyyy-MM-dd"
    const val DATE_FORMAT_TIME_ONLY = "hh:mm a"
    const val DATE_FORMAT_DATE_TIME = "dd/MM/yyyy HH:mm"

    // ==================== REQUEST CODES ====================
    const val REQUEST_CODE_IMAGE_PICK = 1001
    const val REQUEST_CODE_DOCUMENT_PICK = 1002
    const val REQUEST_CODE_CAMERA = 1003
    const val REQUEST_CODE_BIOMETRIC = 1004

    // ==================== FILE LIMITS ====================
    const val MAX_RESUME_SIZE_MB = 5L
    const val MAX_IMAGE_SIZE_MB = 2L
    const val MAX_RESUME_SIZE_BYTES = MAX_RESUME_SIZE_MB * 1024 * 1024
    const val MAX_IMAGE_SIZE_BYTES = MAX_IMAGE_SIZE_MB * 1024 * 1024

    // ==================== PAGINATION ====================
    const val PAGE_SIZE = 20
    const val INITIAL_LOAD_SIZE = 20

    // ==================== ANIMATION DURATIONS ====================
    const val ANIMATION_DURATION_SHORT = 200L
    const val ANIMATION_DURATION_MEDIUM = 300L
    const val ANIMATION_DURATION_LONG = 500L

    // ==================== DEADLINE URGENCY DAYS ====================
    const val DEADLINE_URGENT_DAYS = 3
    const val DEADLINE_WARNING_DAYS = 7

    // ==================== COMPANY TYPES ====================
    const val COMPANY_TYPE_SERVICE = "SERVICE"
    const val COMPANY_TYPE_PRODUCT = "PRODUCT"
    const val COMPANY_TYPE_BOTH = "BOTH"

    // ==================== BRANCHES ====================
    val BRANCHES = listOf(
        "CSE", "IT", "ECE", "EEE", "ME", "CE", "AE", "BT", "CHE"
    )

    // ==================== SKILLS ====================
    val COMMON_SKILLS = listOf(
        "Java", "Kotlin", "Python", "C++", "JavaScript", "React", "Angular",
        "Android Development", "iOS Development", "Machine Learning", "Data Science",
        "Cloud Computing", "AWS", "Azure", "DevOps", "Git", "SQL", "NoSQL"
    )
}

