package com.example.collegeplacementtracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ApplicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(application: Application): Long

    @Update
    suspend fun update(application: Application)

    @Delete
    suspend fun delete(application: Application)

    @Query("SELECT * FROM application_table WHERE id = :applicationId")
    fun getApplicationById(applicationId: Int): LiveData<Application>

    @Query("SELECT * FROM application_table WHERE studentId = :studentId ORDER BY appliedAt DESC")
    fun getApplicationsByStudent(studentId: Long): LiveData<List<Application>>

    @Query("SELECT * FROM application_table WHERE companyId = :companyId ORDER BY appliedAt DESC")
    fun getApplicationsByCompany(companyId: Int): LiveData<List<Application>>

    @Query("SELECT * FROM application_table WHERE status = :status ORDER BY appliedAt DESC")
    fun getApplicationsByStatus(status: String): LiveData<List<Application>>

    @Query("SELECT * FROM application_table WHERE studentId = :studentId AND companyId = :companyId LIMIT 1")
    suspend fun getExistingApplication(studentId: Long, companyId: Int): Application?

    @Query("UPDATE application_table SET status = :status, lastUpdated = :timestamp WHERE id = :applicationId")
    suspend fun updateApplicationStatus(applicationId: Int, status: String, timestamp: Long)

    @Query("UPDATE application_table SET hodApproved = :approved, hodApprovedAt = :timestamp WHERE id = :applicationId")
    suspend fun updateHODApproval(applicationId: Int, approved: Boolean, timestamp: Long)

    @Query("UPDATE application_table SET tpoApproved = :approved, tpoApprovedAt = :timestamp WHERE id = :applicationId")
    suspend fun updateTPOApproval(applicationId: Int, approved: Boolean, timestamp: Long)

    @Query("SELECT COUNT(*) FROM application_table WHERE studentId = :studentId")
    fun getApplicationCountByStudent(studentId: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM application_table WHERE studentId = :studentId AND status = 'SELECTED'")
    fun getSelectedCountByStudent(studentId: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM application_table WHERE status = 'PENDING' AND hodApproved = 0")
    fun getPendingHODApprovalsCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM application_table WHERE status = 'PENDING' AND tpoApproved = 0")
    fun getPendingTPOApprovalsCount(): LiveData<Int>

    @Query(
        """
        SELECT a.* FROM application_table a
        INNER JOIN user_table u ON a.studentId = u.id
        WHERE u.branch = :branch AND a.hodApproved = 0
        ORDER BY a.appliedAt DESC
    """
    )
    fun getPendingApplicationsByBranch(branch: String): LiveData<List<Application>>

    @Query("SELECT * FROM application_table")
    fun getAllApplications(): LiveData<List<Application>>

    // Suspend functions for direct list access (used in coroutines)
    @Query("SELECT * FROM application_table")
    suspend fun getAllApplicationsSync(): List<Application>

    @Query("SELECT * FROM application_table WHERE studentId = :studentId ORDER BY appliedAt DESC")
    suspend fun getApplicationsByStudentId(studentId: Int): List<Application>

    // Queries that return ApplicationWithCompany (joined with company details)
    @Transaction
    @Query("SELECT * FROM application_table WHERE studentId = :studentId ORDER BY appliedAt DESC")
    fun getApplicationsWithCompanyByStudent(studentId: Long): LiveData<List<ApplicationWithCompany>>

    @Transaction
    @Query(
        """
        SELECT a.* FROM application_table a
        INNER JOIN user_table u ON a.studentId = u.id
        WHERE u.branch = :branch AND a.hodApproved = 0
        ORDER BY a.appliedAt DESC
    """
    )
    fun getPendingApplicationsWithCompanyByBranch(branch: String): LiveData<List<ApplicationWithCompany>>

    @Transaction
    @Query("SELECT * FROM application_table ORDER BY appliedAt DESC")
    fun getAllApplicationsWithCompany(): LiveData<List<ApplicationWithCompany>>

    @Transaction
    @Query("SELECT * FROM application_table WHERE hodApproved = 0 ORDER BY appliedAt DESC")
    fun getPendingHODApplicationsWithCompany(): LiveData<List<ApplicationWithCompany>>

    @Transaction
    @Query("SELECT * FROM application_table WHERE tpoApproved = 0 ORDER BY appliedAt DESC")
    fun getPendingTPOApplicationsWithCompany(): LiveData<List<ApplicationWithCompany>>

    // Alias for insert method
    suspend fun insertApplication(application: Application): Long = insert(application)

    // Suspend version for getting application by ID
    @Query("SELECT * FROM application_table WHERE id = :applicationId")
    suspend fun getApplicationByIdSync(applicationId: Int): Application?

}
