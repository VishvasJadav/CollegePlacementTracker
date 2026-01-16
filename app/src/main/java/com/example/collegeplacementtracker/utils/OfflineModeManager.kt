package com.example.collegeplacementtracker.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.collegeplacementtracker.AppDatabaseNew
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

/**
 * Manages offline mode and syncing
 * Monitors network connectivity and syncs data when online
 */
class OfflineModeManager(private val context: Context) {

    private val _isOnline = MutableStateFlow(checkNetworkStatus())
    val isOnline: StateFlow<Boolean> = _isOnline

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    init {
        registerNetworkCallback()
    }

    /**
     * Register network callback to monitor connectivity changes
     */
    private fun registerNetworkCallback() {
        try {
            connectivityManager.registerDefaultNetworkCallback(
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        _isOnline.value = true
                        syncPendingData()
                    }

                    override fun onLost(network: Network) {
                        _isOnline.value = false
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities
                    ) {
                        val isConnected = networkCapabilities.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_INTERNET
                        ) && networkCapabilities.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_VALIDATED
                        )
                        _isOnline.value = isConnected
                    }
                }
            )
        } catch (e: Exception) {
            // Fallback if callback registration fails
            _isOnline.value = checkNetworkStatus()
        }
    }

    /**
     * Check current network status
     */
    private fun checkNetworkStatus(): Boolean {
        try {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Sync pending data when network becomes available
     */
    fun syncPendingData() {
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10, TimeUnit.SECONDS
            )
            .addTag("sync_pending_data")
            .build()

        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }

    /**
     * Force sync now (if online)
     */
    fun forceSyncNow() {
        if (_isOnline.value) {
            syncPendingData()
        }
    }

    /**
     * Check if a specific operation can be performed
     * (e.g., uploading files requires network)
     */
    fun canPerformOperation(requiresNetwork: Boolean): Boolean {
        return if (requiresNetwork) _isOnline.value else true
    }
}

/**
 * Worker for syncing data in background
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabaseNew.getDatabase(
                applicationContext,
                CoroutineScope(Dispatchers.IO)
            )

            // Sync logic here
            // Example: Sync pending applications, refresh companies, etc.

            // For now, just mark as success
            // In a real app, you would:
            // 1. Get all pending operations from a queue table
            // 2. Execute them
            // 3. Mark them as completed

            Result.success()
        } catch (e: Exception) {
            // Retry with exponential backoff
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

/**
 * Extension function to check if device is online
 */
fun Context.isOnline(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
