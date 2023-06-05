package com.example.pill_good.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.data.dto.PillDTO
import com.example.pill_good.data.dto.SearchingConditionDTO
import com.example.pill_good.repository.PillRepositoryImpl
import kotlinx.coroutines.launch

class PillViewModel(private val pillRepository : PillRepositoryImpl) : ViewModel() {

    private val _pillData = MutableLiveData<List<PillDTO>>()
    val pillData : LiveData<List<PillDTO>> get() = _pillData

    fun loadPillData(searchPillDTO : SearchingConditionDTO){
        viewModelScope.launch {
            try{
                val loadPillData = pillRepository.readBySearchingCondition(searchPillDTO)
                loadPillData?.let{
                    _pillData.value = loadPillData!!
                }
            } catch (e : Exception) {
                // 예외 처리
            }
        }
    }

    fun loadOnlyPillName(pillName : String){
        viewModelScope.launch {
            try{
                val loadPillData = listOf(pillRepository.readByPillName(pillName)!!)
                loadPillData?.let {
                    _pillData.value = loadPillData
                }
            } catch (e : Exception){
                // 예외 처리
            }
        }
    }
}