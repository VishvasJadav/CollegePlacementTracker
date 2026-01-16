package com.example.collegeplacementtracker


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var studentViewModel: StudentViewModel
    private lateinit var adapter: StudentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var statsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView)
        emptyView = findViewById(R.id.emptyView)
        statsText = findViewById(R.id.statsText)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        // Setup RecyclerView
        adapter = StudentAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup ViewModel
        studentViewModel = ViewModelProvider(this).get(StudentViewModel::class.java)

        // Observe data
        studentViewModel.allStudents.observe(this) { students ->
            adapter.submitList(students)
            if (students.isEmpty()) {
                recyclerView.visibility = RecyclerView.GONE
                emptyView.visibility = TextView.VISIBLE
            } else {
                recyclerView.visibility = RecyclerView.VISIBLE
                emptyView.visibility = TextView.GONE
                updateStats(students)
            }
        }

        // FAB click listener
        fab.setOnClickListener {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivity(intent)
        }

        // Item click listener
        adapter.setOnItemClickListener { student ->
            val intent = Intent(this, StudentDetailActivity::class.java)
            intent.putExtra("STUDENT_ID", student.id)
            startActivity(intent)
        }

        // Item delete listener
        adapter.setOnDeleteClickListener { student ->
            studentViewModel.delete(student)
            Snackbar.make(
                findViewById(android.R.id.content),
                "Student deleted",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all -> {
                studentViewModel.deleteAll()
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "All students deleted",
                    Snackbar.LENGTH_SHORT
                ).show()
                true
            }

            R.id.action_filter_placed -> {
                studentViewModel.getPlacedStudents().observe(this) { students ->
                    adapter.submitList(students)
                }
                true
            }

            R.id.action_show_all -> {
                studentViewModel.allStudents.observe(this) { students ->
                    adapter.submitList(students)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateStats(students: List<Student>) {
        val total = students.size
        val placed = students.count { it.isPlaced }
        val avgPackage = students.filter { it.isPlaced }
            .mapNotNull { it.packageAmount }
            .average()

        val placementRate = if (total > 0) (placed * 100.0 / total) else 0.0

        val stats = """
            Total Students: $total
            Placed: $placed
            Placement Rate: ${"%.1f".format(placementRate)}%
            Avg Package: ${if (avgPackage.isNaN()) "N/A" else "%.2f LPA".format(avgPackage)}
        """.trimIndent()

        statsText.text = stats
    }
}
