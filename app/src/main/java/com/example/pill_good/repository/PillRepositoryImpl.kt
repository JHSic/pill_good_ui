package com.example.pill_good.repository

import com.example.pill_good.data.dto.PillDTO
import com.example.pill_good.data.dto.SearchingConditionDTO
import com.example.pill_good.data.remote.ApiService

class PillRepositoryImpl(private val apiService: ApiService) {
    suspend fun readById(id: Long): PillDTO? {
        val response = apiService.getPillById(id)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

    suspend fun readByPillName(pillName: String): PillDTO? {
        val response = apiService.getPillByPillName(pillName)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

    suspend fun readBySearchingCondition(searchingConditionDTO: SearchingConditionDTO): List<PillDTO>? {
        val response = apiService.getPillBySearchingCondition(searchingConditionDTO)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }
}