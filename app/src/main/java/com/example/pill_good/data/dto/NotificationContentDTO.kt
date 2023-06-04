package com.example.pill_good.data.dto

data class NotificationContentDTO (
    val userIndex: Long? = null,
    val groupMemberName: String? = null,
    val takePillTime: Int? = -1,
    val userFcmToken: String? = null
)