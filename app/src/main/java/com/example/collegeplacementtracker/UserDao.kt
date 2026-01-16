package com.example.collegeplacementtracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    // Multi-option login methods
    @Query("SELECT * FROM user_table WHERE email = :identifier OR phone = :identifier OR collegeId = :identifier LIMIT 1")
    suspend fun getUserByIdentifier(identifier: String): User?

    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM user_table WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM user_table WHERE collegeId = :collegeId LIMIT 1")
    suspend fun getUserByCollegeId(collegeId: String): User?

    @Query("SELECT * FROM user_table WHERE email = :email OR phone = :phone OR (collegeId IS NOT NULL AND collegeId = :collegeId) LIMIT 1")
    suspend fun getUserByEmailPhoneOrCollegeId(
        email: String,
        phone: String,
        collegeId: String?
    ): User?

    @Query("SELECT * FROM user_table WHERE id = :userId")
    fun getUserById(userId: Int): LiveData<User>

    @Query("SELECT * FROM user_table WHERE id = :userId")
    suspend fun getUserByIdSync(userId: Int): User?

    @Query("SELECT * FROM user_table WHERE role = :role ORDER BY fullName ASC")
    fun getUsersByRole(role: String): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE role = 'STUDENT' AND branch = :branch ORDER BY fullName ASC")
    fun getStudentsByBranch(branch: String): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE role = 'STUDENT' ORDER BY cgpa DESC")
    fun getAllStudentsSortedByCGPA(): LiveData<List<User>>

    @Query("SELECT COUNT(*) FROM user_table WHERE role = :role")
    fun getUserCountByRole(role: String): LiveData<Int>

    @Query("UPDATE user_table SET lastLogin = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: Int, timestamp: Long)

    @Query("UPDATE user_table SET isActive = :isActive WHERE id = :userId")
    suspend fun updateUserStatus(userId: Int, isActive: Boolean)

    @Query("SELECT * FROM user_table WHERE role = 'STUDENT' AND rollNumber = :rollNumber LIMIT 1")
    suspend fun getStudentByRollNumber(rollNumber: String): User?


    @Query("SELECT * FROM user_table")
    fun getAllUsers(): LiveData<List<User>>


}