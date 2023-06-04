package com.example.pill_good.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.repository.GroupMemberRepositoryImpl
import kotlinx.coroutines.launch

class GroupViewModel(private val groupMemberRepository : GroupMemberRepositoryImpl) : ViewModel(){

    private val _groupData = MutableLiveData<List<GroupMemberAndUserIndexDTO>>()
    val groupData : LiveData<List<GroupMemberAndUserIndexDTO>> get() = _groupData
//    fun loadGroupMembers() {
//        viewModelScope.launch {
//            try {
//                val groupMemberList = groupMemberRepository.readListByUserId() // user_index 줘야함
//                _groupData.value = groupMemberList!!
//            } catch (e: Exception) {
//                // 에러 처리
//            }
//        }
//    }

    fun setGroupDataByMainData(groupMemberList : ArrayList<GroupMemberAndUserIndexDTO>){
        _groupData.value = groupMemberList
    }

    // groupMemberDTO가 아닌 유저 이름, 생년월일, 전화번호 정보만 받는걸로 수정 -> 이게 모델 만드는건강
    fun addGroupMember(groupMember : GroupMemberAndUserIndexDTO) {
        viewModelScope.launch {
            try{
                val addGroupMember = groupMemberRepository.create(groupMember)
                addGroupMember?.let { addGroupMember ->
                    val currentList = groupData.value?.toMutableList()
                    currentList!!.add(addGroupMember!!)
                    println(currentList.size)
                    _groupData.value = currentList!!
                }
            } catch (e : Exception){
                // 에러 처리
            }
        }
    }

    // 그룹원 수정
    fun editGroupMember(groupMember: GroupMemberAndUserIndexDTO) {
        viewModelScope.launch {
            try {
                val updatedGroupMember = groupMemberRepository.updateById(groupMember.groupMemberIndex!!, groupMember)
            // 응답이 성공적으로 받아왔을 경우
                updatedGroupMember?.let { updateGroupMember ->
                    val currentList = groupData.value?.toMutableList()
                    val index = currentList?.indexOfFirst { it.groupMemberIndex == updateGroupMember.groupMemberIndex }
                    if (index != null && index != -1) {
                        currentList[index] = updateGroupMember
                        _groupData.value = currentList!!
                    }
                }
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }


    // 그룹원 삭제
    fun removeGroupMember(groupMember: GroupMemberAndUserIndexDTO) {
        viewModelScope.launch {
            try {
                if (groupMember.groupMemberIndex != null) {
                    val deletedData = groupMemberRepository.deleteById(groupMember.groupMemberIndex)
                    if (deletedData == null) {
                        val currentList = _groupData.value?.toMutableList()
                        currentList?.remove(groupMember)
                        _groupData.value = currentList!!
                    }
                }
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

}
