package com.example.pill_good.data.dto

import java.time.LocalDate

data class TakePillCheckDTO(
    val takePillCheckIndex: Long? = null,
    val takePillIndex: Long? = null,
    val takeDate: LocalDate? = null,
    val takePillTime: Int? = null,
    val takeCheck: Boolean? = null
)
