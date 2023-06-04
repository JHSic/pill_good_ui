package com.example.pill_good.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.data.dto.EditOCRDTO
import com.example.pill_good.repository.OCRRepositoryImpl
import kotlinx.coroutines.launch


class EditOCRViewModel(private val ocrRepository: OCRRepositoryImpl) : ViewModel(){
    private val _editOCRData = MutableLiveData<EditOCRDTO>() //수정
    val editOCRData : LiveData<EditOCRDTO> get() = _editOCRData //수정

    fun setPrescriptionEditData(initialEditOCRData : EditOCRDTO){ // dto 수정
        _editOCRData.value = initialEditOCRData
    }

    fun editPillData(updatedEditOCRData: EditOCRDTO) { //dto 수정
        // 라이브 데이터 업데이트
        viewModelScope.launch {
            try {
                // 대치 작업 수행
                _editOCRData.value = updatedEditOCRData
                ocrRepository.createUpdatedOCR(editOCRData.value!!)
            } catch(e : Exception){
                // 에러 처리
            }
        }
    }
}