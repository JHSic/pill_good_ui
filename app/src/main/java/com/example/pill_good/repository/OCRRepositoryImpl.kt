package com.example.pill_good.repository

import com.example.pill_good.data.dto.EditOCRDTO
import com.example.pill_good.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate

class OCRRepositoryImpl(private val apiService: ApiService) {
    suspend fun createInitialOCR(
        groupMemberIndex: Long,
        groupMemberName: String,
        dateStart: LocalDate,
        userFcmToken: String,
        prescriptionImage: File
    ): Boolean {
        val requestFile = prescriptionImage.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "image",
            prescriptionImage.name,
            requestFile
        )
        val response = apiService.createInitialOCR(
            groupMemberIndex,
            groupMemberName,
            dateStart,
            userFcmToken,
            imagePart
        )

        if (response.statusCode == 200) {
            return true
        } else {
            throw Exception()
        }
    }

    suspend fun createUpdatedOCR(editOCR: EditOCRDTO): Boolean {
        val response = apiService.createUpdatedOCR(editOCR)
        if (response.statusCode == 200) {
            return true
        } else {
            throw Exception()
        }
    }

}