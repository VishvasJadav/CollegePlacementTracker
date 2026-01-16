package com.example.collegeplacementtracker.utils

import android.util.Patterns

object ValidationUtils {

    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Pair<Boolean, String> {
        return when {
            email.isEmpty() -> Pair(false, "Email is required")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                Pair(false, "Invalid email format")

            else -> Pair(true, "")
        }
    }

    /**
     * Validate phone number (Indian format)
     */
    fun isValidPhone(phone: String): Pair<Boolean, String> {
        return when {
            phone.isEmpty() -> Pair(false, "Phone number is required")
            !phone.matches(Regex("^[6-9]\\d{9}$")) ->
                Pair(false, "Invalid phone number. Must be 10 digits starting with 6-9")

            else -> Pair(true, "")
        }
    }

    /**
     * Validate roll number format
     */
    fun isValidRollNumber(rollNo: String): Pair<Boolean, String> {
        return when {
            rollNo.isEmpty() -> Pair(false, "Roll number is required")
            rollNo.length < 3 -> Pair(false, "Roll number too short")
            else -> Pair(true, "")
        }
    }

    /**
     * Validate CGPA
     */
    fun isValidCGPA(cgpa: Double): Pair<Boolean, String> {
        return when {
            cgpa < 0.0 -> Pair(false, "CGPA cannot be negative")
            cgpa > 10.0 -> Pair(false, "CGPA cannot exceed 10.0")
            else -> Pair(true, "")
        }
    }

    /**
     * Validate password strength
     */
    fun isStrongPassword(password: String): Pair<Boolean, String> {
        return when {
            password.isEmpty() -> Pair(false, "Password is required")
            password.length < 6 -> Pair(false, "Password must be at least 6 characters")
            password.length < 8 -> Pair(
                true,
                "Password is acceptable but consider using 8+ characters"
            )

            !password.any { it.isDigit() } ->
                Pair(true, "Strong password recommended: Add numbers")

            !password.any { it.isUpperCase() } ->
                Pair(true, "Strong password recommended: Add uppercase letters")

            !password.any { !it.isLetterOrDigit() } ->
                Pair(true, "Strong password recommended: Add special characters")

            else -> Pair(true, "Strong password ✓")
        }
    }

    /**
     * Validate package amount
     */
    fun isValidPackage(packageAmount: String): Pair<Boolean, String> {
        return try {
            val amount = packageAmount.toDouble()
            when {
                amount < 0 -> Pair(false, "Package amount cannot be negative")
                amount > 100 -> Pair(false, "Package amount seems unrealistic")
                else -> Pair(true, "")
            }
        } catch (e: NumberFormatException) {
            Pair(false, "Invalid package amount")
        }
    }

    /**
     * Validate company name
     */
    fun isValidCompanyName(name: String): Pair<Boolean, String> {
        return when {
            name.isEmpty() -> Pair(false, "Company name is required")
            name.length < 2 -> Pair(false, "Company name too short")
            name.length > 100 -> Pair(false, "Company name too long")
            else -> Pair(true, "")
        }
    }

    /**
     * Validate job role
     */
    fun isValidJobRole(role: String): Pair<Boolean, String> {
        return when {
            role.isEmpty() -> Pair(false, "Job role is required")
            role.length < 3 -> Pair(false, "Job role too short")
            else -> Pair(true, "")
        }
    }

    /**
     * Validate package amount (Double version)
     */
    fun isValidPackageAmount(packageAmount: Double): Pair<Boolean, String> {
        return when {
            packageAmount < 0 -> Pair(false, "Package amount cannot be negative")
            packageAmount > 100 -> Pair(false, "Package amount seems unrealistic")
            else -> Pair(true, "")
        }
    }
}
