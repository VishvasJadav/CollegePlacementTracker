package com.example.collegeplacementtracker

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "application_table",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["companyId"])
    ]
)
data class Application(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val studentId: Int,
    val companyId: Int,

    val status: String,

    val currentRound: Int = 0,
    val roundsCleared: String? = null,
    val feedback: String? = null,

    val appliedAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis(),

    val selectedDate: String? = null,
    val offeredPackage: Double? = null,
    val joiningDate: String? = null,

    val resumeUrl: String? = null,
    val coverLetter: String? = null,

    val hodApproved: Boolean = false,
    val tpoApproved: Boolean = false,
    val hodApprovedAt: Long? = null,
    val tpoApprovedAt: Long? = null
)

object ApplicationStatus {
    const val PENDING = "PENDING"
    const val SHORTLISTED = "SHORTLISTED"
    const val REJECTED = "REJECTED"
    const val SELECTED = "SELECTED"
    const val WITHDRAWN = "WITHDRAWN"
}
