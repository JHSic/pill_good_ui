package com.example.pill_good.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.repository.OCRRepositoryImpl
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

class CameraResultViewModel(private val ocrRepositoryImpl: OCRRepositoryImpl) : ViewModel() {
    fun sendOcrImage(
        groupMemberIndex: Long,
        groupMemberName: String,
        dateStart: LocalDate,
        userFcmToken: String,
        prescriptionImage: File
    ) {
        viewModelScope.launch {
            ocrRepositoryImpl.createInitialOCR(groupMemberIndex, groupMemberName, dateStart, userFcmToken, prescriptionImage)
        }
    }
}