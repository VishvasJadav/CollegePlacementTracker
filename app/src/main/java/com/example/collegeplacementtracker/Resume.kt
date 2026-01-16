package com.example.collegeplacementtracker

import androidx.room.*

@Entity(
    tableName = "resumes",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["uploadedAt"])
    ]
)
data class Resume(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val studentId: Long,
    
    val fileName: String,
    
    val filePath: String, // Local file path or cloud URL
    
    val fileSize: Long, // in bytes
    
    val mimeType: String, // "application/pdf"
    
    val resumeType: String, // "primary", "technical", "creative"
    
    val uploadedAt: Long = System.currentTimeMillis(),
    
    val lastModified: Long = System.currentTimeMillis(),
    
    val isActive: Boolean = true,
    
    // Parsed data from resume
    val parsedSkills: String? = null, // JSON array
    
    val parsedExperience: String? = null, // JSON array
    
    val parsedEducation: String? = null, // JSON array
    
    val resumeScore: Int? = null, // 0-100 score
    
    val lastScanned: Long? = null
)

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resumes WHERE studentId = :studentId AND isActive = 1 ORDER BY uploadedAt DESC")
    suspend fun getStudentResumes(studentId: Long): List<Resume>
    
    @Query("SELECT * FROM resumes WHERE id = :resumeId")
    suspend fun getResumeById(resumeId: Long): Resume?
    
    @Query("SELECT * FROM resumes WHERE studentId = :studentId AND resumeType = :type AND isActive = 1 LIMIT 1")
    suspend fun getPrimaryResume(studentId: Long, type: String = "primary"): Resume?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResume(resume: Resume): Long
    
    @Update
    suspend fun updateResume(resume: Resume)
    
    @Query("UPDATE resumes SET isActive = 0 WHERE id = :resumeId")
    suspend fun deactivateResume(resumeId: Long)
    
    @Delete
    suspend fun deleteResume(resume: Resume)
    
    @Query("SELECT COUNT(*) FROM resumes WHERE studentId = :studentId AND isActive = 1")
    suspend fun getResumeCount(studentId: Long): Int
}