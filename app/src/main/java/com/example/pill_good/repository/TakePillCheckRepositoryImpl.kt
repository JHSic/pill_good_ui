package com.example.pill_good.repository

import com.example.pill_good.data.remote.ApiService

class TakePillCheckRepositoryImpl(private val apiService: ApiService) {
    suspend fun updateTakeCheck(id: Long, takeCheck: Boolean) {
        val response = apiService.updateTakeCheck(id, takeCheck)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

}