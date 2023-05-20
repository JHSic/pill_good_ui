package com.example.pill_good.repository

import com.example.pill_good.data.dto.UserDTO
import com.example.pill_good.data.remote.ApiService

class UserRepositoryImpl(private val apiService: ApiService) {
    suspend fun updateUserToken(id: Long, userDTO: UserDTO): UserDTO? {
        val response = apiService.updateUserToken(id, userDTO)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

    suspend fun deleteUser(id: Long): Unit? {
        val response = apiService.deleteUserById(id)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }
}