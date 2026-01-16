package com.example.collegeplacementtracker

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CompanyRepository {
    fun getAllActiveCompanies(): Flow<List<Company>>
    fun searchCompanies(query: String): Flow<List<Company>>
    fun getCompanyById(id: Long): Flow<Company?>
    suspend fun addCompany(company: Company): Long
    suspend fun updateCompany(company: Company)
    suspend fun deactivateCompany(id: Long)
}

class CompanyRepositoryImpl(
    private val companyDao: CompanyDao
) : CompanyRepository {

    // In-memory cache
    private var cachedCompanies: List<Company>? = null
    private var lastCacheTime: Long = 0
    private val cacheTimeout = 5 * 60 * 1000L // 5 minutes

    override fun getAllActiveCompanies(): Flow<List<Company>> = flow {
        // Check cache first
        if (isCacheValid()) {
            emit(cachedCompanies!!)
        } else {
            // Note: LiveData needs to be observed, but we're using Flow
            // We'll use the sync version instead
            val companies = companyDao.getAllCompaniesSync().filter { it.isActive }
            cachedCompanies = companies
            lastCacheTime = System.currentTimeMillis()
            emit(companies)
        }
    }

    override fun searchCompanies(query: String): Flow<List<Company>> = flow {
        // Use sync version - we need to convert LiveData to Flow properly
        val allCompanies = companyDao.getAllCompaniesSync()
        val filtered = allCompanies.filter {
            it.companyName.contains(query, ignoreCase = true) ||
                    it.jobRole.contains(query, ignoreCase = true)
        }
        emit(filtered)
    }

    override fun getCompanyById(id: Long): Flow<Company?> = flow {
        val company = companyDao.getCompanyByIdSync(id.toInt())
        emit(company)
    }

    override suspend fun addCompany(company: Company): Long {
        invalidateCache()
        return companyDao.insertCompany(company)
    }

    override suspend fun updateCompany(company: Company) {
        invalidateCache()
        companyDao.updateCompany(company)
    }

    override suspend fun deactivateCompany(id: Long) {
        invalidateCache()
        companyDao.updateCompanyStatus(id.toInt(), false)
    }

    private fun isCacheValid(): Boolean {
        return cachedCompanies != null &&
                (System.currentTimeMillis() - lastCacheTime) < cacheTimeout
    }

    private fun invalidateCache() {
        cachedCompanies = null
        lastCacheTime = 0
    }
}