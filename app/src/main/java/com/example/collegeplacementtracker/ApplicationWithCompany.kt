package com.example.collegeplacementtracker

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class that represents an Application joined with its Company details
 */
data class ApplicationWithCompany(
    @Embedded val application: Application,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val company: Company?
)
