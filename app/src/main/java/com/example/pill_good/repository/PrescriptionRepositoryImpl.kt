package com.example.pill_good.repository

import com.example.pill_good.data.dto.PrescriptionAndDiseaseNameDTO
import com.example.pill_good.data.dto.PrescriptionDTO
import com.example.pill_good.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PrescriptionRepositoryImpl(private val apiService: ApiService) {
    suspend fun createByImage(
        userId: Long,
        groupMemberId: Long,
        prescriptionImage: File
    ): PrescriptionDTO { // 확인 후 공부해야함
        val requestFile = prescriptionImage.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "prescriptionImage",
            prescriptionImage.name,
            requestFile
        )
        val response = apiService.createPrescriptionByImage(userId, groupMemberId, imagePart)

        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

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