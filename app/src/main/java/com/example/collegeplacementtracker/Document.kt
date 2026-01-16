package com.example.collegeplacementtracker

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(
    tableName = "documents",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"])]
)
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val studentId: Long,
    
    val documentType: String, // "10th", "12th", "degree", "marksheet", "other"
    
    val fileName: String,
    
    val filePath: String,
    
    val uploadedAt: Long = System.currentTimeMillis(),
    
    val verificationStatus: String = "pending", // "pending", "verified", "rejected"
    
    val verifiedBy: Long? = null, // HOD/TPO id
    
    val verifiedAt: Long? = null,
    
    val rejectionReason: String? = null,
    
    val expiryDate: Long? = null,
    
    val isActive: Boolean = true
)

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE studentId = :studentId AND isActive = 1")
    suspend fun getStudentDocuments(studentId: Long): List<Document>
    
    @Query("SELECT * FROM documents WHERE verificationStatus = 'pending' ORDER BY uploadedAt ASC")
    suspend fun getPendingDocuments(): List<Document>
    
    @Insert
    suspend fun insertDocument(document: Document): Long
    
    @Update
    suspend fun updateDocument(document: Document)
    
    @Query("UPDATE documents SET verificationStatus = :status, verifiedBy = :verifierId, verifiedAt = :verifiedAt WHERE id = :documentId")
    suspend fun updateVerificationStatus(documentId: Long, status: String, verifierId: Long, verifiedAt: Long)
}