package com.example.collegeplacementtracker

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Custom Application class for app-wide initialization
 *
 * Responsibilities:
 * - Setup app-wide coroutine scope
 * - Initialize other app-wide components (analytics, crash reporting, etc.)
 */
class PlacementTrackerApplication : Application() {

    // Application-wide coroutine scope for background operations
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        // Initialize other app-wide components here
        // Example: Firebase, Analytics, Crash Reporting, etc.
    }
}

