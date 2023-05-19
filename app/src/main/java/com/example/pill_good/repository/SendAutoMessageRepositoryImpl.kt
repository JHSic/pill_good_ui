package com.example.pill_good.repository

import com.example.pill_good.data.dto.AutoMessageDTO
import com.example.pill_good.data.remote.ApiService

class SendAutoMessageRepositoryImpl(private val apiService: ApiService) {
    suspend fun readByUserId(userId: Long): List<AutoMessageDTO>? {
        val response = apiService.getMessageContentByUserId(userId)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }
}