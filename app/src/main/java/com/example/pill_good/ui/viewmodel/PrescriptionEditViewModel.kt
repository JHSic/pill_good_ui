package com.example.pill_good.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.data.model.PrescriptionData
import kotlinx.coroutines.launch


class PrescriptionEditViewModel() : ViewModel(){
    private val _prescriptionEditData = MutableLiveData<PrescriptionData>()
    val prescriptionEditData : LiveData<PrescriptionData> get() = _prescriptionEditData

    fun setPrescriptionEditData(prescriptionData : PrescriptionData){
        _prescriptionEditData.value = prescriptionData
    }

    fun editprescriptionEditData(prescriptionData : PrescriptionData){
        viewModelScope.launch {
            try {

                // 대치 작업 수행
                _prescriptionEditData.value = prescriptionData
            } catch(e : Exception){
                // 에러 처리
            }
        }
    }

    fun editPillData(prescriptionEditData: PrescriptionData) {
        // 라이브 데이터 업데이트
        _prescriptionEditData.value = prescriptionEditData
    }
}