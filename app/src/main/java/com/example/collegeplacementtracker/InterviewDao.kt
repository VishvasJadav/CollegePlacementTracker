package com.example.collegeplacementtracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface InterviewDao {

    @Insert
    suspend fun insert(interview: Interview): Long

    @Update
    suspend fun update(interview: Interview)

    @Delete
    suspend fun delete(interview: Interview)

    @Query("SELECT * FROM interview_table ORDER BY interviewDate ASC, interviewTime ASC")
    fun getAllInterviews(): LiveData<List<Interview>>

    @Query("SELECT * FROM interview_table WHERE interviewId = :interviewId")
    fun getInterviewById(interviewId: Int): LiveData<Interview>

    @Query("SELECT * FROM interview_table WHERE studentId = :studentId ORDER BY interviewDate ASC")
    fun getInterviewsByStudent(studentId: Int): LiveData<List<Interview>>

    @Query("SELECT * FROM interview_table WHERE companyId = :companyId ORDER BY interviewDate ASC")
    fun getInterviewsByCompany(companyId: Int): LiveData<List<Interview>>

    @Query("SELECT * FROM interview_table WHERE applicationId = :applicationId ORDER BY interviewRound ASC")
    fun getInterviewsByApplication(applicationId: Int): LiveData<List<Interview>>

    @Query("SELECT * FROM interview_table WHERE status = :status ORDER BY interviewDate ASC")
    fun getInterviewsByStatus(status: InterviewStatus): LiveData<List<Interview>>

    @Query("SELECT * FROM interview_table WHERE interviewDate = :date ORDER BY interviewTime ASC")
    fun getInterviewsByDate(date: String): LiveData<List<Interview>>

    @Query("SELECT * FROM interview_table WHERE interviewDate >= :startDate AND interviewDate <= :endDate ORDER BY interviewDate ASC, interviewTime ASC")
    fun getInterviewsInRange(startDate: String, endDate: String): LiveData<List<Interview>>

    @Query("SELECT COUNT(*) FROM interview_table WHERE studentId = :studentId AND status = :status")
    suspend fun getInterviewCountByStatus(studentId: Int, status: InterviewStatus): Int

    @Query("DELETE FROM interview_table WHERE applicationId = :applicationId")
    suspend fun deleteInterviewsByApplication(applicationId: Int)

    // Sync methods for coroutines
    @Query("SELECT * FROM interview_table ORDER BY interviewDate ASC, interviewTime ASC")
    suspend fun getAllInterviewsSync(): List<Interview>

    @Query("SELECT * FROM interview_table WHERE companyId = :companyId ORDER BY interviewDate ASC")
    suspend fun getInterviewsByCompanySync(companyId: Int): List<Interview>

    @Query("SELECT * FROM interview_table WHERE studentId = :studentId ORDER BY interviewDate ASC")
    suspend fun getInterviewsByStudentSync(studentId: Int): List<Interview>

    @Query("SELECT * FROM interview_table WHERE interviewId = :interviewId")
    suspend fun getInterviewByIdSync(interviewId: Int): Interview?

    @Query("SELECT COUNT(*) FROM interview_table")
    suspend fun getTotalInterviewCount(): Int

    @Query("SELECT COUNT(*) FROM interview_table WHERE status = :status")
    suspend fun getInterviewCountByStatusSync(status: InterviewStatus): Int
}
