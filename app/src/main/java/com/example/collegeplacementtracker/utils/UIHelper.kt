package com.example.collegeplacementtracker.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object UIHelper {

    /**
     * Show error message with Snackbar
     */
    fun showError(context: Context, message: String, duration: Int = Snackbar.LENGTH_LONG) {
        if (context is Activity) {
            val rootView = context.findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, message, duration)
                .setBackgroundTint(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                .setTextColor(ContextCompat.getColor(context, android.R.color.white))
                .show()
        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Show success message with Snackbar
     */
    fun showSuccess(context: Context, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        if (context is Activity) {
            val rootView = context.findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, message, duration)
                .setBackgroundTint(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                .setTextColor(ContextCompat.getColor(context, android.R.color.white))
                .show()
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Show info message with Snackbar
     */
    fun showInfo(context: Context, message: String, duration: Int = Snackbar.LENGTH_LONG) {
        if (context is Activity) {
            val rootView = context.findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, message, duration)
                .setBackgroundTint(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                .setTextColor(ContextCompat.getColor(context, android.R.color.white))
                .show()
        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Show error with retry action
     */
    fun showErrorWithRetry(
        context: Context,
        message: String,
        onRetry: () -> Unit
    ) {
        if (context is Activity) {
            val rootView = context.findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE)
                .setBackgroundTint(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                .setTextColor(ContextCompat.getColor(context, android.R.color.white))
                .setAction("RETRY") {
                    onRetry()
                }
                .show()
        }
    }

    /**
     * Parse exception to user-friendly message
     */
    fun getErrorMessage(error: Throwable): String {
        return when (error) {
            is IOException -> "Network error. Please check your internet connection."
            is SocketTimeoutException -> "Request timed out. Please try again."
            is UnknownHostException -> "Unable to connect. Please check your internet connection."
            else -> error.localizedMessage ?: "An unexpected error occurred"
        }
    }
}
