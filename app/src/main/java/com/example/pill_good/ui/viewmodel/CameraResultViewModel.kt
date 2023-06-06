package com.example.pill_good.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.repository.OCRRepositoryImpl
import com.orhanobut.logger.Logger
import kotlinx.coroutines.async
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
            val result = async {ocrRepositoryImpl.createInitialOCR(groupMemberIndex, groupMemberName, dateStart, userFcmToken, prescriptionImage)}
            val requestResult = result.await()

            if (requestResult)
                return@launch
            else
                Logger.d("요청실패")
        }
    }
}