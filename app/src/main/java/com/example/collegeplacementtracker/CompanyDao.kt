package com.example.collegeplacementtracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CompanyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(company: Company): Long

    @Update
    suspend fun update(company: Company)

    @Delete
    suspend fun delete(company: Company)

    @Query("SELECT * FROM company_table WHERE id = :companyId")
    fun getCompanyById(companyId: Int): LiveData<Company>

    @Query("SELECT * FROM company_table WHERE isActive = 1 ORDER BY postedAt DESC")
    fun getAllActiveCompanies(): LiveData<List<Company>>

    @Query("SELECT * FROM company_table ORDER BY postedAt DESC")
    fun getAllCompanies(): LiveData<List<Company>>

    @Query("SELECT * FROM company_table WHERE isActive = 1 AND eligibleBranches LIKE '%' || :branch || '%' ORDER BY packageAmount DESC")
    fun getCompaniesByBranch(branch: String): LiveData<List<Company>>

    @Query("SELECT * FROM company_table WHERE isActive = 1 AND minimumCGPA <= :cgpa ORDER BY packageAmount DESC")
    fun getEligibleCompanies(cgpa: Double): LiveData<List<Company>>

    @Query("SELECT * FROM company_table WHERE companyName LIKE '%' || :query || '%' OR jobRole LIKE '%' || :query || '%'")
    fun searchCompanies(query: String): LiveData<List<Company>>

    @Query("UPDATE company_table SET filledPositions = filledPositions + 1 WHERE id = :companyId")
    suspend fun incrementFilledPositions(companyId: Int)

    @Query("UPDATE company_table SET isActive = :isActive WHERE id = :companyId")
    suspend fun updateCompanyStatus(companyId: Int, isActive: Boolean)

    @Query("SELECT COUNT(*) FROM company_table WHERE isActive = 1")
    fun getActiveCompanyCount(): LiveData<Int>

    @Query("SELECT AVG(packageAmount) FROM company_table WHERE isActive = 1")
    fun getAveragePackage(): LiveData<Double>

    @Query("SELECT MAX(packageAmount) FROM company_table")
    fun getHighestPackage(): LiveData<Double>

    // Suspend functions for direct list access (used in coroutines)
    @Query("SELECT * FROM company_table ORDER BY postedAt DESC")
    suspend fun getAllCompaniesSync(): List<Company>

    @Query("SELECT * FROM company_table WHERE id = :companyId")
    suspend fun getCompanyByIdSync(companyId: Int): Company?

    // Alias for insert method
    suspend fun insertCompany(company: Company): Long = insert(company)

    // Alias for update method
    suspend fun updateCompany(company: Company) = update(company)
}