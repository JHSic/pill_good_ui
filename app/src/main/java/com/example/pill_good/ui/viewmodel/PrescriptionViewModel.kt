package com.example.pill_good.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.data.dto.PrescriptionAndDiseaseNameDTO
import com.example.pill_good.data.dto.PrescriptionDTO
import com.example.pill_good.repository.PrescriptionRepositoryImpl
import kotlinx.coroutines.launch

class PrescriptionViewModel(private val prescriptionRepository : PrescriptionRepositoryImpl) : ViewModel() {

    private val _prescriptionData = MutableLiveData<List<PrescriptionAndDiseaseNameDTO>>()
    val prescriptionData : LiveData<List<PrescriptionAndDiseaseNameDTO>> get() = _prescriptionData

    fun loadPrescriptionData(groupMemberIndex : Long){
        viewModelScope.launch {
            try{
                val prescriptionList = prescriptionRepository.readListByGroupMemberId(groupMemberIndex)
                if(prescriptionList != null){
                    _prescriptionData.value = prescriptionList!!
                }

            } catch (e : Exception){
                // 에러 처리
            }
        }
    }

    fun removePrescription(prescriptionDTO : PrescriptionAndDiseaseNameDTO){
        viewModelScope.launch {
            try{
                if (prescriptionDTO.prescriptionIndex != null) {
//                    val deletedData = prescriptionRepository.deleteById(prescriptionDTO.prescriptionIndex)
//                    if (deletedData == null) {
                    val currentList = _prescriptionData.value?.toMutableList()
                    currentList?.remove(prescriptionDTO)
                    _prescriptionData.value = currentList!!
//                    }
                }
            } catch (e : Exception){
                // 에러 처리
            }
        }
    }
}