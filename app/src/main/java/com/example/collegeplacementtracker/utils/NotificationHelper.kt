package com.example.collegeplacementtracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.collegeplacementtracker.MyApplicationsActivity
import com.example.collegeplacementtracker.R

object NotificationHelper {

    private const val CHANNEL_ID = "placement_notifications"
    private const val CHANNEL_NAME = "Placement Updates"
    private const val CHANNEL_DESCRIPTION = "Notifications for placement applications and updates"

    /**
     * Create notification channel (Android O+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Send application status update notification
     */
    fun sendApplicationStatusNotification(
        context: Context,
        companyName: String,
        status: String,
        applicationId: Int
    ) {
        val intent = Intent(context, MyApplicationsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("APPLICATION_ID", applicationId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            applicationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = when (status) {
            "SHORTLISTED" -> "🎉 Shortlisted!"
            "SELECTED" -> "🎊 Congratulations!"
            "REJECTED" -> "Update"
            else -> "Application Update"
        }

        val message = when (status) {
            "SHORTLISTED" -> "You've been shortlisted by $companyName"
            "SELECTED" -> "You've been selected by $companyName!"
            "REJECTED" -> "Update from $companyName"
            else -> "Your application to $companyName has been updated"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(applicationId, notification)
    }

    /**
     * Send new company notification
     */
    fun sendNewCompanyNotification(
        context: Context,
        companyName: String,
        packageAmount: Double,
        companyId: Int
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Company Posted!")
            .setContentText("$companyName is hiring - Package: $packageAmount LPA")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(companyId + 10000, notification)
    }

    /**
     * Send deadline reminder notification
     */
    fun sendDeadlineReminder(
        context: Context,
        companyName: String,
        deadline: String,
        companyId: Int
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⏰ Application Deadline")
            .setContentText("Deadline for $companyName is $deadline")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(companyId + 20000, notification)
    }

    /**
     * Send approval request notification (for HOD)
     */
    fun sendApprovalRequestNotification(
        context: Context,
        studentName: String,
        companyName: String,
        applicationId: Int
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Approval Required")
            .setContentText("$studentName applied to $companyName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(applicationId + 30000, notification)
    }
}
