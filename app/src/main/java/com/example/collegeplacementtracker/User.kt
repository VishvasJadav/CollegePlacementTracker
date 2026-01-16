package com.example.collegeplacementtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val role: String,

    val collegeId: String? = null,  // Unique College ID
    val rollNumber: String? = null,
    val branch: String? = null,
    val cgpa: Double? = null,

    val profileImageUrl: String? = null,
    val professionalSummary: String? = null,
    val skills: String? = null,
    val internships: String? = null,
    val projects: String? = null,
    val certifications: String? = null,
    val linkedinUrl: String? = null,
    val resumeUrl: String? = null,

    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long? = null
)

object UserRole {
    const val STUDENT = "STUDENT"
    const val HOD = "HOD"
    const val TPO = "TPO"
}