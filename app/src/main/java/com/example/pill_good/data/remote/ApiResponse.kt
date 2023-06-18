package com.example.pill_good.data.remote

data class ApiResponse<T>(
    val statusCode: Int,
    val message: String,
    val data: T
)
