package com.example.pill_good.data.dto

import java.time.LocalDateTime

data class AutoMessageDTO(
    val diseaseName: String? = null,
    val takeDate: LocalDateTime? = null,
    val takePillTime: LocalDateTime? = null,
    val messageContent: String? = null
)
