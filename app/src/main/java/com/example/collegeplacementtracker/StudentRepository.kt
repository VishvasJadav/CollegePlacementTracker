package com.example.collegeplacementtracker


import androidx.lifecycle.LiveData

class StudentRepository(private val studentDao: StudentDao) {

    val allStudents: LiveData<List<Student>> = studentDao.getAllStudents()
    val placedStudents: LiveData<List<Student>> = studentDao.getPlacedStudents()
    val unplacedStudents: LiveData<List<Student>> = studentDao.getUnplacedStudents()
    val totalCount: LiveData<Int> = studentDao.getTotalCount()
    val placedCount: LiveData<Int> = studentDao.getPlacedCount()
    val averagePackage: LiveData<Double> = studentDao.getAveragePackage()
    val highestPackage: LiveData<Double> = studentDao.getHighestPackage()

    suspend fun insert(student: Student) {
        studentDao.insert(student)
    }

    suspend fun update(student: Student) {
        studentDao.update(student)
    }

    suspend fun delete(student: Student) {
        studentDao.delete(student)
    }

    suspend fun deleteAll() {
        studentDao.deleteAll()
    }

    fun getStudentById(studentId: Int): LiveData<Student> {
        return studentDao.getStudentById(studentId)
    }

    fun getStudentsByBranch(branch: String): LiveData<List<Student>> {
        return studentDao.getStudentsByBranch(branch)
    }

    fun searchStudents(searchQuery: String): LiveData<List<Student>> {
        return studentDao.searchStudents(searchQuery)
    }
}
