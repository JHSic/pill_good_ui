package com.example.pill_good.repository

import com.example.pill_good.data.dto.PrescriptionAndDiseaseNameDTO
import com.example.pill_good.data.remote.ApiService

class PrescriptionRepositoryImpl(private val apiService: ApiService) {
    suspend fun readListByGroupMemberId(groupMemberId: Long): List<PrescriptionAndDiseaseNameDTO>? {
        val response = apiService.getPrescriptionByGroupMemberId(groupMemberId)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

    suspend fun deleteById(id: Long): Unit? {
        val response = apiService.deletePrescriptionById(id)
        if (response.statusCode == 200) {
            return null
        } else {
            throw Exception()
        }
    }
}