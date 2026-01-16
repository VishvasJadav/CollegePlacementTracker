package com.example.collegeplacementtracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.util.Calendar

class TPOInterviewManagementActivity : AppCompatActivity() {

    private lateinit var database: AppDatabaseNew
    private lateinit var interviewDao: InterviewDao
    private lateinit var companyDao: CompanyDao
    private lateinit var userDao: UserDao
    private lateinit var applicationDao: ApplicationDao

    private lateinit var adapter: InterviewAdapter
    private var allInterviews: List<InterviewAdapter.InterviewWithDetails> = emptyList()
    private var currentFilter: InterviewStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tpo_interview_management)

        database = AppDatabaseNew.getDatabase(this, lifecycleScope)
        interviewDao = database.interviewDao()
        companyDao = database.companyDao()
        userDao = database.userDao()
        applicationDao = database.applicationDao()

        setupToolbar()
        setupRecyclerView()
        setupFilterChips()
        setupFab()
        loadInterviews()
        loadStatistics()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        adapter = InterviewAdapter(
            onEditClick = { interview -> showEditInterviewDialog(interview) },
            onCancelClick = { interview -> cancelInterview(interview) },
            onCompleteClick = { interview -> completeInterview(interview) }
        )

        findViewById<RecyclerView>(R.id.interviewsRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@TPOInterviewManagementActivity)
            adapter = this@TPOInterviewManagementActivity.adapter
        }
    }

    private fun setupFilterChips() {
        findViewById<Chip>(R.id.chipAll).setOnClickListener {
            currentFilter = null
            filterInterviews()
        }
        findViewById<Chip>(R.id.chipScheduled).setOnClickListener {
            currentFilter = InterviewStatus.SCHEDULED
            filterInterviews()
        }
        findViewById<Chip>(R.id.chipCompleted).setOnClickListener {
            currentFilter = InterviewStatus.COMPLETED
            filterInterviews()
        }
        findViewById<Chip>(R.id.chipCancelled).setOnClickListener {
            currentFilter = InterviewStatus.CANCELLED
            filterInterviews()
        }
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.addInterviewFab).setOnClickListener {
            showAddInterviewDialog()
        }
    }

    private fun loadInterviews() {
        lifecycleScope.launch {
            try {
                val interviews = interviewDao.getAllInterviewsSync()
                val companies = companyDao.getAllCompaniesSync()

                userDao.getAllUsers().observe(this@TPOInterviewManagementActivity) { users ->
                    val companyMap = companies.associateBy { it.id }
                    val userMap = users.associateBy { it.id }

                    allInterviews = interviews.map { interview ->
                        InterviewAdapter.InterviewWithDetails(
                            interview = interview,
                            companyName = companyMap[interview.companyId]?.companyName
                                ?: "Unknown Company",
                            studentName = userMap[interview.studentId]?.fullName
                                ?: "Unknown Student"
                        )
                    }

                    filterInterviews()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@TPOInterviewManagementActivity,
                    "Error loading interviews",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun filterInterviews() {
        val filtered = if (currentFilter == null) {
            allInterviews
        } else {
            allInterviews.filter { it.interview.status == currentFilter }
        }

        adapter.submitList(filtered)

        val recyclerView = findViewById<RecyclerView>(R.id.interviewsRecyclerView)
        val emptyState = findViewById<LinearLayout>(R.id.emptyStateLayout)

        if (filtered.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
        }
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            try {
                val total = interviewDao.getTotalInterviewCount()
                val scheduled =
                    interviewDao.getInterviewCountByStatusSync(InterviewStatus.SCHEDULED)
                val completed =
                    interviewDao.getInterviewCountByStatusSync(InterviewStatus.COMPLETED)

                runOnUiThread {
                    findViewById<TextView>(R.id.totalInterviewsTextView).text = total.toString()
                    findViewById<TextView>(R.id.scheduledInterviewsTextView).text =
                        scheduled.toString()
                    findViewById<TextView>(R.id.completedInterviewsTextView).text =
                        completed.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showAddInterviewDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_interview, null)

        val dateEditText = dialogView.findViewById<TextInputEditText>(R.id.dateEditText)
        val timeEditText = dialogView.findViewById<TextInputEditText>(R.id.timeEditText)
        val companySpinner = dialogView.findViewById<Spinner>(R.id.companySpinner)
        val studentSpinner = dialogView.findViewById<Spinner>(R.id.studentSpinner)
        val modeSpinner = dialogView.findViewById<Spinner>(R.id.modeSpinner)
        val roundTypeSpinner = dialogView.findViewById<Spinner>(R.id.roundTypeSpinner)
        val locationEditText = dialogView.findViewById<TextInputEditText>(R.id.locationEditText)
        val roundNumberEditText =
            dialogView.findViewById<TextInputEditText>(R.id.roundNumberEditText)

        // Setup date picker
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    dateEditText.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Setup time picker
        timeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                val amPm = if (hour < 12) "AM" else "PM"
                val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                timeEditText.setText(String.format("%02d:%02d %s", hour12, minute, amPm))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        // Load companies and students
        lifecycleScope.launch {
            val companies = companyDao.getAllCompaniesSync()
            val companyNames = companies.map { it.companyName }
            companySpinner.adapter = ArrayAdapter(
                this@TPOInterviewManagementActivity,
                android.R.layout.simple_spinner_dropdown_item, companyNames
            )

            userDao.getUsersByRole(UserRole.STUDENT)
                .observe(this@TPOInterviewManagementActivity) { students ->
                    val studentNames = students.map { it.fullName }
                    studentSpinner.adapter = ArrayAdapter(
                        this@TPOInterviewManagementActivity,
                        android.R.layout.simple_spinner_dropdown_item, studentNames
                    )
                }

            // Setup mode spinner
            val modes = listOf("Online", "Offline")
            modeSpinner.adapter = ArrayAdapter(
                this@TPOInterviewManagementActivity,
                android.R.layout.simple_spinner_dropdown_item, modes
            )

            // Setup round type spinner
            val roundTypes = listOf("Technical", "HR", "Managerial", "Group Discussion", "Aptitude")
            roundTypeSpinner.adapter = ArrayAdapter(
                this@TPOInterviewManagementActivity,
                android.R.layout.simple_spinner_dropdown_item, roundTypes
            )
        }

        AlertDialog.Builder(this)
            .setTitle("Schedule Interview")
            .setView(dialogView)
            .setPositiveButton("Schedule") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val companies = companyDao.getAllCompaniesSync()
                        userDao.getUsersByRole(UserRole.STUDENT)
                            .observe(this@TPOInterviewManagementActivity) { students ->
                                if (companySpinner.selectedItemPosition >= 0 && studentSpinner.selectedItemPosition >= 0) {
                                    val company = companies[companySpinner.selectedItemPosition]
                                    val student = students[studentSpinner.selectedItemPosition]

                                    val interview = Interview(
                                        applicationId = 0,
                                        studentId = student.id,
                                        companyId = company.id,
                                        interviewDate = dateEditText.text.toString(),
                                        interviewTime = timeEditText.text.toString(),
                                        interviewMode = modeSpinner.selectedItem.toString(),
                                        interviewLocation = locationEditText.text.toString(),
                                        interviewRound = roundNumberEditText.text.toString()
                                            .toIntOrNull() ?: 1,
                                        roundType = roundTypeSpinner.selectedItem.toString(),
                                        status = InterviewStatus.SCHEDULED
                                    )

                                    lifecycleScope.launch {
                                        interviewDao.insert(interview)
                                        loadInterviews()
                                        loadStatistics()
                                        Toast.makeText(
                                            this@TPOInterviewManagementActivity,
                                            "Interview scheduled!", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@TPOInterviewManagementActivity,
                            "Error scheduling interview", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditInterviewDialog(interview: Interview) {
        // Similar to add dialog but pre-filled with interview data
        Toast.makeText(this, "Edit interview: ${interview.interviewId}", Toast.LENGTH_SHORT).show()
    }

    private fun cancelInterview(interview: Interview) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Interview")
            .setMessage("Are you sure you want to cancel this interview?")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    val updated = interview.copy(
                        status = InterviewStatus.CANCELLED,
                        updatedAt = System.currentTimeMillis()
                    )
                    interviewDao.update(updated)
                    loadInterviews()
                    loadStatistics()
                    Toast.makeText(
                        this@TPOInterviewManagementActivity,
                        "Interview cancelled", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun completeInterview(interview: Interview) {
        AlertDialog.Builder(this)
            .setTitle("Complete Interview")
            .setMessage("Mark this interview as completed?")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    val updated = interview.copy(
                        status = InterviewStatus.COMPLETED,
                        updatedAt = System.currentTimeMillis()
                    )
                    interviewDao.update(updated)
                    loadInterviews()
                    loadStatistics()
                    Toast.makeText(
                        this@TPOInterviewManagementActivity,
                        "Interview marked as completed", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
