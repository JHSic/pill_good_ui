package com.example.pill_good.data.dto

import java.io.Serializable

data class UserDTO(
    val userIndex: Long? = null,
    val userEmail: String? = null,
    val userFcmToken: String? = null,
): Serializable
