package com.example.collegeplacementtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interview_table")
data class Interview(
    @PrimaryKey(autoGenerate = true)
    val interviewId: Int = 0,

    val applicationId: Int,
    val studentId: Int,
    val companyId: Int,

    val interviewDate: String, // Format: "2025-01-15"
    val interviewTime: String, // Format: "10:00 AM"
    val interviewMode: String, // "Online" or "Offline"
    val interviewLocation: String, // Venue or meeting link

    val interviewRound: Int = 1, // 1, 2, 3, etc.
    val roundType: String, // "Technical", "HR", "Managerial", "Group Discussion"

    val status: InterviewStatus = InterviewStatus.SCHEDULED,

    val notes: String? = null,
    val feedback: String? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class InterviewStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED,
    RESCHEDULED,
    NO_SHOW
}
