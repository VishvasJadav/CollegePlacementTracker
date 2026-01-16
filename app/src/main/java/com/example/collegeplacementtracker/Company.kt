package com.example.collegeplacementtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company_table")
data class Company(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val companyName: String,
    val companyLogo: String? = null,
    val jobRole: String,
    val jobDescription: String,
    val packageAmount: Double,
    val location: String,

    val eligibleBranches: String,
    val minimumCGPA: Double,
    val backlogs: Int = 0,

    val selectionProcess: String,
    val numberOfRounds: Int,

    val applicationDeadline: String,
    val driveDate: String? = null,

    val isActive: Boolean = true,
    val totalPositions: Int,
    val filledPositions: Int = 0,

    val postedBy: Int,
    val postedAt: Long = System.currentTimeMillis(),

    val websiteUrl: String? = null,
    val companyType: String,
    val bond: String? = null,
    val employeesCount: Int? = null,
    val workFromHomePolicy: Boolean? = null,
    val learningOpportunities: Boolean? = null,
    val growthPotential: Boolean? = null
)