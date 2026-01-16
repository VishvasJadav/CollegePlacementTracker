package com.example.collegeplacementtracker

import com.example.collegeplacementtracker.Application
import com.example.collegeplacementtracker.ApplicationDao
import com.example.collegeplacementtracker.ApplicationWithCompany
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ApplicationRepository {
    fun getStudentApplications(studentId: Long): Flow<List<ApplicationWithCompany>>
    fun getApplicationById(id: Long): Flow<Application?>
    suspend fun submitApplication(application: Application): Long
    suspend fun updateApplicationStatus(id: Long, status: String)
    suspend fun withdrawApplication(id: Long)
}

class ApplicationRepositoryImpl(
    private val applicationDao: ApplicationDao
) : ApplicationRepository {
    
    override fun getStudentApplications(studentId: Long): Flow<List<ApplicationWithCompany>> = flow {
        val applications = applicationDao.getStudentApplicationsWithCompany(studentId)
        emit(applications)
    }
    
    override fun getApplicationById(id: Long): Flow<Application?> = flow {
        val application = applicationDao.getApplicationById(id)
        emit(application)
    }
    
    override suspend fun submitApplication(application: Application): Long {
        return applicationDao.insertApplication(application)
    }
    
    override suspend fun updateApplicationStatus(id: Long, status: String) {
        applicationDao.updateApplicationStatus(id, status, System.currentTimeMillis())
    }
    
    override suspend fun withdrawApplication(id: Long) {
        applicationDao.updateApplicationStatus(id, "withdrawn", System.currentTimeMillis())
    }
}
