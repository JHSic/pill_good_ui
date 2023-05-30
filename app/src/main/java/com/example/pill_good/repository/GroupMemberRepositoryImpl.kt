package com.example.pill_good.repository

import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.data.dto.GroupMemberDTO
import com.example.pill_good.data.remote.ApiService

class GroupMemberRepositoryImpl(private val apiService: ApiService) {
    suspend fun create(groupMemberAndUserIndexDTO: GroupMemberAndUserIndexDTO): GroupMemberAndUserIndexDTO? {
        val response = apiService.createGroupMember(groupMemberAndUserIndexDTO)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

    suspend fun readListByUserId(userId: Long): List<GroupMemberAndUserIndexDTO>? {
        val response = apiService.getGroupMembersByUserId(userId)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

    suspend fun readById(id: Long): GroupMemberAndUserIndexDTO? {
        val response = apiService.getGroupMemberById(id)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

    suspend fun updateById(id: Long, groupMemberAndUserIndexDTO: GroupMemberAndUserIndexDTO): GroupMemberAndUserIndexDTO? {
        val response = apiService.updateGroupMember(id, groupMemberAndUserIndexDTO)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

    suspend fun deleteById(id: Long): Unit? {
        val response = apiService.deleteGroupMember(id)
        if (response.statusCode == 200) {
            return null
        } else {
            throw Exception()
        }
    }

}