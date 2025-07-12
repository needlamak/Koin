
package com.koin.data.notification

import com.koin.domain.notification.Notification
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {

    fun getNotifications(): Flow<List<Notification>> = notificationDao.getNotifications()

    suspend fun insert(notification: Notification) {
        notificationDao.insert(notification)
    }

    suspend fun markAsRead(id: Long) {
        notificationDao.markAsRead(id)
    }

    fun getUnreadCount(): Flow<Int> = notificationDao.getUnreadCount()
}
