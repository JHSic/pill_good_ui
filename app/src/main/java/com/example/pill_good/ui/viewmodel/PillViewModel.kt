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
//            _pillData.value = pillRepository.readBySearchingCondition(searchPillDTO)
                test()
            } catch (e : Exception) {
                // 예외 처리
            }
        }
    }

    private fun test(){
        val testPill1 = PillDTO(
            pillIndex = 1,
            pillNum = "195900043",
            pillName = "Test Pill 1",
            pillFrontWord = "Front Word 1",
            pillBackWord = "Back Word 1",
            pillShape = "Shape 1",
            pillColor = "Color 1",
            pillCategoryName = "Category 1",
            pillFormulation = "Formulation 1",
            pillEffect = "Effect 1",
            pillPrecaution = "Precaution 1"
        )

        val testPill2 = PillDTO(
            pillIndex = 2,
            pillNum = "196000001",
            pillName = "Test Pill 2",
            pillFrontWord = "Front Word 2",
            pillBackWord = "Back Word 2",
            pillShape = "Shape 2",
            pillColor = "Color 2",
            pillCategoryName = "Category 2",
            pillFormulation = "Formulation 2",
            pillEffect = "Effect 2",
            pillPrecaution = "Precaution 2"
        )

        val testPill3 = PillDTO(
            pillIndex = 3,
            pillNum = "196000008",
            pillName = "Test Pill 3",
            pillFrontWord = "Front Word 3",
            pillBackWord = "Back Word 3",
            pillShape = "Shape 3",
            pillColor = "Color 3",
            pillCategoryName = "Category 3",
            pillFormulation = "Formulation 3",
            pillEffect = "Effect 3",
            pillPrecaution = "Precaution 3"
        )

        val testPillList = listOf(testPill1, testPill2, testPill3)

        _pillData.value = testPillList

    }

}