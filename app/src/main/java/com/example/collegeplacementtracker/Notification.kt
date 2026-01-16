package com.example.collegeplacementtracker

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["createdAt"]),
        Index(value = ["isRead"])
    ]
)
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val userId: Long,
    
    val title: String,
    
    val message: String,
    
    val type: String, // "application_status", "new_company", "deadline", "approval", "interview"
    
    val relatedId: Long? = null, // Company ID, Application ID, etc.
    
    val createdAt: Long = System.currentTimeMillis(),
    
    val isRead: Boolean = false,
    
    val readAt: Long? = null,
    
    val actionUrl: String? = null, // Deep link to relevant screen
    
    val priority: String = "normal" // "low", "normal", "high", "urgent"
)

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getUserNotifications(userId: Long): List<Notification>
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    suspend fun getUnreadNotifications(userId: Long): List<Notification>
    
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    suspend fun getUnreadCount(userId: Long): Int
    
    @Insert
    suspend fun insertNotification(notification: Notification): Long
    
    @Update
    suspend fun updateNotification(notification: Notification)
    
    @Query("UPDATE notifications SET isRead = 1, readAt = :readAt WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: Long, readAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE notifications SET isRead = 1, readAt = :readAt WHERE userId = :userId")
    suspend fun markAllAsRead(userId: Long, readAt: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM notifications WHERE userId = :userId AND createdAt < :beforeDate")
    suspend fun deleteOldNotifications(userId: Long, beforeDate: Long)
}