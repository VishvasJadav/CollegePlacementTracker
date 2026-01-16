package com.example.collegeplacementtracker


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_table")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val rollNumber: String,
    val email: String,
    val phone: String,
    val branch: String,
    val cgpa: Double,

    // Placement details
    val isPlaced: Boolean = false,
    val companyName: String? = null,
    val packageAmount: Double? = null,
    val jobRole: String? = null,
    val placementDate: String? = null,

    // Additional details
    val skills: String? = null,
    val internships: String? = null,
    val projects: String? = null,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
