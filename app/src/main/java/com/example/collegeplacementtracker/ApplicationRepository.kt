package com.example.collegeplacementtracker

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
    private val applicationDao: ApplicationDao,
    private val companyDao: CompanyDao
) : ApplicationRepository {
    
    override fun getStudentApplications(studentId: Long): Flow<List<ApplicationWithCompany>> = flow {
        // Get applications and manually join with companies
        val applications = applicationDao.getApplicationsByStudentId(studentId.toInt())
        val applicationsWithCompany = applications.map { application ->
            val company = companyDao.getCompanyByIdSync(application.companyId)
            ApplicationWithCompany(application, company)
        }
        emit(applicationsWithCompany)
    }
    
    override fun getApplicationById(id: Long): Flow<Application?> = flow {
        val application = applicationDao.getApplicationByIdSync(id.toInt())
        emit(application)
    }
    
    override suspend fun submitApplication(application: Application): Long {
        return applicationDao.insertApplication(application)
    }
    
    override suspend fun updateApplicationStatus(id: Long, status: String) {
        applicationDao.updateApplicationStatus(id.toInt(), status, System.currentTimeMillis())
    }
    
    override suspend fun withdrawApplication(id: Long) {
        applicationDao.updateApplicationStatus(id.toInt(), "withdrawn", System.currentTimeMillis())
    }
}
