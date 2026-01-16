package com.example.collegeplacementtracker.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val DATE_FORMAT = "dd/MM/yyyy"
    private const val DATE_TIME_FORMAT = "dd/MM/yyyy hh:mm a"
    private const val TIME_FORMAT = "hh:mm a"

    /**
     * Format timestamp to date string
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Format timestamp to date-time string
     */
    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Format timestamp to time string
     */
    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Get relative time string (e.g., "2 hours ago")
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000} minutes ago"
            diff < 86400_000 -> "${diff / 3600_000} hours ago"
            diff < 604800_000 -> "${diff / 86400_000} days ago"
            diff < 2592000_000 -> "${diff / 604800_000} weeks ago"
            else -> formatDate(timestamp)
        }
    }

    /**
     * Parse date string to timestamp
     */
    fun parseDate(dateString: String): Long? {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            sdf.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Check if date is in the past
     */
    fun isPastDate(dateString: String): Boolean {
        val timestamp = parseDate(dateString) ?: return false
        return timestamp < System.currentTimeMillis()
    }

    /**
     * Check if date is today
     */
    fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }

        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Get days until deadline
     */
    fun getDaysUntil(deadlineDate: String): Int {
        val deadline = parseDate(deadlineDate) ?: return 0
        val diff = deadline - System.currentTimeMillis()
        return (diff / 86400_000).toInt()
    }
}
