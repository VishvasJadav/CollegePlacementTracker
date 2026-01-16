package com.example.collegeplacementtracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository
    val allStudents: LiveData<List<Student>>
    val totalCount: LiveData<Int>
    val placedCount: LiveData<Int>
    val averagePackage: LiveData<Double>
    val highestPackage: LiveData<Double>

    init {
        // FIXED: Use AppDatabaseNew instead of AppDatabase_OLD
        val studentDao = AppDatabaseNew.getDatabase(application, viewModelScope).studentDao()
        repository = StudentRepository(studentDao)
        allStudents = repository.allStudents
        totalCount = repository.totalCount
        placedCount = repository.placedCount
        averagePackage = repository.averagePackage
        highestPackage = repository.highestPackage
    }

    fun insert(student: Student) = viewModelScope.launch {
        repository.insert(student)
    }

    fun update(student: Student) = viewModelScope.launch {
        repository.update(student)
    }

    fun delete(student: Student) = viewModelScope.launch {
        repository.delete(student)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun getPlacedStudents(): LiveData<List<Student>> {
        return repository.placedStudents
    }

    fun getUnplacedStudents(): LiveData<List<Student>> {
        return repository.unplacedStudents
    }

    fun getStudentById(studentId: Int): LiveData<Student> {
        return repository.getStudentById(studentId)
    }

    fun getStudentsByBranch(branch: String): LiveData<List<Student>> {
        return repository.getStudentsByBranch(branch)
    }

    fun searchStudents(searchQuery: String): LiveData<List<Student>> {
        return repository.searchStudents("%$searchQuery%")
    }
}
