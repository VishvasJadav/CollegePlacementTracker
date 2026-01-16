package com.example.collegeplacementtracker


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)

    @Update
    suspend fun update(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("DELETE FROM student_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM student_table ORDER BY name ASC")
    fun getAllStudents(): LiveData<List<Student>>

    @Query("SELECT * FROM student_table WHERE isPlaced = 1 ORDER BY packageAmount DESC")
    fun getPlacedStudents(): LiveData<List<Student>>

    @Query("SELECT * FROM student_table WHERE isPlaced = 0 ORDER BY cgpa DESC")
    fun getUnplacedStudents(): LiveData<List<Student>>

    @Query("SELECT * FROM student_table WHERE id = :studentId")
    fun getStudentById(studentId: Int): LiveData<Student>

    @Query("SELECT * FROM student_table WHERE branch = :branch ORDER BY name ASC")
    fun getStudentsByBranch(branch: String): LiveData<List<Student>>

    @Query("SELECT * FROM student_table WHERE name LIKE :searchQuery OR rollNumber LIKE :searchQuery")
    fun searchStudents(searchQuery: String): LiveData<List<Student>>

    @Query("SELECT COUNT(*) FROM student_table")
    fun getTotalCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM student_table WHERE isPlaced = 1")
    fun getPlacedCount(): LiveData<Int>

    @Query("SELECT AVG(packageAmount) FROM student_table WHERE isPlaced = 1")
    fun getAveragePackage(): LiveData<Double>

    @Query("SELECT MAX(packageAmount) FROM student_table WHERE isPlaced = 1")
    fun getHighestPackage(): LiveData<Double>

    // Suspend functions for direct list access (used in coroutines)
    @Query("SELECT * FROM student_table ORDER BY name ASC")
    suspend fun getAllStudentsSync(): List<Student>

    @Query("SELECT * FROM student_table WHERE email = :email LIMIT 1")
    suspend fun getStudentByEmail(email: String): Student?

    @Query("SELECT * FROM student_table WHERE id = :studentId")
    suspend fun getStudentByIdSync(studentId: Int): Student?
}
