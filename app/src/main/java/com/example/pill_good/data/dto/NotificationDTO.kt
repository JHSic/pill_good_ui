package com.example.pill_good.data.dto

import java.time.LocalDateTime

data class NotificationDTO(
    val notificationIndex: Long? = null,
    val notificationContent: String? = null,
    val notificationTime: LocalDateTime? = null,
    val notificationCheck: Boolean? = null
)
