package com.example.collegeplacementtracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(
    tableName = "alumni",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId"], unique = true),
        Index(value = ["graduationYear"]),
        Index(value = ["currentCompany"])
    ]
)
data class Alumni(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val studentId: Long,

    val graduationYear: Int,

    val currentCompany: String,

    val currentPosition: String,

    val currentPackage: Double? = null,

    val yearsOfExperience: Int = 0,

    val linkedInUrl: String? = null,

    val githubUrl: String? = null,

    val portfolioUrl: String? = null,

    val willingToMentor: Boolean = false,

    val mentorshipAreas: String? = null, // JSON array of skills/areas

    val availableForReferrals: Boolean = false,

    val bio: String? = null,

    val achievements: String? = null, // JSON array

    val isVerified: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    val lastUpdated: Long = System.currentTimeMillis()
)

@Dao
interface AlumniDao {
    @Query("SELECT * FROM alumni WHERE isVerified = 1 ORDER BY graduationYear DESC")
    suspend fun getAllVerifiedAlumni(): List<Alumni>

    @Query("SELECT * FROM alumni WHERE currentCompany LIKE '%' || :companyName || '%' AND isVerified = 1")
    suspend fun searchByCompany(companyName: String): List<Alumni>

    @Query("SELECT * FROM alumni WHERE willingToMentor = 1 AND isVerified = 1 ORDER BY yearsOfExperience DESC")
    suspend fun getMentors(): List<Alumni>

    @Query("SELECT * FROM alumni WHERE availableForReferrals = 1 AND isVerified = 1")
    suspend fun getAvailableForReferrals(): List<Alumni>

    @Query("SELECT * FROM alumni WHERE graduationYear = :year AND isVerified = 1")
    suspend fun getAlumniByYear(year: Int): List<Alumni>

    @Query("SELECT * FROM alumni WHERE studentId = :studentId")
    suspend fun getAlumniByStudentId(studentId: Long): Alumni?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlumni(alumni: Alumni): Long

    @Update
    suspend fun updateAlumni(alumni: Alumni)

    @Delete
    suspend fun deleteAlumni(alumni: Alumni)

    @Query("UPDATE alumni SET isVerified = :verified WHERE id = :alumniId")
    suspend fun updateVerificationStatus(alumniId: Long, verified: Boolean)

    @Query("SELECT COUNT(*) FROM alumni WHERE isVerified = 1")
    suspend fun getVerifiedAlumniCount(): Int

    @Query("SELECT COUNT(*) FROM alumni WHERE willingToMentor = 1 AND isVerified = 1")
    suspend fun getMentorCount(): Int
}
