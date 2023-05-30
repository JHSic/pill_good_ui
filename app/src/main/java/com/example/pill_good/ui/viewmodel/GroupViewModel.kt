package com.example.pill_good.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.data.dto.GroupMemberDTO
import com.example.pill_good.repository.GroupMemberRepositoryImpl
import kotlinx.coroutines.launch

class GroupViewModel(private val groupMemberRepository : GroupMemberRepositoryImpl) : ViewModel(){

    private val _groupData = MutableLiveData<List<GroupMemberAndUserIndexDTO>>()
    val groupData : LiveData<List<GroupMemberAndUserIndexDTO>> get() = _groupData

    fun loadGroupMembers() {
        viewModelScope.launch {
            try {
//                val groupMemberList = groupMemberRepository.readListByUserId() // user_index 줘야함
//                _groupData.value = groupMemberList!!
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    // groupMemberDTO가 아닌 유저 이름, 생년월일, 전화번호 정보만 받는걸로 수정 -> 이게 모델 만드는건강
    fun addGroupMember(groupMember : GroupMemberAndUserIndexDTO) {
        viewModelScope.launch {
            try{
                val addGroupMember = groupMemberRepository.create(groupMember)

            } catch (e : Exception){
                // 에러 처리
            }
//            groupMemberRepository.create() //groupMemberAndUserIndexDTO를 받도록 구성되어있는데 동희형한테 물어봐야함
        }
        // 그 다음 그룹원 리스트에 추가해야하는데 해당 값이 groupViewModel에 있으므로 합쳐서 쓸지 고민
    }

    // 그룹원 수정
    fun editGroupMember(groupMember: GroupMemberAndUserIndexDTO) {
        viewModelScope.launch {
            try {
                val updatedGroupMember = groupMemberRepository.updateById(groupMember.groupMemberIndex!!, groupMember)
                // 응답이 성공적으로 받아왔을 경우
                updatedGroupMember?.let {
                    // 대치 작업 수행
                    val currentList = _groupData.value?.toMutableList()
                    val index = currentList?.indexOfFirst { it.groupMemberIndex == groupMember.groupMemberIndex }
                    if (index != null && index != -1) {
                        currentList[index] = updatedGroupMember
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


    fun setGroupData(groupData : List<GroupMemberAndUserIndexDTO>){
//        this._groupData.value = groupData
    }
}
