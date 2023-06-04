package com.example.pill_good.repository

import com.example.pill_good.data.dto.LoginDTO
import com.example.pill_good.data.dto.UserDTO
import com.example.pill_good.data.remote.ApiService
import com.orhanobut.logger.Logger

class LoginRepositoryImpl(private val apiService: ApiService) {
    suspend fun login(loginDTO: LoginDTO): UserDTO? {
        val response = apiService.login(loginDTO)
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }
}