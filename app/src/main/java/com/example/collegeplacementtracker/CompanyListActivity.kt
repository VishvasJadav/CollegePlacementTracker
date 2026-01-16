package com.example.collegeplacementtracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegeplacementtracker.utils.DateUtils
import com.example.collegeplacementtracker.utils.NotificationHelper
import com.example.collegeplacementtracker.utils.SessionManager
import com.example.collegeplacementtracker.utils.UIHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class CompanyListActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var companyDao: CompanyDao
    private lateinit var applicationDao: ApplicationDao
    private lateinit var userDao: UserDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var searchView: SearchView
    private lateinit var chipGroup: ChipGroup
    private lateinit var adapter: CompanyAdapter

    private var allCompanies = listOf<Company>()
    private var filteredCompanies = listOf<Company>()
    private var showEligibleOnly = false

    // Filter options
    private var selectedPackageRange: String? = null
    private var selectedCompanyType: String? = null
    private var selectedBranches = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Available Companies"

        sessionManager = SessionManager.getInstance(this)
        showEligibleOnly = intent.getBooleanExtra("SHOW_ELIGIBLE_ONLY", false)

        val database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        companyDao = database.companyDao()
        applicationDao = database.applicationDao()
        userDao = database.userDao()

        initializeViews()
        setupRecyclerView()
        setupSearch()
        setupFilters()
        loadCompanies()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.companiesRecyclerView)
        emptyView = findViewById(R.id.emptyView)
        searchView = findViewById(R.id.searchView)
        chipGroup = findViewById(R.id.filterChipGroup)
    }

    private fun setupRecyclerView() {
        adapter = CompanyAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener { company ->
            showCompanyDetails(company)
        }

        adapter.setOnApplyClickListener { company ->
            applyToCompany(company)
        }
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterCompanies(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCompanies(newText ?: "")
                return true
            }
        })
    }

    private fun setupFilters() {
        // Add filter chips
        addChip("All Package", "all")
        addChip("0-5 LPA", "0-5")
        addChip("5-10 LPA", "5-10")
        addChip("10+ LPA", "10+")
        addChip("Service", "service")
        addChip("Product", "product")

        // Professional insight filters
        addChip("WFH", "wfh")
        addChip("Learning", "learning")
        addChip("Growth", "growth")
        addChip("500+ Emp", "500emp")
    }

    private fun addChip(text: String, tag: String) {
        val chip = Chip(this).apply {
            this.text = text
            this.tag = tag
            isCheckable = true

            // Default selection for "All Package"
            if (tag == "all") {
                isChecked = true
            }

            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    handleChipSelection(tag)
                }
            }
        }
        chipGroup.addView(chip)
    }

    private fun handleChipSelection(tag: String) {
        when {
            tag.contains("-") || tag == "10+" -> {
                selectedPackageRange = tag
                uncheckOtherChips(tag, listOf("all", "0-5", "5-10", "10+"))
            }

            tag == "all" -> {
                selectedPackageRange = null
                uncheckOtherChips(tag, listOf("0-5", "5-10", "10+"))
            }

            tag == "service" || tag == "product" -> {
                selectedCompanyType = tag
                uncheckOtherChips(tag, listOf("service", "product"))
            }

            tag == "wfh" -> {
                // Toggle Work From Home filter
                uncheckOtherChips(tag, listOf("wfh", "learning", "growth", "500emp"))
            }

            tag == "learning" -> {
                // Toggle Learning Opportunities filter
                uncheckOtherChips(tag, listOf("wfh", "learning", "growth", "500emp"))
            }

            tag == "growth" -> {
                // Toggle Growth Potential filter
                uncheckOtherChips(tag, listOf("wfh", "learning", "growth", "500emp"))
            }

            tag == "500emp" -> {
                // Toggle Employee Count filter
                uncheckOtherChips(tag, listOf("wfh", "learning", "growth", "500emp"))
            }
        }
        applyFilters()
    }

    private fun uncheckOtherChips(selectedTag: String, tags: List<String>) {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.let {
                if (it.tag in tags && it.tag != selectedTag) {
                    it.isChecked = false
                }
            }
        }
    }

    private fun getChipByTag(tag: String): Chip? {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            if (chip?.tag == tag) {
                return chip
            }
        }
        return null
    }

    private fun loadCompanies() {
        lifecycleScope.launch {
            val userId = sessionManager.getUserId()
            userDao.getUserById(userId.toInt().toLong()).observe(this@CompanyListActivity) { user ->
                if (showEligibleOnly && user != null) {
                    companyDao.getEligibleCompanies(user.cgpa ?: 0.0)
                        .observe(this@CompanyListActivity) { companies ->
                            allCompanies = companies.filter {
                                it.eligibleBranches.contains(user.branch ?: "", ignoreCase = true)
                            }
                            applyFilters()
                        }
                } else {
                    companyDao.getAllActiveCompanies()
                        .observe(this@CompanyListActivity) { companies ->
                            allCompanies = companies
                            applyFilters()
                        }
                }
            }
        }
    }

    private fun filterCompanies(query: String) {
        if (query.isEmpty()) {
            applyFilters()
            return
        }

        filteredCompanies = allCompanies.filter { company ->
            company.companyName.contains(query, ignoreCase = true) ||
                    company.jobRole.contains(query, ignoreCase = true) ||
                    company.location.contains(query, ignoreCase = true)
        }

        adapter.submitList(filteredCompanies)
        updateEmptyView(filteredCompanies.isEmpty())
    }

    private fun applyFilters() {
        filteredCompanies = allCompanies

        // Apply package filter
        selectedPackageRange?.let { range ->
            filteredCompanies = when (range) {
                "0-5" -> filteredCompanies.filter { it.packageAmount in 0.0..5.0 }
                "5-10" -> filteredCompanies.filter { it.packageAmount in 5.0..10.0 }
                "10+" -> filteredCompanies.filter { it.packageAmount >= 10.0 }
                else -> filteredCompanies
            }
        }

        // Apply company type filter
        selectedCompanyType?.let { type ->
            filteredCompanies = filteredCompanies.filter {
                it.companyType.equals(type, ignoreCase = true)
            }
        }

        // Apply professional insight filters
        val wfhChip = getChipByTag("wfh")
        if (wfhChip?.isChecked == true) {
            filteredCompanies = filteredCompanies.filter { it.workFromHomePolicy == true }
        }

        val learningChip = getChipByTag("learning")
        if (learningChip?.isChecked == true) {
            filteredCompanies = filteredCompanies.filter { it.learningOpportunities == true }
        }

        val growthChip = getChipByTag("growth")
        if (growthChip?.isChecked == true) {
            filteredCompanies = filteredCompanies.filter { it.growthPotential == true }
        }

        val empChip = getChipByTag("500emp")
        if (empChip?.isChecked == true) {
            filteredCompanies =
                filteredCompanies.filter { it.employeesCount != null && it.employeesCount!! >= 500 }
        }

        // Sort by package (highest first)
        filteredCompanies = filteredCompanies.sortedByDescending { it.packageAmount }

        adapter.submitList(filteredCompanies)
        updateEmptyView(filteredCompanies.isEmpty())
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            recyclerView.visibility = RecyclerView.GONE
            emptyView.visibility = TextView.VISIBLE
            emptyView.text = when {
                searchView.query.isNotEmpty() -> "No companies found matching '${searchView.query}'"
                showEligibleOnly -> "No eligible companies available"
                else -> "No companies posted yet"
            }
        } else {
            recyclerView.visibility = RecyclerView.VISIBLE
            emptyView.visibility = TextView.GONE
        }
    }

    private fun showCompanyDetails(company: Company) {
        val daysUntil = DateUtils.getDaysUntil(company.applicationDeadline)
        val deadlineText = when {
            daysUntil < 0 -> "❌ Deadline passed"
            daysUntil == 0 -> "⏰ Deadline today!"
            daysUntil <= 3 -> "⚠️ ${daysUntil} days left"
            else -> "✅ ${daysUntil} days left"
        }

        val message = """
            🏢 Company: ${company.companyName}
            💼 Role: ${company.jobRole}
            💰 Package: ${company.packageAmount} LPA
            📍 Location: ${company.location}
            🏷️ Type: ${company.companyType}
            
            ━━━━━━━━━━━━━━━━━━━━
            
            📋 Eligibility Criteria:
            • Branches: ${company.eligibleBranches}
            • Minimum CGPA: ${company.minimumCGPA}
            • Max Backlogs: ${company.backlogs}
            
            ━━━━━━━━━━━━━━━━━━━━
            
            📝 Selection Process:
            ${company.selectionProcess}
            
            🔢 Number of Rounds: ${company.numberOfRounds}
            
            ━━━━━━━━━━━━━━━━━━━━
            
            📅 Application Deadline: ${company.applicationDeadline}
            $deadlineText
            
            ${if (company.driveDate != null) "🗓️ Drive Date: ${company.driveDate}\n" else ""}
            💺 Total Positions: ${company.totalPositions}
            ✅ Filled: ${company.filledPositions}
            📊 Available: ${company.totalPositions - company.filledPositions}
            
            ${if (company.bond != null) "⚠️ Bond: ${company.bond}\n" else ""}
            
            ━━━━━━━━━━━━━━━━━━━━
            
            📄 Description:
            ${company.jobDescription}
        """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle(company.companyName)
            .setMessage(message)
            .setPositiveButton("Apply Now") { _, _ ->
                if (daysUntil < 0) {
                    UIHelper.showError(this, "Application deadline has passed")
                } else {
                    applyToCompany(company)
                }
            }
            .setNeutralButton("Share") { _, _ ->
                shareCompany(company)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun shareCompany(company: Company) {
        val shareText = """
            Check out this opportunity!
            
            Company: ${company.companyName}
            Role: ${company.jobRole}
            Package: ${company.packageAmount} LPA
            Location: ${company.location}
            Deadline: ${company.applicationDeadline}
            
            Apply through College Placement Tracker app!
        """.trimIndent()

        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(android.content.Intent.createChooser(shareIntent, "Share Company Details"))
    }

    private fun applyToCompany(company: Company) {
        lifecycleScope.launch {
            try {
                val userId = sessionManager.getUserId()

                // Check if user is eligible
                val user = userDao.getUserById(userId.toInt().toLong()).value
                if (user != null) {
                    val userCGPA = user.cgpa ?: 0.0
                    if (userCGPA < company.minimumCGPA) {
                        UIHelper.showError(
                            this@CompanyListActivity,
                            "You don't meet the minimum CGPA requirement (${company.minimumCGPA})"
                        )
                        return@launch
                    }

                    if (!company.eligibleBranches.contains(user.branch ?: "", ignoreCase = true)) {
                        UIHelper.showError(
                            this@CompanyListActivity,
                            "Your branch is not eligible for this company"
                        )
                        return@launch
                    }
                }

                // Check if already applied
                val existingApp =
                    applicationDao.getExistingApplication(userId.toInt().toLong(), company.id)
                if (existingApp != null) {
                    UIHelper.showInfo(
                        this@CompanyListActivity,
                        "You have already applied to this company"
                    )
                    return@launch
                }

                // Check if positions are full
                if (company.filledPositions >= company.totalPositions) {
                    UIHelper.showError(
                        this@CompanyListActivity,
                        "All positions for this company have been filled"
                    )
                    return@launch
                }

                // Create application
                val application = Application(
                    studentId = userId.toInt().toLong(),
                    companyId = company.id,
                    status = ApplicationStatus.PENDING,
                    appliedAt = System.currentTimeMillis()
                )

                applicationDao.insert(application)

                // Send notification
                NotificationHelper.sendApplicationStatusNotification(
                    this@CompanyListActivity,
                    company.companyName,
                    "Applied",
                    application.id
                )

                UIHelper.showSuccess(
                    this@CompanyListActivity,
                    "Application submitted successfully! 🎉"
                )

            } catch (e: Exception) {
                UIHelper.showError(
                    this@CompanyListActivity,
                    "Failed to apply: ${e.message}"
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.company_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                loadCompanies()
                UIHelper.showSuccess(this, "Refreshed")
                true
            }

            R.id.action_sort_package -> {
                filteredCompanies = filteredCompanies.sortedByDescending { it.packageAmount }
                adapter.submitList(filteredCompanies)
                true
            }

            R.id.action_sort_deadline -> {
                filteredCompanies = filteredCompanies.sortedBy {
                    DateUtils.parseDate(it.applicationDeadline) ?: Long.MAX_VALUE
                }
                adapter.submitList(filteredCompanies)
                true
            }

            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
