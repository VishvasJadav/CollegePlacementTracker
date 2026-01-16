package com.example.collegeplacementtracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StudentResourcesActivity : AppCompatActivity() {

    private lateinit var resourcesRecyclerView: RecyclerView
    private lateinit var resourcesAdapter: ResourcesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_resources)

        // Setup action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Placement Resources"

        setupQuickAccessCards()
        setupResourcesList()
    }

    private fun setupQuickAccessCards() {
        // Resume Templates Card
        findViewById<CardView>(R.id.resumeTemplatesCard).setOnClickListener {
            openWebUrl("https://www.overleaf.com/gallery/tagged/cv")
        }

        // Interview Tips Card
        findViewById<CardView>(R.id.interviewTipsCard).setOnClickListener {
            openWebUrl("https://www.themuse.com/advice/interview-tips")
        }

        // Aptitude Test Card
        findViewById<CardView>(R.id.aptitudeTestCard).setOnClickListener {
            openWebUrl("https://www.indiabix.com/")
        }

        // Coding Practice Card
        findViewById<CardView>(R.id.codingPracticeCard).setOnClickListener {
            openWebUrl("https://leetcode.com/")
        }

        // Certifications Card
        findViewById<CardView>(R.id.certificationsCard).setOnClickListener {
            openWebUrl("https://www.coursera.org/careers/programming")
        }

        // Networking Card
        findViewById<CardView>(R.id.networkingCard).setOnClickListener {
            openWebUrl("https://www.linkedin.com")
        }

        // Salary Negotiation Card
        findViewById<CardView>(R.id.salaryNegotiationCard).setOnClickListener {
            openWebUrl("https://www.glassdoor.com/blog/guide/salary-negotiation/")
        }

        // Career Guidance Card
        findViewById<CardView>(R.id.careerGuidanceCard).setOnClickListener {
            openWebUrl("https://www.mymajors.com/")
        }
    }

    private fun setupResourcesList() {
        resourcesRecyclerView = findViewById(R.id.resourcesRecyclerView)
        resourcesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Sample resources data
        val resources = listOf(
            Resource(
                "Top 100 Interview Questions",
                "Commonly asked technical interview questions",
                "PDF",
                "https://www.geeksforgeeks.org/top-100-data-structures-and-algorithms-dsa-interview-questions-topic-wise/"
            ),
            Resource(
                "Resume Writing Guide",
                "Complete guide to writing ATS-friendly resumes",
                "Article",
                "https://www.indeed.com/career-advice/resumes-cover-letters/how-to-make-a-resume"
            ),
            Resource(
                "Salary Negotiation Tips",
                "How to negotiate your first job offer",
                "Article",
                "https://www.glassdoor.com/blog/guide/salary-negotiation/"
            ),
            Resource(
                "Company Research Guide",
                "How to research companies before interviews",
                "Guide",
                "https://www.themuse.com/advice/the-ultimate-guide-to-researching-a-company-pre-interview"
            ),
            Resource(
                "Behavioral Questions",
                "50+ behavioral interview questions with answers",
                "PDF",
                "https://www.themuse.com/advice/behavioral-interview-questions-answers-examples"
            ),
            Resource(
                "Technical Mock Interviews",
                "Practice technical interviews with peers",
                "Platform",
                "https://www.pramp.com/"
            ),
            Resource(
                "Group Discussion Topics",
                "150+ GD topics with points",
                "Article",
                "https://www.mbauniverse.com/group-discussion/topics"
            ),
            Resource(
                "Email Etiquette",
                "Professional email writing guide",
                "Guide",
                "https://www.grammarly.com/blog/email-etiquette/"
            )
        )

        resourcesAdapter = ResourcesAdapter(resources) { resource ->
            openWebUrl(resource.url)
        }
        resourcesRecyclerView.adapter = resourcesAdapter
    }

    private fun openWebUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open link: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

// Data class for Resource
data class Resource(
    val title: String,
    val description: String,
    val type: String,
    val url: String
)
