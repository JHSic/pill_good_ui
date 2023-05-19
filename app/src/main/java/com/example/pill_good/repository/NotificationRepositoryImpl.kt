package com.example.pill_good.repository

import com.example.pill_good.data.dto.NotificationDTO
import com.example.pill_good.data.remote.ApiService

class NotificationRepositoryImpl(private val apiService: ApiService) {
    suspend fun readByUserId(userId: Long): List<NotificationDTO>? {
        val response = apiService.getNotificationByUserId(userId)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }
}